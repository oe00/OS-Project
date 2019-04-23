package models.bank.controller;

import controller.LauncherController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Main;
import models.bank.logic.Account;
import models.bank.logic.Transaction;
import models.user.logic.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BankController {

    public Main main;
    private User user;

    private int currentDelayValue = 0;

    private int delay4Demo = 2;

    public ObjectInputStream inputFromClient;
    public ObjectOutputStream outputToClient;

    ServerSocket serverSocket;

    public void initialize() {

        initTransactionTable();
        initPendingTable();
        initUserTable();
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public void startBank() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(LauncherController.port);
                while (true) {
                    Socket socket = serverSocket.accept();
                    handleUser(socket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    void sendAccounts() {

        List<Account> accounts = new ArrayList<>(main.bank.bankAccounts.values());

        List<Account> authenticated_accounts = accounts.stream()
                .filter(a -> a.authenticatedUsers.contains(user.getID())).collect(Collectors.toList());

        try {
            authenticated_accounts.get(0).updateBalance(-10000.0, 'D');
            outputToClient.writeObject(authenticated_accounts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleUser(Socket socket) {

        new Thread(() -> {
            try {

                outputToClient = new ObjectOutputStream(socket.getOutputStream());
                inputFromClient = new ObjectInputStream(socket.getInputStream());

                user = (User) inputFromClient.readObject();

                updateUserList(user);

                sendAccounts();

                while (true) {
                    Transaction mock_transaction = null;
                    try {
                        mock_transaction = (Transaction) inputFromClient.readObject();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    addToPendingTransactionsTable(mock_transaction);

                    switch (mock_transaction.getType()) {
                        case "Deposit":
                            deposit(mock_transaction);
                            break;
                        case "Withdraw":
                            withdraw(mock_transaction);
                            break;
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void initUserTable() {

        userTable_User.setCellValueFactory(cD -> cD.getValue().getNameSP());
        userTable.setItems(userTableList);

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
        pendingTransactionsTable_User.setCellValueFactory(cD -> cD.getValue().getUserSP());
        pendingTransactionsTable_Account.setCellValueFactory(cD -> cD.getValue().getAccountSP());
        pendingTransactionsTable_Type.setCellValueFactory(cD -> cD.getValue().getTypeSP());
        pendingTransactionsTable_Amount.setCellValueFactory(cD -> cD.getValue().getAmountSP());
        pendingTransactionsTable_Balance.setCellValueFactory(cD -> cD.getValue().getBalanceSP());
        pendingTransactionsTable_Result.setCellValueFactory(cD -> cD.getValue().getStatusSP());

        pendingTransactionsTable.setItems(pendingTransactionsTableList);
    }


    private ObservableList<User> userTableList = FXCollections.observableArrayList();
    private ObservableList<Transaction> transactionHistoryTableList = FXCollections.observableArrayList();
    private ObservableList<Transaction> pendingTransactionsTableList = FXCollections.observableArrayList();

    public void updateUserList(User user) {

        userTableList.add(user);
        userTable.refresh();

    }

    public void addToTransactionHistoryTable(Transaction transaction) {

        transactionHistoryTableList.add(transaction);
        transactionHistoryTable.refresh();
    }

    public void addToPendingTransactionsTable(Transaction transaction) {
        pendingTransactionsTableList.add(transaction);
        pendingTransactionsTable.refresh();
    }

    public void deleteFromPendingTransactionsTable(Transaction transaction) {
        pendingTransactionsTableList.remove(transaction);
        pendingTransactionsTable.refresh();
    }

    private boolean checkTransactionInput(Account account, User user, Double amountDouble) {

        try {
            if (user == null) throw new Exception("Please Select UserApp");
            if (account == null) throw new Exception("Please Select Account");

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.showAndWait();
            return false;
        }

        try {
            if (amountDouble < 0) throw new Exception("Negative Input");

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please Enter Valid Positive Number", ButtonType.OK);
            alert.showAndWait();
            return false;
        }
        return true;
    }

    public void delayThread() {

        int currentThreadDelay = currentDelayValue;
        currentDelayValue = 0;

        if (currentThreadDelay == 0) {
            currentThreadDelay = delay4Demo;
        }

        try {
            TimeUnit.SECONDS.sleep(currentThreadDelay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void deposit(Transaction transaction) {

        main.bank.deposit4Demo(transaction);

        deleteFromPendingTransactionsTable(transaction);

        addToTransactionHistoryTable(transaction);

        try {
            outputToClient.writeObject(transaction);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void withdraw(Transaction transaction) {

        if (!checkTransactionInput(transaction.getAccount(), transaction.getUser(), transaction.getAmount())) {
            return;
        }

        //main.bank.withdraw4Demo();

        deleteFromPendingTransactionsTable(transaction);

        addToTransactionHistoryTable(transaction);

        try {
            outputToClient.writeObject(transaction);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendAccounts();

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
    private TableColumn<Transaction, String> pendingTransactionsTable_User;

    @FXML
    private TableColumn<Transaction, String> pendingTransactionsTable_Account;

    @FXML
    private TableColumn<Transaction, String> pendingTransactionsTable_Type;

    @FXML
    private TableColumn<Transaction, String> pendingTransactionsTable_Amount;

    @FXML
    private TableColumn<Transaction, String> pendingTransactionsTable_Balance;

    @FXML
    private TableColumn<Transaction, String> pendingTransactionsTable_Result;

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, String> userTable_User;

}
