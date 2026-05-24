package com.eazybytes.accounts.service.impl;

import com.eazybytes.accounts.constants.AccountsConstants;
import com.eazybytes.accounts.dto.AccountsDto;
import com.eazybytes.accounts.dto.CustomerDto;
import com.eazybytes.accounts.entity.Accounts;
import com.eazybytes.accounts.entity.Customer;
import com.eazybytes.accounts.exception.CustomerAlreadyExistsException;
import com.eazybytes.accounts.exception.ResourceNotFoundException;
import com.eazybytes.accounts.mapper.AccountsMapper;
import com.eazybytes.accounts.mapper.CustomerMapper;
import com.eazybytes.accounts.repository.AccountsRepository;
import com.eazybytes.accounts.repository.CustomerRepository;
import com.eazybytes.accounts.service.IAccountsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AccountsServiceImpl implements IAccountsService {

    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;
        @Override
        public void createAccount(CustomerDto customerDto) {
            Customer saveCustomer = CustomerMapper.mapToCustomer(customerDto, new Customer());
            Optional<Customer> optionalCustomer = customerRepository.findByMobileNumber(customerDto.getMobileNumber());
            if(optionalCustomer.isPresent()){
                throw  new CustomerAlreadyExistsException("Mobile number is already present : "+customerDto.getMobileNumber());
                }
            saveCustomer.setCreatedAt(LocalDateTime.now());
            saveCustomer.setCreatedBy("Anil");
            customerRepository.save(saveCustomer);
            accountsRepository.save(createNewAccount(saveCustomer));
        }

    @Override
    public CustomerDto fetchAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString())
        );
        CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
        customerDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));
        return customerDto;
    }

    @Override
    public boolean updateAccount(CustomerDto customerDto){
            boolean isUpdate = false;

            AccountsDto accountsDto = customerDto.getAccountsDto();
            if(accountsDto != null){
                Accounts accounts = accountsRepository.findById(accountsDto.getAccountNumber()).orElseThrow(() ->
                        new ResourceNotFoundException("Account","AccountNumber",accountsDto.getAccountNumber().toString()));
                AccountsMapper.mapToAccounts(accountsDto, accounts);
                accountsRepository.save(accounts);

                Long customerId = accounts.getCustomerId();
                Customer customer = customerRepository.findById(customerId).orElseThrow(()->
                        new ResourceNotFoundException("Customer","CustomerId", customerId.toString()));
                CustomerMapper.mapToCustomer(customerDto, customer);
                customerRepository.save(customer);
                isUpdate = true;
            }

            return isUpdate;

    }

    @Override
    public boolean deleteAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(() ->
                new ResourceNotFoundException("Customer", "Mobile ", mobileNumber));
        accountsRepository.deleteByCustomerId(customer.getCustomerId());
        customerRepository.deleteById(customer.getCustomerId());
        return true;
    }


    /**
     * @param customer - Customer Object
     * @return the new account details
     */
    private Accounts createNewAccount(Customer customer) {
        Accounts newAccount = new Accounts();
        newAccount.setCustomerId(customer.getCustomerId());
        long randomAccNumber = 1000000000L + new Random().nextInt(900000000);
        newAccount.setCreatedAt(LocalDateTime.now());
        newAccount.setAccountType(AccountsConstants.SAVINGS);
        newAccount.setBranchAddress(AccountsConstants.ADDRESS);
        newAccount.setCreatedBy("Anil");
        newAccount.setAccountNumber(randomAccNumber);
        return newAccount;
    }

}
