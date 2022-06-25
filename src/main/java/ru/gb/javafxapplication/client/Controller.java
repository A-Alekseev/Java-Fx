package ru.gb.javafxapplication.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class Controller {

    final NetChatClient client;
    @FXML
    private HBox authBox;
    @FXML
    private HBox sendBox;
    @FXML
    private TextField loginField;
    @FXML
    private TextField passField;

    @FXML
    private TextArea historyArea;

    @FXML
    public TextField userMessageField;

    public Controller() {
        client = new NetChatClient(this);
        while (true){
            try {
                client.openConnection();
                break;
            } catch (IOException e) {
                showNotification(e.getMessage());
            }
        }
    }

    private void showNotification(String message) {
        final Alert alert = new Alert(Alert.AlertType.ERROR,
                message,
                new ButtonType("Try again", ButtonBar.ButtonData.OK_DONE),
                new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE)
                );
        alert.setTitle("Connection error");
        if(alert.showAndWait().map( select->select.getButtonData().isCancelButton()).orElse(false) ){
            System.exit(0);
        }
    }

    public void clickSendButton(ActionEvent actionEvent) {
        String userMessage = userMessageField.getText();
        if (userMessage == null || userMessage.length() == 0)
            return;

        client.sendMessage(userMessage);
        userMessageField.clear();
        userMessageField.requestFocus();
    }

    public void addMessage(String message) {
        historyArea.appendText(message + "\n");
    }

    public void clickAuthButton(ActionEvent actionEvent) {
        client.sendMessage("/auth "+ loginField.getText() + " " + passField.getText());
    }

    public void setAuth(boolean success){
        authBox.setVisible(!success);
        sendBox.setVisible(success);
    }

    public void shutdown(){
        client.sendMessage(NetChatClient.END_MESSAGE);
    }
}
