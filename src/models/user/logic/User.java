package models.user.logic;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import models.bank.logic.Account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class User implements Serializable {

    public String getName() {
        return name;
    }

    private String name;
    final private UUID uuid;
    public final ArrayList<Account> ownedAccounts;

    private User() {
        uuid = UUID.randomUUID();
        ownedAccounts = new ArrayList<>();
    }

    public User(String name) {
        this();
        this.name = name;
    }

    public UUID getID() {
        return uuid;
    }


    public StringProperty getNameSP() {
        return new SimpleStringProperty(name);
    }
}
