package de.bht.fpa.mail.s736580s791788.model.applicationLogic.imap;

import de.bht.fpa.mail.s736580s791788.model.applicationData.Email;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;

/**
 * This class is responsible for converting Java Mail Message Objects (coming
 * from IMAP or POP3 accounts) to our own {@link Email} objects.
 * 
 * @author Siamak Haschemi, Simone Strippgen
 */

public class IMapEmailConverter {
    
    /**
   * Converts a javax {@link javax.mail.Message} to an {@link Email}.
   * 
   * @param message
   *          the {@link javax.mail.Message} to convert
   * @return the converted {@link Email}, or <code>null</code> if conversion
   *         failed.
   */
    public static Email convertMessage(Message message) {
        try {
            Email mail = new Email();
            mail.setSubject(message.getSubject());
            mail.setReceived(message.getReceivedDate());
            mail.setSent(message.getSentDate());
            mail.setRead(message.isSet(Flags.Flag.SEEN));
            mail.setImportance(convertImportance(message));
            convertContent(message, mail);
            mail.setSender(getAddress(message.getFrom()[0]));
            convertRecipients(mail, message);
            return mail;


        } catch (MessagingException ex) {
            Logger.getLogger(IMapEmailManager.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static Email.Importance convertImportance(Message javaMailMessage) throws MessagingException {
        Email.Importance handleImportanceBasedOnXPriority = handleImportanceBasedOnXPriority(javaMailMessage);
        if (handleImportanceBasedOnXPriority != null) {
//            System.out.println("ImportanceBasedOnXPriority");
            return handleImportanceBasedOnXPriority;
        }

        Email.Importance handleImportanceBasedOnXMSMailPriority = handleImportanceBasedOnXMSMailPriority(javaMailMessage);
        if (handleImportanceBasedOnXMSMailPriority != null) {
//            System.out.println("ImportanceBasedOnXMSMailPriority");
            return handleImportanceBasedOnXMSMailPriority;
        }

        Email.Importance handleImportance = handleImportance(javaMailMessage);
        if (handleImportance != null) {
//            System.out.println("Normale Importance");
            return handleImportance;
        }
        return Email.Importance.NORMAL;
    }

    private static Email.Importance handleImportanceBasedOnXMSMailPriority(Message javaMailMessage)
            throws MessagingException {
        String[] header = javaMailMessage.getHeader("X-MSMail-Priority");
        if (header == null) {
            return null;
        }

        for (String entry : header) {
            if (entry.equals("High")) {
                return Email.Importance.HIGH;
            }
            if (entry.equals("Normal")) {
                return Email.Importance.NORMAL;
            }
            if (entry.equals("Low")) {
                return Email.Importance.LOW;
            }
        }
        return null;
    }

    private static Email.Importance handleImportanceBasedOnXPriority(Message javaMailMessage)
            throws MessagingException {

        Pattern X_PRIORITY_VALUE_PATTERN = Pattern.compile("(\\d).*");
        int X_PRIORITY_HIGH_END = 4;
        int X_PRIORITY_HIGH_START = 2;
        String[] header = javaMailMessage.getHeader("X-Priority");
        if (header
                == null) {
            return null;
        }
        for (String entry : header) {
            Matcher matcher = X_PRIORITY_VALUE_PATTERN.matcher(entry);
            if (!matcher.matches()) {
                continue;
            }

            Integer flag = Integer.valueOf(matcher.group(1));
            if (flag < X_PRIORITY_HIGH_START) {
                return Email.Importance.HIGH;
            }
            if (X_PRIORITY_HIGH_START <= flag && flag <= X_PRIORITY_HIGH_END) {
                return Email.Importance.NORMAL;
            }
            if (flag > X_PRIORITY_HIGH_END) {
                return Email.Importance.LOW;
            }
        }

        return null;
    }

    private static Email.Importance handleImportance(Message javaMailMessage) throws MessagingException {
        String[] header = javaMailMessage.getHeader("Importance");
        if (header == null) {
            return null;
        }

        for (String entry : header) {
            if (entry.equals("high")) {
                return Email.Importance.HIGH;
            }
            if (entry.equals("normal")) {
                return Email.Importance.NORMAL;
            }
            if (entry.equals("low")) {
                return Email.Importance.LOW;
            }
        }
        return null;
    }

    private static String convertText(Message message) {
        Object cont;
        try {
            cont = message.getContent();
            if (cont instanceof String) {
                return ((String) cont);
            }
        } catch (IOException ex) {
            Logger.getLogger(IMapEmailManager.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(IMapEmailManager.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return "";
    }

    private static void convertContent(Message javaMailMessage, Email email) throws MessagingException {
        try {
            handleContent(javaMailMessage.getContent(), null, email);


        } catch (IOException ex) {
            Logger.getLogger(IMapEmailManager.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void handleContent(Object content, BodyPart bodyPart, Email email) throws MessagingException {
        if (content instanceof String) {
            email.setText((String) content);
        } else if (content instanceof Multipart) {
            handleMultipart((Multipart) content, email);
        } else if (content instanceof InputStream) {
            handleInputStream((InputStream) content, bodyPart, email);
        }
    }

    private static void handleInputStream(InputStream content, BodyPart bodyPart, Email email) {
        String text = email.getText();
        text = text + "\n\n ------ Attachements wurden entfernt!! ------ ";
        email.setText(text);
    }

    private static void handleMultipart(Multipart content, Email email) throws MessagingException {
        for (int i = 0; i < content.getCount(); i++) {
            try {
                BodyPart bodyPart = content.getBodyPart(i);
                handleContent(bodyPart.getContent(), bodyPart, email);


            } catch (IOException ex) {
                Logger.getLogger(IMapEmailManager.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void convertRecipients(Email mail, Message message) {
        try {
//            System.out.println("Set To-Recipients");
            setRecipients(message.getRecipients(Message.RecipientType.TO), mail.getReceiverListTo());
//            System.out.println("Set CC-Recipients");
            setRecipients(message.getRecipients(Message.RecipientType.CC), mail.getReceiverListCC());
        } catch (MessagingException ex) {
            Logger.getLogger(IMapEmailManager.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void setRecipients(Address[] recipients, ArrayList<String> receiverList) {
        if (recipients == null) {
//            System.out.println("No recipients!");
            return;
        }
        for (Address address : recipients) {
            String addr = getAddress(address);
            if (addr != null) {
                receiverList.add(addr);
            }
        }
    }

    private static String getAddress(Address address) {
        if (!(address instanceof InternetAddress)) {
            return null;
        }
        InternetAddress internetAddress = (InternetAddress) address;
        return internetAddress.getAddress();
    }
}
