package sg.edu.nus.iss.vttp5_ssf_mini_project.utilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {
    private static final String algorithm = "SHA-256";

    public static String hashPassword(String plainPassword) {

    try {

      // create a MessageDigest object using the SHA-256 cryptographic hash algorithm
      MessageDigest messageDigest = MessageDigest.getInstance(algorithm);

      // update messageDigest with the bytes from the plaintext password
      messageDigest.update(plainPassword.getBytes());

      // retrieve the hashed bytes from the MessageDigest
      byte[] hashedBytes = messageDigest.digest();

      // convert hashed bytes into a hexadecimal string
      return bytesToHex(hashedBytes);
    }

    catch (NoSuchAlgorithmException e) {

      e.printStackTrace();

      return null;
    }
  }

  public static boolean checkPassword(String plainPassword, String hashedPassword) {

    String hashedAttempt = hashPassword(plainPassword);
    
    return hashedAttempt.equals(hashedPassword);
  }

  private static String bytesToHex(byte[] hash) {

    StringBuilder hexString = new StringBuilder(hash.length * 2);
    for (byte b : hash) {

      // convert each byte into a hexadecimal string
      String hex = Integer.toHexString(0xff & b);

      if (hex.length() == 1) {
        hexString.append('0');
      }

      hexString.append(hex);
    }
    
    return hexString.toString();
  }

}
