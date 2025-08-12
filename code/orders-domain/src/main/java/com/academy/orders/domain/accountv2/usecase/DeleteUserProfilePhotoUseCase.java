package com.academy.orders.domain.accountv2.usecase;

/**
 * Use case interface for deleting the profile photo of a user account.
 */
public interface DeleteUserProfilePhotoUseCase {

  /**
   * Removes the profile photo for the given user (sets it to {@code null}).
   *
   * @param userId the {@link Long} user's ID.
   */
  void deleteProfilePhoto(Long userId);
}
