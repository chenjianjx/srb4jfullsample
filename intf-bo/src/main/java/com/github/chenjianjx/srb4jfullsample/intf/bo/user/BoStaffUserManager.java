package com.github.chenjianjx.srb4jfullsample.intf.bo.user;

import com.github.chenjianjx.srb4jfullsample.intf.bo.auth.BoLoginRequest;
import com.github.chenjianjx.srb4jfullsample.intf.bo.auth.BoLoginResult;
import com.github.chenjianjx.srb4jfullsample.intf.bo.basic.BoResponse;

/**
 * @author chenjianjx@gmail.com
 */
public interface BoStaffUserManager {

    public BoResponse<Void> changePassword(Long currentStaffUserId, BoChangePasswordRequest request);

}
