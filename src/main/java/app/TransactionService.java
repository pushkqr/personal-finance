package app;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionService {
    public static void addTransaction(Transaction transaction){
        String sql = "INSERT INTO transactions(user_id, amount, category, type, date, category_id, type_id, note) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.Connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transaction.getUserId());
            stmt.setDouble(2, transaction.getAmount());
            stmt.setString(3, transaction.getCategory());
            stmt.setString(4, transaction.getType());
            stmt.setString(5, transaction.getDate());
            stmt.setInt(6, transaction.getCategoryID());
            stmt.setInt(7, transaction.getTypeID());
            stmt.setString(8, transaction.getNote());
            stmt.executeUpdate();
            SiLog.Message("Transaction added successfully for user ID: " + transaction.getUserId());
        }
        catch (SQLException e){
            SiLog.Error("Adding transaction failed: " + e.getMessage());
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static List<Transaction> getTransactionsByUser(int userId){
        String sql = "SELECT * FROM transactions WHERE user_id = ? ORDER BY date";
        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = Database.Connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                Transaction t = new Transaction(
                        rs.getInt("user_id"),
                        rs.getDouble("amount"),
                        rs.getString("category"),
                        rs.getString("type"),
                        rs.getString("date"),
                        rs.getInt("category_id"),
                        rs.getInt("type_id"),
                        rs.getString("note")
                );
                t.setId(rs.getInt("id"));
                transactions.add(t);
            }

        }
        catch (SQLException e){
            SiLog.Error("Fetching transactions failed: " + e.getMessage());
        }

        return transactions;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static double fetchBalance(int userId){
        String sql = "SELECT SUM(amount) AS balance FROM transactions WHERE user_id = ? AND type = 'Income';";
        double balance = 0.0;

        try (Connection conn = Database.Connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                balance = rs.getDouble("balance");
            }
        }
        catch (SQLException e) {
            SiLog.Error("Fetching balance failed: " + e.getMessage());
        }

        return balance - fetchExpense(userId);
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static double fetchExpense(int userId){
        String sql = "SELECT SUM(amount) AS expense FROM transactions WHERE user_id = ? AND type = 'Expense';";
        double expense = 0.0;

        try (Connection conn = Database.Connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                expense = rs.getDouble("expense");
            }
        }
        catch (SQLException e) {
            SiLog.Error("Fetching expenses failed: " + e.getMessage());
        }

        return expense;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static int fetchCategoryId(String category){
        String sql = "SELECT category_id FROM categories WHERE category_name = ?;";
        int category_id = 0;

        try(Connection conn = Database.Connect()){
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                category_id = rs.getInt("category_id");
            }
        }catch (SQLException e){
            SiLog.Message("Could not fetch category ID: " + e.getMessage());
        }
        return category_id;
    }

    public static int fetchTransactionTypeId(String typeName) {
        String query = "SELECT id FROM transaction_types WHERE type_name = ?";
        try (Connection conn = Database.Connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, typeName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            SiLog.Error("Error fetching transaction type ID: " + e.getMessage());
        }
        return -1;
    }
}
