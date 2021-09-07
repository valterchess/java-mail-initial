import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.*;
public class SendEmail {
    public static void main(String[] args) {
        /*
         * Iniciando uma Session com um grupo específico de propriedades
         * utilizando o metodo Session.getInstance()
         * utilizando inicialmente como propriedades padrão
         */
        Properties props = new Properties();
        Session session = Session.getInstance(props);

        /*
         * Com o objeto Session construimos um objeto do tipo Message.
         * Neste caso o MimeMessage é especificado devido o formato
         * da mensagem, para envios em caracteres UTF-8
         */
        MimeMessage msg = new MimeMessage(session);

        /*
         * Tendo o objeto Message preparado é preciso configurar os campos
         * e conteúdos.
         * Os endereços de envio (from) e recebimento (to)
         * serão objetos do tipo InternetAddress
         * é possível atribuir como valores o endereço de email e um nome,
         * ou apenas um endereço de email
         */
        Transport t = null;
        try {
            /*
             * construindo os endereços,
             * veja que especifiquei o nome em apenas um deles
              */
            Address bill = new InternetAddress("god@microsoft.com", "Bill Gates");
            Address elliotte = new InternetAddress("elharo@ibiblio.org");

            /*
             * Valor a ser enviado, neste caso apenas o texto.
             * Mas é possível enviar objetos em HTML.(não neste caso especificamente),
             * por exemplo:
             * <p>Um texto qualquer<p/>
             */
            msg.setText("Resistir é inútil. Você será assimilado!");
            /*Mensagem de Bill para...*/
            msg.setFrom(bill);
            /*
            * Neste caso, além de especificar o endereço
            * devemos indicar como o endereço será usado
            * Se será utilizado como:
            * Message.RecipientType.TO ou
            * Message.RecipientType.CC ou
            * Message.RecipientType.BCC
            * três exemplos de mnemonic constants da Classe Message.RecipientType
             */
            msg.setRecipients(Message.RecipientType.TO, String.valueOf(elliotte));

            /*Assunto da mensagem*/
            msg.setSubject("Você de Cumprir");

            /* Aqui selecionamos o tipo de transporte que queremos utilizar*/
            t = session.getTransport("smtps");
            /*
             * Uma vez feito o transporte
             * fazemos a conexão com um host específico, passando nome e senha
             * Aqui é basicamente onde passamos os dados de ‘login’ e senha
             */
            t.connect("smtp.gmail.com", "nome@gmail.com", "password");
            /* Aqui ocorre a conexão com o servidor e envia a mensagem*/
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
