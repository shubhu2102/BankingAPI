package com.bankingsystem.banking_api.Controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankingsystem.banking_api.Entity.Account;
import com.bankingsystem.banking_api.Service.AccountService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/accounts")
public class AccountController {

	@Autowired
	private AccountService accountService;

	@PostMapping("/create")
	public ResponseEntity<Account> createAccount(@RequestBody Account account) {
		Account newAccount = accountService.createAccount(account.getAccountNumber(), account.getBalance());
		return ResponseEntity.ok(newAccount);
	}

	@GetMapping
	public ResponseEntity<?> getAll() {

		List<Account> allAccount = accountService.getAllAccount();

		if (allAccount == null || allAccount.isEmpty()) {
			return new ResponseEntity<>("No Account are found", HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(allAccount, HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getAccount(@PathVariable Long id) {
	    if (id == null) {
	        log.error("Invalid account ID: {}", id);
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid account ID: " + id);
	    }

	   
	    Optional<Account> account = accountService.getAccountById(id);
	    
	    if (account.isPresent()) {
	        log.info("Account found: {}", id);
	        return ResponseEntity.ok(account.get());
	    } else {
	        log.error("Account not found: {}", id);
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found: " + id); 
	    }
	}

	@DeleteMapping("/{accountNumber}")
	public ResponseEntity<?> deleteAccount(@PathVariable String accountNumber) {
		try {
			accountService.deleteAccount(accountNumber);
			return new ResponseEntity<>("Account delete Successfull !!!", HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			log.error("Account not found: {}", accountNumber, e);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found: " + accountNumber);
		}

	}

	@GetMapping("/{accountNumber}/balance")
	public ResponseEntity<?> getBalance(@PathVariable String accountNumber) {

		try {
			Double balance = accountService.getBalance(accountNumber);
			log.info("Balance retrieved successfully for account: {}, Balance: {}", accountNumber, balance);
			return ResponseEntity.ok(balance);
		} catch (IllegalArgumentException e) {
			log.error("Account not found: {}", accountNumber, e);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found: " + accountNumber);
		} catch (Exception e) {
			log.error("An error occurred while fetching balance for account: {}", accountNumber, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
		}
	}

	@PostMapping("/transfer")
	public ResponseEntity<String> transfer(@RequestParam String source, @RequestParam String target,
			@RequestParam Double amount) {
		try {
			Optional<ResponseEntity<String>> response = accountService.transferMoney(source, target, amount);

			if (response.isPresent()) {
				log.info("Response from service: {}", response.get().getBody());
				return response.get();
			} else {
				log.warn("Transfer failed due to unexpected error.");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
			}
		} catch (Exception e) {

			log.error("An error occurred during transfer: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred during the transfer. Please try again later.");
		}
	}

}
