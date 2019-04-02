package models.bank.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Main;
import models.bank.logic.Account;
import models.bank.logic.Transaction;
import models.user.logic.User;

import java.util.concurrent.TimeUnit;

public class BankController {

    private Main main;

    private int currentDelayValue = 0;

    private int delay4Demo = 2;

    public void initialize() {


        userTable_User.setCellValueFactory(cD -> cD.getValue().getNameSP());

        userTable.setItems(userTableList);

        accountTable_Account.setCellValueFactory(cD -> cD.getValue().getNameSP());
        accountTable_Balance.setCellValueFactory(cD -> cD.getValue().getBalanceSP());

        accountTable.setItems(accountTableList);

        initPendingTable();

        initTransactionTable();

        delay3sec.setToggleGroup(delayController);
        delay5sec.setToggleGroup(delayController);
        delay10sec.setToggleGroup(delayController);

        assignDelays();

    }

    private void initTransactionTable() {
        transactionHistoryTable_ID.setCellValueFactory(cD -> cD.getValue().getIdSP());
        transactionHistoryTable_StartTime.setCellValueFactory(cD -> cD.getValue().getStartTimeSP());
        transactionHistoryTable_Delay.setCellValueFactory(cD -> cD.getValue().getDelaySP());
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
        pendingTransactionsTable_Delay.setCellValueFactory(cD -> cD.getValue().getDelaySP());
        pendingTransactionsTable_User.setCellValueFactory(cD -> cD.getValue().getUserSP());
        pendingTransactionsTable_Account.setCellValueFactory(cD -> cD.getValue().getAccountSP());
        pendingTransactionsTable_Type.setCellValueFactory(cD -> cD.getValue().getTypeSP());
        pendingTransactionsTable_Amount.setCellValueFactory(cD -> cD.getValue().getAmountSP());
        pendingTransactionsTable_Balance.setCellValueFactory(cD -> cD.getValue().getBalanceSP());
        pendingTransactionsTable_Result.setCellValueFactory(cD -> cD.getValue().getStatusSP());

        pendingTransactionsTable.setItems(pendingTransactionsTableList);
    }

    private void assignDelays() {
        delay3sec.setOnAction(e -> currentDelayValue = 3);
        delay5sec.setOnAction(e -> currentDelayValue = 5);
        delay10sec.setOnAction(e -> currentDelayValue = 10);
    }

    private void clearDelayButtons() {
        if (delayController.getSelectedToggle() != null) {
            delayController.selectToggle(null);
        }
    }

    private ObservableList<User> userTableList = FXCollections.observableArrayList();
    private ObservableList<Account> accountTableList = FXCollections.observableArrayList();
    private ObservableList<Transaction> transactionHistoryTableList = FXCollections.observableArrayList();
    private ObservableList<Transaction> pendingTransactionsTableList = FXCollections.observableArrayList();

    public void updateUserList(User user) {

        userTableList.add(user);
        userTable.refresh();

    }

    public void updateAccountList(Account account) {

        accountTableList.add(account);
        accountTable.refresh();
    }

    public void updateTransactionHistoryTableList(Transaction transaction) {

        transactionHistoryTableList.add(transaction);
        transactionHistoryTable.refresh();

        accountTable.refresh();

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
        User user = userTable.getSelectionModel().getSelectedItem();

        Account account = accountTable.getSelectionModel().getSelectedItem();

        clearDelayButtons();

        try {
            if (user == null) throw new Exception("Please Select UserApp");
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

    public void deposit() {

        if (!checkTransactionInput()) {
            return;
        }

        User user = userTable.getSelectionModel().getSelectedItem();

        Account account = accountTable.getSelectionModel().getSelectedItem();

        Double amountDouble = Double.parseDouble(amount.getText());

        main.bank.deposit4Demo(account, user, amountDouble, currentDelayValue);

    }

    public void withdraw() {

        if (!checkTransactionInput()) {
            return;
        }

        User user = userTable.getSelectionModel().getSelectedItem();

        Account account = accountTable.getSelectionModel().getSelectedItem();

        Double amountDouble = Double.parseDouble(amount.getText());

        main.bank.withdraw4Demo(account, user, amountDouble, currentDelayValue);

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
    private TableColumn<Transaction, String> transactionHistoryTable_Delay;

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
    private RadioButton delay3sec;

    @FXML
    private RadioButton delay5sec;

    @FXML
    private RadioButton delay10sec;

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
    private TableColumn<Transaction, String> pendingTransactionsTable_Delay;

    @FXML
    private TableView<Account> accountTable;

    @FXML
    private TableColumn<Account, String> accountTable_Account;

    @FXML
    private TableColumn<Account, String> accountTable_Balance;

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, String> userTable_User;

    private ToggleGroup delayController = new ToggleGroup();


}
