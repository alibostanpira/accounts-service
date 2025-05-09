package org.abpira.accounts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.abpira.accounts.constants.AccountsConstants;
import org.abpira.accounts.dto.AccountsDTO;
import org.abpira.accounts.dto.CustomerDTO;
import org.abpira.accounts.service.AccountsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = AccountsController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccountsService accountsService;

    @Test
    void shouldCreateAccountSuccessfully() throws Exception {
        // given
        CustomerDTO customerDTO = CustomerDTO.builder()
                .name("abcde")
                .mobileNumber("1234567890")
                .email("abcde@gmail.com")
                .build();

        doNothing().when(accountsService).createAccount(any(CustomerDTO.class));

        // when & then
        mockMvc.perform(post("/api/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode", is(AccountsConstants.STATUS_201)))
                .andExpect(jsonPath("$.statusMessage", is(AccountsConstants.MESSAGE_201)));
    }

    @Test
    void shouldFetchAccountDetailsSuccessfully() throws Exception {
        // given
        AccountsDTO accountsDTO = AccountsDTO.builder()
                .accountNumber(1027418131L)
                .accountType("Savings")
                .branchAddress("New York")
                .build();

        CustomerDTO customerDTO = CustomerDTO.builder()
                .name("abcde")
                .mobileNumber("1234567890")
                .email("abc@gmail.com")
                .accountsDTO(accountsDTO)
                .build();
        when(accountsService.fetchAccountDetails("1234567890")).thenReturn(customerDTO);

        // when & then
        mockMvc.perform(get("/api/fetch").param("mobileNumber", "1234567890"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("abcde"))
                .andExpect(jsonPath("$.email").value("abc@gmail.com"))
                .andExpect(jsonPath("$.mobileNumber").value("1234567890"))
                .andExpect(jsonPath("$.accountsDTO.accountNumber").value(1027418131))
                .andExpect(jsonPath("$.accountsDTO.accountType").value("Savings"))
                .andExpect(jsonPath("$.accountsDTO.branchAddress").value("New York"));
    }

    @Test
    void shouldUpdateAccountSuccessfully() throws Exception {
        // given
        CustomerDTO customerDTO = CustomerDTO.builder()
                .name("abcde")
                .mobileNumber("1234567890")
                .email("abc@gmail.com")
                .build();
        when(accountsService.updateAccount(any(CustomerDTO.class))).thenReturn(true);

        // when & then
        mockMvc.perform(put("/api/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode", is(AccountsConstants.STATUS_200)))
                .andExpect(jsonPath("$.statusMessage", is(AccountsConstants.MESSAGE_200)));
    }

    @Test
    void shouldReturnErrorWhenUpdateFails() throws Exception {
        // given
        CustomerDTO customerDTO = CustomerDTO.builder()
                .name("abcde")
                .mobileNumber("1234567890")
                .email("abc@gmail.com")
                .build();
        when(accountsService.updateAccount(any(CustomerDTO.class))).thenReturn(false);

        // when & then
        mockMvc.perform(put("/api/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode", is(AccountsConstants.STATUS_500)))
                .andExpect(jsonPath("$.statusMessage", is(AccountsConstants.MESSAGE_500)));
    }

    @Test
    void shouldDeleteAccountSuccessfully() throws Exception {
        // given
        String mobileNumber = "1234567890";
        when(accountsService.deleteAccounts(mobileNumber)).thenReturn(true);

        // when & then
        mockMvc.perform(delete("/api/delete").param("mobileNumber", mobileNumber))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode", is(AccountsConstants.STATUS_200)))
                .andExpect(jsonPath("$.statusMessage", is(AccountsConstants.MESSAGE_200)));
    }

    @Test
    void shouldReturnErrorWhenDeleteFails() throws Exception {
        // given
        String mobileNumber = "1234567890";
        when(accountsService.deleteAccounts(mobileNumber)).thenReturn(false);

        // when & then
        mockMvc.perform(delete("/api/delete").param("mobileNumber", mobileNumber))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.statusCode", is(AccountsConstants.STATUS_500)))
                .andExpect(jsonPath("$.statusMessage", is(AccountsConstants.MESSAGE_500)));
    }
}