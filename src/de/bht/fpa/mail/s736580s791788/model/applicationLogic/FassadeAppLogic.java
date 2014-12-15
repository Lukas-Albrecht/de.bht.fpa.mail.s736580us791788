/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bht.fpa.mail.s736580s791788.model.applicationLogic;

import de.bht.fpa.mail.s736580s791788.model.applicationData.Folder;
import java.io.File;

/**
 *
 * @author Peter Albrecht, Lukas Albrecht
 */
public class FassadeAppLogic implements ApplicationLogicIF{
    
    private FolderManagerIF fileManager;
    private final EmailManagerIF emailManager;
    
    public FassadeAppLogic(File file){
        fileManager = new FileManager(file);
        emailManager = new XMLEmailManager();
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
    }

    @Override
    public void changeDirectory(File file) {
        fileManager = new FileManager(file);
    }

    @Override
    public void saveEmails(File file) {
        emailManager.saveEmails(file);
    }   
}
