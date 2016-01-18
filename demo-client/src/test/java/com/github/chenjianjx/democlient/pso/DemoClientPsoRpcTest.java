package com.github.chenjianjx.srb4jfullsample.democlient.pso;

import java.net.MalformedURLException;
import java.util.List;

import org.junit.Test;
import com.github.chenjianjx.srb4jfullsample.democlient.util.DemoClientConstants;
import com.github.chenjianjx.srb4jfullsample.pso.celebritysystem.bbs.CsBbsRpc;
import com.github.chenjianjx.srb4jfullsample.pso.celebritysystem.bbs.CsPost;

import com.caucho.hessian.client.HessianProxyFactory;

/**
 * 
 * @author chenjianjx@gmail.com
 *
 */
public class DemoClientPsoRpcTest {
	@Test
	public void invokePso() throws MalformedURLException {
		HessianProxyFactory factory = new HessianProxyFactory();
		CsBbsRpc rpc = (CsBbsRpc) factory.create(CsBbsRpc.class,
				DemoClientConstants.PSO_TEST_URL);
		List<CsPost> result = rpc.getPostsByCelebrity("tom");
		System.out.println("Posts: " + result);
	}
}
