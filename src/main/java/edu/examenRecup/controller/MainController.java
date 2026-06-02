package edu.examenRecup.controller;

import edu.examenRecup.dao.UserDAO;
import edu.examenRecup.model.Role;
import edu.examenRecup.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class MainController {
    @FXML private Label welcomeLabel;
    @FXML private FlowPane cardsContainer;
    @FXML private VBox adminPanel;

    @FXML private TextField idField;
    @FXML private TextField nombreField;
    @FXML private TextField nicknameField;
    @FXML private TextField emailField;
    @FXML private TextField edadField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<Role> roleComboBox;
    @FXML private Label adminMesageLabel;

    private User currentUser;
    private UserDAO userDAO = new UserDAO();

    @FXML public void initialize() {
        roleComboBox.getItems().addAll(Role.values());
    }
    public void initData(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome," + user.getNombre() + " (" + user.getRole().name() + ")");

        if (user.getRole() == Role.admin){
            adminPanel.setVisible(true);
            adminPanel.setManaged(true);
        } else {
            adminPanel.setVisible(false);
            adminPanel.setManaged(false);
        }
        loadUserCards();
    }
    private void loadUserCards() {
        cardsContainer.getChildren().clear();
        List<User> users = userDAO.getAllUsers();

        if (users.isEmpty()) return;

        for (User user : users) {
            boolean isAdmin = user.getRole() == Role.admin;
            VBox card = createUserCard(user, isAdmin);
            cardsContainer.getChildren().add(card);
        }
    }

    private VBox createUserCard(User user, boolean isAdmin) {
        VBox card = new VBox();
        card.setSpacing(8);
        card.setPadding(new Insets(15));
        card.setPrefWidth(240);
        card.setMinHeight(150);

        String baseStyle = "-fx-background-radius: 5px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 8, 0, 0, 2);";

        if(isAdmin){
            card.setStyle(baseStyle + "-fx-background-color: #FCE13A;");
        } else {
            card.setStyle(baseStyle + "-fx-background-color: #ffffff;");
        }
        if (currentUser.getRole() == Role.admin) {
            card.setStyle(card.getStyle() + "-fx-cursor: hand;");
            card.setOnMouseClicked(e -> populateAdminForm(user));
        }
        Label nameLbl = new Label(user.getNombre());
        nameLbl.setStyle(baseStyle + "-fx-font-weight: bold;");

        Label nicknameLbl = new Label(user.getNickname());
        nicknameLbl.setStyle(baseStyle + "-fx-font-weight: bold;");

        Separator separator = new Separator();
        separator.setStyle(" -fx-background-color: #444444; -fx-padding: 0;");

        Label emailLbl = new Label(user.getEmail());
        emailLbl.setStyle(baseStyle + "-fx-font-weight: bold;");

        Label edadLbl = new Label("Edad: " + user.getEdad());
        edadLbl.setStyle(baseStyle + "-fx-font-weight: bold;");

        Label roleLbl = new Label("Role: " + user.getRole().name());
        if (user.getRole() == Role.admin) {
            roleLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");
        } else {
            roleLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: green;");
        }
        card.getChildren().addAll(nameLbl,nicknameLbl, separator, emailLbl, edadLbl, roleLbl);
        return card;
    }

    private void populateAdminForm(User user) {
        idField.setText(String.valueOf(user.getId()));
        nombreField.setText(user.getNombre());
        nicknameField.setText(user.getNickname());
        emailField.setText(user.getEmail());
        edadField.setText(String.valueOf(user.getEdad()));
        passwordField.setText(user.getPassword());
        roleComboBox.setValue(user.getRole());
        adminMesageLabel.setText("");
    }

    @FXML
    private void clearForm() {
        idField.clear();
        nombreField.clear();
        nicknameField.clear();
        emailField.clear();
        edadField.clear();
        passwordField.clear();
        roleComboBox.setValue(null);
        adminMesageLabel.setText("");
    }

    private boolean validateForm() {
        if (nombreField.getText().isBlank() || nicknameField.getText().isBlank() || emailField.getText().isBlank() || edadField.getText().isBlank() || passwordField.getText().isBlank() || roleComboBox.getValue() == null) {
            setAdminMessage("All fields are required.", true);
            return false;
        }
        try {
            Integer.parseInt(edadField.getText().trim());
        } catch (NumberFormatException e) {
            setAdminMessage("Age must be a number.", true);
            return false;
        }
        return true;
    }

    private void setAdminMessage(String message, boolean isError) {
        adminMesageLabel.setText(message);
        if(isError) {
            adminMesageLabel.setStyle("-fx-text-fill: red;");
        } else {
            adminMesageLabel.setStyle("-fx-text-fill: green;");
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/edu/examenRecup/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("User Login");
            stage.show();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        if (!validateForm()) return;

        User user = new User(0, nombreField.getText().trim(), nicknameField.getText().trim(), emailField.getText().trim(), Integer.parseInt(edadField.getText().trim()), passwordField.getText().trim(), roleComboBox.getValue());
        if (userDAO.createUser(user)) {
            setAdminMessage("User created.", false);
            loadUserCards();
            clearForm();
        } else {
            setAdminMessage("User creation failed.", true);
        }
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        if (idField.getText().isBlank()) {
            setAdminMessage("Please select a user to update.", true);
            return;
        }
        if (!validateForm()) return;

        User user = new User(Integer.parseInt(idField.getText()), nombreField.getText().trim(), nicknameField.getText().trim(), emailField.getText().trim(), Integer.parseInt(edadField.getText().trim()), passwordField.getText().trim(), roleComboBox.getValue())
        if (UserDAO.updateUser(user)){
            setAdminMessage("User successfully updated.", false);
            loadUserCards();
            if(currentUser.getId() )
        }
    }


}
