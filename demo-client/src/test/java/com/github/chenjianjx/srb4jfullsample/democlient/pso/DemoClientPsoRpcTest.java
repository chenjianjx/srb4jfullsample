package com.github.chenjianjx.srb4jfullsample.democlient.pso;

import java.net.MalformedURLException;
import java.util.List;

import org.junit.Test;

import com.caucho.hessian.client.HessianProxyFactory;
import com.github.chenjianjx.srb4jfullsample.pso.bbs.PsoBbsRpc;
import com.github.chenjianjx.srb4jfullsample.pso.bbs.PsoPost;

/**
 * 
 * @author chenjianjx@gmail.com
 *
 */
public class DemoClientPsoRpcTest {

	private static final String PSO_TEST_URL = "http://localhost:9090/pso/bbs";

	@Test
	public void invokePso() throws MalformedURLException {
		HessianProxyFactory factory = new HessianProxyFactory();
		PsoBbsRpc rpc = (PsoBbsRpc) factory.create(PsoBbsRpc.class,
				PSO_TEST_URL);
		List<PsoPost> result = rpc.getPostsByCelebrity("tom");
		System.out.println("Posts: " + result);
	}
}
