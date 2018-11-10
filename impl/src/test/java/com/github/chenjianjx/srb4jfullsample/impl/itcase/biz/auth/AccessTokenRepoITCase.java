package com.github.chenjianjx.srb4jfullsample.impl.itcase.biz.auth;

import com.github.chenjianjx.srb4jfullsample.impl.biz.auth.AccessToken;
import com.github.chenjianjx.srb4jfullsample.impl.biz.auth.AccessTokenRepo;
import com.github.chenjianjx.srb4jfullsample.impl.itcase.BaseITCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.GregorianCalendar;

/**
 * @author chenjianjx@gmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/applicationContext-test.xml"})
public class AccessTokenRepoITCase extends BaseITCase {

    @Resource
    AccessTokenRepo repo;

    @Test
    public void crudTest() throws Exception {
        //insert
        AccessToken t = new AccessToken();
        String tokenStr = "test-token-" + System.currentTimeMillis();
        String refreshTokenStr = "test-refresh-token-" + System.currentTimeMillis();
        t.setTokenStr(tokenStr);
        t.setRefreshTokenStr(refreshTokenStr);
        t.setUserId(2l);
        t.setLifespan(3600);
        t.setExpiresAt(new GregorianCalendar());
        t.setCreatedBy("some-man");
        repo.saveNewToken(t);
        Assert.assertNotNull(t.getId());
        Assert.assertNotNull(t.getCreatedAt());
        System.out.println(t);

        //get by token
        t = repo.getByTokenStr(tokenStr);
        Assert.assertNotNull(t);


        //get by refresh token
        t = repo.getByRefreshTokenStr(refreshTokenStr);
        Assert.assertNotNull(t);

        //update token
        t.setExpiresAt(new GregorianCalendar());
        repo.updateAccessToken(t);
        t = repo.getByTokenStr(tokenStr);
        System.out.println(t.getExpiresAt());

        //delete by token
        repo.deleteByTokenStr(tokenStr);
        t = repo.getByTokenStr(tokenStr);
        Assert.assertNull(t);
    }

}
