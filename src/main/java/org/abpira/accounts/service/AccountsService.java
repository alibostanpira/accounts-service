package org.abpira.accounts.service;

import org.abpira.accounts.dto.CustomerDTO;

public interface AccountsService {

    void createAccount(CustomerDTO customerDTO);
}
