package models.bank.logic;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Account implements Serializable {
    private String name;
    final public UUID uuid;
    private UUID accountOwner;

    public ArrayList<UUID> authenticatedUsers;

    private Double balance;
    ArrayList<Transaction> transactions;

    private Account() {
        uuid = UUID.randomUUID();
    }

    Account(UUID accountOwner, String name) {
        this();
        this.name = name;
        this.accountOwner = accountOwner;
        balance = (double) 0;

        authenticatedUsers = new ArrayList<>();
        authenticatedUsers.add(accountOwner);

        transactions = new ArrayList<>();
    }

    UUID getID() {
        return uuid;
    }

    Double getBalance() {
        return balance;
    }

    void checkLimit(Double amount) throws Exception {
        if (balance - amount < 0) {
            throw new Exception("Failed / Insufficient Funds");
        }
    }

    UUID getAccountOwnerID() {
        return accountOwner;
    }

    public StringProperty getNameSP() {
        return new SimpleStringProperty(name);
    }

    String getName() {
        return name;
    }

    public StringProperty getBalanceSP() {
        return new SimpleStringProperty("$" + balance.toString());
    }

    public void updateBalance(Double amount, char type) {
        switch (type) {
            case 'D':
                balance += amount;
                break;
            case 'W':
                balance -= amount;
                break;
            default:
                break;
        }
    }
}
