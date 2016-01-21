package com.github.chenjianjx.srb4jfullsample.pso.bbs;

import java.util.List;

/**
 * bbs rpc endpoint for partner systems. (An exemplary pso rpc endpoint)
 * 
 * @author chenjianjx@gmail.com
 *
 */
	
public interface PsoBbsRpc {
	List<PsoPost> getPostsByCelebrity(String celebrity);
}
