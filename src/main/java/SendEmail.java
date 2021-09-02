import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.*;
public class SendEmail {
    public static void main(String[] args) {
        Properties props = new Properties();
        Session session = Session.getInstance(props);
        MimeMessage msg = new MimeMessage(session);

        Transport t = null;
        try {
            Address bill = new InternetAddress("god@microsoft.com", "Bill Gates");
            Address elliotte = new InternetAddress("elharo@ibiblio.org");

            msg.setText("Resistance is futile. You will be assimiated!");
            msg.setFrom(bill);
            msg.setRecipients(Message.RecipientType.TO, String.valueOf(elliotte));
            msg.setSubject("You must comply.");

            t = session.getTransport("smtps");
            t.connect("smtp.gmail.com", "erharold", "password");
            t.sendMessage(msg, msg.getAllRecipients());
        } catch (MessagingException | UnsupportedEncodingException ex){
            ex.printStackTrace();
        } finally {
            if (t != null){
                try {
                    t.close();
                } catch (MessagingException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
