package com.academy.orders.domain.passwordreset.usecase;

/**
  * Port interface for password hashing operations.
  * Implementations should use cryptographically secure hashing algorithms
  * with proper salt generation and appropriate work factors.
  * <p>
  * This interface follows the hexagonal architecture pattern to abstract
  * password hashing from the domain logic.
  */
@FunctionalInterface
public interface PasswordHashingPort {
  /**
      * Hashes a plaintext password using a cryptographically secure algorithm.
      * The implementation should:
      * - Use a strong hashing algorithm (bcrypt)
      * - Generate a unique salt for each password
      * - Use appropriate work factors to resist brute force attacks
      *
      * @param password the plaintext password to hash
      * @return the hashed password with salt and algorithm parameters
      * @throws IllegalArgumentException if the password is null or empty
   */
  String hash(String password);
}
