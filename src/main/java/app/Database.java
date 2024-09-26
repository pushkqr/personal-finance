package app;

import java.sql.*;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:financeapp.db";

    private Database() {}

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
        try(Connection conn = Database.Connect()) {
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
                + " category_id INTEGER,\n"
                + " type_id INTEGER,\n"
                + " date TEXT,\n"
                + " note TEXT,\n"
                + " FOREIGN KEY(user_id) REFERENCES users(id),\n"
                + " FOREIGN KEY(category_id) REFERENCES category(category_id),\n"
                + " FOREIGN KEY(type_id) REFERENCES transaction_types(id)\n"
                + ");";

        String categoriesTable = """
                CREATE TABLE IF NOT EXISTS categories (
                  category_id INTEGER PRIMARY KEY AUTOINCREMENT,
                  category_name TEXT NOT NULL
                );
                """;

        String typesTable = """
                CREATE TABLE IF NOT EXISTS transaction_types (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    type_name TEXT NOT NULL
                );
                """;

        String budgetsTable = """
                CREATE TABLE IF NOT EXISTS budgets (
                    budget_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER,
                    category_id INTEGER,
                    amount REAL NOT NULL,
                    start_date TEXT,
                    end_date TEXT,
                    FOREIGN KEY(user_id) REFERENCES users(id),
                    FOREIGN KEY(category_id) REFERENCES categories(category_id)
                );
                """;

        try(Connection conn = Database.Connect()) {
            if(conn != null){
                Statement stmt = conn.createStatement();

                stmt.execute(usersTable);
                stmt.execute(categoriesTable);
                stmt.execute(transactionsTable);
                stmt.execute(typesTable);
                stmt.execute(budgetsTable);
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
    public static void initTables() {
        try(Connection conn = Database.Connect()) {
            if(conn != null){
                String q1 = "INSERT INTO categories (category_name) VALUES ('Food'), ('Salary'), ('Entertainment'), ('Utilities');";
                String q2 = "INSERT INTO transaction_types (type_name) VALUES ('Income'), ('Expense');";
                Statement stmt = conn.createStatement();
                stmt.execute(q1);
                stmt.execute(q2);
            }
        }
        catch (SQLException e){
            SiLog.Error("Initialization failed: " + e.getMessage());
        }
    }
}
