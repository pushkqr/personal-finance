package app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionService {
    public static void addTransaction(Transaction transaction){
        String sql = "INSERT INTO transactions(user_id, amount, category, type, date) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = Database.Connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transaction.getUserId());
            stmt.setDouble(2, transaction.getAmount());
            stmt.setString(3, transaction.getCategory());
            stmt.setString(4, transaction.getType());
            stmt.setString(5, transaction.getDate());
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
        String sql = "SELECT * FROM transactions WHERE user_id = ?";
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
                        rs.getString("date")
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
        String sql = "SELECT SUM(amount) AS balance FROM transactions WHERE user_id = ?;";
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

        return balance;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
