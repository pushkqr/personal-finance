package app;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetService {

    public static void addBudget(Budget budget) {
        String sql = "INSERT INTO budgets (user_id, category_id, amount, start_date, end_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Database.Connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, budget.getUserId());
            stmt.setInt(2, budget.getCategoryId());
            stmt.setDouble(3, budget.getAmount());
            stmt.setString(4, budget.getStartDate());
            stmt.setString(5, budget.getEndDate());

            stmt.executeUpdate();
            SiLog.Message("Budget added successfully for user ID: " + budget.getUserId());

        } catch (SQLException e) {
            SiLog.Error("Failed to add budget: " + e.getMessage());
        }
    }

    public static List<Budget> getBudgetsByUser(int user_id){
        String sql = "SELECT category_id,amount,start_date,end_date FROM budgets WHERE user_id = ?;";
        List<Budget> budgets = new ArrayList<Budget>();

        try(Connection conn = Database.Connect();
        PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setInt(1, user_id);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                Budget b = new Budget(
                        user_id,
                        rs.getInt("category_id"),
                        rs.getDouble("amount"),
                        rs.getString("start_date"),
                        rs.getString("end_date")
                        );
                budgets.add(b);
            }
        }
        catch (SQLException e){
            SiLog.Message("Could not get budgets for user-id : " + e.getMessage());
        }

        return budgets;
    }

    public static boolean updateBudget(Budget budget) {
        String sql = "UPDATE budgets SET amount = ? WHERE user_id = ? AND category_id = ?;";

        try (Connection conn = Database.Connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, budget.getAmount());
            stmt.setInt(2, budget.getUserId());
            stmt.setInt(3, budget.getCategoryId());

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                SiLog.Message("Budget updated successfully for user ID: " + budget.getUserId());
                return true;
            } else {
                SiLog.Message("Budget not updated for user ID: " + budget.getUserId());
                return false;
            }
        } catch (SQLException e) {
            SiLog.Error("Failed to update budget: " + e.getMessage());
        }

        return true;
    }
}
