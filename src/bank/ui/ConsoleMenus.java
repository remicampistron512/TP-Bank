package bank.ui;

import bank.business.BankingService;
import bank.daos.DaoException;
import bank.models.Account;
import bank.models.Customer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class ConsoleMenus {
  private final Scanner in = new Scanner(System.in);
  private static final String CHOICE_TEXT = "Choose: ";
  private final BankingService service;

  public ConsoleMenus(BankingService service) {
    this.service = service;
  }

  public void run() {
    mainMenu();   // blocks until user exits
    System.out.println("Goodbye.");
  }

  private void mainMenu() {
    while (true) {
      System.out.println("\n=== MAIN MENU ===");
      System.out.println("1) Customers");
      System.out.println("2) Accounts");
      System.out.println("0) Exit");

      int choice = readInt(CHOICE_TEXT, 0, 2);
      switch (choice) {
        case 1 -> customersMenu();
        case 2 -> accountsMenu();
        case 0 -> { return; } // exit application
      }
    }
  }

  private int readInt(String prompt, int min, int max) {
    while (true) {
      System.out.print(prompt);
      String s = in.nextLine().trim();
      try {
        int v = Integer.parseInt(s);
        if (v < min || v > max) {
          System.out.printf("Enter %d..%d%n", min, max);
          continue;
        }
        return v;
      } catch (NumberFormatException e) {
        System.out.println("Invalid number.");
      }
    }
  }

  private void accountsMenu() {
    while (true) {
      System.out.println("\n--- Accounts ---");
      System.out.println("1) List accounts");
      System.out.println("2) Open account");
      System.out.println("3) Deposit");
      System.out.println("4) Transfer");
      System.out.println("0) Back");

      int choice = readInt(CHOICE_TEXT, 0, 3);
      switch (choice) {
        case 1 -> listAccounts();
        case 2 -> openAccountMenu();
        case 3 -> depositChooseAccount();
        case 4 -> transfer();
        case 0 -> { return; }
      }
    }
  }

    private void listAccounts() {
        List<Account> accountsList = service.listAccounts();

        if (accountsList == null || accountsList.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }

        System.out.println("============================== Accounts ==============================");
        System.out.printf("%-4s %-16s %-25s %12s%n",
                "No.", "Account Number", "Owner", "Balance");
        System.out.println("---------------------------------------------------------------------");

        
        for (Account account : accountsList) {
            String ownerName;
            String first = account.getAccountOwner().getFirstName() == null ? "" : account.getAccountOwner().getFirstName();
            String last  = account.getAccountOwner().getLastName()  == null ? "" : account.getAccountOwner().getLastName();
            ownerName = (first + " " + last).trim();
            if (ownerName.isEmpty()) ownerName = "(unknown)";


            BigDecimal balance = account.getBalance() == null ? BigDecimal.ZERO : account.getBalance();

            System.out.printf("%-4d %-16s %-25s %12.2f%n",
                    account.getId(),
                    account.getAccountNumber(),
                    truncate(ownerName, 25),
                    balance);
        }

        System.out.println("=====================================================================");
    }

    // Helper to avoid long names breaking the table alignment
    private String truncate(String s, int max) {
        if (s == null) return "";
        return (s.length() <= max) ? s : s.substring(0, max - 3) + "...";
    }


    private void transfer() {
        listAccounts();
        List<Account> accountsList = service.listAccounts();
        System.out.println("\n--- Transfer from which Account ? ---");
        int choice = readInt(CHOICE_TEXT, 0, accountsList.size());
        Account sourceAccount = service.getAccountById(choice);
        transferChooseTarget(sourceAccount);
  }

    private void transferChooseTarget(Account sourceAccount) {
        listAccounts();
        List<Account> accountsList = service.listAccounts();
        System.out.println("\n--- Transfer to which Account ? ---");
        int choice = readInt(CHOICE_TEXT, 0, accountsList.size());
        Account targetAccount = service.getAccountById(choice);
        transferChooseAmount(sourceAccount,targetAccount);
    }

    private void transferChooseAmount(Account sourceAccount, Account targetAccount) {
        System.out.println("\n--- How much ? ---");
        String s = in.nextLine().trim();
        BigDecimal amount = new BigDecimal(s);
        service.transfer(amount,sourceAccount,targetAccount);
    }

    private void depositChooseAccount() {
      listAccounts();
      List<Account> accountsList = service.listAccounts();
      System.out.println("\n--- Deposit to  which Account ? ---");
      int choice = readInt(CHOICE_TEXT, 0, accountsList.size());
      Account account = service.getAccountById(choice);
      depositChooseAmount(account);

  }

    private void depositChooseAmount( Account account) {

        System.out.println("\n--- How much ? ---");
        String s = in.nextLine().trim();
        BigDecimal amount = new BigDecimal(s);
        service.deposit(account,amount);


    }

    private void openAccountMenu() {
      while (true) {
          System.out.println("\n--- Open an account for which user ? ---");

          listCustomers();

          List<Customer> customersList = service.listCustomers();
          int choice = readInt(CHOICE_TEXT, 0, customersList.size());

          if(choice == 0){
              return;
          } else {
              String accountName = generateAccountName();
              openAccount(customersList.get(choice-1),accountName);
          }

      }
  }



    // Génère: FR-XXXX-XXXX (X = chiffre)
    private String generateAccountName() {
        int part1 = ThreadLocalRandom.current().nextInt(0, 10000); // 0..9999
        int part2 = ThreadLocalRandom.current().nextInt(0, 10000); // 0..9999
        return String.format("FR-%04d-%04d", part1, part2);
    }

    private void openAccount(Customer customer,String accountName) {
        try {
            service.createAccountForCustomer(customer,accountName);
            System.out.printf("An account (%s) for %s %s has been created",accountName,customer.getFirstName(),
                    customer.getLastName());
        }  catch (DaoException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void customersMenu() {
    while (true) {
      System.out.println("\n--- Customers ---");
      System.out.println("1) List customers");
      System.out.println("2) Create customer");
      System.out.println("0) Back");

      int choice = readInt(CHOICE_TEXT, 0, 2);
      switch (choice) {
        case 1 -> listCustomers();
        case 2 -> createCustomerMenu();
        case 0 -> { return; } // back to main menu
      }
    }
  }

  private void createCustomerMenu() {
    while (true) {
      System.out.println("\n--- Customer creation ---");
      System.out.println("0) Back");

      String[] fullName = getFullName();
      if(fullName.length != 0 ) {
          if (!fullName[0].equals("0")) {
              createCustomer(fullName[0], fullName[1]);
          }
          return;
      }
    }
  }

  private String[] getFullName() {


    try {
      System.out.println("Enter full name : ");
      String fullName = in.nextLine().trim();
      String trimmed = fullName.trim();
      return (trimmed.split("\\s+", 2));
    } catch (Exception  e){
      System.out.print("Customer Full name : ");
      return new String[0];
    }
  }

  private void listCustomers() {

    List<Customer> customersList = service.listCustomers();

    if (customersList.isEmpty()) {
      System.out.println("No customers found.");
      return;
    }

    System.out.println("============== Customers ==============");
    System.out.printf("%-4s %-20s %-20s%n", "No.", "First name", "Last name");
    System.out.println("---------------------------------------");

    int i = 1;
    for (Customer customer : customersList) {
      System.out.printf("%-4d %-20s %-20s%n", i++, customer.getFirstName(), customer.getLastName());
    }
    System.out.println("=======================================");

  }

  private void createCustomer(String firstName,String lastName) {
    try {
      service.createCustomer(firstName,lastName);
      System.out.print ("l'utilisateur " + firstName + " " +  lastName +" a été créé");
    }  catch (DaoException e) {
      System.out.println("Database error: " + e.getMessage());
    }
  }

}
