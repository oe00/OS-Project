package models.user;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.user.controller.UserController;
import models.user.logic.User;

public class UserApp extends Stage {

    public UserApp(User user) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/models/user/view/UserView.fxml"));
        loader.load();
        Parent root = loader.getRoot();

        UserController controller = loader.getController();

        controller.setUser(user);
        controller.updateUser();

        controller.startUser();

        setTitle("334-User");
        setResizable(false);
        setScene(new Scene(root));
        sizeToScene();
        show();

    }



}
