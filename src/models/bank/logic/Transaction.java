package models.bank.logic;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import models.user.logic.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Transaction {

    private static SimpleDateFormat date_format = new SimpleDateFormat("HH:mm:ss.SSS");

    private String completeTime;
    private String startTime;
    private String delayTime;

    private String account;
    private String user;
    private String amount;
    private String balanceAfterTransaction;
    private String type;
    private String status;
    private String transactionUUID;


    private Transaction() {
        completeTime = date_format.format(new Date());
        transactionUUID = UUID.randomUUID().toString();
    }

    private Transaction(Account account, User user, Double amount, String type, int delay) {
        this();
        this.account = account.getName();
        this.user = user.getName();
        this.amount = amount.toString();
        this.type = type;
        balanceAfterTransaction = account.getBalance().toString();
        status = "Completed";

        if (delay != 0)
            this.delayTime = delay + " Second";
        else {
            delayTime = "None";
        }
    }

    Transaction(Account account, User user, Double amount, String type, Date startTime, int delay) {
        this(account, user, amount, type, delay);
        this.startTime = date_format.format(startTime);
    }

    Transaction(Account account, User user, Double amount, String type, Date startTime, String status, int delay) {
        this(account, user, amount, type, startTime, delay);
        balanceAfterTransaction = account.getBalance().toString();
        this.status = status;
    }

    public StringProperty getIdSP() {
        return new SimpleStringProperty(transactionUUID);
    }

    public StringProperty getAccountSP() {
        return new SimpleStringProperty(account);
    }

    public StringProperty getBalanceSP() {
        return new SimpleStringProperty("$" + balanceAfterTransaction);
    }

    public StringProperty getStartTimeSP() {
        return new SimpleStringProperty(startTime);
    }

    public StringProperty getDelaySP() {
        return new SimpleStringProperty(delayTime);
    }

    public StringProperty getCompleteTimeSP() {
        return new SimpleStringProperty(completeTime);
    }

    public StringProperty getAmountSP() {
        return new SimpleStringProperty("$" + amount);
    }

    public StringProperty getStatusSP() {
        return new SimpleStringProperty(status);
    }

    public StringProperty getTypeSP() {
        return new SimpleStringProperty(type);
    }

    public StringProperty getUserSP() {
        return new SimpleStringProperty(user);
    }

    void completeSuccessful(Account account, Double newAmount, Date newDate) {
        amount = newAmount.toString();
        balanceAfterTransaction = account.getBalance().toString();
        completeTime = date_format.format(newDate);
        this.status = "Completed";
    }

    void completeFail(Account account, Double newAmount, Date newDate) {
        completeSuccessful(account, newAmount, newDate);
        this.status = "Failed";
    }
}

