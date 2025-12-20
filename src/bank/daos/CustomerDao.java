package bank.daos;

import bank.models.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) responsible for Customer persistence and retrieval.
 * This implementation uses JDBC via ConnectionFactory.
 */
public class CustomerDao {

  /**
   * Creates a new Customer by:
   *  1) inserting a row into the users table (first_name, last_name)
   *  2) inserting a row into the customers table referencing the created user_id
   *
   * The method runs both inserts inside a single SQL transaction.
   *
   * @param firstName Customer first name
   * @param lastName  Customer last name
   * @return a Customer domain object representing the created record
   * @throws DaoException if insertion fails or generated key is missing
   */
  public Customer createCustomer(String firstName, String lastName) {
    // SQL statement to insert the base "user" record
    String sqlUser = "INSERT INTO users(first_name, last_name) VALUES (?, ?)";
    // SQL statement to create the "customer" record linked to the user_id
    String sqlCustomer = "INSERT INTO customers(user_id) VALUES (?)";

    // Acquire a DB connection; try-with-resources ensures it is closed automatically
    try (Connection cn = ConnectionFactory.getConnection()) {
      // Start an explicit transaction so both inserts succeed/fail together
      cn.setAutoCommit(false);

      long userId;

      // Insert into users and request the generated primary key
      try (PreparedStatement ps = cn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
        // Bind parameters to prevent SQL injection and handle escaping properly
        ps.setString(1, firstName);
        ps.setString(2, lastName);
        // Execute the insert
        ps.executeUpdate();

        // Read the auto-generated key (id) returned by the DB
        try (ResultSet keys = ps.getGeneratedKeys()) {
          // If no key is returned, the insert is inconsistent with expectations
          if (!keys.next()) throw new DaoException("User inserted but no generated key returned.");
          userId = keys.getLong(1);
        }
      }

      // Insert into customers using the newly created userId as FK
      try (PreparedStatement ps = cn.prepareStatement(sqlCustomer)) {
        ps.setLong(1, userId);
        ps.executeUpdate();
      }

      // Commit the transaction (both inserts are persisted)
      cn.commit();
      // Restore default auto-commit mode (optional but keeps connection state clean)
      cn.setAutoCommit(true);

      // Return a domain object reflecting the new persisted customer
      return new Customer(userId, firstName, lastName);

    } catch (SQLException e) {
      // Wrap low-level SQL exceptions into a DAO-layer exception
      throw new DaoException("Failed to create customer.", e);
    }
  }


  /**
   * Retrieves all customers by joining users and customers tables.
   * Results are ordered by last name then first name.
   *
   * @return list of all customers
   * @throws DaoException if the query fails
   */
  public List<Customer> findAll() {
    // Multi-line SQL (text block) for readability
    String sql = """
        SELECT u.id, u.first_name, u.last_name
        FROM users u
        JOIN customers c ON c.user_id = u.id
        ORDER BY u.last_name, u.first_name
        """;

    // Collection to store mapped Customer objects
    List<Customer> customers = new ArrayList<>();

    // Open connection, prepare statement, execute query; all resources auto-close
    try (Connection cn = ConnectionFactory.getConnection();
        PreparedStatement ps = cn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {

      // Iterate through result rows and map each row to a Customer object
      while (rs.next()) {
        long id = rs.getLong("id");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");

        // Build the domain object from the current row
        // Adjust this line if your Customer constructor is different
        customers.add(new Customer(id, firstName, lastName));
      }

      // Return the full list
      return customers;

    } catch (SQLException e) {
      // Wrap SQL error into a DAO exception
      throw new DaoException("Failed to list customers.", e);
    }
  }


  /**
   * Retrieves a customer by its id (stored as users.id and referenced by customers.user_id).
   *
   * @param customerId the customer/user id to search for
   * @return the Customer if found
   * @throws DaoException if not found or SQL error occurs
   */
  public Customer findById(long customerId) {
    // Multi-line SQL (text block) selecting a single customer by id
    String sql = """
            SELECT u.id, u.first_name, u.last_name
            FROM users u
            JOIN customers c ON c.user_id = u.id
            WHERE u.id = ?
            """;

    // Open connection and prepare the statement; resources auto-close
    try (Connection cn = ConnectionFactory.getConnection();
        PreparedStatement ps = cn.prepareStatement(sql)) {

      // Bind the id parameter
      ps.setLong(1, customerId);

      // Execute the query and map the first row if present
      try (ResultSet rs = ps.executeQuery()) {
        // If no row, signal that the customer does not exist
        if (!rs.next()) throw new DaoException("Customer not found: id=" + customerId);
        // Map row -> Customer domain object
        return new Customer(
            rs.getLong("id"),
            rs.getString("first_name"),
            rs.getString("last_name")
        );
      }

    } catch (SQLException e) {
      // Wrap SQL error into a DAO exception
      throw new DaoException("Failed to find customer.", e);
    }
  }
}
