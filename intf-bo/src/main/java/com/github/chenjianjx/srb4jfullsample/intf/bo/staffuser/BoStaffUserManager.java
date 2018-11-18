package com.github.chenjianjx.srb4jfullsample.intf.bo.staffuser;

import com.github.chenjianjx.srb4jfullsample.intf.bo.basic.BoResponse;

/**
 * @author chenjianjx@gmail.com
 */
public interface BoStaffUserManager {

    public BoResponse<Void> changePassword(Long currentStaffUserId, BoChangePasswordRequest request);

}
