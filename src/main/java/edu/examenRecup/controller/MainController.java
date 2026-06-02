package edu.examenRecup.controller;

import edu.examenRecup.dao.UserDAO;
import edu.examenRecup.model.Role;
import edu.examenRecup.model.User;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

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

        String baseStyle = "-fx-background-radius: 5px; -fx-effect: dropshadow(three-pass-box); rgba(0,0,0,0.15), 8, 0, 0, 2);"

        if(isAdmin){
            card.setStyle()
        }

        return card;
    }
}
