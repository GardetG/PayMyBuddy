package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service class for providing the user credentials from the database.
 */
@Service
public class CredentialsService implements UserDetailsService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CredentialsService.class);

  @Autowired
  UserRepository userRepository;

  /**
   * Get a user from repository by it's email and return it in the form of UserDetails.
   *
   * @param username email of the user
   * @return UserDetaols of the user
   * @throws UsernameNotFoundException if user not found
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByEmail(username)
        .orElseThrow(() -> {
          LOGGER.error("Can't login, this user not found: {}", username);
          return new UsernameNotFoundException("Can't login, this user not found");
        });
  }
}
