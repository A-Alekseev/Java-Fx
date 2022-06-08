module ru.gb.javafxapplication {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.gb.javafxapplication to javafx.fxml;
    exports ru.gb.javafxapplication;
}