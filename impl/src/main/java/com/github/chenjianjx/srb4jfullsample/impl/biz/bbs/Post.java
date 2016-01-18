package com.github.chenjianjx.srb4jfullsample.impl.biz.bbs;

import java.io.Serializable;

import com.github.chenjianjx.srb4jfullsample.impl.biz.common.EntityBase;

/**
 * 
 * An exemplary biz bean
 * 
 * @author chenjianjx@gmail.com
 *
 */
public class Post extends EntityBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 275482654501109246L;

	private long userId;

	private String content;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	

}
