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
import ch.rootlogin.rezeptverwaltung.repository.CategoryRepository;
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

    final private List<Category> defaultCategories = Arrays.asList(
            new Category("Pasta"),
            new Category("Salate"),
            new Category("Kuchen")
    );

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        seedData();
    }

    private void seedData() {
        logger.info("Checking if database is populated...");

        final var categoryCount = categoryRepository.count();
        if(categoryCount == 0) {
            logger.info("Populating database...");
            categoryRepository.saveAll(defaultCategories);
        }
    }

}
