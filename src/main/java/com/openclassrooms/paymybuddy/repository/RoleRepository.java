package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.Role;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository Class for role in database.
 */
public interface RoleRepository extends CrudRepository<Role, Integer> {

  Role findByName(String name);

}
