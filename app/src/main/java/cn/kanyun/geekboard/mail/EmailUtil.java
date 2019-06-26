package cn.kanyun.geekboard.mail;



import com.louisgeek.javamail.EmailMessage;
import com.louisgeek.javamail.interfaces.IEmailFactory;

import java.io.File;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;



public class EmailUtil {

    /**
     * 普通文字邮件
     * @param context
     */
    public static boolean sendNormalEmail(String context,String address) {
        IEmailFactory qqEmailFactory = new QQEmailFactory();
        try {
            EmailMessage emailMessage = EmailMessage.newBuilder()
                    .setTitle("GeekBoard反馈")
                    .setText("哇陈搜有限公司1")
                    .setContent(context)
                    .setTOAddresses(new Address[]{new InternetAddress(address)})
                    .build();
            qqEmailFactory.getProtocolSmtp().send(emailMessage);

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 带附件的邮件
     * @param context
     * @param filePath
     */
    public static void sendWithFileEmail(String context,String filePath,String address) {
        File file = new File(filePath);
        IEmailFactory qqEmailFactory = new QQEmailFactory();
        EmailMessage emailMessageWithFile = null;
        try {
            emailMessageWithFile = EmailMessage.newBuilder()
                    .setTitle("test_163_email")
                    .setText("test_163_email text")
                    // .setContent("test_163_email 带附件")
                      .setFiles(new File[]{file})
                    .setTOAddresses(new Address[]{new InternetAddress(address)})
                    .build();
            qqEmailFactory.getProtocolSmtp().send(emailMessageWithFile);
        } catch (AddressException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送图文邮件
     *
     * @param context
     */
    public static void sendTextWithImgEmail(String context,String imagePath,String address) {
        try {
            IEmailFactory qqEmailFactory = new QQEmailFactory();
            EmailMessage emailMessage = EmailMessage.newBuilder()
                    .setTitle("test_163_email")
                    .setText("test_163_email text")
                    .setContent(context)
                    .setImageFiles(new File[]{new File(imagePath)})
                    .setTOAddresses(new Address[]{new InternetAddress(address)})
                    .build();
            qqEmailFactory.getProtocolSmtp().send(emailMessage);
        } catch (AddressException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送图文邮件加附件
     *
     * @param context
     */
    public static void sendTextWithImgWithFileEmail(String context,String imagePath,String filePath,String address) {
        try {
            IEmailFactory qqEmailFactory = new QQEmailFactory();
            EmailMessage emailMessage = EmailMessage.newBuilder()
                    .setTitle("test_163_email")
                    .setText("test_163_email text")
                    .setContent(context)
                    .setImageFiles(new File[]{new File(imagePath)})
                    .setFiles(new File[]{new File(filePath)})
                    .setTOAddresses(new Address[]{new InternetAddress(address)})
                    .build();
            qqEmailFactory.getProtocolSmtp().send(emailMessage);
        } catch (AddressException e) {
            e.printStackTrace();
        }
    }
}
