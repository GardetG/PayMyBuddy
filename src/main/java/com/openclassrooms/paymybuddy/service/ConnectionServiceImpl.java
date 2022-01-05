package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service Class for managing user's connections with other users.
 */
@Service
public class ConnectionServiceImpl implements ConnectionService {

  @Autowired
  UserRepository userRepository;

}
