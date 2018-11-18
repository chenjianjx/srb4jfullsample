package com.github.chenjianjx.srb4jfullsample.impl.biz.staff;

import com.github.chenjianjx.srb4jfullsample.utils.lang.MyCodecUtils;
import org.springframework.stereotype.Service;

/**
 * @author chenjianjx@gmail.com
 */
@Service
public class StaffAuthService {

    public static void main(String[] args) {
        String sqlTemplate = "insert into StaffUser(username, password, createdAt, createdBy) "
                + "values ('%s', '%s', now(), 'staff user generator');";
        String username = "firstadmin";
        String password = "abc123";
        String encodedPassword = MyCodecUtils.encodePasswordLikeDjango(password);

        System.out.println(String.format(sqlTemplate, username, encodedPassword));
    }

}
