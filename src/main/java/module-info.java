module ru.gb.javafxapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    exports ru.gb.javafxapplication.client;
    opens ru.gb.javafxapplication.client to javafx.fxml;
}