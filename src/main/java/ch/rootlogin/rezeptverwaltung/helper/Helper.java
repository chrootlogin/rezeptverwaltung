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
package ch.rootlogin.rezeptverwaltung.helper;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class Helper {
    private final static Logger logger = Logger.getLogger(Helper.class.getName());

    public static InputStream getInputStream(String fileName) {
        try {
            logger.info("Opening file: " + fileName);
            InputStream fis = Helper.class.getResourceAsStream(fileName);
            if(fis == null){
                logger.warning("Can't open file: " + fileName);

                System.exit(127);
            }

            return fis;
        } catch (NullPointerException e) {
            logger.warning("Can't open file: " + e.getMessage());

            System.exit(127);
        }

        return null;
    }

    public static Parent loadFXParent(String path) throws IOException {
        var context = ApplicationContextProvider.getApplicationContext();

        var fxmlLoader = new FXMLLoader();
        fxmlLoader.setControllerFactory(context::getBean);

        return (Parent) fxmlLoader.load(
                Helper.getInputStream(path)
        );
    }
}
