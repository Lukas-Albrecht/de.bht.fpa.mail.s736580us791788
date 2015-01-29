/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bht.fpa.mail.s736580s791788.model.applicationLogic.imap;

import de.bht.fpa.mail.s736580s791788.model.applicationData.Account;
import de.bht.fpa.mail.s736580s791788.model.applicationData.Email;
import de.bht.fpa.mail.s736580s791788.model.applicationData.Folder;
import de.bht.fpa.mail.s736580s791788.model.applicationLogic.EmailManagerIF;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

/**
 *
 * @author Lukas Albrecht, Peter Albrecht
 */
public class IMapEmailManager implements EmailManagerIF{
    
    private final Account account;
    
    public IMapEmailManager(Account account){
        this.account = account;
    }

    @Override
    public void loadEmails(Folder parent) {  
        try {
            if(parent.getEmails().isEmpty()){
                Store store = IMapConnectionHelper.connect(account);
                if(store == null){
                    return;
                }
                javax.mail.Folder mailFolder = store.getFolder(parent.getPath());
                if(mailFolder.exists()){
                    int type = mailFolder.getType();
                    //type == 1 HOLDS_MESSAGES
                    //type == 3 HOLDS_MESSAGES and HOLDS_FOLDERS
                    if(type == 1 || type == 3){
                        mailFolder.open(javax.mail.Folder.READ_ONLY);
                        for(Message message : mailFolder.getMessages()){
                            System.out.println("email detacted");
                            Email mail = IMapEmailConverter.convertMessage(message);
                            parent.addEmail(mail);
                        }   
                        mailFolder.close(true);
                    }
                }
                store.close();
            } 
        }catch (MessagingException ex) {
            Logger.getLogger(IMapEmailManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void saveEmails(File file, Folder selectedFolder) {
        if(file != null){ 
            try{
                JAXBContext context = JAXBContext.newInstance(Email.class);
                Marshaller m = context.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                //countMails ensures that each saved email has a different name
                int countMails = 1;
                for(Email email: selectedFolder.getEmails()){ 
                    //save email
                    m.marshal(email, new File(file.getPath() + "/mail" + countMails + ".xml")); 
                    countMails++;
                }
                System.out.println("Writing emails done!");
            }catch(PropertyException ex){
                System.err.println("PropertyException: " + ex.getMessage());
            }catch(JAXBException ex){
                System.err.println("JAXBException:" + ex.getMessage());
            }
        }
    }
}
