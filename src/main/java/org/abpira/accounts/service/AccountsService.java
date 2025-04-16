package org.abpira.accounts.service;

import org.abpira.accounts.dto.CustomerDTO;

public interface AccountsService {

    void createAccount(CustomerDTO customerDTO);

    CustomerDTO fetchAccountDetails(String mobileNumber);

    boolean updateAccount(CustomerDTO customerDTO);

    boolean deleteAccounts(String mobileNumber);
}
