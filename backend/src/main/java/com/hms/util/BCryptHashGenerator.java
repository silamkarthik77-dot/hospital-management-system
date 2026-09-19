package com.hms.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * One-off helper: run this class's main() to print a real BCrypt hash for
 * any plaintext password, then paste the output into database/seed_data.sql.
 * Not wired into the Spring context - this is a dev tool only.
 *
 * Usage:
 *   mvn -q compile exec:java -Dexec.mainClass=com.hms.util.BCryptHashGenerator -Dexec.args="Password@123"
 * or run it directly from your IDE with a program argument.
 */
public class BCryptHashGenerator {
    public static void main(String[] args) {
        String plaintext = args.length > 0 ? args[0] : "Password@123";
        String hash = new BCryptPasswordEncoder(12).encode(plaintext);
        System.out.println("Plaintext: " + plaintext);
        System.out.println("BCrypt hash: " + hash);
    }
}
