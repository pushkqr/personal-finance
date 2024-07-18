package app;

import java.sql.*;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:financeapp.db";

    public static Connection Connect(){
        Connection conn = null;

        try{
            conn = DriverManager.getConnection(DB_URL);
            SiLog.Message("Connection to SQLite Database has been established.");
        }
        catch (SQLException e){
            SiLog.Error("Connection failed: " + e.getMessage());
        }
        return conn;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void createNewDatabase(){
        try {
            Connection conn = Database.Connect();

            if(conn != null){
                DatabaseMetaData meta = conn.getMetaData();
                SiLog.Message("The driver name is: " + meta.getDriverName());
                SiLog.Message("A new database has been successfully created.");
            }
        }
        catch (SQLException e){
            SiLog.Error("Database creation failed: " + e.getMessage());
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void createTables(){
        String usersTable = "CREATE TABLE IF NOT EXISTS users (\n" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                " username TEXT NOT NULL UNIQUE,\n" +
                " password TEXT NOT NULL\n" +
                ");";

        String transactionsTable = "CREATE TABLE IF NOT EXISTS transactions (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " user_id INTEGER,\n"
                + " amount REAL,\n"
                + " category TEXT,\n"
                + " type TEXT,\n"
                + " date TEXT,\n"
                + " FOREIGN KEY(user_id) REFERENCES users(id)\n"
                + ");";

        try {
            Connection conn = Database.Connect();

            if(conn != null){
                Statement stmt = conn.createStatement();

                stmt.execute(usersTable);
                stmt.execute(transactionsTable);
                SiLog.Message("Tables have been created.");
            }
        }
        catch (SQLException e){
            SiLog.Error("Table creation failed: " + e.getMessage());
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
