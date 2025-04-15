package org.abpira.accounts.repository;

import org.abpira.accounts.entities.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void shouldFindCustomerByMobileNumber() {
        // given
        Customer customer = Customer.builder()
                .name("John Doe")
                .email("jde@example.com")
                .mobileNumber("1234567890")
                .createdAt(LocalDateTime.of(2025, 1, 1, 0, 0))
                .createdBy("Admin")
                .build();
        underTest.save(customer);

        // when
        Optional<Customer> found = underTest.findByMobileNumber("1234567890");

        // then
        assertThat(found)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c.getName()).isEqualTo("John Doe");
                    assertThat(c.getEmail()).isEqualTo("jde@example.com");
                    assertThat(c.getCreatedAt()).isEqualTo(LocalDateTime.of(2025, 1, 1, 0, 0));
                    assertThat(c.getCreatedBy()).isEqualTo("Admin");
                    assertThat(c.getMobileNumber()).isEqualTo("1234567890");
                });
    }

    @Test
    void shouldReturnEmptyWhenMobileNumberNotFound() {
        // when
        Optional<Customer> found = underTest.findByMobileNumber("1234567890");

        // then
        assertThat(found).isEmpty();
    }
}