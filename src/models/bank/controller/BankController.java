package models.bank.controller;

import controller.LauncherController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import models.Main;
import models.bank.logic.Account;
import models.bank.logic.Transaction;
import models.user.logic.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BankController {

    private Main main;

    private ServerSocket serverSocket;

    public void initialize() {

        initTransactionTable();
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


    private void sendAccounts(User user, ObjectOutputStream os) {

        List<Account> accounts = new ArrayList<>(main.bank.bankAccounts.values());

        List<Account> authenticated_accounts = accounts.stream()
                .filter(a -> a.authenticatedUsers.contains(user.getID())).collect(Collectors.toList());

        try {
            os.reset();
            os.writeObject(authenticated_accounts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void handleUser(Socket socket) {

        new Thread(() -> {
            try {


                ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream is = new ObjectInputStream(socket.getInputStream());

                User user = (User) is.readObject();

                updateUserList(user);

                sendAccounts(user, os);

                while (true) {

                    try {

                        Transaction mock_transaction = (Transaction) is.readObject();

                        switch (mock_transaction.getType()) {
                            case "Deposit":
                                deposit(mock_transaction, os);
                                break;
                            case "Withdraw":
                                withdraw(mock_transaction, os);
                                break;
                        }

                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
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

    private final ObservableList<User> userTableList = FXCollections.observableArrayList();
    private final ObservableList<Transaction> transactionHistoryTableList = FXCollections.observableArrayList();

    private void updateUserList(User user) {

        userTableList.add(user);
        userTable.refresh();

    }

    private void addToTransactionHistoryTable(Transaction transaction) {

        transactionHistoryTableList.add(transaction);
        transactionHistoryTable.refresh();
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


    private void deposit(Transaction transaction, ObjectOutputStream os) {

        if (!checkTransactionInput(transaction.getAccount(), transaction.getUser(), transaction.getAmount())) {
            return;
        }

        new Thread(() -> {

            main.bank.deposit(transaction);

            try {
                os.writeObject(transaction);
            } catch (IOException e) {
                e.printStackTrace();
            }

            sendAccounts(transaction.getUser(), os);

        }).start();
    }

    private void withdraw(Transaction transaction, ObjectOutputStream os) {

        if (!checkTransactionInput(transaction.getAccount(), transaction.getUser(), transaction.getAmount())) {
            return;
        }

        new Thread(() -> {

            main.bank.withdraw(transaction);

            try {
                os.writeObject(transaction);
            } catch (IOException e) {
                e.printStackTrace();
            }

            addToTransactionHistoryTable(transaction);

            sendAccounts(transaction.getUser(), os);

        }).start();
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
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, String> userTable_User;

}
