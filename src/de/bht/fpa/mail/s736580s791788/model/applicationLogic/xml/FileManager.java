
/*
 * This class manages a hierarchy of folders and their content which is loaded 
 * from a given directory path.
 * 
 * @author Simone Strippgen
 */

package de.bht.fpa.mail.s736580s791788.model.applicationLogic.xml;

import de.bht.fpa.mail.s736580s791788.model.applicationData.Folder;
import de.bht.fpa.mail.s736580s791788.model.applicationLogic.FolderManagerIF;
import java.io.File;
import java.io.FileFilter;

public class FileManager implements FolderManagerIF{

    //top Folder of the managed hierarchy
    Folder topFolder;

    /**
     * Constructs a new FileManager object which manages a folder hierarchy, 
     * where file contains the path to the top directory. 
     * The contents of the  directory file are loaded into the top folder
     * @param file File which points to the top directory
     */
    public FileManager(File file) {
        topFolder = new Folder(file, true);
    }
    
    /**
     * Loads all relevant content in the directory path of a folder
     * object into the folder.
     * @param parent the folder into which the content of the corresponding 
     *          directory should be loaded
     */
    @Override
    public void loadContent(Folder parent) { 
        ////if data loaded don't do it again 
        if(parent.getComponents().isEmpty()){         
            File root = new File(parent.getPath());
            for(File child: root.listFiles()){
                if(child.isDirectory()){  
                    //check if directory is expandable
                    FileFilter filter = (File path) -> path.isDirectory();
                    if(child.listFiles() != null && child.listFiles(filter).length == 0){
                        Folder folder = new Folder(child, false);
                        parent.addComponent(folder);
                    } else {
                        Folder folder = new Folder(child, true);
                        parent.addComponent(folder);
                    }
                }
            }
        }
    }

    @Override
    public Folder getTopFolder() {
        return topFolder;
    }
}
