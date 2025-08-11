package com.academy.orders.domain.postaddress.repository;

import com.academy.orders.domain.postaddress.entity.PostAddressV2;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for accessing and managing post addresses.
 */
public interface PostAddressV2Repository {
  /**
   * Retrieves all permanent post addresses for the given user. A permanent post address is identified by the "permanent: " prefix in its
   * title.
   *
   * @param userId the ID of the user
   * @return a list of {@link PostAddressV2} entries associated with the user that have the "permanent: " prefix in their title
   * @author Oleksandra Bulhakova
   */
  List<PostAddressV2> getPermanentPostAddressesByUserId(Long userId);

  /**
   * Removes a permanent post address from the user's list of permanent post addresses.
   * A permanent post address is identified by the "permanent: " prefix in its title.
   * Instead of physically deleting the address from the database, this operation
   * updates the title to remove the "permanent: " prefix, effectively marking it as temporary.
   *
   * @param userId    the ID of the user
   * @param addressId the UUID of the post address to be removed from the permanent list
   * @author Oleksandra Bulhakova
   */
  void  deletePermanentAddress(Long userId, UUID addressId);

  /**
   * Checks whether a post address with the specified ID exists.
   *
   * @param addressId the UUID of the post address to check
   * @return {@code true} if a post address with the given ID exists, {@code false} otherwise
   * @author Oleksandra Bulhakova
   */
  boolean checkIfAddressExistsById(UUID addressId);

  /**
   * Checks whether the post address with the specified ID is marked as permanent.
   * A permanent post address is identified by the "permanent: " prefix in its title.
   *
   * @param addressId the UUID of the post address to check
   * @return {@code true} if the post address exists and its title starts with the "permanent: " prefix,
   *         {@code false} otherwise
   * @author Oleksandra Bulhakova
   */
  boolean checkIfAddressIsPermanent(UUID addressId);
}
