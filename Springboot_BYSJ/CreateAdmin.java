import java.sql.*;

public class CreateAdmin {
    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3306/springbootrd362?useUnicode=true&characterEncoding=utf-8&useSSL=false";
        String user = "root";
        String password = "123123";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE 'users'");
            
            if (!rs.next()) {
                System.out.println("Creating users table...");
                stmt.execute(
                    "CREATE TABLE `users` (" +
                    "`id` bigint(20) NOT NULL AUTO_INCREMENT," +
                    "`username` varchar(50) NOT NULL," +
                    "`password` varchar(100) NOT NULL," +
                    "`role` varchar(20) DEFAULT 'admin'," +
                    "`addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "PRIMARY KEY (`id`)," +
                    "UNIQUE KEY `uk_username` (`username`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
                );
            }
            
            String bcryptHash = "$2a$10$7JeHnYCKMJfMqZTz9vK1x.dQXrNmFqSVMvfLqDhCvRjZk3pWqL1uG";
            
            int rows = stmt.executeUpdate(
                "INSERT INTO users (username, password, role) VALUES ('admin', '" + bcryptHash + "', '管理员') " +
                "ON DUPLICATE KEY UPDATE password = '" + bcryptHash + "', role = '管理员'"
            );
            
            System.out.println("Success!");
            System.out.println("Username: admin");
            System.out.println("Password: admin123");
            System.out.println("Role: 管理员");
            
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
