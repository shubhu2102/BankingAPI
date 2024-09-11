package com.bankingsystem.banking_api.Controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.bankingsystem.banking_api.Entity.Account;
import com.bankingsystem.banking_api.Service.AccountService;

@SpringBootTest
class AccountControllerTest {

	 @Mock
	    private AccountService accountService;

	    @InjectMocks
	    private AccountController accountController;

	    @SuppressWarnings("deprecation")
		@BeforeEach
	    void setUp() {
	        MockitoAnnotations.initMocks(this);
	    }

	
      @Test
    void testCreateAccount() {
        Account account = new Account((long) 1, "12345", 1000.0);
        when(accountService.createAccount(anyString(), anyDouble())).thenReturn(account);

        ResponseEntity<Account> response = accountController.createAccount(account);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("12345", response.getBody().getAccountNumber());
        assertEquals(1000.0, response.getBody().getBalance());

        verify(accountService, times(1)).createAccount(anyString(), anyDouble());
    }

    @SuppressWarnings("unchecked")
	@Test
    void testGetAll() {
        when(accountService.getAllAccount()).thenReturn(Arrays.asList(new Account( (long) 1,"12345", 1000.0)));

        ResponseEntity<?> response = accountController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(( (List<Account>) response.getBody()).size() > 0);

        verify(accountService, times(1)).getAllAccount();
    }

    @Test
    void testGetAccount() {
        Account account = new Account((long) 1, "12345", 1000.0);
        when(accountService.getAccountById(1L)).thenReturn(Optional.of(account));

        ResponseEntity<?> response = accountController.getAccount(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("12345", ((Account) response.getBody()).getAccountNumber());

        verify(accountService, times(1)).getAccountById(1L);
    }

    @Test
    void testGetAccountNotFound() {
        when(accountService.getAccountById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = accountController.getAccount(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Account not found: 1", response.getBody());

        verify(accountService, times(1)).getAccountById(1L);
    }

    @Test
    void testDeleteAccount() {
        

        ResponseEntity<?> response = accountController.deleteAccount("12345");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Account delete Successfull !!!", response.getBody());

        verify(accountService, times(1)).deleteAccount("12345");
    }

    @Test
    void testGetBalance() {
        when(accountService.getBalance("12345")).thenReturn(1000.0);

        ResponseEntity<?> response = accountController.getBalance("12345");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1000.0, response.getBody());

        verify(accountService, times(1)).getBalance("12345");
    }

    @Test
    void testTransferMoney() {
        when(accountService.transferMoney("123", "456", 100.0))
                .thenReturn(Optional.of(ResponseEntity.ok("Transfer successful!")));

        ResponseEntity<String> response = accountController.transfer("123", "456", 100.0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Transfer successful!", response.getBody());

        verify(accountService, times(1)).transferMoney("123", "456", 100.0);
    }
}

