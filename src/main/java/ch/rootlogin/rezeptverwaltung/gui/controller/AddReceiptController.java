package ch.rootlogin.rezeptverwaltung.gui.controller;

import ch.rootlogin.rezeptverwaltung.event.UpdatedReceiptsEvent;
import ch.rootlogin.rezeptverwaltung.helper.DialogHelper;
import ch.rootlogin.rezeptverwaltung.model.Category;
import ch.rootlogin.rezeptverwaltung.model.Receipt;
import ch.rootlogin.rezeptverwaltung.repository.CategoryRepository;
import ch.rootlogin.rezeptverwaltung.repository.ReceiptRepository;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * This controller is used for handling
 * the add receipt window.
 */
@Component
public class AddReceiptController {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ReceiptRepository receiptRepository;

    @FXML
    private TextField title;

    @FXML
    private ComboBox category;

    @FXML
    private TextArea content;

    @FXML
    private Button btnSave;

    @FXML
    public void initialize() {
        category.setEditable(true);

        // request focus after start
        Platform.runLater(() -> title.requestFocus());

        renderCategoryList();
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

        // create receipt
        var receipt = new Receipt(title, content);
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
        var categoryList = new ArrayList<String>();

        while(categories.hasNext()) {
            var category = categories.next();

            categoryList.add(category.getName());
        }

        category.getItems().setAll(categoryList);
        category.setValue(categoryList.get(0));
    }

    private Category getCreateCategory(String category) {
        var cat = categoryRepository.getByNameEquals(category);
        if(cat == null) {
            cat = new Category(category);
            categoryRepository.save(cat);
        }

        return cat;
    }
}
