package tech.cspioneer.backend.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Date;
import java.util.Properties;

@Component
public class SMTPUtils {

    private static String host;
    private static String port;
    private static String username;
    private static String password;
    private static String protocol;

    @Value("${spring.mail.host}")
    public void setHost(String host) {
        SMTPUtils.host = host;
    }

    @Value("${spring.mail.port}")
    public void setPort(String port) {
        SMTPUtils.port = port;
    }

    @Value("${spring.mail.username}")
    public void setUsername(String username) {
        SMTPUtils.username = username;
    }

    @Value("${spring.mail.password}")
    public void setPassword(String password) {
        SMTPUtils.password = password;
    }

    @Value("${spring.mail.protocol:smtp}")
    public void setProtocol(String protocol) {
        SMTPUtils.protocol = protocol;
    }

    /**
     * 发送邮件
     * @param recipientEmail 收件人邮箱
     * @param title 邮件标题
     * @param content 邮件正文
     * @return 是否发送成功
     */
    public boolean sendEmail(String recipientEmail, String title, String content) {
        try {
            // 1. 创建参数配置, 用于连接邮件服务器的参数配置
            Properties props = new Properties();
            props.setProperty("mail.transport.protocol", protocol); // 使用的协议（JavaMail规范要求）
            props.setProperty("mail.smtp.host", host); // 指定SMTP服务器地址
            props.setProperty("mail.smtp.port", port); // 指定SMTP端口号
            // 使用SMTP身份验证
            props.setProperty("mail.smtp.auth", "true"); // 需要请求认证
            props.put("mail.smtp.ssl.enable", "true"); // 开启SSL
            props.put("mail.smtp.ssl.protocols", "TLSv1.2"); // 指定SSL版本
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            // 设置请求超时时间
            props.setProperty("mail.smtp.connectiontimeout", "10000"); // 与邮件服务器建立连接的时间限制
            props.setProperty("mail.smtp.timeout", "10000"); // 邮件SMTP读取的时间限制
            props.setProperty("mail.smtp.writetimeout", "10000"); // 邮件内容上传的时间限制

            // 2. 根据配置创建会话对象, 用于和邮件服务器交互
            Session session = Session.getDefaultInstance(props);
            session.setDebug(false); // 设置为debug模式, 可以查看详细的发送log

            // 3. 创建邮件
            MimeMessage message = new MimeMessage(session);
            // From: 发件人
            message.setFrom(new InternetAddress(username, "验证码服务", "UTF-8"));
            // To: 收件人
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail, recipientEmail, "UTF-8"));
            // Subject: 邮件主题
            message.setSubject(title, "UTF-8");
            // Content: 邮件正文
            message.setContent(content, "text/html;charset=UTF-8");
            // 设置发件时间
            message.setSentDate(new Date());
            // 保存设置
            message.saveChanges();

            // 4. 根据 Session 获取邮件传输对象
            Transport transport = session.getTransport();
            transport.connect(username, password);
            // 发送邮件
            transport.sendMessage(message, message.getAllRecipients());
            // 关闭传输连接
            transport.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("发送邮件失败！" + e.getMessage());
            return false;
        }
    }
}

//    public static void main(String[] args) {
//        // 测试发送邮件
//        String recipientEmail = "xxx@163.com";
//        String title = "测试发送邮件";
//        String content = "您好！这是我发送的一封测试邮件。";
//        boolean result = sendEmail(recipientEmail, title, content);
//        System.out.println("邮件发送结果：" + (result ? "成功" : "失败"));
//    }
//}