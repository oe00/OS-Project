package models.bank;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BankApp extends Stage {

    private BankApp() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/models/bank/view/BankView.fxml"));
        setTitle("334-Bank");
        setResizable(false);
        setScene(new Scene(root));
        sizeToScene();
        show();
    }

    public static void launch(){
        try {
            new BankApp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
