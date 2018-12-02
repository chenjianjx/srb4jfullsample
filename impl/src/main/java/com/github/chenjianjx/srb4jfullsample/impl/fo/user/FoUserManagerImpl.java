package com.github.chenjianjx.srb4jfullsample.impl.fo.user;

import com.github.chenjianjx.srb4jfullsample.impl.biz.user.*;
import com.github.chenjianjx.srb4jfullsample.impl.fo.common.FoManagerImplBase;
import com.github.chenjianjx.srb4jfullsample.impl.support.beanvalidate.MyValidator;
import com.github.chenjianjx.srb4jfullsample.impl.support.beanvalidate.ValidationError;
import com.github.chenjianjx.srb4jfullsample.intf.fo.auth.FoChangePasswordRequest;
import com.github.chenjianjx.srb4jfullsample.intf.fo.basic.FoConstants;
import com.github.chenjianjx.srb4jfullsample.intf.fo.basic.FoResponse;
import com.github.chenjianjx.srb4jfullsample.intf.fo.user.FoUser;
import com.github.chenjianjx.srb4jfullsample.intf.fo.user.FoUserManager;
import com.github.chenjianjx.srb4jfullsample.utils.lang.MyCodecUtils;
import com.github.chenjianjx.srb4jfullsample.utils.lang.MyLangUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.github.chenjianjx.srb4jfullsample.intf.fo.basic.FoConstants.NULL_REQUEST_BEAN_TIP;

/**
 * @author chenjianjx@gmail.com
 */
@Service("foUserManager")
public class FoUserManagerImpl extends FoManagerImplBase implements
        FoUserManager {

    private static final Logger logger = LoggerFactory.getLogger(FoUserManagerImpl.class);

    @Resource
    MyValidator myValidator;
    

    @Resource
    UserRepo userRepo;

    @Resource
    UserService userService;

    @Resource
    EmailVerificationDigestRepo emailVerificationDigestRepo;

    @Override
    public FoResponse<Void> changePassword(Long currentUserId,
                                           FoChangePasswordRequest request) {

        ValidationError error = myValidator.validateBean(request, NULL_REQUEST_BEAN_TIP);
        if (error.hasErrors()) {
            return FoResponse.userErrResponse(
                    FoConstants.FEC_OAUTH2_INVALID_REQUEST, error.getNonFieldError(), error.getFieldErrors());
        }

        User currentUser = getCurrentUserConsideringInvalidId(currentUserId);
        if (currentUser == null) {
            return buildNotLoginErr();
        }

        if (!MyCodecUtils.isPasswordDjangoMatches(request.getCurrentPassword(), currentUser.getPassword())) {
            return FoResponse.userErrResponse(
                    FoConstants.FEC_OAUTH2_INVALID_REQUEST,
                    "The current password you input is wrong", null);
        }

        String newPassword = MyCodecUtils.encodePasswordLikeDjango(request.getNewPassword());

        User user = userRepo.getUserById(currentUserId);
        user.setPassword(newPassword);
        user.setUpdatedBy(user.getPrincipal());
        userRepo.updateUser(user);

        return FoResponse.success(null);

    }

    @Override
    public FoResponse<Void> startEmailVerification(Long currentUserId, String verificationUrlBase, String digestParamName) {

        User currentUser = getCurrentUserConsideringInvalidId(currentUserId);
        if (currentUser == null) {
            return buildNotLoginErr();
        }

        User user = currentUser;
        if (user.isEmailVerified()) {
            return FoResponse.userErrResponse(
                    FoConstants.FEC_ILLEGAL_STATUS, "Your email is already verified.", null);
        }

        EmailVerificationDigest digest = userService.saveNewEmailVerificationDigestForUser(user);

        // send the email
        try {
            userService.sendEmailForEmailVerificationAsync(user, digest, verificationUrlBase, digestParamName);
        } catch (Exception e) {
            logger.error("fail to send email verification link asyncly  for user "
                    + user.getPrincipal(), e);
        }

        return FoResponse.success(null);
    }

    @Override
    public FoResponse<Void> verifyEmail(String digestStr) {
        digestStr = StringUtils.trimToNull(digestStr);
        if (digestStr == null) {
            return FoResponse.userErrResponse(FoConstants.FEC_INVALID_INPUT, "Invalid request", null);
        }

        EmailVerificationDigest digest = emailVerificationDigestRepo.getByDigestStr(digestStr);
        if (digest == null) {
            return FoResponse.userErrResponse(FoConstants.FEC_INVALID_INPUT, "Invalid request", null);
        }

        User user = userRepo.getUserById(digest.getUserId());

        if (user == null) {
            return FoResponse.userErrResponse(FoConstants.FEC_INVALID_INPUT, "Invalid request", null);
        }

        if (user.isEmailVerified()) {
            //verified already? Just say congratulations
            return FoResponse.success(null);
        }

        if (digest.hasExpired()) {
            return FoResponse.userErrResponse(FoConstants.FEC_INVALID_INPUT, "The link has expired.", null);
        }

        user.setEmailVerified(true);
        userRepo.updateUser(user);
        return FoResponse.success(null);
    }

    @Override
    public FoResponse<FoUser> getCurrentUser(Long currentUserId) {
        User currentUser = getCurrentUserConsideringInvalidId(currentUserId);
        if (currentUser == null) {
            return buildNotLoginErr();
        }
        FoUser foUser = MyLangUtils.copyPropertiesToNewObject(FoUser.class, currentUser);
        return FoResponse.success(foUser);
    }
}
