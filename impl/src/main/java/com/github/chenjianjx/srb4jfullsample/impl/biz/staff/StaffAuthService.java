package com.github.chenjianjx.srb4jfullsample.impl.biz.staff;

import com.github.chenjianjx.srb4jfullsample.impl.biz.auth.AccessToken;
import com.github.chenjianjx.srb4jfullsample.impl.biz.auth.AccessTokenRepo;
import com.github.chenjianjx.srb4jfullsample.impl.biz.auth.RandomLoginCode;
import com.github.chenjianjx.srb4jfullsample.impl.biz.auth.RandomLoginCodeRepo;
import com.github.chenjianjx.srb4jfullsample.impl.biz.user.User;
import com.github.chenjianjx.srb4jfullsample.impl.support.config.AppProperties;
import com.github.chenjianjx.srb4jfullsample.impl.support.mail.MailEngine;
import com.github.chenjianjx.srb4jfullsample.impl.util.tools.lang.MyLangUtils;
import com.github.chenjianjx.srb4jfullsample.intf.fo.basic.FoConstants;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Sha2Crypt;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.github.chenjianjx.srb4jfullsample.impl.util.tools.lang.MyLangUtils.toUtf8Bytes;
import static com.github.chenjianjx.srb4jfullsample.intf.fo.basic.FoConstants.RANDOM_LOGIN_CODE_LIFESPAN;

/**
 * 
 * @author chenjianjx@gmail.com
 *
 */
@Service
public class StaffAuthService {

	public String encodePassword(String raw) {

		return DigestUtils.sha1Hex(raw);
	}

}
