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

public class LauncherController {

    public void initialize() {
        main = new Main(this);

        initUserTable();

    }

    public void updateUserList(User user) {

        userTableList.add(user);
        userTable.refresh();

    }

    public void launchUser() {
        UserApp.launch();
    }

    public void launchBank() {
        BankApp.launch();
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

    private Main main;

    public static int port = 5050;
}