/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bht.fpa.mail.s736580s791788.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import de.bht.fpa.mail.s736580s791788.model.Component;
import de.bht.fpa.mail.s736580s791788.model.Email;
import de.bht.fpa.mail.s736580s791788.model.Folder;
import de.bht.fpa.mail.s736580s791788.model.applicationLogic.EmailManagerIF;
import de.bht.fpa.mail.s736580s791788.model.applicationLogic.FileManager;
import de.bht.fpa.mail.s736580s791788.model.applicationLogic.FolderManagerIF;
import de.bht.fpa.mail.s736580s791788.model.applicationLogic.XMLEmailManager;


/**
 *
 * @author peteralbrecht
 */
public class FXMLController implements Initializable {
    
    @FXML
    private TreeView tree;
    
    @FXML
    private MenuItem open;
    
    private FolderManagerIF manager;
    private EmailManagerIF emailManager;
    
    public FXMLController(){
        String pathRoot = System.getProperty("user.home");
        File fileRoot = new File(pathRoot);
        manager = new FileManager(fileRoot);
        emailManager = new XMLEmailManager();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureMenue();
        createTree(true);
    }    
    
    public void createTree(boolean addListener){
        final Folder home = manager.getTopFolder();
        manager.loadContent(home);
        
        TreeItem<Component> root = new TreeItem<>(home);
        String path = "/de/bht/fpa/mail/s736580s791788/resources/openFolder.png";
        root.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(path))));
        root.setExpanded(true);
        updateTree(root, home);
        
        //manage expand event
        root.addEventHandler(TreeItem.<Component>branchExpandedEvent(), (TreeModificationEvent<Component> event) -> {
            TreeItem<Component> eventNode = event.getTreeItem();
            eventNode.getChildren().clear();
            eventNode.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(path))));
            Folder parent = (Folder) eventNode.getValue();
            manager.loadContent(parent);
            updateTree(eventNode, parent);   
        }); 
        
        //manage the change of the icon for a collapse event
        root.addEventHandler(TreeItem.<Component>branchCollapsedEvent(), (TreeModificationEvent<Component> event) -> {
            TreeItem<Component> eventNode = event.getTreeItem();
            String pathCollapsed = "/de/bht/fpa/mail/s736580s791788/resources/folder.png";
            eventNode.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(pathCollapsed))));
        });
        
        //manage the focus of TreeItems
        ChangeListener listener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            if(newValue != null){
                TreeItem<Component> treeItem = (TreeItem) newValue;
                Folder folder = (Folder)treeItem.getValue();
                emailManager.loadEmails(folder);
                System.out.println("Selected directory: " + folder.getPath());
                System.out.println("Number of emails: " + folder.getEmails().size());
                for(Email email: folder.getEmails()){
                    System.out.println(email);
                }   
                System.out.println();
            }    
        };
        
        //addListener ensures that only one ChangeListener is assigned to tree
        if(addListener){
            tree.getSelectionModel().selectedItemProperty().addListener(listener);
        }
        tree.setRoot(root); 
    }
    
    //transform Folder-hierarchy in TreeItem-hierarchy
    public void updateTree(TreeItem<Component> root, Component parent){
        if(root.getChildren().isEmpty()){
            for(Component child: parent.getComponents()){
                TreeItem<Component> itemChild = new TreeItem<>(child);
                itemChild.setExpanded(false);     
                
                //assing icon to directory
                String path = "/de/bht/fpa/mail/s736580s791788/resources/folder.png";
                itemChild.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(path))));   
                root.getChildren().add(itemChild);
                
                if(child.isExpandable()){
                    itemChild.getChildren().add(new TreeItem(null)); //temporary added
                }
            }
        }
    }
       
    public void configureMenue(){  
        
        //EventHandler for all MenuItem's
        EventHandler handler = (EventHandler<ActionEvent>) (ActionEvent event) -> {
            MenuItem menuItem = (MenuItem) event.getSource();
            String context = menuItem.getText();
            
            //handle choosing a new directory
            if(context.equals("Open")){
                DirectoryChooser chooser = new DirectoryChooser();
                File newRoot = chooser.showDialog(null);
                
                //if canceled skip
                if(newRoot != null){
                    manager = new FileManager(newRoot);
                    createTree(false);
                }
            }
        };
        open.setOnAction(handler);   
    }
}