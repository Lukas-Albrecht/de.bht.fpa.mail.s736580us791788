
package de.bht.fpa.mail.s736580s791788.controller;

import de.bht.fpa.mail.s736580s791788.model.applicationData.Account;
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
import de.bht.fpa.mail.s736580s791788.model.applicationData.Component;
import de.bht.fpa.mail.s736580s791788.model.applicationData.Email;
import de.bht.fpa.mail.s736580s791788.model.applicationData.Folder;
import de.bht.fpa.mail.s736580s791788.model.applicationLogic.ApplicationLogicIF;
import de.bht.fpa.mail.s736580s791788.model.applicationLogic.FassadeAppLogic;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 *
 * @author Lukas Albrecht, Peter Albrecht
 */
public class FXMLController implements Initializable { 
    //TreeView
    @FXML
    private TreeView tree;
    @FXML
    //Menu
    private MenuBar menuBar; 
    @FXML
    //TableView
    private TableView table; 
    @FXML
    private TableColumn receivedDate;
    //TextArea
    @FXML
    private Label senderLabel;
    @FXML 
    private Label subjectLabel;
    @FXML
    private Label receivedLabel;
    @FXML
    private Label receiverLabel;
    @FXML
    private TextArea textArea;
    @FXML
    private Menu openAccountMenu;
    @FXML
    private Menu editAccountMenu;  
    //application logic
    private final ApplicationLogicIF fassade;
    //data for table
    private final ObservableList<Email> tableData;
    public final static int EDIT_ACCOUNT = 0;
    public final static int CREATE_ACCOUNT = 1;
    
    public FXMLController(){
        String pathRoot = System.getProperty("user.home");
        File fileRoot = new File(pathRoot);
        this.fassade = new FassadeAppLogic(fileRoot);
        this.tableData = FXCollections.observableArrayList();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        createTree(true);
        configureMenue();
        configureTable();
    }    
    
    public void createTree(boolean addListener){
        final Folder topFolder = fassade.getTopFolder();
         //configure root
        TreeItem<Component> root = new TreeItem<>(topFolder); 
        String path = "/de/bht/fpa/mail/s736580s791788/resources/openFolder.png";
        root.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(path))));
        root.setExpanded(true);
        //manage expand event
        root.addEventHandler(TreeItem.<Component>branchExpandedEvent(), (TreeModificationEvent<Component> event) -> 
            handleExpandEvent(event)
        ); 
        //manage the change of the icon for a collapse event
        root.addEventHandler(TreeItem.<Component>branchCollapsedEvent(), (TreeModificationEvent<Component> event) ->
            handleCollapsedEvent(event)
        );
        //manage the focus of TreeItems
        ChangeListener listener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> 
            handleTreeItemFocus(newValue); 
        //addListener ensures that only one ChangeListener is assigned to tree
        if(addListener){
            tree.getSelectionModel().selectedItemProperty().addListener(listener);
        }
        //load and update
        fassade.loadContent(topFolder);
        updateTree(root, topFolder); 
        //assign root to tree
        tree.setRoot(root);
    }
    
    //handle the expand of TreeItems
    public void handleExpandEvent(TreeModificationEvent<Component> event){
        TreeItem<Component> eventNode = event.getTreeItem();
        Folder parent = (Folder) eventNode.getValue();
        
        //delete and update only if a null-TreeTtem appears
        //-->load TreeItems only one time
        if(eventNode.getChildren().get(0) == null){
            eventNode.getChildren().clear();
            fassade.loadContent(parent);
            updateTree(eventNode, parent);
        }
        String path = "/de/bht/fpa/mail/s736580s791788/resources/openFolder.png";
        eventNode.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(path))));
    }
    
    //handle the collapse of TreeItems
    public void handleCollapsedEvent(TreeModificationEvent<Component> event){
        TreeItem<Component> eventNode = event.getTreeItem();
        String pathCollapsed = "/de/bht/fpa/mail/s736580s791788/resources/folder.png";
        eventNode.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(pathCollapsed))));
    }
    
    //handle the selection of TreeItems
    public void handleTreeItemFocus(Object newValue){
        resetTableInfo();
        if(newValue != null){
            TreeItem<Component> treeItem = (TreeItem) newValue;
            Folder folder = (Folder)treeItem.getValue();
            fassade.loadEmails(folder);
            System.out.println("Selected directory: " + folder.getPath());
            System.out.println("Number of emails: " + folder.getEmails().size());
            for(Email email : folder.getEmails()){
                System.out.println(email);
                tableData.add(email);
            }
            System.out.println();
            //congure sort of the TableView
            table.setItems(tableData);
            receivedDate.setSortType(TableColumn.SortType.DESCENDING);
            table.getSortOrder().add(receivedDate);
            showEmailNumberInTreeView(folder);
        }    
    }
    
    //clear the table and the email-description
    public void resetTableInfo(){
        tableData.clear();
        senderLabel.setText("");
        subjectLabel.setText("");
        receivedLabel.setText("");
        receiverLabel.setText("");
        textArea.setText("");
    }
    
    public void showEmailNumberInTreeView(Folder folder){
        tree.setShowRoot(false);
        tree.setShowRoot(true);
    }
    
    public void configureMenue(){     
        for(String name: fassade.getAllAccounts()){
            this.openAccountMenu.getItems().add(new MenuItem(name));
            this.editAccountMenu.getItems().add(new MenuItem(name));
        }
        
        //recursive variant:
        for(Menu menu: menuBar.getMenus()){
            addEventHandlerToAllMenuItems(menu);
        }
    }
    
    public void addEventHandlerToAllMenuItems(Menu menu){
        //EventHandler for all MenuItem's
        EventHandler handler = (EventHandler<ActionEvent>) (ActionEvent event) 
                -> handleMenuItemEvent(event);
        
        for(MenuItem child: menu.getItems()){
            if(child instanceof Menu){
                addEventHandlerToAllMenuItems((Menu)child);
                continue;
            }
            child.setOnAction(handler);
        }
    }
    
    //handle the click on a MenuItem
    public void handleMenuItemEvent(ActionEvent event){
        MenuItem menuItem = (MenuItem) event.getSource();
        String description = menuItem.getText();
        
        //handle choosing a new directory
        if(description.equals("Open")){
            activateOpen();
            return;
        }
        
        if(description.equals("Save")){
            activateSave();
            return;
        }
        
        if(description.equals("New Account")){
            AccountViewController accContr = new AccountViewController(this, CREATE_ACCOUNT);
            activateCreateAccount(accContr);
            accContr.configreCreateWindow();
            return;
        }
        
        Menu parentMenu = menuItem.getParentMenu();
        if(parentMenu.getText().equals("Open Account")){
            fassade.openAccount(description);
            createTree(false);
            return;
        }
        
        if(parentMenu.getText().equals("Edit Account")){
            Account account = fassade.getAccount(description);
            AccountViewController accContr = new AccountViewController(this, EDIT_ACCOUNT);
            activateCreateAccount(accContr);
            accContr.configureEditWindow(account);
        }
    }
    
    //is called by handleMenuItemEvent()
    public void activateOpen(){
        DirectoryChooser chooser = new DirectoryChooser();
        File newRoot = chooser.showDialog(null);
                
        //if canceled skip
        if(newRoot != null){ 
            //manager = new FileManager(newRoot);
            fassade.changeDirectory(newRoot);
            createTree(false);
        }
    }
    
    //is called by handleMenuItemEvent()
    public void activateSave(){
        DirectoryChooser chooser = new DirectoryChooser();
        File newDirectory = chooser.showDialog(null);
        fassade.saveEmails(newDirectory);
    }
    
    //is called by handleMenuItemEvent()
    public void activateCreateAccount(AccountViewController accContr){
        Stage newStage = new Stage();
        String path = "/de/bht/fpa/mail/s736580s791788/view/FXMLAccount.fxml";
        URL location = getClass().getResource(path);
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        fxmlLoader.setController(accContr);
        try {
            Scene accountScene = new Scene(fxmlLoader.load());
            newStage.setScene(accountScene);
            newStage.show();
        } catch (IOException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //prepare TableView
    public void configureTable(){
        ObservableList<TableColumn> columns = table.getColumns();
        for(TableColumn column : columns){
            String description = column.getText();
            //defines in which column what property of Email should be placed
            switch(description){
                case "Importance":
                    column.setCellValueFactory(new PropertyValueFactory<>("importance"));
                    break;
                case "Received":
                    column.setCellValueFactory(new PropertyValueFactory<>("received"));
                    //defines how to compare the date-values
                    column.setComparator((Comparator<String>)(String t, String t1) -> {
                        return compareDate(t, t1);    
                    });
                    break;
                case "Read":
                    column.setCellValueFactory(new PropertyValueFactory<>("read"));
                    break;
                case "Sender":
                    column.setCellValueFactory(new PropertyValueFactory<>("sender"));
                    break;
                case "Recipients":
                    column.setCellValueFactory(new PropertyValueFactory<>("receiverTo"));
                    break;
                case "Subject":
                    column.setCellValueFactory(new PropertyValueFactory<>("subject"));
                    break;
                default:
                    System.out.println("Column is not existing");
            }
        }
        table.setPlaceholder(new Label("No emails selected"));
        //adding ListChangeListener for TableView-Elements
        table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Email>) 
                (ListChangeListener.Change<? extends Email> c) -> displayEmailDataOnFocus());   
    }
    
    //comparing two dates by parsing it into the right DateFormat
    public int compareDate(String t, String t1){
        try{
            DateFormat format = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT, Locale.GERMANY);
            Date d = format.parse(t);
            Date d1 = format.parse(t1);
            return Long.compare(d.getTime(),d1.getTime());
        }catch(ParseException p){
            p.getMessage();
        }
        return -1;
    }
    
    //show email-description
    public void displayEmailDataOnFocus(){
        Email email = (Email) table.getSelectionModel().getSelectedItem();
        if(email != null){
            senderLabel.setText(email.getSender());
            subjectLabel.setText(email.getSubject());
            receivedLabel.setText(email.getReceived());
            receiverLabel.setText(email.getReceiver());
            textArea.setText(email.getText());
        }
    }
    
    //transform Folder-hierarchy in TreeItem-hierarchy
    public void updateTree(TreeItem<Component> root, Component parent){
        for(Component child: parent.getComponents()){
            TreeItem<Component> itemChild = new TreeItem<>(child);
            itemChild.setExpanded(false);     
                
            //assing icon to directory
            String path = "/de/bht/fpa/mail/s736580s791788/resources/folder.png";
            itemChild.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(path))));   
            root.getChildren().add(itemChild);
                
            if(child.isExpandable()){
                itemChild.getChildren().add(null); //temporary added
            }
        }
    }
    
    public boolean saveAccount(Account account){        
        boolean saved = fassade.saveAccount(account);
        if(saved){
            //add one different MenuItem for each Menu
            MenuItem item1 = new MenuItem(account.getName());
            MenuItem item2 = new MenuItem(account.getName());
            openAccountMenu.getItems().add(item1);
            editAccountMenu.getItems().add(item2);
            
            //add EventHandler
            item1.addEventHandler(ActionEvent.ACTION, (EventHandler<ActionEvent>)
                    (ActionEvent e) -> handleMenuItemEvent(e));
            item2.addEventHandler(ActionEvent.ACTION, (EventHandler<ActionEvent>)
                    (ActionEvent e) -> handleMenuItemEvent(e));
        }
        return saved;
    }
    
    public void updateAccount(Account account){
        fassade.updateAccount(account);
    }
}