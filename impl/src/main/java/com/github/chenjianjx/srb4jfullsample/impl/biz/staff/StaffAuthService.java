package com.github.chenjianjx.srb4jfullsample.impl.biz.staff;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

/**
 * @author chenjianjx@gmail.com
 */
@Service
public class StaffAuthService {

    public String encodePassword(String raw) {

        return DigestUtils.sha1Hex(raw);
    }

    public static void main(String[] args) {
        String sqlTemplate = "insert into StaffUser(username, password, createdAt, createdBy) "
                + "values ('%s', '%s', now(), 'staff user generator');";
        String username = "firstadmin";
        String password = "abc123";
        String encodedPassword = new StaffAuthService().encodePassword(password);

        System.out.println(String.format(sqlTemplate, username, encodedPassword));
    }

}
