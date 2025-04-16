package org.abpira.accounts.service;

import java.util.Optional;

import org.abpira.accounts.dto.AccountsDTO;
import org.abpira.accounts.dto.CustomerDTO;
import org.abpira.accounts.entities.Accounts;
import org.abpira.accounts.entities.Customer;
import org.abpira.accounts.exceptions.CustomerAlreadyExistsException;
import org.abpira.accounts.exceptions.ResourceNotFoundException;
import org.abpira.accounts.repository.AccountsRepository;
import org.abpira.accounts.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class AccountsServiceTest {

    @Mock
    private AccountsRepository accountsRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AccountsServiceImpl underTest;

//    private AutoCloseable autoCloseable;
//    private AccountsServiceImpl underTest;
//
//    @BeforeEach
//    void setUp() {
//        autoCloseable = MockitoAnnotations.openMocks(this);
//        underTest = new AccountsServiceImpl(accountsRepository, customerRepository);
//    }
//
//    @AfterEach
//    void tearDown() throws Exception {
//        autoCloseable.close();
//    }

    @Test
    void shouldCreateAccountSuccessfully() {
        // given
        CustomerDTO customerDTO = CustomerDTO.builder()
                .name("abc")
                .mobileNumber("123456")
                .email("123456@gmail.com")
                .build();

        Customer mockCustomer = mock(Customer.class);
        when(mockCustomer.getCustomerId()).thenReturn(1L);

        when(customerRepository.findByMobileNumber("123456")).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(mockCustomer);

        // when
        underTest.createAccount(customerDTO);

        // then
        verify(customerRepository).findByMobileNumber("123456");
        verify(customerRepository).save(any(Customer.class));
        verify(accountsRepository).save(any(Accounts.class));
    }

    @Test
    void shouldThrowCustomerAlreadyExistsException() {
        // given
        CustomerDTO customerDTO = CustomerDTO.builder()
                .name("abc")
                .mobileNumber("123456")
                .email("123456@gmail.com")
                .build();
        // when
        Customer existingCustomer = new Customer();
        existingCustomer.setMobileNumber("123456");

        given(customerRepository.findByMobileNumber("123456"))
                .willReturn(Optional.of(existingCustomer));

        // then
        assertThatThrownBy(() -> underTest.createAccount(customerDTO))
                .isInstanceOf(CustomerAlreadyExistsException.class)
                .hasMessageContaining("Customer already exists with mobile number 123456");
        verify(customerRepository, never()).save(any());
        verify(accountsRepository, never()).save(any());
    }

    @Test
    void shouldFetchAccountDetailsSuccessfully() {
        // given
        String mobileNumber = "123456";
        Customer customer = Customer.builder()
                .mobileNumber(mobileNumber)
                .customerId(1L)
                .build();
        Accounts accounts = Accounts.builder()
                .accountNumber(1L)
                .customerId(1L)
                .accountType("Test")
                .build();
        when(customerRepository.findByMobileNumber("123456")).thenReturn(Optional.of(customer));
        when(accountsRepository.findByCustomerId(1L)).thenReturn(Optional.of(accounts));

        // when
        underTest.fetchAccountDetails(mobileNumber);

        // then
        verify(customerRepository).findByMobileNumber(mobileNumber);
        verify(accountsRepository).findByCustomerId(customer.getCustomerId());
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        // given
        String mobileNumber = "111111";
        when(customerRepository.findByMobileNumber(mobileNumber)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> underTest.fetchAccountDetails(mobileNumber))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer", "mobileNumber", mobileNumber);

        verify(accountsRepository, never()).findByCustomerId(any());
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFound() {
        // given
        String mobileNumber = "111111";
        Customer customer = Customer.builder()
                .customerId(1L)
                .mobileNumber(mobileNumber)
                .build();

        when(customerRepository.findByMobileNumber(mobileNumber)).thenReturn(Optional.of(customer));
        when(accountsRepository.findByCustomerId(1L)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> underTest.fetchAccountDetails(mobileNumber))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Accounts", "CustomerId", "1");

        verify(customerRepository).findByMobileNumber(mobileNumber);
        verify(accountsRepository).findByCustomerId(1L);
    }

    @Test
    void shouldUpdateAccountSuccessfully() {
        // given
        AccountsDTO accountsDTO = AccountsDTO.builder()
                .accountNumber(1L)
                .accountType("Savings")
                .branchAddress("123 Street")
                .build();

        CustomerDTO customerDTO = CustomerDTO.builder()
                .name("Updated Name")
                .email("updated@email.com")
                .mobileNumber("1234567890")
                .accountsDTO(accountsDTO)
                .build();

        Accounts existingAccount = new Accounts();
        existingAccount.setCustomerId(1L);
        when(accountsRepository.findById(1L)).thenReturn(Optional.of(existingAccount));
        when(accountsRepository.save(any(Accounts.class))).thenReturn(existingAccount);

        Customer existingCustomer = new Customer();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(existingCustomer);

        // when
        boolean result = underTest.updateAccount(customerDTO);

        // then
        assertTrue(result);
        verify(accountsRepository).findById(1L);
        verify(accountsRepository).save(any(Accounts.class));
        verify(customerRepository).findById(1L);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenAccountNotFound() {
        // given
        AccountsDTO accountsDTO = AccountsDTO.builder()
                .accountNumber(999L)
                .accountType("Savings")
                .branchAddress("123 Street")
                .build();

        CustomerDTO customerDTO = CustomerDTO.builder()
                .name("Test Name")
                .email("test@email.com")
                .mobileNumber("1234567890")
                .accountsDTO(accountsDTO)
                .build();

        when(accountsRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> underTest.updateAccount(customerDTO));
        verify(accountsRepository).findById(999L);
        verify(accountsRepository, never()).save(any(Accounts.class));
        verify(customerRepository, never()).findById(anyLong());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldReturnFalseWhenAccountsDTOIsNull() {
        // given
        CustomerDTO customerDTO = CustomerDTO.builder()
                .name("Test Name")
                .email("test@email.com")
                .mobileNumber("1234567890")
                .accountsDTO(null)
                .build();

        // when
        boolean result = underTest.updateAccount(customerDTO);

        // then
        assertFalse(result);
        verify(accountsRepository, never()).findById(anyLong());
        verify(accountsRepository, never()).save(any(Accounts.class));
        verify(customerRepository, never()).findById(anyLong());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenCustomerNotFound() {
        // given
        AccountsDTO accountsDTO = AccountsDTO.builder()
                .accountNumber(1L)
                .accountType("Savings")
                .branchAddress("123 Street")
                .build();

        CustomerDTO customerDTO = CustomerDTO.builder()
                .name("Test Name")
                .email("test@email.com")
                .mobileNumber("1234567890")
                .accountsDTO(accountsDTO)
                .build();

        Accounts existingAccount = new Accounts();
        existingAccount.setCustomerId(1L);
        when(accountsRepository.findById(1L)).thenReturn(Optional.of(existingAccount));
        when(accountsRepository.save(any(Accounts.class))).thenReturn(existingAccount);
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> underTest.updateAccount(customerDTO));
        verify(accountsRepository).findById(1L);
        verify(accountsRepository).save(any(Accounts.class));
        verify(customerRepository).findById(1L);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldDeleteAccountsSuccessfully() {
        // given
        Customer customer = Customer.builder()
                .mobileNumber("123456")
                .customerId(1L)
                .build();
        Accounts accounts = Accounts.builder()
                .accountNumber(1L)
                .customerId(1L)
                .accountType("Test")
                .build();

        when(customerRepository.findByMobileNumber("123456")).thenReturn(Optional.of(customer));
        doNothing().when(accountsRepository).deleteByCustomerId(customer.getCustomerId());
        doNothing().when(customerRepository).deleteById(customer.getCustomerId());

        // when
        boolean result = underTest.deleteAccounts("123456");

        // then
        verify(customerRepository).findByMobileNumber("123456");
        verify(accountsRepository).deleteByCustomerId(customer.getCustomerId());
        verify(customerRepository).deleteById(customer.getCustomerId());
        assertTrue(result);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenCustomerNotFoundForDelete() {
        // given
        when(customerRepository.findByMobileNumber("123456")).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> underTest.deleteAccounts("123456"));
        verify(customerRepository).findByMobileNumber("123456");
        verify(accountsRepository, never()).deleteByCustomerId(anyLong());
        verify(customerRepository, never()).deleteById(anyLong());
    }
}