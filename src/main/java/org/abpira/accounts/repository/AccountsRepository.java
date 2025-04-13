package org.abpira.accounts.repository;

import org.abpira.accounts.entities.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountsRepository extends JpaRepository<Accounts, Long> {
}
