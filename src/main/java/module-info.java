module ch.rootlogin.rezeptverwaltung {
    requires java.annotation;
    requires java.logging;
    requires java.sql;

    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;

    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;

    requires spring.orm;
    requires java.persistence;
    requires spring.data.commons;

    opens ch.rootlogin.rezeptverwaltung to spring.core, spring.beans, spring.context, javafx.graphics;

    opens ch.rootlogin.rezeptverwaltung.gui.controller to spring.core, spring.beans, javafx.fxml;
    opens ch.rootlogin.rezeptverwaltung.gui.preloader to javafx.graphics;
}
