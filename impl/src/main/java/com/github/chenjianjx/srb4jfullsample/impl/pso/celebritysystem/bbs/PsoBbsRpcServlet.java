package com.github.chenjianjx.srb4jfullsample.impl.pso.celebritysystem.bbs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;

import com.github.chenjianjx.srb4jfullsample.impl.biz.bbs.Post;
import com.github.chenjianjx.srb4jfullsample.impl.biz.bbs.PostRepo;
import com.github.chenjianjx.srb4jfullsample.impl.pso.common.PsoAbstractHessianServlet;
import com.github.chenjianjx.srb4jfullsample.impl.util.tools.lang.MyLangUtils;
import com.github.chenjianjx.srb4jfullsample.pso.bbs.PsoBbsRpc;
import com.github.chenjianjx.srb4jfullsample.pso.bbs.PsoPost;

/**
 * 
 * @author chenjianjx@gmail.com
 *
 */
public class PsoBbsRpcServlet extends PsoAbstractHessianServlet implements
		PsoBbsRpc {

	private static final long serialVersionUID = 7698725957127177454L;

	
	@Resource
	PostRepo postRepo;

	@Override
	public List<PsoPost> getPostsByCelebrity(String celebrity) {
		if (celebrity == null) {
			throw new IllegalArgumentException("celebrity name cannot be null");
		}

		List<PsoPost> csList = new ArrayList<PsoPost>();

		List<Post> bizPosts = postRepo.getAllPosts();
		for (Post biz : bizPosts) {
			String content = biz.getContent();
			int count = StringUtils.countMatches(content.toLowerCase(),
					celebrity.toLowerCase());
			if (count == 0) {
				continue;
			}

			PsoPost cs = new PsoPost();
			MyLangUtils.copyProperties(cs, biz);
			Map<String, Integer> occMap = new HashMap<String, Integer>();
			occMap.put(celebrity.toLowerCase(), count);
			cs.setCelebrityOccurenceMap(occMap);

			csList.add(cs);
		}
		return csList;
	}
}
