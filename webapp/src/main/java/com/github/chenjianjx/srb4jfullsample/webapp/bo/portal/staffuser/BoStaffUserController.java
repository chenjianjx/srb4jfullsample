package com.github.chenjianjx.srb4jfullsample.webapp.bo.portal.staffuser;


import com.github.chenjianjx.srb4jfullsample.intf.bo.basic.BoConstants;
import com.github.chenjianjx.srb4jfullsample.intf.bo.basic.BoResponse;
import com.github.chenjianjx.srb4jfullsample.intf.bo.user.BoChangePasswordRequest;
import com.github.chenjianjx.srb4jfullsample.intf.bo.user.BoStaffUserManager;
import com.github.chenjianjx.srb4jfullsample.intf.fo.basic.ErrorResult;
import com.github.chenjianjx.srb4jfullsample.webapp.bo.portal.support.BoModelHelper;
import com.github.chenjianjx.srb4jfullsample.webapp.bo.portal.support.BoSessionHelper;
import com.github.chenjianjx.srb4jfullsample.webapp.bo.portal.support.BoSessionStaffUser;
import com.github.chenjianjx.srb4jfullsample.webapp.bo.portal.support.BoUrlHelper;
import org.apache.commons.lang.StringUtils;
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
import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.chenjianjx.srb4jfullsample.webapp.bo.portal.support.BoResourcePaths.CHANGE_PASSWORD;
import static com.github.chenjianjx.srb4jfullsample.webapp.bo.portal.support.BoResourcePaths.DASHBOARD;
import static com.github.chenjianjx.srb4jfullsample.webapp.bo.portal.support.BoSessionHelper.getSessionStaffUserId;

@Path("/")
@Controller
@Produces(MediaType.TEXT_HTML)
public class BoStaffUserController {


    private static final String CHANGE_PASSWORD_FORM_VIEW = "/user/changePassword";

    @Resource
    BoStaffUserManager boStaffUserManager;

    @GET
    @Path(CHANGE_PASSWORD)
    public Viewable changePasswordForm(@Context HttpServletRequest servletRequest) {
        BoSessionStaffUser sessionStaffUser = BoSessionHelper.getStaffUser(servletRequest.getSession());

        Map<String, Object> model = new LinkedHashMap<>();
        model.put("username", sessionStaffUser.getUsername());

        if (sessionStaffUser.isMustChangePassword() && sessionStaffUser.getChangePasswordReason() == BoChangePasswordReason.FIRST_TIME_LOGIN) {
            model.put("changeReason", "You have to change the password for the first time login");
        }

        return new Viewable(CHANGE_PASSWORD_FORM_VIEW, model);
    }

    @POST
    @Path(CHANGE_PASSWORD)
    public Object changePassword(@FormParam("currentPassword") String currentPassword,
                                 @FormParam("newPassword") String newPassword,
                                 @FormParam("newPasswordRepeat") String newPasswordRepeat,
                                 @Context HttpServletRequest servletRequest) throws MalformedURLException, URISyntaxException {

        BoSessionStaffUser sessionStaffUser = BoSessionHelper.getStaffUser(servletRequest.getSession());

        Map<String, Object> model = new LinkedHashMap<>();
        model.put("currentPassword", currentPassword);
        model.put("newPassword", newPassword);
        model.put("newPasswordRepeat", newPasswordRepeat);

        if (!StringUtils.equals(newPassword, newPasswordRepeat)) {
            ErrorResult err = BoResponse.userErrResponse(BoConstants.FEC_INVALID_INPUT, "The new passwords don't match").getErr();
            BoModelHelper.addError(model, err);
            return new Viewable(CHANGE_PASSWORD_FORM_VIEW, model);
        }

        BoChangePasswordRequest request = new BoChangePasswordRequest();
        request.setCurrentPassword(currentPassword);
        request.setNewPassword(newPassword);

        BoResponse<Void> response = boStaffUserManager.changePassword(getSessionStaffUserId(servletRequest.getSession()), request);

        if (response.isSuccessful()) {
            //reset the session staff user
            BoSessionStaffUser newSessionStaffUser = new BoSessionStaffUser();
            newSessionStaffUser.setUserId(sessionStaffUser.getUserId());
            newSessionStaffUser.setUsername(sessionStaffUser.getUsername());
            BoSessionHelper.setStaffUser(servletRequest.getSession(true), newSessionStaffUser);

            return Response.seeOther(BoUrlHelper.path2URI(DASHBOARD)).build();
        } else {
            BoModelHelper.addError(model, response.getErr());
            return new Viewable(CHANGE_PASSWORD_FORM_VIEW, model);
        }

    }

}
