package com.github.chenjianjx.srb4jfullsample.webapp.bo.portal.home;

import com.github.chenjianjx.srb4jfullsample.webapp.bo.portal.support.BoResourcePaths;
import org.glassfish.jersey.server.mvc.Viewable;
import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;


@Controller
@Path("/")
@Produces(MediaType.TEXT_HTML)
public class BoHomeController {

    @GET
    @Path(BoResourcePaths.DASHBOARD)
    public Viewable dashboard() {
        Map<String, String> model = new HashMap<>();
        return new Viewable("/home/dashboard", model);
    }
}