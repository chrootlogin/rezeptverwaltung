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

import ch.rootlogin.rezeptverwaltung.model.Category;
import ch.rootlogin.rezeptverwaltung.model.Receipt;
import ch.rootlogin.rezeptverwaltung.repository.CategoryRepository;
import ch.rootlogin.rezeptverwaltung.repository.ReceiptRepository;
import ch.rootlogin.rezeptverwaltung.type.ReceiptType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class is run on the start of the application, it checks
 * if the database is already populated and does it if not.
 */
@Component
public class DataLoader implements ApplicationListener<ApplicationReadyEvent> {
    private final static Logger logger = Logger.getLogger(DataLoader.class.getName());

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ReceiptRepository receiptRepository;

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        seedData();
    }

    private void seedData() {
        logger.info("Checking if database is populated...");

        final var categoryCount = categoryRepository.count();
        if(categoryCount == 0) {
            logger.info("Populating database...");

            // Kuchen
            var kuchen = new Category("Kuchen");
            categoryRepository.save(kuchen);

            var gleichschwer = new Receipt();
            gleichschwer.setTitle("Gleichschwer");
            gleichschwer.setReceiptType(ReceiptType.MARKDOWN);
            gleichschwer.setContent(
                    "**Zutaten**\n\n" +
                    " * 250g Butter\n" +
                    " * 250g Zucker\n" +
                    " * 250g Mehl\n" +
                    " * 1TL Backpulver\n" +
                    " * 3 Eier\n\n" +
                    "**Zubereitung**\n\n" +
                    "Zuerst die Eier, den Zucker und die Butter verrühren. Danach das Mehl und Backpulver unterrühren.\n\n" +
                    "Nach Belieben Früchte oder ähnliches beigeben.\n\n" +
                    "**Backen**\n\n" +
                    "30-45 Minuten bei 180°C Umluft."
            );
            gleichschwer.setCategory(kuchen);
            receiptRepository.save(gleichschwer);

            var marmorkuchen = new Receipt();
            marmorkuchen.setTitle("Marmorkuchen");
            marmorkuchen.setReceiptType(ReceiptType.MARKDOWN);
            marmorkuchen.setContent(
                    "**Zutaten**\n\n" +
                    " * 250g Butter\n" +
                    " * 250g Zucker\n" +
                    " * 1 Beutel Vanillezucker\n" +
                    " * 2 TL Rum\n" +
                    " * 4 Eier\n" +
                    " * 500g Mehl\n" +
                    " * 1 TL Backpulver\n" +
                    " * 1 Prise Salz\n" +
                    " * 1 dl Milch\n" +
                    " * 30g Kakaopulver\n\n" +
                    "**Zubereitung**\n\n" +
                    "Zuerst die Eier, den Zucker und die Butter verrühren. Danach alle Zutaten, *ausser das Kakaopulver*, unterrühren.\n" +
                    "Teig in 2 Teile teilen. Bei der einen Hälfte Kakaopulver unterühren. Danach in die Form füllen und mit einer Gabel durchziehen.\n\n" +
                    "**Backen**\n\n" +
                    "50 Minuten bei 160°C Umluft."
            );
            marmorkuchen.setCategory(kuchen);
            receiptRepository.save(marmorkuchen);
        }
    }

}
