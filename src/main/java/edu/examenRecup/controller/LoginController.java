package edu.examenRecup.controller;

import edu.examenRecup.dao.UserDAO;
import edu.examenRecup.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;


public class LoginController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            errorLabel.setText("Invalid email or password");
            return;
        }

        User user = userDAO.authenticate(email, password);

       if (user != null) {
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/examenRecup/main.fxml"));

                Parent root = loader.load();
                MainController mainController = loader.getController();
                mainController.initData(user);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root, 900, 600));
                stage.setTitle("User Management");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                errorLabel.setText("Error loading main view");
            }

           } else {errorLabel.setText("Invalid email or password");}
        }
    }
