package cn.kanyun.geekboard.mail;

import com.louisgeek.javamail.EmailProtocol;
import com.louisgeek.javamail.EmailService;
import com.louisgeek.javamail.abstracts.AbstractProtocolSmtp;

public class QQProtocolSmtp extends AbstractProtocolSmtp {
    private static final String MAIL_HOST = "smtp.qq.com";
    private static final int MAIL_HOST_PORT = 25;
    private static final int MAIL_HOST_PORT_SSL = 465;// 465 / 587

    public QQProtocolSmtp(EmailService mEmailService) {
        super(mEmailService);
    }

    @Override
    protected EmailProtocol setupEmailProtocol() {
        return EmailProtocol.create(MAIL_HOST, MAIL_HOST_PORT, MAIL_HOST_PORT_SSL);
    }
}
