package com.academy.orders.domain.accountv2.usecase;

/**
 * Use case interface for updating the profile photo URL of a user account.
 */
public interface UpdateUserProfilePhotoUseCase {

  /**
   * Updates the profile photo URL for the given user.
   *
   * @param userId the {@link Long} user's ID.
   * @param photoUrl the new profile photo URL as a {@link String}.
   */
  void updateProfilePhoto(Long userId, String photoUrl);
}
