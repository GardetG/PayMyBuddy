package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.BankTransfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository Class for CRUD operations on Banktransfer in database.
 */
@Repository
public interface BankTransferRepository extends CrudRepository<BankTransfer, Integer> {

  Page<BankTransfer> findAll(Pageable pageable);

  Page<BankTransfer> findByUser(int userId, Pageable pageable);

}
