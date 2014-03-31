/*
 * Copyright (C) 2014 Frank Steiler <frank@steiler.eu>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package assets;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * This class encrypts the user password. On top of that the class can check if the given password is equal to the encrypted using the salt and the encrypted password.
 * @author Frank Steiler <frank@steiler.eu>
 */
public class PasswordEncryptionService {

    /**
     * This function checks if the attempted password is equal to the encrypted password.
     * @param attemptedPassword The password entered by the user.
     * @param encryptedPassword The password stored in the database.
     * @param salt The salt used to encrypt the password.
     * @return Returns true if password is equal.
     */
    public boolean authenticate(String attemptedPassword, String encryptedPassword, String salt)
    {
        // Encrypt the clear-text password using the same salt that was used to encrypt the original password.
        String encryptedAttemptedPassword = getEncryptedPassword(attemptedPassword, salt);
        
        // Authentication succeeds if encrypted password that the user entered is equal to the stored hash.
        return Arrays.equals(encryptedPassword.getBytes(), encryptedAttemptedPassword.getBytes());
    }

    /**
     * This function encrypts a password using a salt.
     * @param password Password that needs to be encrypted.
     * @param salt The salt used to encrypt the password.
     * @return Returns the encrypted password.
     */
    public String getEncryptedPassword(String password, String salt)
    {
        try
        {
            // PBKDF2 with SHA-1 as the hashing algorithm. Note that the NIST specifically names SHA-1 as an acceptable hashing algorithm for PBKDF2.
            String algorithm = "PBKDF2WithHmacSHA1";
            // SHA-1 generates 160 bit hashes, so that's what makes sense here
            int derivedKeyLength = 160;
            // Pick an iteration count that works for you. The NIST recommends at least 1,000 iterations.
            int iterations = 20000;

            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), iterations, derivedKeyLength);

            SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);

            return new String(f.generateSecret(spec).getEncoded());
        }
        catch( NoSuchAlgorithmException | InvalidKeySpecException e)
        {
            return null;
        }
    }

    /**
     * This function generates a random salt. Needed to encrypt a new password.
     * @return A random salt.
     * @throws NoSuchAlgorithmException
     */
    public String generateSalt() throws NoSuchAlgorithmException 
    {
        try
        {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

            // Generate a 8 byte (64 bit) salt as recommended by RSA PKCS5
            byte[] salt = new byte[8];
            random.nextBytes(salt);

            return new String(salt);
        }
        catch( NoSuchAlgorithmException  e)
        {
            return null;
        }
    }
}