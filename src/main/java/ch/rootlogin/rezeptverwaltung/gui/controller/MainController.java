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
package ch.rootlogin.rezeptverwaltung.gui.controller;

import ch.rootlogin.rezeptverwaltung.model.Category;
import ch.rootlogin.rezeptverwaltung.model.Receipt;
import ch.rootlogin.rezeptverwaltung.repository.CategoryRepository;
import ch.rootlogin.rezeptverwaltung.repository.ReceiptRepository;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
public class MainController {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ReceiptRepository receiptRepository;

    @FXML
    private MenuBar mainMenuBar;

    @FXML
    private Accordion categoryAccordion;

    @FXML
    public void initialize() {
        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac")) {
            mainMenuBar.useSystemMenuBarProperty().set(true);
        }

        renderAccordion();
    }

    @FXML
    public void handleAboutAction(ActionEvent event) {

    }

    @FXML
    public void handleCreateCategoryAction(ActionEvent event) {
        var dialog = new TextInputDialog();
        dialog.setTitle("Kategorie erstellen");
        dialog.setHeaderText("Erstelle eine neue Rezept-Kategorie.");
        dialog.setContentText("Kategoriename:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(this::createCategory);
    }

    @FXML
    public void handleCloseAction(ActionEvent event) {
        Platform.exit();
    }

    private void createCategory(String name) {
        if(categoryRepository.getByNameEquals(name) == null) {
            var cat = new Category(name);
            categoryRepository.save(cat);


            var receipt = new Receipt("Testrezept", "bla 1234");
            receipt.setCategory(cat);

            receiptRepository.save(receipt);


            renderAccordion();
        }
    }

    /**
     * Renders the accordion on the left side
     */
    private void renderAccordion() {
        var categories = categoryRepository.findAll().iterator();

        // iterate through all categories and create a title pane with links
        var i = 0;
        var categoryPanes = new ArrayList<TitledPane>();

        while(categories.hasNext()) {
            var category = categories.next();

            var receiptList = new VBox();
            for (Receipt receipt : category.getReceipts()) {
                var receiptLink = new Hyperlink();
                receiptLink.setText(receipt.getTitle());

                receiptList.getChildren().add(receiptLink);
            }

            var titledPane = new TitledPane();
            titledPane.setText(category.getName());
            titledPane.setContent(receiptList);

            categoryPanes.add(titledPane);

            i++;
        }

        // refresh accordion
        categoryAccordion.getPanes().setAll(categoryPanes);

        // if we have more than one pane, expand first
        if(i > 0) {
            categoryAccordion.setExpandedPane(categoryPanes.get(0));
        }
    }
}
