package graphicalsmtpclient;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

public class SMTPClient extends JFrame {

    private final JTextField  fromField      = new JTextField(40);
    private final JTextField  toField        = new JTextField(40);
    private final JTextField  hostField      = new JTextField(40);
    private final JTextField  subjectField   = new JTextField(40);
    private final JTextField  usernameField  = new JTextField(40);
    private final JTextField  passwordField  = new JPasswordField(40);
    private final JTextArea   message        = new JTextArea(40,72);

    public SMTPClient(){
        super("SMTP Client");

        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());

        JPanel labels = new JPanel();
        labels.setLayout(new GridLayout(6, 1));

        JLabel hostLabel = new JLabel("SMTP Server: ");
        labels.add(hostLabel);

        JPanel fields = new JPanel();
        fields.setLayout(new GridLayout(6,1));
        String host = System.getProperty("mail.host", "smtp.gmail.com");
        hostField.setText(host);
        fields.add(hostField);

        JLabel toLabel = new JLabel("To: ");
        labels.add(toLabel);
        fields.add(toField);

        String from = System.getProperty("mail.from","");
        fromField.setText(from);
        JLabel fromLabel = new JLabel("From: ");
        labels.add(fromLabel);
        fields.add(fromField);

        JLabel subjectLabel = new JLabel("Subject: ");
        labels.add(subjectLabel);
        fields.add(subjectField);

        JLabel usernameLabel = new JLabel("Username: ");
        labels.add(usernameLabel);
        fields.add(usernameField);

        JLabel passwordLabel = new JLabel("Password: ");
        labels.add(passwordLabel);
        fields.add(passwordField);

        Box north = Box.createHorizontalBox();
        north.add(Box.createHorizontalStrut(5));
        north.add(labels);
        north.add(fields);

        contentPane.add(north, BorderLayout.NORTH);

        message.setFont(new Font("Monospaced", Font.PLAIN,12));
        JScrollPane jsp = new JScrollPane(message);
        contentPane.add(jsp, BorderLayout.CENTER);

        JPanel south = new JPanel();
        south.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton sendButton = new JButton("Send Message");
        south.add(sendButton);
        sendButton.addActionListener(new SendAction());
        contentPane.add(south, BorderLayout.SOUTH);

        this.pack();
    }

    private class SendAction implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent evt){
            try {
                Properties props = new Properties();
                final Session session = Session.getInstance(props);
                final Message msg = new MimeMessage(session);

                Address to = new InternetAddress(toField.getText());
                Address from = new InternetAddress(fromField.getText());

                msg.setContent(message.getText(), "text/plain");
                msg.setFrom(from);
                msg.setRecipient(Message.RecipientType.TO, to);
                msg.setSubject(subjectField.getText());

                final String hostname = hostField.getText();
                final String username = usernameField.getText();
                final String password = passwordField.getText();

                // Sending a message can take a non-trivial amount of time so
                // spawn a thread to handle it.
                Runnable r = () -> {
                    Transport t = null;
                    try{
                        t = session.getTransport("smtps");
                        t.connect(hostname, username, password);
                        t.sendMessage(msg, msg.getAllRecipients());
                    } catch (MessagingException ex){
                        ex.printStackTrace();
                    } finally {
                        if (t != null)
                        try {
                            t.close();
                        } catch (MessagingException ignored){
                        }
                    }
                };
                Thread t = new Thread(r);
                t.start();

                message.setText("");
            } catch (MessagingException ex){
                JOptionPane.showMessageDialog(getRootPane(),
                        "Error sending message: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
                SMTPClient client = new SMTPClient();
            // I set up the exit behavior here rather than in
            // the constructor since other programs that use this class
            // may not want to exit the application when the SMTPClient
            // window closes.
            client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            client.setVisible(true);
        });
    }
}
