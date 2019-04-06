package com.tilchina.timp.util;/*
 * @author XueYuSong
 * @date 2018-07-18 11:25
 */

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import java.util.List;

@Slf4j
public class EmailUtil {
	private static final String HOST_NAME = "smtp.qiye.163.com";
	private static final Integer SMTP_PORT = 465;
	private static final String USERNAME = "yusong.xue@til-china.com";
	private static final String PASSWORD = "hK[LT#->b1IIm6tJ";

	public static void send(String subject, String message, List<String> addresses) {
		if (CollectionUtils.isEmpty(addresses)) {
			return;
		}

		try {
			for (String address : addresses) {
				Email email = new SimpleEmail();
				email.setHostName(HOST_NAME);
				email.setSmtpPort(SMTP_PORT);
				email.setAuthenticator(new DefaultAuthenticator(USERNAME, PASSWORD));
				email.setSSLOnConnect(true);
				email.setSubject(subject);
				email.setMsg(message);
				email.setFrom(USERNAME);
				email.addTo(address);
				email.send();
			}
		} catch (EmailException e) {
			log.error("{}", e);
		}
	}
}
