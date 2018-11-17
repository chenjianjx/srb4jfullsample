package com.github.chenjianjx.srb4jfullsample.impl.itcase.biz.staff;


import com.github.chenjianjx.srb4jfullsample.impl.biz.staff.StaffUser;
import com.github.chenjianjx.srb4jfullsample.impl.biz.staff.StaffUserRepo;
import com.github.chenjianjx.srb4jfullsample.impl.itcase.BaseITCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Calendar;

/**
 * @author chenjianjx@gmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/applicationContext-test.xml"})
public class StaffUserRepoITCase extends BaseITCase {

    @Resource
    StaffUserRepo repo;

    @Test
    public void crudTest() throws Exception {
        StaffUser staffUser = new StaffUser();

        staffUser.setPassword("sdafhksldafds728926347");
        staffUser.setUsername("someUser" + System.currentTimeMillis());
        staffUser.setCreatedBy("someAdmin");


        //save
        repo.saveNewStaffUser(staffUser);
        Assert.assertTrue(staffUser.getId() > 0);
        Assert.assertNotNull(staffUser.getCreatedAt());
        Assert.assertFalse(staffUser.isLoggedInOnce());
        System.out.println(staffUser);


        long staffUserId = staffUser.getId();
        String username = staffUser.getUsername();


        //query		
        staffUser = repo.getStaffUserById(staffUserId);
        Assert.assertNotNull(staffUser);

        staffUser = repo.getStaffUserByUsername(username);
        Assert.assertNotNull(staffUser);

        //update
        staffUser.setPassword("newPassword");
        staffUser.setLastLoginDate(Calendar.getInstance());
        staffUser.setUpdatedBy("newUpdatedBy");
        repo.updateStaffUser(staffUser);
        Assert.assertNotNull(staffUser.getUpdatedAt());
        staffUser = repo.getStaffUserById(staffUserId);
        Assert.assertEquals("newPassword", staffUser.getPassword());
        Assert.assertEquals("newUpdatedBy", staffUser.getUpdatedBy());
        Assert.assertNotNull(staffUser.getLastLoginDate());
    }


}
