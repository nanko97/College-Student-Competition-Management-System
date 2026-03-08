import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class temp_test {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "123456";
        String hashed = encoder.encode(password);
        System.out.println("BCrypt hash for '123456': " + hashed);
        
        // Test multiple hashes for different users
        System.out.println("/nMultiple hashes:");
        for (int i = 0; i < 3; i++) {
            System.out.println((i+1) + ": " + encoder.encode(password));
        }
    }
}
