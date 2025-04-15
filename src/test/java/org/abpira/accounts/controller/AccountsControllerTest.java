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
                .name("abc")
                .mobileNumber("123456")
                .email("abc@gmail.com")
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
    void shouldFetchAccountSuccessfully() throws Exception {
        // given
        AccountsDTO accountsDTO = AccountsDTO.builder()
                .accountNumber(1027418131L)
                .accountType("Savings")
                .branchAddress("New York")
                .build();

        CustomerDTO customerDTO = CustomerDTO.builder()
                .name("abc")
                .mobileNumber("123456")
                .email("abc@gmail.com")
                .accountsDTO(accountsDTO)
                .build();
        when(accountsService.fetchAccountDetails("123456")).thenReturn(customerDTO);

        // when & then
        mockMvc.perform(get("/api/fetch").param("mobileNumber", "123456"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("abc"))
                .andExpect(jsonPath("$.email").value("abc@gmail.com"))
                .andExpect(jsonPath("$.mobileNumber").value("123456"))
                .andExpect(jsonPath("$.accountsDTO.accountNumber").value(1027418131))
                .andExpect(jsonPath("$.accountsDTO.accountType").value("Savings"))
                .andExpect(jsonPath("$.accountsDTO.branchAddress").value("New York"));
    }
}