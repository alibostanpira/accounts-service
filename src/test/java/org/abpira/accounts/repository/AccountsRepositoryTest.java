package org.abpira.accounts.repository;

import org.abpira.accounts.entities.Accounts;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
class AccountsRepositoryTest {

    @Autowired
    private AccountsRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void accountShouldExist() {
        // given
        Accounts accounts = Accounts.builder()
                .accountNumber(1L)
                .accountType("Savings")
                .branchAddress("123 Main Street, New York")
                .createdAt(LocalDateTime.of(2025, 1, 1, 0, 0))
                .createdBy("Admin")
                .build();
        underTest.save(accounts);

        // when
        Optional<Accounts> accountOptional = underTest.findById(1L);

        // then
        assertThat(accountOptional.isPresent()).isTrue();

    }

    @Test
    void accountShouldNotExist() {
        // given
        Accounts accounts = Accounts.builder()
                .accountNumber(1L)
                .accountType("Savings")
                .branchAddress("123 Main Street, New York")
                .createdAt(LocalDateTime.of(2025, 1, 1, 0, 0))
                .createdBy("Admin")
                .build();
        underTest.save(accounts);

        // when
        Optional<Accounts> accountOptional = underTest.findById(2L);

        // then
        assertThat(accountOptional.isPresent()).isFalse();
    }

    @Test
    void shouldFindAccountByCustomerId() {
        // given
        Accounts accounts = Accounts.builder()
                .accountNumber(1L)
                .customerId(1L)
                .accountType("Savings")
                .branchAddress("New York")
                .createdAt(LocalDateTime.of(2025, 1, 1, 0, 0))
                .createdBy("Admin")
                .build();
        underTest.save(accounts);

        // when
        Optional<Accounts> found = underTest.findByCustomerId(1L);

        // then
        assertThat(found)
                .isPresent()
                .hasValueSatisfying(a -> {
                    assertThat(a.getCustomerId()).isEqualTo(1L);
                    assertThat(a.getAccountType()).isEqualTo("Savings");
                    assertThat(a.getBranchAddress()).isEqualTo("New York");
                });
    }

    @Test
    void shouldReturnEmptyWhenCustomerIdNotFound() {
        // when
        Optional<Accounts> found = underTest.findByCustomerId(999L);

        // then
        assertThat(found).isEmpty();
    }

}