package com.github.chenjianjx.srb4jfullsample.impl.pso.common;

import java.net.InetSocketAddress;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import com.github.chenjianjx.srb4jfullsample.impl.pso.celebritysystem.bbs.PsoBbsRpcServlet;

/**
 * The class to initialize an embedded server to host rpc endpoints
 * 
 * @author chenjianjx@gmail.com
 *
 */	
@Component
public class PsoRpcServer implements ApplicationContextAware {

	
	public static ApplicationContext contextDefinedInWebappProject;

	@Resource
	PsoConfig psoConfig;

	@PostConstruct
	public void init() throws Exception {
		if (psoConfig.getHost() == null || psoConfig.getPort() <= 0) {
			throw new IllegalArgumentException(
					"You have included the partner system oriented rpc module, but you didn't set up the hostname and port well. "
							+ "Please set up 'psoRpcServerHost' and 'psoRpcServerHostPort' in the properties file. "
							+ "VERY IMPORTANT: The host and port should only be accessble from intranet");
		}
		InetSocketAddress address = new InetSocketAddress(psoConfig.getHost(),
				psoConfig.getPort());
		Server server = new Server(address);

		ServletContextHandler context = new ServletContextHandler();
		context.setContextPath("/");
		context.addServlet(PsoBbsRpcServlet.class, "/pso/bbs");
		server.setHandler(context);
		server.start();
	}

	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
	
		contextDefinedInWebappProject = context;
	}


}
