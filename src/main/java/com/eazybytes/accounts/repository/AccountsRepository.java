package com.eazybytes.accounts.repository;

import com.eazybytes.accounts.entity.Accounts;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts, Long> {
   public Optional<Accounts> findByCustomerId(Long customerId);
   @Transactional
   @Modifying
   public void deleteByCustomerId(Long customerId);
}
