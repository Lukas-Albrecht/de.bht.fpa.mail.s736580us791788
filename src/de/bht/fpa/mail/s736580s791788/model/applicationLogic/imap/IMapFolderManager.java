/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bht.fpa.mail.s736580s791788.model.applicationLogic.imap;

import de.bht.fpa.mail.s736580s791788.model.applicationData.Account;
import de.bht.fpa.mail.s736580s791788.model.applicationData.Component;
import de.bht.fpa.mail.s736580s791788.model.applicationData.Folder;
import de.bht.fpa.mail.s736580s791788.model.applicationLogic.FolderManagerIF;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.Store;

/**
 *
 * @author Peter Albrecht, Lukas Albrecht
 */
public class IMapFolderManager implements FolderManagerIF {
    
    private Folder topFolder;
    private Account account;
    
    public IMapFolderManager(Account account){
        try {
            this.account = account;
            Store store = IMapConnectionHelper.connect(account);  
            if(store != null){
                javax.mail.Folder mailFolder = store.getDefaultFolder();
                topFolder = new Folder(new File(account.getName()), true);
                topFolder.setPath(mailFolder.getFullName());
                store.close();
            }else{
                topFolder = new Folder(new File(account.getName()), false);
            }
        } catch (MessagingException ex) {
            Logger.getLogger(IMapFolderManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Folder getTopFolder() {
        return topFolder;
    }

    @Override
    public void loadContent(Folder parent) {
        try {
            if(parent.getComponents().isEmpty()){ 
                Store store = IMapConnectionHelper.connect(account);
                if(store == null){
                    return;
                }
                javax.mail.Folder mailFolder = store.getFolder(parent.getPath());
                if(mailFolder.exists()){
                    for(javax.mail.Folder child : mailFolder.list()){
                        if(child.list().length == 0){
                            Folder folder = new Folder(new File(child.getName()), false);
                            folder.setPath(child.getFullName());
                            parent.addComponent(folder);
                        } else {
                            Folder folder = new Folder(new File(child.getName()), true);
                            folder.setPath(child.getFullName());
                            
                            if(child.getName().equals("[Gmail]")){
                                loadContent(folder);
                                for(Component subFolder : folder.getComponents()){
                                    parent.addComponent(subFolder);
                                }
                                continue;
                            }
                            parent.addComponent(folder);
                        }
                    }
                }
                store.close();
            }
        } catch (MessagingException ex) {
            Logger.getLogger(IMapFolderManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
}
