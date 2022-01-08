package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository Class for CRUD operations on Transaction in database.
 */
@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Integer> {

  Page<Transaction> findAll(Pageable pageable);

  Page<Transaction> findByEmitterOrReceiver(User emitter, User receiver,
                                            Pageable pageable);

}
