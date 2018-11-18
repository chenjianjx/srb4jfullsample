package com.github.chenjianjx.srb4jfullsample.intf.fo.user;

import com.github.chenjianjx.srb4jfullsample.intf.fo.auth.FoChangePasswordRequest;
import com.github.chenjianjx.srb4jfullsample.intf.fo.basic.FoResponse;

/**
 * 
 * @author chenjianjx@gmail.com
 *
 */
public interface FoUserManager {
	/**
	 * for local user to change password
	 * 
	 * @param currentUserId
	 * @param request
	 * @return
	 */
	FoResponse<Void> changePassword(Long currentUserId, FoChangePasswordRequest request);

	/**
	 * start email verification.  An email containing the email verification link will be sent
     */
	FoResponse<Void> startEmailVerification(Long currentUserId, String verificationUrlBase, String digestParamName);

	/**
	 * verify the email verification link
	 * @param digest the key information in the verification link
     */
	FoResponse<Void> verifyEmail(String digest);


}
