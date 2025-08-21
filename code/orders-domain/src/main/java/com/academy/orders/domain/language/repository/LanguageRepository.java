package com.academy.orders.domain.language.repository;

import com.academy.orders.domain.product.entity.Language;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for loading and managing language info.
 */
public interface LanguageRepository {
  /**
   * Finds a language entity by its code.
   *
   * @param code the code of the language to be retrieved.
   * @return an {@link Optional} containing the {@link Language} if found, or empty if not found.
   */
  Optional<Language> findByCode(String code);

  /**
   * Retrieves all languages from the database.
   *
   * @return a list of all {@link Language} entities.
   */
  List<Language> findAll();
}
