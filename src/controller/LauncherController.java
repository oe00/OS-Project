package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import models.Main;
import models.bank.BankApp;
import models.user.UserApp;
import models.user.logic.User;

import java.util.ArrayList;

public class LauncherController {

    private Main main;

    public void initialize() {
        initUserTable();

        connectedUserList = new ArrayList<>();

        try {
            main = new Main(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateUserList(User user) {

        userTableList.add(user);
        userTable.refresh();

    }

    public void launchUser() {
        User user = userTable.getSelectionModel().getSelectedItem();

        try {
            if(!connectedUserList.contains(user)) {
                connectedUserList.add(user);
                new UserApp(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void launchBank() {
        try {
            new BankApp(main);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initUserTable() {

        userTable_User.setCellValueFactory(cD -> cD.getValue().getNameSP());

        userTable.setItems(userTableList);
    }


    private ObservableList<User> userTableList = FXCollections.observableArrayList();

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, String> userTable_User;

    public static int port = 5050;

    private ArrayList<User> connectedUserList;


}