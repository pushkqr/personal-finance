package app;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Database.createNewDatabase();
        Database.createTables();
        Database.initTables();

       SwingUtilities.invokeLater(() -> {
            AppUI app = new AppUI();
            app.start();
        });
    }
}
