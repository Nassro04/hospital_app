module com.example.hospital_system {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires transitive javafx.base;
    requires javafx.graphics;

    opens com.example.hospital_system.Controllers to javafx.fxml;

    exports com.example.hospital_system.Controllers;

    opens com.example.hospital_system to javafx.fxml;

    exports com.example.hospital_system;

    opens com.example.hospital_system.Models to javafx.base;

    exports com.example.hospital_system.Models;

}