package models;

import controller.LauncherController;
import models.bank.logic.Account;
import models.bank.logic.Bank;
import models.bank.logic.Transaction;
import models.user.logic.User;

import java.util.ArrayList;

public class Main {

    public final Bank bank;

    public Main(LauncherController launcherController) {

        bank = new Bank();

        User company = bank.createUser("Oğuzhan");

        Account account1 = bank.createAccount(company, "C Holdings");
        Account account2 = bank.createAccount(company, "N Holdings");
        Account account3 = bank.createAccount(company, "G Holdings");

        ArrayList<User> employee = new ArrayList<>(10);


        String[] employeeNames = {"Mark", "John", "Lena", "Juliet", "Hans", "Billy", "Sandra"};

        for (int i = 0; i < employeeNames.length; i++) {

            employee.add(bank.createUser(employeeNames[i]));

            try {
                bank.addAccessToAccount(account1, company, employee.get(i));
                bank.addAccessToAccount(account2, company, employee.get(i));
                bank.addAccessToAccount(account3, company, employee.get(i));
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        launcherController.updateUserList(new ArrayList<>(bank.bankUsers.values()));

        bank.deposit(new Transaction(account1, company, 1000.0, "Deposit"));
        bank.deposit(new Transaction(account2, company, 2000.0, "Deposit"));
        bank.deposit(new Transaction(account3, company, 3000.0, "Deposit"));

    }
}
