package ru.gb.javafxapplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Controller {

    @FXML
    private TextArea historyArea;

    @FXML
    public TextField userMessageField;

    public void clickSendButton(ActionEvent actionEvent) {
        String userMessage = userMessageField.getText();
        if (userMessage == null || userMessage.length() == 0)
            return;

        historyArea.setText(historyArea.getText() + "\r\n" + userMessage);
        userMessageField.setText("");
        userMessageField.requestFocus();
    }
}
