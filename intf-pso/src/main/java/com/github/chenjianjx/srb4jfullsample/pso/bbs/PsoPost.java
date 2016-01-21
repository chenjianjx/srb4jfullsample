package com.github.chenjianjx.srb4jfullsample.pso.bbs;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.github.chenjianjx.srb4jfullsample.pso.common.PsoEntityBase;


/**
 * post for partner systems. (An exemplary pso bean)
 * 
 * @author chenjianjx@gmail.com
 *
 */
	
public class PsoPost extends PsoEntityBase  implements Serializable{

 
	private static final long serialVersionUID = 771086654014396291L;

	private Map<String, Integer> celebrityOccurenceMap = new HashMap<String, Integer>();

	private String content;

	/**
	 * key = celebrity's name, value = num of times this name exists in the post
	 * @return
	 */
	public Map<String, Integer> getCelebrityOccurenceMap() {
		return celebrityOccurenceMap;
	}

	public void setCelebrityOccurenceMap(
			Map<String, Integer> celebrityOccurenceMap) {
		this.celebrityOccurenceMap = celebrityOccurenceMap;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
