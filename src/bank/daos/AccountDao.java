package bank.daos;

import bank.models.Account;
import bank.models.Customer;
import bank.models.Operation;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDao {
    public Account createAccount(Customer customer, String accountNumber) {
        String sqlAccount = "INSERT INTO accounts(customer_id, account_number) VALUES (?, ?)";

        Connection cn = null;
        try {
            cn = ConnectionFactory.getConnection();
            cn.setAutoCommit(false);

            System.err.println("SQL: " + sqlAccount);
            System.err.println("params: [1]=" + customer.getUserId() + ", [2]=" + accountNumber);

            try (PreparedStatement ps = cn.prepareStatement(sqlAccount)) {
                ps.setLong(1, customer.getUserId());   // Vérifie si ce doit être customer.getId()
                ps.setString(2, accountNumber);
                ps.executeUpdate();
            }

            cn.commit();

            List<Operation> operations = List.of(); // mieux que null
            return new Account(
                    accountNumber,
                    customer,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    operations
            );

        } catch (SQLException e) {
            if (cn != null) {
                try { cn.rollback(); } catch (SQLException ignored) {}
            }
            throw new DaoException("Failed to create account.", e);
        } finally {
            if (cn != null) {
                try { cn.close(); } catch (SQLException ignored) {}
            }
        }
    }


    public List<Account> findAll() {
        // Multi-line SQL (text block) for readability
        String sql = """
        SELECT a.id, a.account_number,a.customer_id,a.balance,a.overdraft_limit,a.daily_withdrawal_limit
        FROM accounts a
        ORDER BY a.id
        """;

        // Collection to store mapped accounts objects
        List<Account> accounts = new ArrayList<>();

        // Open connection, prepare statement, execute query; all resources auto-close
        try (Connection cn = ConnectionFactory.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Iterate through result rows and map each row to a accounts object
            while (rs.next()) {
                long id = rs.getLong("id");
                String accountNumber = rs.getString("account_number");
                long  customerId = rs.getLong("customer_id");
                BigDecimal balance = rs.getBigDecimal("balance");
                BigDecimal overdraftLimit = rs.getBigDecimal("overdraft_limit");
                BigDecimal dailyWithdrawalLimit = rs.getBigDecimal("daily_withdrawal_limit");





                CustomerDao customerDao = new CustomerDao();
                Customer customer = customerDao.findById(customerId);

                // Build the domain object from the current row



                accounts.add(new Account(id, accountNumber, customer,balance,overdraftLimit,dailyWithdrawalLimit));
            }

            // Return the full list
            return accounts;

        } catch (SQLException e) {
            // Wrap SQL error into a DAO exception
            throw new DaoException("Failed to list accounts.", e);
        }
    }

    public Account findById(long accountId) {
        String sql = """
        SELECT a.id, a.account_number, a.customer_id, a.balance, a.overdraft_limit, a.daily_withdrawal_limit
        FROM accounts a
        WHERE a.id = ?
        """;

        try (Connection cn = ConnectionFactory.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null; // or throw new DaoException("Account not found: " + accountId);
                }

                long id = rs.getLong("id");
                String accountNumber = rs.getString("account_number");
                long customerId = rs.getLong("customer_id");
                BigDecimal balance = rs.getBigDecimal("balance");
                BigDecimal overdraftLimit = rs.getBigDecimal("overdraft_limit");
                BigDecimal dailyWithdrawalLimit = rs.getBigDecimal("daily_withdrawal_limit");

                CustomerDao customerDao = new CustomerDao();
                Customer customer = customerDao.findById(customerId);

                return new Account(id, accountNumber, customer, balance, overdraftLimit, dailyWithdrawalLimit);
            }

        } catch (SQLException e) {
            throw new DaoException("Failed to find account by id: " + accountId, e);
        }
    }

    public Boolean deposit(Account account, BigDecimal amount) {
        if (account == null) {
            throw new IllegalArgumentException("Account must not be null.");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be > 0.");
        }

        // If your 'amount' represents euros as an integer, this produces "amount.00".
        // If your 'amount' represents cents, change this to BigDecimal.valueOf(amount, 2).


        String sql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";

        try (Connection cn = ConnectionFactory.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setBigDecimal(1, amount);
            ps.setLong(2, account.getId());

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new DaoException("Deposit failed: account not found (id=" + account.getId() + ").");
            }

            // Keep the in-memory object consistent (optional but practical)
            BigDecimal current = account.getBalance() == null ? BigDecimal.ZERO : account.getBalance();
            account.setBalance(current.add(amount).setScale(2));

        } catch (SQLException e) {
            throw new DaoException("Failed to deposit on account id=" + account.getId(), e);
        }
        return true;
    }
    public boolean transfer(BigDecimal amount,Account sourceAccount,Account targetAccount){
        String sourceSql = "UPDATE accounts SET balance = balance - ? WHERE id = ?";
        try (Connection cn = ConnectionFactory.getConnection();
             PreparedStatement ps = cn.prepareStatement(sourceSql)) {

            ps.setBigDecimal(1, amount);
            ps.setLong(2, sourceAccount.getId());

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new DaoException("Transfer failed: account not found (id=" + sourceAccount.getId() + ").");
            }

            // Keep the in-memory object consistent (optional but practical)
            BigDecimal current = sourceAccount.getBalance() == null ? BigDecimal.ZERO : sourceAccount.getBalance();
            sourceAccount.setBalance(current.add(amount).setScale(2));

        } catch (SQLException e) {
            throw new DaoException("Failed to deposit on account id=" + sourceAccount.getId(), e);
        }

        String targetSql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
        try (Connection cn = ConnectionFactory.getConnection();
             PreparedStatement ps = cn.prepareStatement(targetSql)) {

            ps.setBigDecimal(1, amount);
            ps.setLong(2, targetAccount.getId());

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new DaoException("Transfer failed: account not found (id=" + targetAccount.getId() + ").");
            }

            // Keep the in-memory object consistent (optional but practical)
            BigDecimal current = targetAccount.getBalance() == null ? BigDecimal.ZERO : targetAccount.getBalance();
            targetAccount.setBalance(current.add(amount).setScale(2));

        } catch (SQLException e) {
            throw new DaoException("Failed to deposit on account id=" + sourceAccount.getId(), e);
        }






        return true;
    }
}

