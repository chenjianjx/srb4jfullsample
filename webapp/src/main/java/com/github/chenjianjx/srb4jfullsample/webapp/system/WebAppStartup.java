package com.github.chenjianjx.srb4jfullsample.webapp.system;


import com.github.chenjianjx.srb4jfullsample.datamigration.MigrationRunner;
import com.github.chenjianjx.srb4jfullsample.webapp.bo.portal.BoAllInOneServlet;
import com.github.chenjianjx.srb4jfullsample.webapp.fo.rest.support.FoSwaggerJaxrsConfig;
import com.github.chenjianjx.srb4jfullsample.webapp.infrahelper.rest.spring.ExitOnInitializationErrorContextLoaderListener;
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
import java.net.URL;
import java.util.EventListener;
import java.util.Properties;

/**
 * The webapp starter, based on an embedded jetty
 * Created by chenjianjx@gmail.com on 7/11/18.
 */
public class WebAppStartup {

    private static final Logger logger = LoggerFactory.getLogger(WebAppStartup.class);
    private static StartupConfig startupConfig;
    private static AppPropertiesFactory appPropertiesFactory = new AppPropertiesFactory();

    public static void main(String[] args) throws Exception {


        loadStartupConfig();

        //run migration first
        if (startupConfig.dataMigrationOnStartup) {
            new MigrationRunner().run(startupConfig.jdbcUrl, startupConfig.dbUsername, startupConfig.dbPassword);
        } else {
            logger.warn("No data migration will be run during system startup");
        }


        startServer(startupConfig);
    }


    private static void startServer(StartupConfig startupConfig) throws Exception {
        Server server = new Server(startupConfig.port);
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
        contextHandler.addServlet(HealthCheckServlet.class, "/health");
        contextHandler.addServlet(FoRestDocServlet.class, "/fo-rest-doc");
        contextHandler.addServlet(BoAllInOneServlet.class, "/bo/portal/*");

        return contextHandler;
    }

    private static EventListener createSpringContextListener(ServletContextHandler contextHandler) {
        contextHandler.setInitParameter("contextConfigLocation", "classpath:spring/applicationContext.xml");
        ContextLoaderListener listener = new ExitOnInitializationErrorContextLoaderListener();
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

    private static StartupConfig loadStartupConfig() throws Exception {

        Properties properties = appPropertiesFactory.getProperties();

        startupConfig = new StartupConfig();

        String dbHost = (String) properties.get("dbHost");
        int dbPort = Integer.valueOf((String) properties.get("dbPort"));
        String dbSchema = (String) properties.get("dbSchema");
        startupConfig.jdbcUrl = String.format("jdbc:mysql://%s:%s/%s", dbHost, dbPort, dbSchema);

        startupConfig.dbUsername = (String) properties.get("dbUsername");
        startupConfig.dbPassword = (String) properties.get("dbPassword");

        String schemeAndHost = (String) properties.get("schemeAndHost");
        URL url = new URL(schemeAndHost);
        startupConfig.port = url.getPort();
        startupConfig.dataMigrationOnStartup = Boolean.valueOf((String) properties.get("dataMigrationOnStartup"));

        return startupConfig;
    }

    private static final class StartupConfig {


        public String jdbcUrl;
        public String dbUsername;
        public String dbPassword;

        public int port;

        public boolean dataMigrationOnStartup;

    }
}
