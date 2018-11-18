package com.github.chenjianjx.srb4jfullsample.webapp.system;


import com.github.chenjianjx.srb4jfullsample.datamigration.MigrationRunner;
import com.github.chenjianjx.srb4jfullsample.webapp.bo.portal.support.BoPortalApplication;
import com.github.chenjianjx.srb4jfullsample.webapp.bo.portal.support.BoSessionFilter;
import com.github.chenjianjx.srb4jfullsample.webapp.fo.rest.support.FoRestApplication;
import com.github.chenjianjx.srb4jfullsample.webapp.fo.rest.support.FoSwaggerJaxrsConfig;
import com.github.chenjianjx.srb4jfullsample.webapp.infrahelper.rest.spring.ExitOnInitializationErrorContextLoaderListener;
import com.github.chenjianjx.srb4jfullsample.webapp.root.FoRestDocServlet;
import com.opensymphony.sitemesh.webapp.SiteMeshFilter;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.DispatcherType;
import java.io.IOException;
import java.net.URL;
import java.util.EnumSet;
import java.util.EventListener;
import java.util.Properties;

/**
 * The webapp starter, based on an embedded jetty
 * Created by chenjianjx@gmail.com on 7/11/18.
 */
public class WebAppStartup {

    private static final Logger logger = LoggerFactory.getLogger(WebAppStartup.class);
    public static final String BO_PORTAL_MAPPING_URL = "/bo/portal/*";
    private static StartupConfig startupConfig;
    private static AppPropertiesFactory appPropertiesFactory = new AppPropertiesFactory();

    public static void main(String[] args) throws Exception {


        loadStartupConfig();

        //run migration first
        if (startupConfig.dataMigrationOnStartup) {
            new MigrationRunner().run(startupConfig.jdbcUrl, startupConfig.dbUsername, startupConfig.dbPassword);
        } else {
            logger.warn("No data migration will be run during system startup causes it's disabled in this environment");
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

        // fo
        contextHandler.addServlet(createFoRestServlet(), "/fo/rest/*");

        //swagger
        ServletHolder foRestSwaggerInitServlet = createFoRestSwaggerInitServlet();
        foRestSwaggerInitServlet.setInitOrder(1);
        contextHandler.addServlet(foRestSwaggerInitServlet, null);

        //bo
        if (startupConfig.enableBackOfficePortal) {
            logger.warn("Bo portal site will be enabled");
            contextHandler.addServlet(createBoPortalServlet(), BO_PORTAL_MAPPING_URL);
            contextHandler.addFilter(createBoPortalSiteMeshFilter(), BO_PORTAL_MAPPING_URL,
                    EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD));
            contextHandler.addFilter(BoSessionFilter.class, BO_PORTAL_MAPPING_URL,
                    EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD));
        } else {
            logger.warn("Bo portal site will NOT be enabled");
        }


        //add other servlets
        contextHandler.addServlet(HealthCheckServlet.class, "/health");
        contextHandler.addServlet(FoRestDocServlet.class, "/fo-rest-doc");

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
        holder.setInitParameter("javax.ws.rs.Application", FoRestApplication.class.getName());
        return holder;
    }


    private static ServletHolder createBoPortalServlet() {
        ServletHolder holder = new ServletHolder();
        holder.setServlet(new org.glassfish.jersey.servlet.ServletContainer());
        holder.setInitParameter("javax.ws.rs.Application", BoPortalApplication.class.getName());
        return holder;
    }

    private static FilterHolder createBoPortalSiteMeshFilter() {
        FilterHolder holder = new FilterHolder();
        holder.setFilter(new SiteMeshFilter());
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

        startupConfig.enableBackOfficePortal = Boolean.valueOf((String) properties.get("enableBackOfficePortal"));

        return startupConfig;
    }

    private static final class StartupConfig {


        public String jdbcUrl;
        public String dbUsername;
        public String dbPassword;

        public int port;

        public boolean dataMigrationOnStartup;

        public boolean enableBackOfficePortal;

    }
}
