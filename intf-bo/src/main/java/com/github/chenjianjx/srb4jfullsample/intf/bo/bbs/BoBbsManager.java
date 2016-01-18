package com.github.chenjianjx.srb4jfullsample.intf.bo.bbs;

import java.util.List;

import com.github.chenjianjx.srb4jfullsample.intf.fo.basic.FoResponse;

/**
 * an exemplary bo-intf manager
 * 
 * @author chenjianjx@gmail.com
 *
 */
public interface BoBbsManager {

	public FoResponse<List<BoPost>> getAllPostsForBbsAdmin(Long currentUserId);

}
