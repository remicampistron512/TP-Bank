package bank.business;

import bank.daos.AccountDao;
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

    public List<Account> listAccounts() {
        AccountDao accountDao = new AccountDao();
        return accountDao.findAll();
    }

  // Accounts
  public void createAccountForCustomer(Customer customer,
      String accountName) {
      AccountDao accountDao = new AccountDao();
      accountDao.createAccount(customer,accountName);
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

    public Account getAccountById(int accountId) {
        AccountDao accountDao = new AccountDao();
        return accountDao.findById(accountId);
    }

    public Boolean deposit(Account account, BigDecimal amount) {
        AccountDao accountDao = new AccountDao();
        return accountDao.deposit(account,amount);
    }

    public Boolean transfer(BigDecimal amount,Account sourceAccount,Account targetAccount){
        AccountDao accountDao = new AccountDao();
        return accountDao.transfer(amount,sourceAccount,targetAccount);
    }
}
