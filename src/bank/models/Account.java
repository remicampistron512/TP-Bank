package bank.models;

import java.math.BigDecimal;
import java.util.List;

public class Account {

    private long id;
    private String accountNumber;
    private Customer accountOwner;
    private BigDecimal balance;
    private BigDecimal overdraftLimit;
    private BigDecimal dailyWithdrawalLimit;
    private List<Operation> operations;

    public Account(String accountNumber, Customer accountOwner, BigDecimal balance, BigDecimal overdraftLimit, BigDecimal dailyWithdrawalLimit, List<Operation> operations) {
        this.accountNumber = accountNumber;
        this.accountOwner = accountOwner;
        this.balance = balance;
        this.overdraftLimit = overdraftLimit;
        this.dailyWithdrawalLimit = dailyWithdrawalLimit;
        this.operations = operations;
    }

    public Account(long id,String accountNumber, Customer accountOwner, BigDecimal balance, BigDecimal overdraftLimit, BigDecimal dailyWithdrawalLimit) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.accountOwner = accountOwner;
        this.balance = balance;
        this.overdraftLimit = overdraftLimit;
        this.dailyWithdrawalLimit = dailyWithdrawalLimit;

    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Customer getAccountOwner() {
        return accountOwner;
    }

    public void setAccountOwner(Customer accountOwner) {
        this.accountOwner = accountOwner;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(BigDecimal overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    public BigDecimal getDailyWithdrawalLimit() {
        return dailyWithdrawalLimit;
    }

    public void setDailyWithdrawalLimit(BigDecimal dailyWithdrawalLimit) {
        this.dailyWithdrawalLimit = dailyWithdrawalLimit;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }
}
