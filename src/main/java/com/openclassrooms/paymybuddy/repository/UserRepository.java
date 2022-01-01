package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository Class for CRUD operations on users in database.
 */
@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

  boolean existsByEmail(String email);

}
