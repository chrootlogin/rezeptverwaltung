module ch.rootlogin.rezeptverwaltung {
    requires java.annotation;
    requires java.logging;
    requires java.sql;
    requires java.persistence;

    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.web;

    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.orm;
    requires spring.data.commons;

    requires flexmark;

    requires jtwig.core;

    opens ch.rootlogin.rezeptverwaltung to spring.core, spring.beans, spring.context, javafx.graphics;

    opens ch.rootlogin.rezeptverwaltung.gui.controller to spring.core, spring.beans, javafx.fxml;
    opens ch.rootlogin.rezeptverwaltung.gui.preloader to javafx.graphics;
}
