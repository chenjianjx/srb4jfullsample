package com.github.chenjianjx.srb4jfullsample.impl.fo.auth;

import com.github.chenjianjx.srb4jfullsample.impl.biz.auth.AccessToken;
import com.github.chenjianjx.srb4jfullsample.impl.biz.auth.AccessTokenRepo;
import com.github.chenjianjx.srb4jfullsample.impl.biz.auth.AuthService;
import com.github.chenjianjx.srb4jfullsample.impl.biz.auth.RandomLoginCode;
import com.github.chenjianjx.srb4jfullsample.impl.biz.auth.RandomLoginCodeRepo;
import com.github.chenjianjx.srb4jfullsample.impl.biz.client.Client;
import com.github.chenjianjx.srb4jfullsample.impl.biz.user.User;
import com.github.chenjianjx.srb4jfullsample.impl.biz.user.UserRepo;
import com.github.chenjianjx.srb4jfullsample.impl.fo.auth.socialsite.FoSocialSiteAuthHelper;
import com.github.chenjianjx.srb4jfullsample.impl.fo.common.FoManagerImplBase;
import com.github.chenjianjx.srb4jfullsample.impl.util.infrahelp.beanvalidae.MyValidator;
import com.github.chenjianjx.srb4jfullsample.impl.util.tools.lang.MyDuplet;
import com.github.chenjianjx.srb4jfullsample.intf.fo.auth.FoAuthManager;
import com.github.chenjianjx.srb4jfullsample.intf.fo.auth.FoAuthTokenResult;
import com.github.chenjianjx.srb4jfullsample.intf.fo.auth.FoGenRandomLoginCodeRequest;
import com.github.chenjianjx.srb4jfullsample.intf.fo.auth.FoLocalLoginRequest;
import com.github.chenjianjx.srb4jfullsample.intf.fo.auth.FoRandomCodeLoginRequest;
import com.github.chenjianjx.srb4jfullsample.intf.fo.auth.FoRefreshTokenRequest;
import com.github.chenjianjx.srb4jfullsample.intf.fo.auth.FoRegisterRequest;
import com.github.chenjianjx.srb4jfullsample.intf.fo.auth.FoSocialAuthCodeLoginRequest;
import com.github.chenjianjx.srb4jfullsample.intf.fo.auth.FoSocialLoginByTokenRequest;
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
@Service("foAuthManager")
public class FoAuthManagerImpl extends FoManagerImplBase implements
		FoAuthManager {

	private static final Logger logger = LoggerFactory
			.getLogger(FoAuthManagerImpl.class);

	@Resource
	AccessTokenRepo accessTokenRepo;

	@Resource
	UserRepo userRepo;

	@Resource
	RandomLoginCodeRepo randomCodeRepo;

	@Resource
	MyValidator myValidator;

	@Resource
	AuthService authService;

	@Resource
	FoSocialSiteAuthHelper.Factory socialSiteAuthHelperFactory;

	@Override
	public FoResponse<FoAuthTokenResult> localOauth2Login(
			FoLocalLoginRequest request) {
		String error = myValidator.validateBeanFastFail(request,
				NULL_REQUEST_BEAN_TIP);
		if (error != null) {
			return FoResponse.userErrResponse(
					FoConstants.FEC_OAUTH2_INVALID_REQUEST, error);
		}

		String principal = User.decidePrincipalFromLocal(request.getEmail());
		User user = userRepo.getUserByPrincipal(principal);

		if (user == null) {
			return FoResponse.userErrResponse(
					FoConstants.FEC_OAUTH2_INVALID_REQUEST, "invalid email");
		}

		// now compare password
		String encodedPassword = authService.encodePasswordOrRandomCode(request
				.getPassword());

		if (!encodedPassword.equals(user.getPassword())) {
			return FoResponse.userErrResponse(
					FoConstants.FEC_OAUTH2_INVALID_REQUEST, "invalid password");
		}

		// ok, do the token
		return buildAuthTokenResponse(user, request.isLongSession());
	}

	@Override
	public FoResponse<FoAuthTokenResult> localRandomCodeLogin(
			FoRandomCodeLoginRequest request) {

		String error = myValidator.validateBeanFastFail(request,
				NULL_REQUEST_BEAN_TIP);
		if (error != null) {
			return FoResponse.userErrResponse(
					FoConstants.FEC_OAUTH2_INVALID_REQUEST, error);
		}

		String principal = User.decidePrincipalFromLocal(request.getEmail());
		User user = userRepo.getUserByPrincipal(principal);

		if (user == null) {
			return FoResponse.userErrResponse(
					FoConstants.FEC_OAUTH2_INVALID_REQUEST, "Invalid email.");
		}

		// now compare the codes
		String encodedCode = authService.encodePasswordOrRandomCode(request
				.getRandomCode());
		RandomLoginCode rlc = randomCodeRepo.getByUserId(user.getId());
		if (rlc == null || !encodedCode.equals(rlc.getCodeStr())) {
			return FoResponse.userErrResponse(
					FoConstants.FEC_OAUTH2_INVALID_REQUEST,
					"Invalid random code.");
		}

		if (rlc.hasExpired()) {
			return FoResponse.userErrResponse(
					FoConstants.FEC_OAUTH2_INVALID_REQUEST,
					"Random code expired.");
		}

		// ok, do the token
		FoResponse<FoAuthTokenResult> response = buildAuthTokenResponse(user,
				false);

		// finally delete the code
		randomCodeRepo.deleteByUserId(user.getId());
		return response;
	}

	private FoResponse<FoAuthTokenResult> buildAuthTokenResponse(User user,
			boolean longSession) {
		AccessToken at = authService
				.genNewAccessTokenForUser(user, longSession);
		return buildAuthTokenResponse(at, user);

	}

	private FoResponse<FoAuthTokenResult> buildAuthTokenResponse(
			AccessToken accessToken, User user) {
		FoAuthTokenResult result = new FoAuthTokenResult();
		result.setAccessToken(accessToken.getTokenStr());
		result.setRefreshToken(accessToken.getRefreshTokenStr());
		result.setExpiresIn(accessToken.getLifespan());
		result.defaultTokenType();
		result.setUserPrincipal(user.getPrincipal());
		return FoResponse.success(result);
	}

	@Override
	public FoResponse<FoAuthTokenResult> localRegister(FoRegisterRequest request) {

		String error = myValidator.validateBeanFastFail(request,
				NULL_REQUEST_BEAN_TIP);
		if (error != null) {
			return FoResponse.userErrResponse(
					FoConstants.FEC_OAUTH2_INVALID_REQUEST, error);
		}

		String email = request.getEmail();
		if (!myValidator.isEmailValid(email)) {
			return FoResponse.userErrResponse(
					FoConstants.FEC_OAUTH2_INVALID_REQUEST, "Invalid Email");
		}

		// validate user existence
		User existingUser = userRepo.getUserByEmail(email);

		if (existingUser != null) {
			String err = "This email already exists";
			return FoResponse.userErrResponse(
					FoConstants.FEC_OAUTH2_INVALID_REQUEST, err);
		}

		// save it
		String source = User.SOURCE_LOCAL;
		String encodedPassword = authService.encodePasswordOrRandomCode(request
				.getPassword());
		User user = new User();
		user.setEmail(email);
		user.setEmailVerified(false);
		user.setPassword(encodedPassword);
		user.setSource(source);
		user.setCreatedBy(user.getPrincipal());
		userRepo.saveNewUser(user);

		return buildAuthTokenResponse(user, false);
	}

	@Override
	public FoResponse<Void> oauth2Logout(Long currentUserId, String tokenStr) {
		if (currentUserId == null) {
			// not login yet? say nothing harsh
			return FoResponse.success(null);
		}
		tokenStr = StringUtils.trimToNull(tokenStr);
		if (tokenStr == null) {
			// say nothing harsh
			return FoResponse.success(null);
		}

		AccessToken at = accessTokenRepo.getByTokenStr(tokenStr);
		if (at == null) {
			// invalid token? say nothing harsh
			return FoResponse.success(null);
		}

		if (at.getUserId() != currentUserId) {
			// not his or her token? say nothing harsh
			return FoResponse.success(null);
		}
		accessTokenRepo.deleteByTokenStr(tokenStr);
		return FoResponse.success(null);
	}

	@Override
	public FoResponse<Void> generateRandomLoginCode(
			FoGenRandomLoginCodeRequest request) {
		String error = myValidator.validateBeanFastFail(request,
				NULL_REQUEST_BEAN_TIP);
		if (error != null) {
			return FoResponse.userErrResponse(
					FoConstants.FEC_OAUTH2_INVALID_REQUEST, error);
		}

		String principalName = User
				.decidePrincipalFromLocal(request.getEmail());
		User user = userRepo.getUserByPrincipal(principalName);

		if (user == null) {
			return FoResponse.userErrResponse(
					FoConstants.FEC_OAUTH2_INVALID_REQUEST, "Invalid email.");
		}

		String randomCodeStr = authService.generateRandomLoginCode();
		RandomLoginCode randomCodeObj = authService.saveNewRandomCodeForUser(
				user, randomCodeStr);

		// send the email
		try {
			authService.sendEmailForRandomLoginCodeAsync(user, randomCodeObj,
					randomCodeStr);
		} catch (Exception e) {
			logger.error("fail to send random login codel asyncly  for user "
					+ user.getPrincipal(), e);
		}

		return FoResponse.success(null);
	}

	@Override
	public FoResponse<FoAuthTokenResult> socialLoginByToken(
			FoSocialLoginByTokenRequest request) {

		String error = myValidator.validateBeanFastFail(request,
				NULL_REQUEST_BEAN_TIP);
		if (error != null) {
			return FoResponse.devErrResponse(
					FoConstants.FEC_OAUTH2_INVALID_REQUEST, error, null);
		}
		String source = request.getSource();
		if (!User.isValidSocialAccountSource(source)) {
			return FoResponse.devErrResponse(
					FoConstants.FEC_OAUTH2_INVALID_REQUEST,
					"unsupported source: " + source, null);
		}

		boolean longSession = request.isLongSession();

		String socialToken = request.getToken();
		FoSocialSiteAuthHelper helper = this.socialSiteAuthHelperFactory
				.getHelper(source);
		MyDuplet<String, FoResponse<FoAuthTokenResult>> emailOrErrResp = helper
				.getEmailFromToken(socialToken, request.getClientType());
		return handleSocialSiteEmailResult(source, longSession, emailOrErrResp);
	}

	@Override
	public FoResponse<FoAuthTokenResult> socialLoginByAuthCode(
			FoSocialAuthCodeLoginRequest request) {

		String error = myValidator.validateBeanFastFail(request,
				NULL_REQUEST_BEAN_TIP);
		if (error != null) {
			return FoResponse.devErrResponse(
					FoConstants.FEC_OAUTH2_INVALID_REQUEST, error, null);
		}
		String source = request.getSource();
		if (!User.isValidSocialAccountSource(source)) {
			return FoResponse.devErrResponse(
					FoConstants.FEC_OAUTH2_INVALID_REQUEST,
					"unsupported source: " + source, null);
		}

		String clientType = request.getClientType();
		if (!Client.isValidClientType(clientType)) {
			return FoResponse.devErrResponse(
					FoConstants.FEC_OAUTH2_INVALID_REQUEST,
					"unsupported client type: " + clientType, null);
		}

		String redirectUri = request.getRedirectUri();

		boolean longSession = request.isLongSession();

		String authCode = request.getAuthCode();
		FoSocialSiteAuthHelper helper = this.socialSiteAuthHelperFactory
				.getHelper(source);
		MyDuplet<String, FoResponse<FoAuthTokenResult>> emailOrErrResp = helper
				.getEmailFromCode(authCode, clientType, redirectUri);
		return handleSocialSiteEmailResult(source, longSession, emailOrErrResp);
	}

	private FoResponse<FoAuthTokenResult> handleSocialSiteEmailResult(
			String source, boolean longSession,
			MyDuplet<String, FoResponse<FoAuthTokenResult>> emailOrErrResp) {
		if (emailOrErrResp.right != null) {
			return emailOrErrResp.right;
		}

		String email = emailOrErrResp.left;
		String principal = User.decidePrincipal(source, email);

		User existingUserWithTheSameEmail = userRepo.getUserByEmail(email);

		User user;
		if (existingUserWithTheSameEmail == null) {
			//a new user should be created
			User newUser = new User();
			newUser.setEmail(email);
			newUser.setEmailVerified(true);  //social-account user's email is always considered to be verified
			newUser.setPassword(null);
			newUser.setSource(source);
			newUser.setCreatedBy(principal);
			userRepo.saveNewUser(newUser);

			user = newUser;
		} else { //existingUserWithTheSameEmail != null
			if (existingUserWithTheSameEmail.getPrincipal().equals(principal)) {
				//The user account has been created.
				user = existingUserWithTheSameEmail;
			} else {
				//A user who has the same email, but not the same source, exists
				String err = "A user with the same email (" + email + ") already exists.";
				return FoResponse.userErrResponse(
						FoConstants.FEC_OAUTH2_INVALID_REQUEST, err);
			}
		}
		// ok, do the token
		return buildAuthTokenResponse(user, longSession);
	}

	@Override
	public FoResponse<FoAuthTokenResult> oauth2RefreshToken(
			FoRefreshTokenRequest request) {

		String error = myValidator.validateBeanFastFail(request,
				NULL_REQUEST_BEAN_TIP);
		if (error != null) {
			return FoResponse.devErrResponse(
					FoConstants.FEC_OAUTH2_INVALID_REQUEST, error, null);
		}

		String refreshToken = request.getRefreshToken();

		AccessToken at = accessTokenRepo.getByRefreshTokenStr(refreshToken);

		if (at == null) {
			return FoResponse.devErrResponse(
					FoConstants.FEC_OAUTH2_INVALID_REQUEST,
					"invalid refresh token", null);
		}

		User user = userRepo.getUserById(at.getUserId());

		at.setTokenStr(authService.generateAccessTokenStr());
		at.setRefreshTokenStr(authService.generateRefreshTokenStr());
		at.setExpiresAt(authService.calExpiresAt(at.getLifespan()));
		accessTokenRepo.updateAccessToken(at);

		// ok, do the token
		return buildAuthTokenResponse(at, user);

	}

}
