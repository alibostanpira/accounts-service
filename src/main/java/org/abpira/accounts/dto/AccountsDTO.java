package org.abpira.accounts.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountsDTO {

    private Long accountNumber;
    private String accountType;
    private String branchAddress;
}
