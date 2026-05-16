import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "123456";
        String hashed = encoder.encode(password);
        System.out.println("密码: " + password);
        System.out.println("BCrypt加密: " + hashed);
    }
}
