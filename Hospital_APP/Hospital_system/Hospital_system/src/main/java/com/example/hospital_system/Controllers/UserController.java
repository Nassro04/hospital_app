package com.example.hospital_system.Controllers;

import com.example.hospital_system.Models.User;
import com.example.hospital_system.SceneManager;
import com.example.hospital_system.Services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;

public class UserController {
    @FXML
    private TextField userNameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Text errorText;

    @FXML
    public void initialize() {
        userNameField.setOnAction(this::focusOnPasswordField);
        passwordField.setOnAction(event -> onSubmit(event));
        errorText.setText("");
    }

    @FXML
    private void focusOnPasswordField(ActionEvent event) {
        if (passwordField.getText().isEmpty()) {
            passwordField.requestFocus();
        }
    }

    UserService userService = new UserService();

    @FXML
    public void onSubmit(ActionEvent event) {
        String userName = userNameField.getText().trim();
        String password = passwordField.getText().trim();

        User user = userService.loginAsAdmin(userName, password);
        if (user != null) {
            try {
                SceneManager.changeScene("/com/example/hospital_system/View/Dashboard.fxml");
            } catch (IOException e) {
                System.out.println(e);
            }

        } else {
            errorText.setText("Ce compte n'existe pas");
        }

    }

}
