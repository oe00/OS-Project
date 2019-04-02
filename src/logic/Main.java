package logic;

import controller.Controller;

import java.util.ArrayList;

public class Main {

    public Bank bank;

    public Main(Controller appController) {

        bank = new Bank(appController);

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

        try {

            account1.updateBalance(1000.0, 'D');
            account2.updateBalance(2000.0, 'D');
            account3.updateBalance(3000.0, 'D');

            /** when threads are sync. use below
             bank.deposit4Demo(account1, company, 1000.0, 0);
             bank.deposit4Demo(account2, company, 2000.0, 0);
             bank.deposit4Demo(account3, company, 3000.0, 0);
             **/

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }
}
