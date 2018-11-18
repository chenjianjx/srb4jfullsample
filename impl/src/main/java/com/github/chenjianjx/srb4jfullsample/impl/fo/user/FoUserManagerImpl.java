package com.github.chenjianjx.srb4jfullsample.impl.fo.user;

import com.github.chenjianjx.srb4jfullsample.impl.biz.auth.AuthService;
import com.github.chenjianjx.srb4jfullsample.impl.biz.user.EmailVerificationDigest;
import com.github.chenjianjx.srb4jfullsample.impl.biz.user.EmailVerificationDigestRepo;
import com.github.chenjianjx.srb4jfullsample.impl.biz.user.User;
import com.github.chenjianjx.srb4jfullsample.impl.biz.user.UserRepo;
import com.github.chenjianjx.srb4jfullsample.impl.biz.user.UserService;
import com.github.chenjianjx.srb4jfullsample.impl.fo.common.FoManagerImplBase;
import com.github.chenjianjx.srb4jfullsample.impl.util.infrahelp.beanvalidae.MyValidator;
import com.github.chenjianjx.srb4jfullsample.intf.fo.auth.FoChangePasswordRequest;
import com.github.chenjianjx.srb4jfullsample.intf.fo.user.FoUserManager;
import com.github.chenjianjx.srb4jfullsample.intf.fo.basic.FoConstants;
import com.github.chenjianjx.srb4jfullsample.intf.fo.basic.FoResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.github.chenjianjx.srb4jfullsample.intf.fo.basic.FoConstants.NULL_REQUEST_BEAN_TIP;

/**
 * 
 * @author chenjianjx@gmail.com
 *
 */
@Service("foUserManager")
public class FoUserManagerImpl extends FoManagerImplBase implements
		FoUserManager {

	private static final Logger logger = LoggerFactory.getLogger(FoUserManagerImpl.class);

	@Resource
	MyValidator myValidator;

	@Resource
	AuthService authService;

	@Resource
	UserRepo userRepo;

	@Resource
	UserService userService;

	@Resource
	EmailVerificationDigestRepo emailVerificationDigestRepo;

	@Override
	public FoResponse<Void> changePassword(Long currentUserId,
			FoChangePasswordRequest request) {

		String error = myValidator.validateBeanFastFail(request,
				NULL_REQUEST_BEAN_TIP);
		if (error != null) {
			return FoResponse.userErrResponse(
					FoConstants.FEC_OAUTH2_INVALID_REQUEST, error);
		}

		User currentUser = getCurrentUserConsideringInvalidId(currentUserId);
		if (currentUser == null) {
			return buildNotLoginErr();
		}

		// now compare password
		String encodedCurrentPassword = authService
				.encodePasswordOrRandomCode(request.getCurrentPassword());

		if (!encodedCurrentPassword.equals(currentUser.getPassword())) {
			return FoResponse.userErrResponse(
					FoConstants.FEC_OAUTH2_INVALID_REQUEST,
					"The current password you input is wrong");
		}

		String newPassword = authService.encodePasswordOrRandomCode(request
				.getNewPassword());

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
					FoConstants.FEC_ILLEGAL_STATUS, "Your email is already verified.");
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
			return FoResponse.userErrResponse(FoConstants.FEC_INVALID_INPUT, "Invalid request");
		}

		EmailVerificationDigest digest = emailVerificationDigestRepo.getByDigestStr(digestStr);
		if (digest == null) {
			return FoResponse.userErrResponse(FoConstants.FEC_INVALID_INPUT, "Invalid request");
		}

		User user = userRepo.getUserById(digest.getUserId());

		if (user == null) {
			return FoResponse.userErrResponse(FoConstants.FEC_INVALID_INPUT, "Invalid request");
		}

		if (user.isEmailVerified()) {
			//verified already? Just say congratulations
			return FoResponse.success(null);
		}

		if (digest.hasExpired()) {
			return FoResponse.userErrResponse(FoConstants.FEC_INVALID_INPUT, "The link has expired.");
		}

		user.setEmailVerified(true);
		userRepo.updateUser(user);
		return FoResponse.success(null);
	}
}
