/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bht.fpa.mail.s736580s791788.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author peteralbrecht
 */
public class Main extends Application {    
 
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/de/bht/fpa/mail/s736580s791788/view/FXML.fxml")); 
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("FPA Mailer");
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
