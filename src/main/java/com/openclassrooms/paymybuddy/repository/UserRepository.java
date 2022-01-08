package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository Class for CRUD operations on users in database.
 */
@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

  Page<User> findAll(Pageable pageable);

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

}
