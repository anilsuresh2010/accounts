package com.eazybytes.accounts.repository;

import com.eazybytes.accounts.entity.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts, Long> {
}
