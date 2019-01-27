package ch.rootlogin.rezeptverwaltung.gui.controller;

import ch.rootlogin.rezeptverwaltung.repository.CategoryRepository;
import ch.rootlogin.rezeptverwaltung.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddReceiptController {
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ReceiptRepository receiptRepository;
}
