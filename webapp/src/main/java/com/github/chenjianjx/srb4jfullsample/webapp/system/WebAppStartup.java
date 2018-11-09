package com.github.chenjianjx.srb4jfullsample.webapp.system;


import com.github.chenjianjx.srb4jfullsample.webapp.bo.portal.BoAllInOneServlet;
import com.github.chenjianjx.srb4jfullsample.webapp.fo.rest.support.FoSwaggerJaxrsConfig;
import com.github.chenjianjx.srb4jfullsample.webapp.root.FoRestDocServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.ContextLoaderListener;

import java.io.IOException;
import java.util.EventListener;

/**
 * The webapp starter, based on an embedded jetty
 * Created by chenjianjx@gmail.com on 7/11/18.
 */
public class WebAppStartup {

    private static final Logger logger = LoggerFactory.getLogger(WebAppStartup.class);

    public static void main(String[] args) throws Exception {


        Server server = new Server(8080);
        server.setHandler(createHandler());
        server.start();
        logger.info("Server started up");
        server.join();
    }

    private static Handler createHandler() throws IOException {
        WebAppContext contextHandler = new WebAppContext(new ClassPathResource("webroot").getURI().toString(), "/");

        //add the spring listener
        contextHandler.addEventListener(createSpringContextListener(contextHandler));

        //add load-on-startup servlets
        ServletHolder foRestServlet = createFoRestServlet();
        foRestServlet.setInitOrder(1);
        contextHandler.addServlet(foRestServlet, "/fo/rest/*");

        ServletHolder foRestSwaggerInitServlet = createFoRestSwaggerInitServlet();
        foRestSwaggerInitServlet.setInitOrder(2);
        contextHandler.addServlet(foRestSwaggerInitServlet, null);

        //add other servlets
        contextHandler.addServlet(FoRestDocServlet.class, "/fo-rest-doc");
        contextHandler.addServlet(BoAllInOneServlet.class, "/bo/portal/*");

        return contextHandler;
    }

    private static EventListener createSpringContextListener(ServletContextHandler contextHandler) {
        contextHandler.setInitParameter("contextConfigLocation", "classpath:spring/applicationContext.xml");
        ContextLoaderListener listener = new ContextLoaderListener();
        return listener;
    }

    private static ServletHolder createFoRestSwaggerInitServlet() {
        ServletHolder holder = new ServletHolder();
        holder.setServlet(new FoSwaggerJaxrsConfig());
        holder.setInitOrder(2);
        return holder;
    }

    private static ServletHolder createFoRestServlet() {
        ServletHolder holder = new ServletHolder();
        holder.setServlet(new org.glassfish.jersey.servlet.ServletContainer());
        holder.setInitParameter("jersey.config.server.provider.packages",
                "io.swagger.jaxrs.listing, com.github.chenjianjx.srb4jfullsample.webapp.fo.rest");

        return holder;
    }


}
