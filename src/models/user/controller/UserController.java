package models.user.controller;

import controller.LauncherController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.bank.logic.Account;
import models.bank.logic.Transaction;
import models.user.logic.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;

public class UserController {

    public User user;

    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;

    private Socket socket;


    public void setUser(User user) {
        this.user = user;
    }

    public void initialize() {

        initAccountTable();
        initTransactionTable();
        initPendingTable();
    }

    public void updateUser() {
        usernameLabel.setText(user.getName());
    }

    public void startUser() {
        new Thread(() -> {
            try {
                socket = new Socket("localhost", LauncherController.port);

                toServer = new ObjectOutputStream(socket.getOutputStream());
                fromServer = new ObjectInputStream(socket.getInputStream());

                toServer.writeObject(user);

                updateAccountList();

        } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    private void initAccountTable() {
        accountTable_Account.setCellValueFactory(cD -> cD.getValue().getNameSP());
        accountTable_Balance.setCellValueFactory(cD -> cD.getValue().getBalanceSP());

        accountTable.setItems(accountTableList);

    }


    private void initTransactionTable() {

        transactionHistoryTable_ID.setCellValueFactory(cD -> cD.getValue().getIdSP());
        transactionHistoryTable_StartTime.setCellValueFactory(cD -> cD.getValue().getStartTimeSP());
        transactionHistoryTable_CompleteTime.setCellValueFactory(cD -> cD.getValue().getCompleteTimeSP());
        transactionHistoryTable_Account.setCellValueFactory(cD -> cD.getValue().getAccountSP());
        transactionHistoryTable_Amount.setCellValueFactory(cD -> cD.getValue().getAmountSP());
        transactionHistoryTable_Balance.setCellValueFactory(cD -> cD.getValue().getBalanceSP());
        transactionHistoryTable_Type.setCellValueFactory(cD -> cD.getValue().getTypeSP());
        transactionHistoryTable_Result.setCellValueFactory(cD -> cD.getValue().getStatusSP());
        transactionHistoryTable_User.setCellValueFactory(cD -> cD.getValue().getUserSP());

        transactionHistoryTable.setItems(transactionHistoryTableList);
    }

    private void initPendingTable() {

        pendingTransactionsTable_ID.setCellValueFactory(cD -> cD.getValue().getIdSP());
        pendingTransactionsTable_StartTime.setCellValueFactory(cD -> cD.getValue().getStartTimeSP());
        pendingTransactionsTable_Account.setCellValueFactory(cD -> cD.getValue().getAccountSP());
        pendingTransactionsTable_Type.setCellValueFactory(cD -> cD.getValue().getTypeSP());
        pendingTransactionsTable_Amount.setCellValueFactory(cD -> cD.getValue().getAmountSP());
        pendingTransactionsTable_Balance.setCellValueFactory(cD -> cD.getValue().getBalanceSP());

        pendingTransactionsTable.setItems(pendingTransactionsTableList);
    }

    private ObservableList<Account> accountTableList = FXCollections.observableArrayList();
    private ObservableList<Transaction> transactionHistoryTableList = FXCollections.observableArrayList();
    private ObservableList<Transaction> pendingTransactionsTableList = FXCollections.observableArrayList();


    public void updateAccountList() {

        accountTableList.clear();

        try {
            ArrayList<Account> accounts = (ArrayList<Account>) fromServer.readObject();

            for (Account a : accounts) {
                accountTableList.add(a);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        accountTable.refresh();
    }

    public void addToTransactionHistoryTable() {

        try {

            Transaction transaction = (Transaction) fromServer.readObject();

            transactionHistoryTableList.add(transaction);
            transactionHistoryTable.refresh();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    public void addToPendingTransactionsTableList(Transaction transaction) {
        pendingTransactionsTableList.add(transaction);
        pendingTransactionsTable.refresh();
    }

    public void deleteFromPendingTransactionsTableList(Transaction transaction) {
        pendingTransactionsTableList.remove(transaction);
        pendingTransactionsTable.refresh();
    }

    private boolean checkTransactionInput() {

        Account account = accountTable.getSelectionModel().getSelectedItem();

        try {
            if (account == null) throw new Exception("Please Select Account");

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.showAndWait();
            return false;
        }

        try {
            Double amountDouble = Double.parseDouble(amount.getText());
            if (amountDouble < 0) throw new Exception("Negative Input");

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please Enter Valid Positive Number", ButtonType.OK);
            alert.showAndWait();
            return false;
        }
        return true;
    }


    public void deposit() throws IOException {

        if (!checkTransactionInput()) {
            return;
        }

        new Thread(() -> {

            Account account = accountTable.getSelectionModel().getSelectedItem();

            Double amountDouble = Double.parseDouble(amount.getText());

            Transaction mock_transaction = new Transaction(account, this.user, amountDouble, "Deposit", new Date(), 0);

            try {
                toServer.writeObject(mock_transaction);
            } catch (IOException e) {
                e.printStackTrace();
            }

            addToPendingTransactionsTableList(mock_transaction);

            addToTransactionHistoryTable();

            deleteFromPendingTransactionsTableList(mock_transaction);

            updateAccountList();

        }).start();

    }

    public void withdraw() throws IOException {

        if (!checkTransactionInput()) {
            return;
        }

        Account account = accountTable.getSelectionModel().getSelectedItem();

        Double amountDouble = Double.parseDouble(amount.getText());

        Transaction mock_transaction = new Transaction(account, this.user, amountDouble, "Withdraw", new Date(), 0);

        toServer.writeObject(mock_transaction);

        addToPendingTransactionsTableList(mock_transaction);

        deleteFromPendingTransactionsTableList(mock_transaction);

        addToTransactionHistoryTable();

        updateAccountList();

    }


    @FXML
    private TableView<Transaction> transactionHistoryTable;

    @FXML
    private TableColumn<Transaction, String> transactionHistoryTable_ID;

    @FXML
    private TableColumn<Transaction, String> transactionHistoryTable_StartTime;

    @FXML
    private TableColumn<Transaction, String> transactionHistoryTable_CompleteTime;


    @FXML
    private TableColumn<Transaction, String> transactionHistoryTable_User;

    @FXML
    private TableColumn<Transaction, String> transactionHistoryTable_Account;

    @FXML
    private TableColumn<Transaction, String> transactionHistoryTable_Type;

    @FXML
    private TableColumn<Transaction, String> transactionHistoryTable_Amount;

    @FXML
    private TableColumn<Transaction, String> transactionHistoryTable_Result;

    @FXML
    private TableColumn<Transaction, String> transactionHistoryTable_Balance;

    @FXML
    private TextField amount;

    @FXML
    private Button depositButton;

    @FXML
    private Button withdrawButton;


    @FXML
    private TableView<Transaction> pendingTransactionsTable;

    @FXML
    private TableColumn<Transaction, String> pendingTransactionsTable_ID;

    @FXML
    private TableColumn<Transaction, String> pendingTransactionsTable_StartTime;


    @FXML
    private TableColumn<Transaction, String> pendingTransactionsTable_Account;

    @FXML
    private TableColumn<Transaction, String> pendingTransactionsTable_Type;

    @FXML
    private TableColumn<Transaction, String> pendingTransactionsTable_Amount;

    @FXML
    private TableColumn<Transaction, String> pendingTransactionsTable_Balance;


    @FXML
    private TableView<Account> accountTable;

    @FXML
    private TableColumn<Account, String> accountTable_Account;

    @FXML
    private TableColumn<Account, String> accountTable_Balance;

    @FXML
    private Label usernameLabel;


}
