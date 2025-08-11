package com.academy.orders.domain.accountv2.usecase;

/**
 * Use case interface for retrieving the profile photo URL of a user account.
 */
public interface GetUserProfilePhotoUseCase {

  /**
   * Retrieves the profile photo URL for the given user.
   *
   * @param userId the {@link Long} user's ID.
   * @return the profile photo URL as a {@link String}, or {@code null} if not set.
   */
  String getProfilePhoto(Long userId);
}
