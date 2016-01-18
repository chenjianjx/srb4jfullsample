package com.github.chenjianjx.srb4jfullsample.impl.bo.auth;

import static com.github.chenjianjx.srb4jfullsample.intf.fo.basic.FoConstants.NULL_REQUEST_BEAN_TIP;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import com.github.chenjianjx.srb4jfullsample.impl.biz.auth.AuthService;
import com.github.chenjianjx.srb4jfullsample.impl.biz.user.User;
import com.github.chenjianjx.srb4jfullsample.impl.biz.user.UserRepo;
import com.github.chenjianjx.srb4jfullsample.impl.util.infrahelp.beanvalidae.MyValidator;
import com.github.chenjianjx.srb4jfullsample.intf.bo.auth.BoAuthManager;
import com.github.chenjianjx.srb4jfullsample.intf.bo.auth.BoLocalLoginRequest;
import com.github.chenjianjx.srb4jfullsample.intf.bo.auth.BoLoginResult;
import com.github.chenjianjx.srb4jfullsample.intf.fo.basic.FoConstants;
import com.github.chenjianjx.srb4jfullsample.intf.fo.basic.FoResponse;

/**
 * 
 * @author chenjianjx@gmail.com
 *
 */
@Service("boAuthManager")
public class BoAuthManagerImpl implements BoAuthManager {

	@Resource
	MyValidator myValidator;

	@Resource
	UserRepo userRepo;

	@Resource
	AuthService authService;

	@Override
	public FoResponse<BoLoginResult> localLogin(BoLocalLoginRequest request) {

		String error = myValidator.validateBeanFastFail(request,
				NULL_REQUEST_BEAN_TIP);
		if (error != null) {
			return FoResponse.userErrResponse(FoConstants.FEC_INVALID_INPUT,
					error);
		}

		String principal = User.decidePrincipalFromLocal(request.getEmail());
		User user = userRepo.getUserByPrincipal(principal);

		if (user == null) {
			return FoResponse.userErrResponse(FoConstants.FEC_INVALID_INPUT,
					"Invalid Email");
		}

		// check if bo user
		if (!isBoUser_ThisMethodShouldBeChanged(user)) {
			return FoResponse.userErrResponse(FoConstants.FEC_NO_PERMISSION,
					"You are not a back office user");
		}

		// compare password
		String encodedPassword = authService.encodePasswordOrRandomCode(request
				.getPassword());

		if (!encodedPassword.equals(user.getPassword())) {
			return FoResponse.userErrResponse(FoConstants.FEC_INVALID_INPUT,
					"Invalid Password");
		}

		BoLoginResult result = new BoLoginResult();
		result.setUserId(user.getId());
		return FoResponse.success(result);
	}

	@Deprecated
	private boolean isBoUser_ThisMethodShouldBeChanged(User user) {
		return true;
	}

}
