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

import ch.rootlogin.rezeptverwaltung.event.UpdatedReceiptsEvent;
import ch.rootlogin.rezeptverwaltung.helper.DialogHelper;
import ch.rootlogin.rezeptverwaltung.model.Category;
import ch.rootlogin.rezeptverwaltung.model.Receipt;
import ch.rootlogin.rezeptverwaltung.repository.CategoryRepository;
import ch.rootlogin.rezeptverwaltung.repository.ReceiptRepository;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * This controller is used for handling
 * the add receipt window.
 */
@Component
public class ReceiptController {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ReceiptRepository receiptRepository;

    @FXML
    private Text txtHeader;

    @FXML
    private TextField title;

    @FXML
    private ComboBox category;

    @FXML
    private TextArea content;

    @FXML
    private Button btnSave;

    /**
     * Null on new receipt, set on edit mode
     */
    private Receipt receipt;

    @FXML
    public void initialize() {
        category.setEditable(true);

        // request focus after start
        Platform.runLater(() -> title.requestFocus());

        renderCategoryList();
    }

    /**
     * Allows setting a receipt
     *
     * This is used to edit a receipt.
     * @param receipt Receipt to edit
     */
    @SuppressWarnings("unchecked")
    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;

        txtHeader.setText("Rezept bearbeiten");

        this.title.setText(receipt.getTitle());
        this.content.setText(receipt.getContent());

        for(var i = 0; i < category.getItems().size(); i++) {
            var cat = (Category) category.getItems().get(i);

            if (Objects.equals(cat.getId(), receipt.getCategory().getId())) {
                category.setValue(cat);
                return;
            }
        }
    }

    public void handleSaveAction(ActionEvent event) {
        var category = (String) this.category.getValue();
        var title = this.title.getText();
        var content = this.content.getText();

        if(category.length() == 0) {
            DialogHelper.showWarning("Formular-Fehler","Du musst eine Kategorie angeben!");
            return;
        }

        if(title.length() == 0) {
            DialogHelper.showWarning("Formular-Fehler","Du musst einen Titel eingeben!");
            return;
        }

        if(receipt == null) {
            // create receipt
            receipt = new Receipt();
        }
        receipt.setTitle(title);
        receipt.setContent(content);
        receipt.setCategory(getCreateCategory(category));
        receiptRepository.save(receipt);

        // send updated event
        applicationEventPublisher.publishEvent(new UpdatedReceiptsEvent());

        // close
        Stage stage = (Stage) this.btnSave.getScene().getWindow();
        stage.close();
    }

    @SuppressWarnings("unchecked")
    private void renderCategoryList() {
        var categories = categoryRepository.findAll().iterator();

        ObservableList<Category> categoryList
                = FXCollections.observableArrayList();

        while(categories.hasNext()) {
            var category = categories.next();

            categoryList.add(category);
        }

        category.getItems().setAll(categoryList);
        category.setValue(categoryList.get(0));
    }

    /**
     * Gets a category by string, if it's not existing it gets created.
     * @param category Name of category
     *
     * @return Category object
     */
    private Category getCreateCategory(String category) {
        var cat = categoryRepository.getByNameEquals(category);
        if(cat == null) {
            cat = new Category(category);
            categoryRepository.save(cat);
        }

        return cat;
    }
}
