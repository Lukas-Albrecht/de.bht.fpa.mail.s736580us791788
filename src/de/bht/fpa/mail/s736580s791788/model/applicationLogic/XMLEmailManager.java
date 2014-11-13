/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bht.fpa.mail.s736580s791788.model.applicationLogic;

import de.bht.fpa.mail.s736580s791788.model.Email;
import de.bht.fpa.mail.s736580s791788.model.Folder;
import java.io.File;
import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;

/**
 *
 * @author peteralbrecht
 */
public class XMLEmailManager implements EmailManagerIF{

    @Override
    public void loadEmails(Folder folder) {
        
         //load emails only one time
        if(folder.getEmails().isEmpty()){
            File file = new File(folder.getPath());
            
            //each .xml-File --> Email-Object
            for(File child : file.listFiles((File pathname) -> {
                String path = pathname.getPath();
                return path.endsWith(".xml");
                })){
                try{
                    Email mail = JAXB.unmarshal(child, Email.class);
                    folder.addEmail(mail);
                }catch(DataBindingException e){     
                    System.out.println("Not an email: " + e.toString());
                }
            }
        }
    } 
}
