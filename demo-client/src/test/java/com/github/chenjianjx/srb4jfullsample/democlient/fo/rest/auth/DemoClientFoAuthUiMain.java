package com.github.chenjianjx.srb4jfullsample.democlient.fo.rest.auth;

import static com.github.chenjianjx.srb4jfullsample.democlient.util.DemoClientConstants.BACKEND_FO_REST_URL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Assert;

import com.github.chenjianjx.srb4jfullsample.democlient.util.DemoClientConstants;
import com.github.chenjianjx.srb4jfullsample.restclient.model.GenRandomLoginCodeRequest;

/**
 * This program requires user interaction, but "mvn test" doesn't take user
 * input well. So I have to write a main program instead of a junit test case
 * 
 * @author chenjianjx@gmail.com
 *
 */
public class DemoClientFoAuthUiMain {
	private static Client restClient = ClientBuilder.newClient();

	public static void main(String[] args) throws Exception {

		randomCodeLoginFlow();

	}

	private static void randomCodeLoginFlow() throws IOException {
		// register user account first

		BufferedReader consoleInput = new BufferedReader(new InputStreamReader(
				System.in));
		System.out
				.print("Have you got an email address that has been registered to the system and can receive emails?  \n (yes or no)>>");
		String hasEmail = consoleInput.readLine();
		System.out.println();

		String email = null;
		if (!"yes".equals(hasEmail)) {
			System.out.print("Please input such an email to register: \n >> ");
			email = consoleInput.readLine();
			String pwd = "abc123";
			DemoClientFoAuthTest.doRegister(email, pwd);
			System.out.println("Registration done. Your password is " + pwd);
		} else {
			System.out.print("Please input that email: \n >> ");
			email = consoleInput.readLine();
		}

		// do gen
		GenRandomLoginCodeRequest genCodeRequest = new GenRandomLoginCodeRequest();
		genCodeRequest.setEmail(email);

		Response genCodeResponse = restClient
				.target(BACKEND_FO_REST_URL)
				.path(DemoClientConstants.GEN_RANDOM_CODE_URL)
				.request()
				.post(Entity.entity(genCodeRequest, MediaType.APPLICATION_JSON));

		Assert.assertEquals(200, genCodeResponse.getStatus());

		System.out
				.print("Please input the random code you got from your email. \n >>");
		String randomCode = consoleInput.readLine();

		WebTarget target = restClient.target(BACKEND_FO_REST_URL).path(
				DemoClientConstants.RANDOM_CODE_LOGIN_URL);
		Form form = new Form();
		form.param("grant_type", "password");
		form.param("username", email);
		form.param("password", randomCode);
		form.param("long_session", "false");

		Response randomCodeLoginRestResponse = target
				.request(MediaType.APPLICATION_JSON_TYPE)
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.form(form));

		DemoClientFoAuthTokenResponse clientResponse = DemoClientFoAuthTokenResponse
				.parseRestResponse(randomCodeLoginRestResponse);

		DemoClientFoAuthTest.assertAuthResponseSuccessful(clientResponse);

	}

}
