package com.bankingsystem.banking_api.Service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bankingsystem.banking_api.Entity.Account;
import com.bankingsystem.banking_api.Repository.AccountRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public  class AccountService  {
	
	
	@Autowired
	private AccountRepository accountRepository;
	
	
	@Transactional
	 public Account createAccount(String accountNumber, Double initialBalance) {
	        Account account = new Account();
	        account.setAccountNumber(accountNumber);
	        account.setBalance(initialBalance);
	        return accountRepository.save(account);
	    }
	
	@Transactional
	public void deleteAccount(String accountNumber) {
		accountRepository.deleteByAccountNumber(accountNumber);
	}
	
	public Optional<Account> getAccountById(Long id){
		return accountRepository.findById(id);
	}
	
	public Double getBalance(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .map(Account::getBalance)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }
	
	public List<Account> getAllAccount(){
		return accountRepository.findAll();
	}
	
	public Optional<ResponseEntity<String>> transferMoney(String sourceAccount, String targetAccount, Double amount) {
        log.info("Initiating transfer of {} from account {} to account {}", amount, sourceAccount, targetAccount);
        
        Optional<Account> sourceAccountOpt = accountRepository.findByAccountNumber(sourceAccount);
        Optional<Account> targetAccountOpt = accountRepository.findByAccountNumber(targetAccount);
        
        if (sourceAccountOpt.isEmpty()) {
            log.error("Source account {} not found", sourceAccount);
            return Optional.of(ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Account not found: " + sourceAccount));
        }

        if (targetAccountOpt.isEmpty()) {
            log.error("Target account {} not found", targetAccount);
            return Optional.of(ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Account not found: " + targetAccount));
        }

        Account fromAccount = sourceAccountOpt.get();
        Account toAccount = targetAccountOpt.get();

        if (fromAccount.getBalance() < amount) {
            log.error("Insufficient balance in source account {}. Current balance: {}, Attempted transfer: {}",
                    sourceAccount, fromAccount.getBalance(), amount);
            return Optional.of(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Insufficient balance in source account " + sourceAccount));
        }

        try {
            log.info("Transferring {} from account {} to account {}", amount, sourceAccount, targetAccount);

            fromAccount.setBalance(fromAccount.getBalance() - amount);
            accountRepository.save(fromAccount);
            log.info("Deducted {} from source account {}. New balance: {}", amount, sourceAccount, fromAccount.getBalance());

            toAccount.setBalance(toAccount.getBalance() + amount);
            accountRepository.save(toAccount);
            log.info("Added {} to target account {}. New balance: {}", amount, targetAccount, toAccount.getBalance());

            return Optional.of(ResponseEntity.status(HttpStatus.OK)
                    .body("Transfer successful!"));
        } catch (Exception e) {
            log.error("An error occurred during the transfer from {} to {}: {}", sourceAccount, targetAccount, e.getMessage(), e);
            return Optional.of(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during the transfer. Please try again later."));
        }
    }   
	    }

	
