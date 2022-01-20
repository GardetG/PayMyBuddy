package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.User;

/**
 * Interface Class to implement observer pattern on user deletion to clean up bank transfers and
 * transactions involving the user to delete.
 */
public interface UserDeletionObserver {

  /**
   * Called when user service notify the user deletion.
   *
   * @param user to delete
   */
  void onUserDeletion(User user);

}