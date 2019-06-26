package cn.kanyun.geekboard.mail;

import com.louisgeek.javamail.EmailService;
import com.louisgeek.javamail.abstracts.AbstractProtocolSmtp;
import com.louisgeek.javamail.interfaces.IEmailFactory;

public class QQEmailFactory implements IEmailFactory {
//    用户名
    private static final String USER_NAME = "949955482@qq.com";
//    SMTP授权码
    private static final String AUTH_CODE = "wmhuixkhfyxfbbdj";
    //发送方的邮箱
    private static final String FROM_EMAIL = "949955482@qq.com";
    //发送方姓名
    private static final String FROM_NAME = "GeekBoard";
    @Override
    public AbstractProtocolSmtp getProtocolSmtp() {
        return new QQProtocolSmtp(EmailService.create(USER_NAME, AUTH_CODE, FROM_EMAIL, FROM_NAME));
    }
}
