/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bht.fpa.mail.s736580s791788.model;

import java.io.File;
import java.util.List;

/*
 * This is the leaf part of a composite pattern.
 * 
 * @author Simone Strippgen
 */

public class FileElement extends Component {

    public FileElement(File path) {
        super(path);
    }

    @Override
    public void addComponent(Component comp) {
       System.out.println("addComponentToFileError");
    }

    @Override
    public List<Component> getComponents() {
      return null;
    }
    
    @Override
    public boolean isExpandable() {
        return false;
    }
    
}
