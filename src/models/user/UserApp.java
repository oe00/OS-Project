package models.user;

import controller.LauncherController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.user.logic.User;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class UserApp extends Stage {

    private UserApp() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/models/user/view/UserView.fxml"));
        setTitle("334-User");
        setResizable(false);
        setScene(new Scene(root));
        sizeToScene();
        show();
    }

    public static void launch() {
        try {
            new UserApp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
