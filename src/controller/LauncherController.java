package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import models.Main;
import models.bank.BankApp;
import models.user.UserApp;
import models.user.logic.User;

import java.util.ArrayList;

public class LauncherController {

    private Main main;
    private User user;

    private boolean bankLaunched = false;

    public void initialize() {
        initUserTable();

        connectedUserList = new ArrayList<>();

        try {
            main = new Main(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateUserList(ArrayList<User> users) {

        userTableList.addAll(users);
        userTable.refresh();

    }

    public void launchUser() {

        user = userTable.getSelectionModel().getSelectedItem();

        if (!checkInput()) {
            return;
        }

        try {
            if (!connectedUserList.contains(user)) {
                connectedUserList.add(user);
                new UserApp(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void launchBank() {
        try {
            if (!bankLaunched) {
                bankLaunched = true;
                new BankApp(main);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initUserTable() {

        userTable_User.setCellValueFactory(cD -> cD.getValue().getNameSP());

        userTable.setItems(userTableList);
    }


    private boolean checkInput() {

        try {
            if (!bankLaunched) throw new Exception("Please Start Server (Bank) First");

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.showAndWait();
            return false;
        }

        try {
            if (user == null) throw new Exception("Please Select User");

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.showAndWait();
            return false;
        }

        return true;
    }


    private final ObservableList<User> userTableList = FXCollections.observableArrayList();

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, String> userTable_User;

    public static final int port = 5050;

    private ArrayList<User> connectedUserList;


}