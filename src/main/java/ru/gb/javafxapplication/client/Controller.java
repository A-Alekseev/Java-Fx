package ru.gb.javafxapplication.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ru.gb.javafxapplication.common.Command;

import java.io.IOException;

public class Controller {
    @FXML
    public ListView<String> clientsListView;
    @FXML
    private Label targetUserNickLabel;
    @FXML
    private Button sendButton;
    @FXML
    private VBox authVBox;
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

    final NetChatClient client;

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
        String messageText = userMessageField.getText();
        if (messageText == null || messageText.length() == 0)
            return;

        String targetUserNick = targetUserNickLabel.getText();
        if ("all".equals(targetUserNick) ){
            client.sendMessage(Command.MESSAGE, messageText);
        }
        else {
            client.sendMessage(Command.PRIVATE_MESSAGE, targetUserNick, messageText);
        }
        userMessageField.clear();
        userMessageField.requestFocus();
    }

    public void addMessage(String message) {
        historyArea.appendText(message + "\n");
    }

    public void clickAuthButton(ActionEvent actionEvent) {
        client.authenicate(loginField.getText(), passField.getText());
    }

    public void setAuth(boolean success){
        authVBox.setVisible(!success);
        sendBox.setVisible(success);
    }

    public void shutdown(){
        client.sendMessage(Command.END);
    }

    public void addNotification(String s) {
        addMessage(s);
    }

    
    public void updateClients(String[] clients){
        clientsListView.getItems().clear();
        clientsListView.getItems().add("all");
        clientsListView.getItems().addAll(clients);

    }

    public void selectClient(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() >= 2){
            String selectedItem = clientsListView.getSelectionModel().getSelectedItem();
            targetUserNickLabel.setText(selectedItem);
        }
    }
}
