package com.academy.orders.domain.passwordreset.usecase;

@FunctionalInterface
public interface PasswordHashingPort {
  /**
 * Generates a hashed representation of the provided password.
 *
 * @param password the plain text password to hash
 * @return the hashed password as a String
 */
String hash(String password);
}
