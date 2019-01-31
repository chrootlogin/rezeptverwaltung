/* This file is part of Rezeptverwaltung.
 *
 * Rezeptverwaltung is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Rezeptverwaltung is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Rezeptverwaltung.  If not, see <https://www.gnu.org/licenses/>.
 */
package ch.rootlogin.rezeptverwaltung.gui.preloader;

import ch.rootlogin.rezeptverwaltung.helper.Helper;
import ch.rootlogin.rezeptverwaltung.notification.PreloaderCloseNotification;
import ch.rootlogin.rezeptverwaltung.notification.PreloaderStatusNotification;

import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * This class shows a pretty preloader :D
 */
public class ApplicationPreloader extends Preloader {

    private Stage preloaderStage;
    private Label statusMessage = new Label();

    private final static int preloaderHeight = 300;
    private final static int preloaderWidth = 500;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.preloaderStage = primaryStage;

        // Set Background
        var bg = new Background(
            new BackgroundImage(
                new Image(
                        Helper.getInputStream("/img/preloader.png")
                ),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(
                        preloaderHeight,
                        preloaderWidth,
                        false,
                        false,
                        true,
                        true
                )
            )
        );

        // Add info label
        var info = new Label("Bitte warten...");
        AnchorPane.setTopAnchor(info, 175.0);
        AnchorPane.setLeftAnchor(info, 213.0);

        // Set status message label
        statusMessage = new Label();
        AnchorPane.setTopAnchor(statusMessage, 200.0);
        AnchorPane.setLeftAnchor(statusMessage, 213.0);

        var root = new AnchorPane(info, statusMessage);
        root.setBackground(bg);

        primaryStage.setHeight(preloaderHeight);
        primaryStage.setWidth(preloaderWidth);
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);

        primaryStage.show();
    }

    /**
     * Receives notifications and stopps on PreloaderCloseNotification
     *
     * @param pn The notification
     */
    @Override
    public void handleApplicationNotification(PreloaderNotification pn) {
        if(pn instanceof PreloaderStatusNotification) {
            statusMessage.setText(
                    ((PreloaderStatusNotification) pn).getMessage()
            );
        } else if(pn instanceof PreloaderCloseNotification) {
            preloaderStage.hide();
        }
    }
}
