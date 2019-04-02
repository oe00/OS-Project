package models.bank.logic;

import controller.LauncherController;
import models.user.logic.User;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class Bank {

    private LauncherController appController;

    private HashMap<UUID, Account> bankAccounts;
    private HashMap<UUID, User> bankUsers;

    private Bank() {
        bankAccounts = new HashMap<>();
        bankUsers = new HashMap<>();
    }

    public Bank(LauncherController appController) {
        this();
        this.appController = appController;
    }

    public Account createAccount(User user, String accountName) {

        if (bankUsers.get(user.getID()) == null) {
            return null;
        }

        Account account = new Account(user.getID(), accountName);
        user.ownedAccounts.add(account);
        bankAccounts.put(account.getID(), account);

        appController.updateAccountList(account);

        return account;
    }

    public User createUser(String name) {
        User user = new User(name);
        bankUsers.put(user.getID(), user);

        appController.updateUserList(user);
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


    /**
     * withdraw4Demo() method, instantiates a Thread in order to not freeze UI, it generates a mock transaction
     * which is added to "Pending Transactions" table for demonstrating that the
     * transaction is running in the background.
     */
    public void withdraw4Demo(Account account, User user, Double amount, int delay) {

        new Thread(() -> {

            Transaction transaction = null;

            Date requestTime = new Date();

            try {
                checkTransactionPermission(account, user);

                account.checkLimit(amount);

                transaction = new Transaction(account, user, amount, "Withdraw", requestTime, delay);

            } catch (Exception e) {
                transaction = new Transaction(account, user, amount, "Withdraw", requestTime, e.getMessage(), delay);

            } finally {
                appController.addToPendingTransactionsTableList(transaction);

                withdraw(account, user, amount, delay, transaction);
            }

        }).start();
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

    private void withdraw(Account account, User user, Double amount, int delay, Transaction mock_transaction) {

        Transaction transaction = mock_transaction;

        Date requestTime = new Date();

        try {

            checkTransactionPermission(account, user);

            account.checkLimit(amount);

            appController.delayThread();

            account.updateBalance(amount, 'W');

            if (delay != 0) {
                requestTime = new Date();
            }

            transaction.completeSuccessful(account, amount, requestTime);


        } catch (Exception e) {

            appController.delayThread();

            if (delay != 0) {
                requestTime = new Date();
            }

            transaction.completeFail(account, amount, requestTime);


        } finally {

            account.transactions.add(transaction);

            appController.deleteFromPendingTransactionsTableList(transaction);

            appController.updateTransactionHistoryTableList(transaction);

        }

    }


    public void deposit4Demo(Account account, User user, Double amount, int delay) {

        new Thread(() -> {

            Transaction transaction = null;

            Date requestTime = new Date();

            try {
                checkTransactionPermission(account, user);

                transaction = new Transaction(account, user, amount, "Deposit", requestTime, delay);

            } catch (Exception e) {
                transaction = new Transaction(account, user, amount, "Deposit", requestTime, e.getMessage(), delay);
            } finally {
                appController.addToPendingTransactionsTableList(transaction);

                deposit(account, user, amount, delay, transaction);
            }

        }).start();


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

    private void deposit(Account account, User user, Double amount, int delay, Transaction mock_transaction) {

        Transaction transaction = mock_transaction;

        Date requestTime = new Date();

        try {

            appController.delayThread();

            checkTransactionPermission(account, user);

            if (delay != 0) {
                requestTime = new Date();
            }

            account.updateBalance(amount, 'D');

            transaction.completeSuccessful(account, amount, requestTime);

        } catch (Exception ignored) {

        } finally {

            account.transactions.add(transaction);

            appController.deleteFromPendingTransactionsTableList(transaction);

            appController.updateTransactionHistoryTableList(transaction);

        }

    }
}
