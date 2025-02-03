module TheMindGame {
    requires javafx.controls;
    requires javafx.fxml;
	requires java.desktop;
	requires javafx.graphics;
    exports client;
    opens client to javafx.fxml;
}
