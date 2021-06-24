package com.fdmgroup.pilotbank2.controllers;

import com.fdmgroup.pilotbank2.models.Account;
import com.fdmgroup.pilotbank2.models.dto.AccountCreationDTO;
import com.fdmgroup.pilotbank2.models.dto.MainAccountRequestDTO;
import com.fdmgroup.pilotbank2.services.AccountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins="http://localhost:4200")
@RequestMapping(value = "/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/")
    @SecurityRequirement(name = "jwt-bearer")
    public ResponseEntity<List<Account>> getAccounts() {
        List<Account> accountList = accountService.findAll();
        return new ResponseEntity<>(accountList, HttpStatus.OK);
    }

    @GetMapping("/{accountId}")
    @SecurityRequirement(name = "jwt-bearer")
    public <A extends Account> ResponseEntity<?> getAccountById(@PathVariable(value = "accountId") Long accountId){
        try {
            Account account = (A) accountService.findAccountById(accountId);
            return new ResponseEntity<>((A) account, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            String message = String.format("Error finding account: %s", e);
            return new ResponseEntity(message, HttpStatus.UNPROCESSABLE_ENTITY);
        }

    }


    @PutMapping("/setMainAccount")
    @SecurityRequirement(name = "jwt-bearer")
    public <A extends Account> ResponseEntity<?> setMainAccount(@RequestBody MainAccountRequestDTO mainAccountRequest){
        try {
            Account account = (A) accountService.setAsMainAccount(mainAccountRequest);
            return new ResponseEntity<>((A) account, HttpStatus.OK);
        } catch (IllegalStateException e) {
            String message = String.format("Error setting account: %s as main account", e);
            return new ResponseEntity(message, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @PostMapping("/create")
    @SecurityRequirement(name = "jwt-bearer")
    public <A extends Account> ResponseEntity<?> createAccount(@RequestBody AccountCreationDTO accountRequest) {
        try {
            Account account = accountService.createAccount(accountRequest);
            return new ResponseEntity(account, HttpStatus.CREATED);
        } catch (IllegalArgumentException e){
            String message = String.format("Error while trying to create account: %s", e);
            return new ResponseEntity(message, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
