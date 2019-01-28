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
package ch.rootlogin.rezeptverwaltung;

import ch.rootlogin.rezeptverwaltung.notification.PreloaderCloseNotification;
import ch.rootlogin.rezeptverwaltung.notification.PreloaderStatusNotification;
import ch.rootlogin.rezeptverwaltung.helper.Helper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class RezeptverwaltungApplication extends Application {
	private ConfigurableApplicationContext context;
	private Parent rootNode;

	@Override
	public void init() throws Exception {
		// Init spring
		notifyPreloader(new PreloaderStatusNotification("Initializing spring application..."));
		context = SpringApplication.run(RezeptverwaltungApplication.class);

		// Init main view
		notifyPreloader(new PreloaderStatusNotification("Loading main view..."));
		rootNode = Helper.loadFXParent("/views/main.fxml");

		// Starting application
		notifyPreloader(new PreloaderStatusNotification("Starting application..."));
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
		double width = visualBounds.getWidth();
		double height = visualBounds.getHeight();

		primaryStage.setTitle("Rezeptverwaltung");
		primaryStage.setScene(new Scene(rootNode, width, height));
		primaryStage.centerOnScreen();

		// Set on close action.
		primaryStage.setOnCloseRequest(we -> {
			Platform.exit();
		});

		// Close preloader
		notifyPreloader(new PreloaderCloseNotification());

		primaryStage.show();
	}

	@Override
	public void stop() throws Exception {
		context.close();
	}

	public static void main(String[] args) {
		System.setProperty("javafx.preloader", "ch.rootlogin.rezeptverwaltung.gui.preloader.ApplicationPreloader");

		Application.launch(RezeptverwaltungApplication.class);
	}
}
