package com.github.chenjianjx.srb4jfullsample.impl.fo.auth.socialsite;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NoHttpResponseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.chenjianjx.srb4jfullsample.impl.biz.client.Client;
import com.github.chenjianjx.srb4jfullsample.impl.util.tools.lang.MyDuplet;
import com.github.chenjianjx.srb4jfullsample.intf.fo.auth.FoAuthTokenResult;
import com.github.chenjianjx.srb4jfullsample.intf.fo.basic.FoConstants;
import com.github.chenjianjx.srb4jfullsample.intf.fo.basic.FoResponse;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.apis.google.GoogleToken;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

/**
 * 
 * @author chenjianjx@gmail.com
 *
 */
@Service
public class FoGoogleAuthHelper implements FoSocialSiteAuthHelper {

	/**
	 * for desktop clients (non-web, non-mobile)
	 */
	private String googleClientId;
	private String googleClientSecret;

	/**
	 * for web clients which uses google's javascript SDK
	 */
	private String googleWebClientId;
	private String googleWebClientSecret;

	HttpTransport googleHttpTransport = new ApacheHttpTransport();
	JsonFactory googleJsonFactory = new JacksonFactory();

	@Override
	public MyDuplet<String, FoResponse<FoAuthTokenResult>> getEmailFromToken(
			String token, String clientType) {
		String idToken = token;
		GoogleIdToken git = verifyGoogleIdToken(idToken, clientType);
		if (git == null) {
			FoResponse<FoAuthTokenResult> errResp = FoResponse.devErrResponse(
					FoConstants.FEC_OAUTH2_INVALID_REQUEST,
					"invalid google open id token", null);
			return MyDuplet.newInstance(null, errResp);
		}
		String email = git.getPayload().getEmail();
		if (email == null) {
			FoResponse<FoAuthTokenResult> errResp = FoResponse
					.devErrResponse(
							FoConstants.FEC_OAUTH2_INVALID_REQUEST,
							"cannot extract email from this open id token. please check the scope used for google sign-in",
							null);
			return MyDuplet.newInstance(null, errResp);
		}
		return MyDuplet.newInstance(email, null);

	}

	private GoogleIdToken verifyGoogleIdToken(String idToken, String clientType) {
		GoogleIdToken git = verifyGoogleIdToken(idToken, clientType,
				"https://accounts.google.com");
		if (git == null) {
			// stupid compatibility google bug. You have to try both issuers
			git = verifyGoogleIdToken(idToken, clientType,
					"accounts.google.com");
		}
		return git;
	}

	private GoogleIdToken verifyGoogleIdToken(String idToken,
			String clientType, String issuer) {
		GoogleIdTokenVerifier.Builder vb = new GoogleIdTokenVerifier.Builder(
				googleHttpTransport, googleJsonFactory).setIssuer(issuer);
		

		if (clientType.equals(Client.TYPE_DESKTOP)) {
			vb.setAudience(Arrays.asList(this.googleClientId));
		}

		if (clientType.equals(Client.TYPE_WEB)
				|| clientType.equals(Client.TYPE_MOBILE)) {
			// according to
			// https://developers.google.com/identity/sign-in/android/backend-auth#send-the-id-token-to-your-server,
			// the web client Id will be used even for mobile login
			vb.setAudience(Arrays.asList(this.googleWebClientId));
		}

		GoogleIdTokenVerifier verifier = vb.build();		
		try {
			return verifier.verify(idToken);
		} catch (NoHttpResponseException e) {
			throw new RuntimeException(e); //TODO: retry the verification or prompt user to retry   
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public MyDuplet<String, FoResponse<FoAuthTokenResult>> getEmailFromCode(
			String authCode, String clientType, String redirectUri) {

		String clientId = null;
		String clientSecret = null;

		if (Client.TYPE_DESKTOP.equals(clientType)) {
			clientId = googleClientId;
			clientSecret = googleClientSecret;
			if (redirectUri == null) {
				redirectUri = FoConstants.GOOGLE_REDIRECT_URI_OOB;
			}
		} else if (Client.TYPE_WEB.equals(clientType)) {
			clientId = googleWebClientId;
			clientSecret = googleWebClientSecret;
			if (redirectUri == null) {
				redirectUri = FoConstants.GOOGLE_REDIRECT_URI_POSTMESSAGE;
			}
		} else {
			throw new IllegalArgumentException(
					"Currently we don't support google login with client type = "
							+ clientType);
		}

		// exchange the code for token
		final OAuth20Service service = new ServiceBuilder().apiKey(clientId)
				.apiSecret(clientSecret).scope("email").callback(redirectUri)
				.build(GoogleApi20.instance());
		GoogleToken googleTokenObj = (GoogleToken) service
				.getAccessToken(new Verifier(authCode));
		String idToken = googleTokenObj.getOpenIdToken();
		// get email by token
		return this.getEmailFromToken(idToken, clientType);

	}

	@Value("${googleWebClientId}")
	public void setGoogleWebClientId(String googleWebClientId) {
		this.googleWebClientId = StringUtils.trimToNull(googleWebClientId);
	}

	@Value("${googleWebClientSecret}")
	public void setGoogleWebClientSecret(String googleWebClientSecret) {
		this.googleWebClientSecret = StringUtils
				.trimToNull(googleWebClientSecret);
	}

	@Value("${googleClientId}")
	public void setGoogleClientId(String googleClientId) {
		this.googleClientId = StringUtils.trimToNull(googleClientId);
	}

	@Value("${googleClientSecret}")
	public void setGoogleClientSecret(String googleClientSecret) {
		this.googleClientSecret = StringUtils.trimToNull(googleClientSecret);
	}

}
