package models.bank;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.Main;
import models.bank.controller.BankController;

public class BankApp extends Stage {

    public BankApp(Main main) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/models/bank/view/BankView.fxml"));
        Parent root = loader.load();
        BankController bankController = loader.getController();

        main.setBankController(bankController);

        bankController.setMain(main);

        bankController.startBank();

        setTitle("334-Bank");
        setResizable(false);
        setScene(new Scene(root));
        sizeToScene();
        show();
    }


}
