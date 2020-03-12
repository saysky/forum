package com.example.forum.util;

import io.github.biezhi.ome.OhMyEmail;
import lombok.extern.slf4j.Slf4j;
import java.util.*;

/**
 * <pre>
 *     常用工具
 * </pre>
 */
@Slf4j
public class SensUtils {

    /**
     * 配置邮件
     *
     * @param smtpHost smtpHost
     * @param userName 邮件地址
     * @param password password
     */
    public static void configMail(String smtpHost, String userName, String password) {
        Properties properties = OhMyEmail.defaultConfig(false);
        properties.setProperty("mail.smtp.host", smtpHost);
        OhMyEmail.config(properties, userName, password);
    }


}
