package logic;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.UUID;

public class User {

    String getName() {
        return name;
    }

    private String name;
    final private UUID uuid;
    ArrayList<Account> ownedAccounts;

    public User() {
        uuid = UUID.randomUUID();
        ownedAccounts = new ArrayList<>();
    }

    public User(String name) {
        this();
        this.name = name;
    }

    UUID getID() {
        return uuid;
    }


    public StringProperty getNameSP() {
        return new SimpleStringProperty(name);
    }
}
