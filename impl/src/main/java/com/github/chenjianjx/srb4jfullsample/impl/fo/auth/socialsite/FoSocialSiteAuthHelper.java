package com.github.chenjianjx.srb4jfullsample.impl.fo.auth.socialsite;

import com.github.chenjianjx.srb4jfullsample.impl.util.tools.lang.MyDuplet;
import com.github.chenjianjx.srb4jfullsample.intf.fo.auth.FoAuthTokenResult;
import com.github.chenjianjx.srb4jfullsample.intf.fo.basic.FoResponse;

/**
 * To get something from social site's token or auth code
 * 
 * @author chenjianjx
 *
 */
public interface FoSocialSiteAuthHelper {

	MyDuplet<String, FoResponse<FoAuthTokenResult>> getEmailFromToken(
			String token, String clientType);

	MyDuplet<String, FoResponse<FoAuthTokenResult>> getEmailFromCode(
			String authCode, String clientType, String redirectUri);
}
