/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bht.fpa.mail.s736580s791788.model;

import java.io.File;
import java.util.List;

/*
 * This is the component part of a composite pattern.
 * 
 * @author Simone Strippgen
 */


public abstract class Component {
    // absolute directory path to this component
    private String path;
    // name of the component (without path)
    private String name;

    public Component(File path) {
        this.path = path.getAbsolutePath();
        this.name = path.getName();
    }

    public void addComponent(Component comp) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public List<Component> getComponents() {
        throw new UnsupportedOperationException("Not supported.");
    }
    
    // @return is the component expandable
    public abstract boolean isExpandable();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String p) {
        path = p;
    }

    public String getPath() {
        return path;
    }

    public String toString() {
        return name;
    }
}
