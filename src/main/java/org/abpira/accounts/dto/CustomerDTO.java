package org.abpira.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDTO {

    private String name;
    private String email;
    private String mobileNumber;
    private AccountsDTO accountsDTO;
}
