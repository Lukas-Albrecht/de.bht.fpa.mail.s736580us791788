
package de.bht.fpa.mail.s736580s791788.model.applicationData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 *
 * @author Simone Strippgen
 *
 */

@Entity
public class Folder extends Component implements Serializable{
    
    private boolean expandable;
    private boolean emailsLoaded;
    
    //TRANSIENT for serialization and @TRANSIENT for JDBC
    @Transient
    private transient final ArrayList<Component> content;
    @Transient
    private transient final List<Email> emails;
    
    //default constructor for JDBC
    public Folder() {
        expandable = false;
        content = new ArrayList();
        emails = new ArrayList();
    }

    public Folder(File path, boolean expandable) {
        super(path);
        this.expandable = expandable;
        this.emailsLoaded = false;
        content = new ArrayList<>();
        emails = new ArrayList<>();
    }

    @Override
    public boolean isExpandable() {
        return expandable;
    }

    @Override
    public void addComponent(Component comp) {
        content.add(comp);
    }

    @Override
    public List<Component> getComponents() {
        return content;
    }

    public List<Email> getEmails() {
        emailsLoaded = true;
        return emails;
    }

    public void addEmail(Email message) {
        emails.add(message);
    }
    
    @Override
    public String toString() {
        if(emailsLoaded){
           return super.toString() +  " (" + emails.size() + ")";
        }
        return super.toString();
    }
 }
