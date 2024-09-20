package app;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class UserService {

    public static void registerUser(User user) {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?);";

        try (Connection conn = Database.Connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {

            String hashpw = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            stmt.setString(1, user.getUser());
            stmt.setString(2, hashpw);
            stmt.executeUpdate();


            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getInt(1));
            }

            SiLog.Message("User Registered Successfully");
        } catch (SQLException e) {
            SiLog.Error("Failed to register user: " + e.getMessage());
        }
    }

    public static User loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = Database.Connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");

                if (BCrypt.checkpw(password, storedPassword)) {
                    User user = new User(username, password);
                    user.setId(rs.getInt("id"));
                    SiLog.Message("User logged in successfully: " + username);
                    return user;
                } else {
                    SiLog.Message("Invalid username/password");
                    return null;
                }
            } else {
                SiLog.Message("User not found: " + username);
                return null;
            }
        } catch (SQLException e) {
            SiLog.Error("Login failed: " + e.getMessage());
            return null;
        }
    }

    public static void updateUserName(User user, String newUsername) {
        String sql = "UPDATE users SET username = ? WHERE id = ?;";

        try (Connection conn = Database.Connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newUsername);
            stmt.setInt(2, user.getId());

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                SiLog.Message("Username updated successfully for user ID: " + user.getId());
            } else {
                SiLog.Message("No username updated for user ID: " + user.getId());
            }
        } catch (SQLException e) {
            SiLog.Error("Failed to update username: " + e.getMessage());
        }
    }

    public static void updatePassword(User user, String newPassword){
        String sql = "UPDATE users SET password = ? WHERE id = ?;";

        try (Connection conn = Database.Connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String hashpw = BCrypt.hashpw(newPassword, BCrypt.gensalt());

            stmt.setString(1, hashpw);
            stmt.setInt(2, user.getId());

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                SiLog.Message("Password updated successfully for user ID: " + user.getId());
            } else {
                SiLog.Message("Password not updated for user ID: " + user.getId());
            }
        } catch (SQLException e) {
            SiLog.Error("Failed to update user password: " + e.getMessage());
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void deleteUser(User user) {
        String q1 = "DELETE FROM users WHERE id = ?";
        String q2 = "DELETE FROM transactions WHERE user_id = ?";

        try (Connection conn = Database.Connect()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(q1)) {
                stmt1.setInt(1, user.getId());
                stmt1.executeUpdate();
            }

            try (PreparedStatement stmt2 = conn.prepareStatement(q2)) {
                stmt2.setInt(1, user.getId());
                stmt2.executeUpdate();
            }

            conn.commit();
            SiLog.Message("User account deleted successfully: " + user.getUser());
        } catch (SQLException e) {
            SiLog.Error("Failed to delete user account: " + e.getMessage());

        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static boolean usernameExists(String username) {
        String sql = "SELECT COUNT(id) AS count FROM users WHERE username = ?";

        try (Connection conn = Database.Connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            SiLog.Message("Error checking username existence: " + e.getMessage());
        }

        return false;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static String fetchHash(int user_id){
        String sql = "SELECT password FROM users WHERE id = ?";
        String pwd = null;

        try(Connection conn = Database.Connect();
            PreparedStatement stmt = conn.prepareStatement(sql);){
            stmt.setInt(1, user_id);

            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                pwd = rs.getString("password");
            }

        }catch (SQLException e){
            SiLog.Message("Could Not Fetch Hash " + e.getMessage());
        }

        return pwd;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
