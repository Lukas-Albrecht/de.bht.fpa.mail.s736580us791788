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
public interface EmailManagerIF {
    
    void loadEmails(Folder f); 
    
    void saveEmails(File file, Folder selectedFolder);
}
