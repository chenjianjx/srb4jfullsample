package com.github.chenjianjx.srb4jfullsample.webapp.bo.portal.auth;


import com.github.chenjianjx.srb4jfullsample.intf.bo.auth.BoAuthManager;
import com.github.chenjianjx.srb4jfullsample.intf.bo.auth.BoLocalLoginRequest;
import com.github.chenjianjx.srb4jfullsample.intf.bo.auth.BoLoginResult;
import com.github.chenjianjx.srb4jfullsample.intf.fo.basic.FoResponse;
import org.glassfish.jersey.server.mvc.Viewable;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static com.github.chenjianjx.srb4jfullsample.webapp.bo.portal.support.BoConstants.MVC_KEY_ERR;
import static com.github.chenjianjx.srb4jfullsample.webapp.bo.portal.support.BoConstants.SESSION_KEY_BO_USERNAME;
import static com.github.chenjianjx.srb4jfullsample.webapp.bo.portal.support.BoResourcePaths.DASHBOARD;
import static com.github.chenjianjx.srb4jfullsample.webapp.bo.portal.support.BoResourcePaths.LOGIN;
import static com.github.chenjianjx.srb4jfullsample.webapp.bo.portal.support.BoResourcePaths.LOGOUT;
import static com.github.chenjianjx.srb4jfullsample.webapp.bo.portal.support.BoUrlHelper.path2URI;

@Path("/")
@Controller
@Produces(MediaType.TEXT_HTML)
public class BoAuthController {

    public static final String LOGIN_FORM_VIEW = "/auth/loginForm";
    @Resource
    BoAuthManager boAuthManager;

    @GET
    @Path(LOGIN)
    public Viewable loginForm() {
        return new Viewable(LOGIN_FORM_VIEW, null);
    }

    @POST
    @Path(LOGIN)
    public Object login(@FormParam("email") String email, @FormParam("password") String password,
                        @Context HttpServletRequest servletRequest) throws MalformedURLException, URISyntaxException {
        BoLocalLoginRequest loginRequest = new BoLocalLoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);
        FoResponse<BoLoginResult> loginResponse = boAuthManager.localLogin(loginRequest);
        if (loginResponse.isSuccessful()) {
            servletRequest.getSession(true).setAttribute(SESSION_KEY_BO_USERNAME, "backham");
            return Response.seeOther(path2URI(DASHBOARD)).build();
        } else {
            Map<String, Object> model = new HashMap<>();
            model.put(MVC_KEY_ERR, loginResponse.getErr());
            model.put("email", email);  //let the form render the value the user input
            model.put("password", password);
            return new Viewable(LOGIN_FORM_VIEW, model);
        }
    }


    @GET
    @Path(LOGOUT)
    public Response logout(@Context HttpServletRequest servletRequest) {
        servletRequest.getSession().invalidate();
        return Response.seeOther(path2URI(DASHBOARD)).build();
    }
}
