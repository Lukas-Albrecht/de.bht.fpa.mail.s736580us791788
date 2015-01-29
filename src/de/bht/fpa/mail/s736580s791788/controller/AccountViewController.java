/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bht.fpa.mail.s736580s791788.controller;

import de.bht.fpa.mail.s736580s791788.model.applicationData.Account;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author Peter Albrecht, Lukas Albrecht
 */
public class AccountViewController implements Initializable{
    
    @FXML
    private TextField nameText;
    
    @FXML
    private TextField hostText;
    
    @FXML
    private TextField usernameText;
    
    @FXML
    private TextField passwordText;
    
    @FXML
    private Button cancelButton;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Label errorLabel;
    
    private final FXMLController controller;
    
    private final int type;

    public AccountViewController(FXMLController controller, int type){
        this.controller = controller;
        this.type = type;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cancelButton.addEventHandler((ActionEvent.ACTION), (EventHandler<ActionEvent>)
                (ActionEvent e) -> closeWindow());
        if(type == FXMLController.CREATE_ACCOUNT){
            saveButton.addEventHandler(ActionEvent.ACTION, 
                    (EventHandler<ActionEvent>) (ActionEvent e) -> saveNewAccount());
        }else{
            saveButton.addEventHandler(ActionEvent.ACTION,
                    (EventHandler<ActionEvent>) (ActionEvent e) -> updateAccount());
        }
    }
    
    public void saveNewAccount(){
        String name = nameText.getText();
        String host = hostText.getText();
        String username = usernameText.getText();
        String password = passwordText.getText();
        if(name.isEmpty() || host.isEmpty() || username.isEmpty() || password.isEmpty()){
            errorLabel.setText("All textfields must contain data!");
        }else{
            Account account = new Account(name, host, username, password);
            if(controller.saveAccount(account)){
                closeWindow();
            }
            errorLabel.setText("Name: " + name + " allready exists!");
        }
    }
    
    public void updateAccount(){
        String name = nameText.getText();
        String host = hostText.getText();
        String username = usernameText.getText();
        String password = passwordText.getText();
        if(name.isEmpty() || host.isEmpty() || username.isEmpty() || password.isEmpty()){
            errorLabel.setText("All textfields must contain data!");
        }else{
            Account account = new Account(name, host, username, password);
            controller.updateAccount(account);
            closeWindow();
        }
    }
    
    //close window
    public void closeWindow(){
        Stage window = (Stage) saveButton.getScene().getWindow();
        window.close();
    }
    
    //prepare and style window with account-data 
    public void configureEditWindow(Account account){
        nameText.setDisable(true);
        nameText.setText(account.getName());
        hostText.setText(account.getHost());
        usernameText.setText(account.getUsername());
        passwordText.setText(account.getPassword());
        
        saveButton.setText("Update");
        Stage window = (Stage) saveButton.getScene().getWindow();
        window.setTitle("Update Account");
    }
    
    //style window for creating accounts
    public void configreCreateWindow(){
        nameText.setDisable(false);
        saveButton.setText("Save");
        Stage window = (Stage) saveButton.getScene().getWindow();
        window.setTitle("New Account");
    }
}
