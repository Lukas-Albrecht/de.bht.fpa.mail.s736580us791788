/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bht.fpa.mail.s736580s791788.model.applicationLogic;

import de.bht.fpa.mail.s736580s791788.model.applicationLogic.xml.FileManager;
import de.bht.fpa.mail.s736580s791788.model.applicationLogic.xml.XMLEmailManager;
import de.bht.fpa.mail.s736580s791788.model.applicationLogic.imap.IMapFolderManager;
import de.bht.fpa.mail.s736580s791788.model.applicationLogic.imap.IMapEmailManager;
import java.io.File;
import java.util.List;
import de.bht.fpa.mail.s736580s791788.model.applicationData.Account;
import de.bht.fpa.mail.s736580s791788.model.applicationData.Email;
import de.bht.fpa.mail.s736580s791788.model.applicationData.Folder;
import de.bht.fpa.mail.s736580s791788.model.applicationLogic.account.AccountManager;
import de.bht.fpa.mail.s736580s791788.model.applicationLogic.account.AccountManagerIF;
import java.util.ArrayList;

/**
 *
 * @author Peter Albrecht, Lukas Albrecht
 */
public class FassadeAppLogic implements ApplicationLogicIF{
    
    private FolderManagerIF fileManager;
    private EmailManagerIF emailManager;
    private final AccountManagerIF accountManager;
    //the Folder which contains the emails for the save-operation
    private Folder selectedFolder;
    
    public FassadeAppLogic(File file){
        fileManager = new FileManager(file);
        emailManager = new XMLEmailManager();
        accountManager = new AccountManager();
    }

    @Override
    public Folder getTopFolder() {
        return fileManager.getTopFolder();
    }

    @Override
    public void loadContent(Folder folder) {
        fileManager.loadContent(folder);
    }

    @Override
    public void loadEmails(Folder folder) {
        emailManager.loadEmails(folder);
        this.selectedFolder = folder;
    }

    @Override
    public void changeDirectory(File file) {
        fileManager = new FileManager(file);
        emailManager = new XMLEmailManager();
    }

    @Override
    public void saveEmails(File file) {
        emailManager.saveEmails(file, this.selectedFolder);
    }   

    @Override
    public List<Email> search(String pattern) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void openAccount(String name) {
        Account account = accountManager.getAccount(name);
        this.fileManager = new IMapFolderManager(account);
        this.emailManager = new IMapEmailManager(account);
    }

    @Override
    public List<String> getAllAccounts() {
        List<String> list = new ArrayList();
        for(Account account: accountManager.getAllAccounts()){
            list.add(account.getName());
        }
        return list;
    }

    @Override
    public Account getAccount(String name) {
        return accountManager.getAccount(name);
    }

    @Override
    public boolean saveAccount(Account account) {
        return accountManager.saveAccount(account);
    }

    @Override
    public void updateAccount(Account account) {
        accountManager.updateAccount(account);
    }
}
