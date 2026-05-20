package com.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

/**
 * 【论文3.1.1(6)】邮件推送服务
 * 通知方式有站内消息和邮件推送两种渠道
 * 
 * 配置说明：
 * - 需在application.yml中配置SMTP信息（spring.mail节点）
 * - 默认关闭（spring.mail.enabled=false），需手动开启并填写SMTP账号
 * - 支持QQ邮箱、163邮箱等常见SMTP服务
 */
@Slf4j
@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    /**
     * 发送邮件通知
     * 如果邮件推送未启用，仅记录日志不发送
     *
     * @param toEmail  收件人邮箱
     * @param subject  邮件主题
     * @param content  邮件内容（支持HTML）
     * @return 是否发送成功
     */
    public boolean sendEmail(String toEmail, String subject, String content) {
        if (!mailEnabled) {
            log.debug("【邮件推送】未启用，跳过发送 - 收件人: {}, 主题: {}", toEmail, subject);
            return false;
        }

        if (mailSender == null) {
            log.warn("【邮件推送】JavaMailSender未配置，无法发送邮件");
            return false;
        }

        if (toEmail == null || toEmail.isEmpty()) {
            log.warn("【邮件推送】收件人邮箱为空，跳过发送");
            return false;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, true); // true表示HTML格式

            mailSender.send(message);
            log.info("【邮件推送】发送成功 - 收件人: {}, 主题: {}", toEmail, subject);
            return true;
        } catch (Exception e) {
            log.error("【邮件推送】发送失败 - 收件人: {}, 主题: {}, 错误: {}", toEmail, subject, e.getMessage());
            return false;
        }
    }

    /**
     * 判断邮件推送是否启用
     */
    public boolean isMailEnabled() {
        return mailEnabled;
    }
}