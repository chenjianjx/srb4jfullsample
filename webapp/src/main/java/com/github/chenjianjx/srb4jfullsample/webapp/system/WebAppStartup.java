package com.github.chenjianjx.srb4jfullsample.webapp.system;


import com.github.chenjianjx.srb4jfullsample.datamigration.MigrationRunner;
import com.github.chenjianjx.srb4jfullsample.webapp.bo.portal.BoAllInOneServlet;
import com.github.chenjianjx.srb4jfullsample.webapp.fo.rest.support.FoSwaggerJaxrsConfig;
import com.github.chenjianjx.srb4jfullsample.webapp.root.FoRestDocServlet;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.OverrideCombiner;
import org.apache.commons.lang3.StringUtils;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EventListener;

/**
 * The webapp starter, based on an embedded jetty
 * Created by chenjianjx@gmail.com on 7/11/18.
 */
public class WebAppStartup {

    private static final Logger logger = LoggerFactory.getLogger(WebAppStartup.class);
    public static final String ENVIRONMENT_KEY = "environment";
    public static final String DEFAULT_ENV = "dev";

    private static StartupConfig startupConfig;
    private static String environment;

    public static void main(String[] args) throws Exception {
        initializeEnv();

        loadStartupConfig();

        //run migration first
        if (startupConfig.dataMigrationOnStartup) {
            new MigrationRunner().run(startupConfig.jdbcUrl, startupConfig.dbUsername, startupConfig.dbPassword);
        }


        startServer(startupConfig);
    }

    private static void initializeEnv() {
        environment = System.getProperty(ENVIRONMENT_KEY);
        if (StringUtils.isBlank(environment)) {
            environment = DEFAULT_ENV;
            System.setProperty(ENVIRONMENT_KEY, environment); //Note: spring context will read this system property
        }


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

    private static StartupConfig loadStartupConfig() throws ConfigurationException, MalformedURLException {

        String overridePropFilename = "app.override." + environment + ".properties";


        Parameters params = new Parameters();

        FileBasedConfigurationBuilder<FileBasedConfiguration> base =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(params.properties().setURL(WebAppStartup.class.getResource("/config/app.properties")));

        FileBasedConfigurationBuilder<FileBasedConfiguration> override =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(params.properties().setURL(WebAppStartup.class.getResource("/config/" + overridePropFilename)));

        CombinedConfiguration config = new CombinedConfiguration(new OverrideCombiner());
        config.addConfiguration(override.getConfiguration());
        config.addConfiguration(base.getConfiguration());


        startupConfig = new StartupConfig();
        startupConfig.environment = environment;

        String dbHost = config.getString("dbHost");
        int dbPort = config.getInt("dbPort");
        String dbSchema = config.getString("dbSchema");
        startupConfig.jdbcUrl = String.format("jdbc:mysql://%s:%s/%s", dbHost, dbPort, dbSchema);

        startupConfig.dbUsername = config.getString("dbUsername");
        startupConfig.dbPassword = config.getString("dbPassword");

        String schemeAndHost = config.getString("schemeAndHost");
        URL url = new URL(schemeAndHost);
        startupConfig.port = url.getPort();

        startupConfig.dataMigrationOnStartup = config.getBoolean("dataMigrationOnStartup");

        return startupConfig;
    }

    private static final class StartupConfig {
        public String environment;

        public String jdbcUrl;
        public String dbUsername;
        public String dbPassword;

        public int port;

        public boolean dataMigrationOnStartup;

    }
}
