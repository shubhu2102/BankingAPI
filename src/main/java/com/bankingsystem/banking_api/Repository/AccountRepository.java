package com.bankingsystem.banking_api.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.bankingsystem.banking_api.Entity.Account;

import jakarta.transaction.Transactional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	 Optional<Account> findByAccountNumber(String accountNumber);
	 
	 @Modifying
	 @Transactional
	 void deleteByAccountNumber(String accountNumber);
}
