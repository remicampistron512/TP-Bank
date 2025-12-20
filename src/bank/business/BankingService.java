package bank.business;

import bank.daos.CustomerDao;
import bank.models.Account;
import bank.models.Customer;
import bank.models.Operation;

import java.math.BigDecimal;
import java.util.List;

/**
 * Minimal skeleton (blank methods) for your business layer.
 * Put this file in: src/bank/business/BankingService.java
 */
public class BankingService {

  // Customers
  public Customer createCustomer(String firstName, String lastName) {
    CustomerDao customerDao = new CustomerDao();
    return customerDao.createCustomer(firstName,lastName);
  }

  public List<Customer> listCustomers() {
    CustomerDao customerDao = new CustomerDao();
    return customerDao.findAll();
  }

  // Accounts
  public void createAccountForCustomer(long customerId,
      String accountNumber,
      BigDecimal initialBalance,
      BigDecimal overdraftLimit,
      BigDecimal dailyWithdrawalLimit) {
    throw new UnsupportedOperationException("Not implemented");
  }

  public Account getAccount(String accountNumber, boolean includeHistory) {
    throw new UnsupportedOperationException("Not implemented");
  }

  // Operations
  public void deposit(String accountNumber, BigDecimal amount, Long advisorId) {
    throw new UnsupportedOperationException("Not implemented");
  }

  public void withdraw(String accountNumber, BigDecimal amount, Long advisorId) {
    throw new UnsupportedOperationException("Not implemented");
  }

  public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount, Long advisorId) {
    throw new UnsupportedOperationException("Not implemented");
  }

  public List<Operation> getHistory(String accountNumber) {
    throw new UnsupportedOperationException("Not implemented");
  }
}
