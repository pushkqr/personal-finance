package app;

import java.sql.*;

public class Budget {
    private final int user_id;
    private int category_id;
    private double amount;
    private final String start_date;
    private final String end_date;

    public Budget(int uid, int cid, double amt, String s_date, String e_date){
        user_id = uid;
        category_id = cid;
        amount = amt;
        start_date = s_date;
        end_date = e_date;
    }

    public int getUserId(){ return user_id;}
    public int getCategoryId(){ return category_id;}
    public double getAmount(){ return amount;}
    public String getStartDate(){ return start_date;}
    public String getEndDate(){ return end_date;}
    public void setCategoryID(int cid) {category_id = cid;}
    public void setAmount(Double amt) {amount = amt;}

    public static String getCategory(int cid){
        String sql = "SELECT category_name FROM categories WHERE category_id = ?";
        String category = "";

        try(Connection conn = Database.Connect();
        PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, cid);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                category = rs.getString("category_name");
            }

        }catch (SQLException e){
            SiLog.Message("Could not fetch category: " + e.getMessage());
        }

        return category;
    }


    public static int fetchCategoryID(String category){
        String sql = "SELECT category_id FROM categories WHERE category_name = ?";
        int cid = 0;

        try(Connection conn = Database.Connect();
            PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                cid = rs.getInt("category_id");
            }

        }catch (SQLException e){
            SiLog.Message("Could not fetch category id: " + e.getMessage());
        }

        return cid;
    }


    public double getSpentAmount() {
        String sql = "SELECT SUM(amount) AS 'total' FROM transactions WHERE user_id = ? AND category_id = ?;";
        double amt = 0.0;

        try(Connection conn = Database.Connect();
            PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, this.user_id);
            stmt.setInt(2, this.category_id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                amt = rs.getDouble("total");
            }

        }catch (SQLException e){
            SiLog.Message("Could not fetch category: " + e.getMessage());
        }

        return amt;
    }

    public double getSpentPercent() {
        if (getAmount() == 0) {
            return 0;
        }
        return Math.round((getSpentAmount() / getAmount()) * 100);
    }

}
