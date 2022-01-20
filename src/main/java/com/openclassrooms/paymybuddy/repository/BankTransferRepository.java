package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.BankAccount;
import com.openclassrooms.paymybuddy.model.BankTransfer;
import java.util.Collection;
import java.util.List;
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

  Page<BankTransfer> findByBankAccountIn(Collection<BankAccount> accounts, Pageable pageable);

  List<BankTransfer> findByBankAccount(BankAccount bankAccount);

}
