package com.github.chenjianjx.srb4jfullsample.impl.bo.staffuser;

import com.github.chenjianjx.srb4jfullsample.impl.biz.staff.StaffAuthService;
import com.github.chenjianjx.srb4jfullsample.impl.biz.staff.StaffUser;
import com.github.chenjianjx.srb4jfullsample.impl.biz.staff.StaffUserRepo;
import com.github.chenjianjx.srb4jfullsample.impl.bo.common.BoManagerImplBase;
import com.github.chenjianjx.srb4jfullsample.impl.util.infrahelp.beanvalidae.MyValidator;
import com.github.chenjianjx.srb4jfullsample.intf.bo.basic.BoConstants;
import com.github.chenjianjx.srb4jfullsample.intf.bo.basic.BoResponse;
import com.github.chenjianjx.srb4jfullsample.intf.bo.user.BoChangePasswordRequest;
import com.github.chenjianjx.srb4jfullsample.intf.bo.user.BoStaffUserManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Calendar;

/**
 * @author chenjianjx@gmail.com
 */
@Service("boAuthManager")
public class BoStaffUserManagerImpl extends BoManagerImplBase implements BoStaffUserManager {

    @Resource
    MyValidator myValidator;

    @Resource
    StaffUserRepo staffUserRepo;

    @Resource
    StaffAuthService staffAuthService;


    @Override
    public BoResponse<Void> changePassword(Long currentStaffUserId, BoChangePasswordRequest request) {
        String error = myValidator.validateBeanFastFail(request, BoConstants.NULL_REQUEST_BEAN_TIP);
        if (error != null) {
            return BoResponse.userErrResponse(BoConstants.FEC_INVALID_INPUT, error);
        }
        StaffUser currentStaffUser = getCurrentStaffUserConsideringInvalidId(currentStaffUserId);
        if (currentStaffUser == null) {
            return buildNotLoginErr();
        }

        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            return BoResponse.userErrResponse(BoConstants.FEC_INVALID_INPUT, "New password cannot be the same as the old one");
        }

        // now compare password
        String encodedCurrentPassword = staffAuthService.encodePassword(request.getCurrentPassword());

        if (!encodedCurrentPassword.equals(currentStaffUser.getPassword())) {
            return BoResponse.userErrResponse(
                    BoConstants.FEC_INVALID_INPUT,
                    "The current password you input is wrong");
        }

        String encodedNewPassword = staffAuthService.encodePassword(request.getNewPassword());
        StaffUser newStaffUser = staffUserRepo.getStaffUserById(currentStaffUserId);
        newStaffUser.setPassword(encodedNewPassword);
        newStaffUser.setUpdatedBy(newStaffUser.getUsername());
        newStaffUser.setLastLoginDate(Calendar.getInstance()); //consider logged in
        staffUserRepo.updateStaffUser(newStaffUser);

        return BoResponse.success(null);

    }


}
