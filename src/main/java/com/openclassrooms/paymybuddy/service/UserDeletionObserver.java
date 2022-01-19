package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.User;

/**
 * Interface Class to implement observer pattern on user deletion to clean up BankTransfer and
 * Transactions of the user to delete.
 */
public interface UserDeletionObserver {

  void onUserDeletion(User user);

}