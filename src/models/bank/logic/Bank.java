package models.bank.logic;

import controller.LauncherController;
import models.bank.controller.BankController;
import models.user.logic.User;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class Bank {

    private BankController bankController;
    private LauncherController launcherController;

    public final HashMap<UUID, Account> bankAccounts;
    private final HashMap<UUID, User> bankUsers;

    private Bank() {
        bankAccounts = new HashMap<>();
        bankUsers = new HashMap<>();
    }

    public Bank(LauncherController launcherController) {
        this();
        this.launcherController = launcherController;
    }

    public void setController(BankController bc) {
        this.bankController = bc;
    }

    public Account createAccount(User user, String accountName) {

        if (bankUsers.get(user.getID()) == null) {
            return null;
        }

        Account account = new Account(user.getID(), accountName);
        user.ownedAccounts.add(account);
        bankAccounts.put(account.getID(), account);

        return account;
    }

    public User createUser(String name) {
        User user = new User(name);
        bankUsers.put(user.getID(), user);

        launcherController.updateUserList(user);
        return user;
    }

    private void checkUserExists(User user) throws Exception {

        User userExists = bankUsers.get(user.getID());

        if (userExists == null) {
            throw new Exception("UserApp not found.");
        }
    }

    private void checkAccountExists(Account account) throws Exception {

        Account accountExists = bankAccounts.get(account.getID());

        if (accountExists == null) {
            throw new Exception("Account not found.");
        }
    }

    private void checkAddAccessPermission(Account account, User user) throws Exception {

        checkAccountExists(account);
        checkUserExists(user);

        if (!user.getID().equals(account.getAccountOwnerID())) {
            throw new Exception("UserApp is not the owner of the account");
        }
    }

    public void addAccessToAccount(Account account, User owner, User newUser) throws Exception {

        checkAddAccessPermission(account, owner);

        account.authenticatedUsers.add(newUser.getID());
    }

    private void checkTransactionPermission(Account account, User user) throws Exception {

        checkAccountExists(account);
        checkUserExists(user);
        checkAccessPermission(account, user);

    }

    private void checkAccessPermission(Account account, User user) throws Exception {

        if (account.authenticatedUsers.stream().noneMatch(u -> u.equals(user.getID()))) {
            throw new Exception("Failed / UserApp Auth.");
        }
    }


    /** sync **/

    /**
     * withdraw() method checks for models.models.user permissions, account limit, completes the transaction,
     * adds it to account transaction history, deletes it from "Pending Transactions" table and adds it
     * to "Transaction History" table.
     * note: it is taking a "mock_transaction" as input because UUID in "mock_transaction" is unique and already
     * added to "Pending Transactions" table, if a new transaction is instantiated its UUID will be different from
     * the "mock_transaction".
     **/

    public void withdraw(Transaction mock_transaction) {

        Transaction transaction = mock_transaction;

        Date requestTime = new Date();

        try {

            checkTransactionPermission(transaction.getAccount(), transaction.getUser());

            this.bankAccounts.get(transaction.getAccount().uuid).checkLimit(transaction.getAmount());

            bankController.delayThread();

            if (transaction.getDelay() != 0) {
                requestTime = new Date();
            }

            this.bankAccounts.get(transaction.getAccount().uuid).updateBalance(transaction.getAmount(), 'W');

            transaction.completeSuccessful(this.bankAccounts.get(transaction.getAccount().uuid), transaction.getAmount(), requestTime);


        } catch (Exception e) {

            bankController.delayThread();

            if (transaction.getDelay() != 0) {
                requestTime = new Date();
            }

            transaction.completeFail(this.bankAccounts.get(transaction.getAccount().uuid), transaction.getAmount(), requestTime);

        } finally {

            transaction.getAccount().transactions.add(transaction);
        }

    }

    /** sync **/

    /**
     * deposit() method checks for models.models.user permissions, completes the transaction,
     * adds it to account transaction history, deletes it from "Pending Transactions" table and adds it
     * to "Transaction History" table.
     * note: it is taking a "mock_transaction" as input because UUID in "mock_transaction" is unique and already
     * added to "Pending Transactions" table, if a new transaction is instantiated its UUID will be different from
     * the "mock_transaction".
     **/

    public void deposit(Transaction mock_transaction) {

        Transaction transaction = mock_transaction;

        Date requestTime = new Date();

        try {

            bankController.delayThread();

            if (transaction.getDelay() != 0) {
                requestTime = new Date();
            }

            this.bankAccounts.get(transaction.getAccount().uuid).updateBalance(transaction.getAmount(), 'D');

            transaction.completeSuccessful(this.bankAccounts.get(transaction.getAccount().uuid), transaction.getAmount(), requestTime);

        } catch (Exception ignored) {

        } finally {

            transaction.getAccount().transactions.add(transaction);

        }

    }
}
