
package de.bht.fpa.mail.s736580s791788.model.applicationLogic;

import de.bht.fpa.mail.s736580s791788.model.applicationData.Email;
import de.bht.fpa.mail.s736580s791788.model.applicationData.Folder;
import java.io.File;
import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

/**
 *
 * @author Peter Albrecht, Lukas Albrecht
 */
public class XMLEmailManager implements EmailManagerIF{
    
    //the Folder which contains the emails for the save-operation
    private Folder selectedFolder;
    
    //load emails from selected folder
    @Override
    public void loadEmails(Folder folder) {
        /*
        the currently in the TreeView selected TreeItem-Value, 
        necessary for the saveEmails(...)-method
        */
        this.selectedFolder = folder;
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
    
    //save emails into user-selected directory
    @Override
    public void saveEmails(File file) {
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
