package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.Transaction;
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

  Page<Transaction> findByEmitterUserIdOrReceiverUserId(int emitterId, int receiverId,
                                                        Pageable pageable);

}
