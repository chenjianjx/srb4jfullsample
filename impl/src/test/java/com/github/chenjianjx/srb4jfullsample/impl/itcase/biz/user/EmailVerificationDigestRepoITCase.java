package com.github.chenjianjx.srb4jfullsample.impl.itcase.biz.user;


import com.github.chenjianjx.srb4jfullsample.impl.biz.user.EmailVerificationDigest;
import com.github.chenjianjx.srb4jfullsample.impl.biz.user.EmailVerificationDigestRepo;
import com.github.chenjianjx.srb4jfullsample.impl.itcase.support.MySpringJunit4ClassRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;
import java.util.GregorianCalendar;

/**
 * 
 * @author chenjianjx@gmail.com
 *
 */
@RunWith(MySpringJunit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class EmailVerificationDigestRepoITCase {

	@Resource
	EmailVerificationDigestRepo repo;

	@Test
	public void crudTest() throws Exception {
		// insert
		long userId = System.currentTimeMillis();

		String digestStr = "test-digest-" + System.currentTimeMillis();
		EmailVerificationDigest t = buildDigest(userId, digestStr);
		repo.saveNewDigest(t);
		Assert.assertNotNull(t.getId());
		Assert.assertNotNull(t.getCreatedAt());
		System.out.println(t);

		// get by userId
		t = repo.getByUserId(userId);
		Assert.assertNotNull(t);

		// delete by userId
		repo.deleteByUserId(userId);
		t = repo.getByUserId(userId);
		Assert.assertNull(t);
	}

	@Test(expected = DuplicateKeyException.class)
	public void duplicateTest() throws Exception {
		long userId = System.currentTimeMillis();
		EmailVerificationDigest t1 = buildDigest(userId, "digest1");
		EmailVerificationDigest t2 = buildDigest(userId, "digest2");
		repo.saveNewDigest(t1);
		repo.saveNewDigest(t2);
	}

	private EmailVerificationDigest buildDigest(long userId, String digestStr) {
		EmailVerificationDigest t = new EmailVerificationDigest();
		t.setDigestStr(digestStr);
		t.setUserId(userId);
		t.setExpiresAt(new GregorianCalendar());
		t.setCreatedAt(new GregorianCalendar());
		t.setCreatedBy("some-man");
		return t;
	}

}
