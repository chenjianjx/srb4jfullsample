package com.github.chenjianjx.srb4jfullsample.impl.itcase.biz.user;

import javax.annotation.Resource;

import com.github.chenjianjx.srb4jfullsample.impl.itcase.BaseITCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import com.github.chenjianjx.srb4jfullsample.impl.biz.user.User;
import com.github.chenjianjx.srb4jfullsample.impl.biz.user.UserRepo;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author chenjianjx@gmail.com
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class UserRepoITCase extends BaseITCase {

	@Resource
	UserRepo repo;

	@Test
	public void crudTest() throws Exception {
		User user = new User();
		user.setSource(User.SOURCE_LOCAL);
		user.setPassword("sdafhksldafds728926347");		
		user.setEmail(System.currentTimeMillis() + "@temp.com");		
		user.setCreatedBy("someone");		
	 
		
		//save
		repo.saveNewUser(user);
		Assert.assertNotNull(user.getId());
		Assert.assertNotNull(user.getCreatedAt());
		Assert.assertFalse(user.isEmailVerified());
		System.out.println(user);
		

		long userId = user.getId();
		String principal = user.getPrincipal();
		
		
		//query		
		user = repo.getUserById(userId);
		Assert.assertNotNull(user);
		Assert.assertFalse(user.isEmailVerified());
		
		user = repo.getUserByPrincipal(principal);
		Assert.assertNotNull(user);
		
		//update
		user.setPassword("newPassword");
		user.setEmailVerified(true);
		user.setUpdatedBy("newUpdatedBy");		
		repo.updateUser(user);
		Assert.assertNotNull(user.getUpdatedAt());
		user = repo.getUserById(userId);
		Assert.assertEquals("newPassword", user.getPassword());
		Assert.assertEquals("newUpdatedBy", user.getUpdatedBy());
		Assert.assertTrue(user.isEmailVerified());
	}
	
	
	 


}
