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
import ch.rootlogin.rezeptverwaltung.helper.ApplicationContextProvider;
import ch.rootlogin.rezeptverwaltung.helper.DialogHelper;
import ch.rootlogin.rezeptverwaltung.helper.Helper;
import ch.rootlogin.rezeptverwaltung.model.Category;
import ch.rootlogin.rezeptverwaltung.model.Receipt;
import ch.rootlogin.rezeptverwaltung.repository.CategoryRepository;
import ch.rootlogin.rezeptverwaltung.repository.ReceiptRepository;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.commons.io.IOUtils;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Logger;

@Component
public class MainController {
    private final static Logger logger = Logger.getLogger(MainController.class.getName());

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ReceiptRepository receiptRepository;

    @FXML
    private MenuBar mainMenuBar;

    @FXML
    private Accordion categoryAccordion;

    @FXML
    private AnchorPane webPane;

    /**
     * Ugly JavaFX runtime issue fix...
     * JavaFX always shows a stacktrace if I try
     * to directly do this in the FXML-file -.-
     */
    private WebView webView;

    @FXML
    public void initialize() {
        // use macos system panel when possible...
        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac")) {
            mainMenuBar.useSystemMenuBarProperty().set(true);
        }

        renderAccordion();

        // render the webview...
        Platform.runLater(this::renderWebView);
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
    public void handleDeleteCategoryAction(ActionEvent event) {
        var categories = categoryRepository.findAll().iterator();

        var categoryList = new ArrayList<Category>();
        while(categories.hasNext()) {
            var category = categories.next();

            categoryList.add(category);
        }

        var dialog = new ChoiceDialog<>(categoryList.get(0), categoryList);
        dialog.setTitle("Kategorie löschen");
        dialog.setHeaderText("Lösche eine neue Rezept-Kategorie.");
        dialog.setContentText("Kategoriename:");

        var result = dialog.showAndWait();

        if (result.isPresent()) {
            categoryRepository.delete(result.get());
        }

        renderAccordion();
        renderWebView();
    }

    @FXML
    public void handleCreateReceiptAction(ActionEvent event) {
        try {
            var stage = new Stage();
            var addReceiptView = Helper.loadFXParent("/views/receipt.fxml");

            stage.setScene(new Scene(addReceiptView));
            stage.show();
        } catch(IOException ex) {
            logger.warning(ex.getMessage());
            DialogHelper.showAlertWithException("View konnte nicht geladen werden!", ex);
        }
    }

    @FXML
    private void handleEditReceipt(Long receiptId) {
        try {
            var context = ApplicationContextProvider.getApplicationContext();

            var fxmlLoader = new FXMLLoader();
            fxmlLoader.setControllerFactory(context::getBean);

            var stage = new Stage();
            stage.setScene(
                    new Scene((Pane) fxmlLoader.load(Helper.getInputStream("/views/receipt.fxml")))
            );

            var receipt = receiptRepository.findById(receiptId);
            if(receipt.isEmpty()) {
                // this should never happen!
                logger.warning("Got unknown receipt id!");
                return;
            }

            var receiptController = fxmlLoader.<ReceiptController>getController();
            receiptController.setReceipt(receipt.get());

            stage.show();
        } catch(IOException ex) {
            logger.warning(ex.getMessage());
            DialogHelper.showAlertWithException("View konnte nicht geladen werden!", ex);
        }
    }

    @FXML
    public void handleCloseAction(ActionEvent event) {
        Platform.exit();
    }

    private void createCategory(String name) {
        if(categoryRepository.getByNameEquals(name) == null) {
            var cat = new Category(name);
            categoryRepository.save(cat);

            // re-render category list
            renderAccordion();
        }
    }

    @EventListener
    public void processUpdatedReceiptsEvent(UpdatedReceiptsEvent event) {
        logger.info("Updated receipts event received");

        renderAccordion();
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

            // Create receipt links
            var receiptList = new VBox();
            for (Receipt receipt : category.getReceipts()) {
                var receiptLink = new Hyperlink();
                receiptLink.setText(receipt.getTitle());
                receiptLink.setOnMouseClicked((event) -> {
                    // check if is right button
                    if(event.getButton() == MouseButton.PRIMARY) {
                        renderReceipt(receipt.getId());
                    }
                });
                receiptLink.setContextMenu(createReceiptContextMenu(receipt.getId()));

                // add element to list
                receiptList.getChildren().add(receiptLink);
            }

            // Create pane for showing receipts
            var titledPane = new TitledPane();
            titledPane.setText(category.getName());
            titledPane.setContent(receiptList);

            // add to pane list
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

    private void renderReceipt(Long receiptId) {
        var parser = Parser.builder().build();
        var renderer = HtmlRenderer.builder().build();
        var receipt = receiptRepository.findById(receiptId);
        if(receipt.isEmpty()) {
            // this should never happen!
            logger.warning("Got unknown receipt id!");
            return;
        }

        // render markdown to html
        var document = parser.parse(receipt.get().getContent());
        var receiptHTML = renderer.render(document);

        // parse templates
        var template = JtwigTemplate.classpathTemplate("/templates/receipt.twig");
        var model = JtwigModel.newModel();
        model.with("receipt", receiptHTML);
        model.with("title", receipt.get().getTitle());

        var html = template.render(model);

        webView.getEngine().loadContent(html);
    }

    private ContextMenu createReceiptContextMenu(Long receiptId) {
        var contextMenu = new ContextMenu();

        // Edit
        var mnuEdit = new MenuItem();
        mnuEdit.setText("Bearbeiten");
        mnuEdit.setOnAction((event) -> handleEditReceipt(receiptId));

        // Delete
        var mnuDelete = new MenuItem();
        mnuDelete.setText("Löschen");
        mnuDelete.setOnAction((event) -> handleDeleteReceipt(receiptId));

        contextMenu.getItems().addAll(mnuEdit, mnuDelete);

        return contextMenu;
    }

    private void handleDeleteReceipt(Long receiptId) {
        var receipt = receiptRepository.findById(receiptId);
        if(receipt.isEmpty()) {
            // this should never happen!
            logger.warning("Got unknown receipt id!");
            return;
        }

        if(!DialogHelper.askConfirmation(
                String.format("Willst du das Rezept '%s' wirklich löschen?",
                        receipt.get().getTitle()
                )
        )) {
            return;
        }

        logger.info("Deleting receipt id: " + receiptId);
        receiptRepository.delete(receipt.get());

        // send event that receipts are updated
        applicationEventPublisher.publishEvent(new UpdatedReceiptsEvent());
    }

    /**
     * Renders the webview
     *
     * I don't know, why but somehow javafx is always failing
     * if I create the webview directly in FXML ;)
     */
    private void renderWebView() {
        webView = new WebView();
        webView.setContextMenuEnabled(false);
        webView.prefHeightProperty().bind(webPane.heightProperty());
        webView.prefWidthProperty().bind(webPane.widthProperty());

        webPane.getChildren().add(webView);

        // load index page
        try {
            var is = MainController.class.getResourceAsStream("/html/index.html");
            var html = IOUtils.toString(is);

            webView.getEngine().loadContent(html);
        } catch (IOException ex) {
            logger.warning(String.format("Couldn't load index.html: %s", ex.getMessage()));
        }
    }
}
