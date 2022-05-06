import java.math.BigInteger;
import java.util.Arrays;

/**
 * Test Area
 */
public class Test {
    // TODO: 5/4/22 test KMACXOF256 and cShake256

    public static void main(String[] args) {
        String passPhrase = "secret_passphrase";
        String randomString = "arandom@password.@#";
        byte[] S; //diversification string
        byte[] K; //key
        byte[] M = randomString.getBytes(); //message        S = "D".getBytes();
        byte[] cryptographic_hash;
        byte[] authentication_tag;
        byte[] decrypt_symmetric_cryptogram;

        //Test 1
        //cryptographic hash test
        S = "D".getBytes();
        K = "".getBytes();
        cryptographic_hash = Shake.KMACXOF256(K, M, 512, S);
        System.out.println("String: " + passPhrase);
        System.out.println("Hash result with cryptographic hash test: \n" + Shake.bytesToHex(cryptographic_hash));
        System.out.println("----------------------------------------------");

        //authentication tag test
        S = "T".getBytes();
        K = passPhrase.getBytes();
        authentication_tag =  Shake.KMACXOF256(K, M, 512, S);
        System.out.println("Hash result with authentication tag test: \n" + Shake.bytesToHex(authentication_tag));
        System.out.println("----------------------------------------------");

        //Encryption and decryption test
        //Same pass phrase
        Encryption enc = new Encryption();
        SymmetricCryptogram sym = enc.encrypt(M, passPhrase);
        decrypt_symmetric_cryptogram = enc.decrypt(sym,passPhrase);
        System.out.println(Arrays.equals(decrypt_symmetric_cryptogram, M));
        //Different pass phrase
        String wrong_passphrase = "not a passphrase";
        byte[] wrong_decrypt_symmetric_cryptogram;
        wrong_decrypt_symmetric_cryptogram = enc.decrypt(sym,wrong_passphrase);
        //Same pass phrase
        System.out.println("Compare two byte arrays with same pass phrase " +
                "and same symmetric cryptogram result: "
                + Arrays.equals(decrypt_symmetric_cryptogram, M));
        //Different pass phrase
        System.out.println("Wrong pass phrase result: " + wrong_decrypt_symmetric_cryptogram);
        System.out.println("----------------------------------------------");

        //Test KMAC
        byte[] data_test = {(byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03};
        byte[] key_test = {(byte)0x40, (byte)0x41, (byte)0x42, (byte)0x43, (byte)0x44, (byte)0x45, (byte)0x46, (byte)0x47,
                (byte)0x48, (byte)0x49, (byte)0x4A, (byte)0x4B, (byte)0x4C, (byte)0x4D, (byte)0x4E, (byte)0x4F,
                (byte)0x50, (byte)0x51, (byte)0x52, (byte)0x53, (byte)0x54, (byte)0x55, (byte)0x56, (byte)0x57,
                (byte)0x58, (byte)0x59, (byte)0x5A, (byte)0x5B, (byte)0x5C, (byte)0x5D, (byte)0x5E, (byte)0x5F};
        byte[] string_test = "My Tagged Application".getBytes();
        int length_test = 512;

        System.out.println("NIST test 1: " + Shake.bytesToHex(data_test));
        System.out.println("Key: " + Shake.bytesToHex(key_test));
        System.out.println("String: " + Shake.bytesToHex(string_test));

        byte[] test_result = Shake.KMACXOF256(key_test, data_test, length_test, string_test);
        System.out.println("Expected result:\n" +
                "17 55 13 3F 15 34 75 2A AD 07 48 F2 C7 06 FB 5C\n" +
                "78 45 12 CA B8 35 CD 15 67 6B 16 C0 C6 64 7F A9\n" +
                "6F AA 7A F6 34 A0 BF 8F F6 DF 39 37 4F A0 0F AD\n" +
                "9A 39 E3 22 A7 C9 20 65 A6 4E B1 FB 08 01 EB 2B ");
        System.out.println("Result: \n" + Shake.bytesToHex(test_result));
        // Testing the different required functions.
//        int encodeTest = 0;
//        byte[] leftTest = Shake.left_encode(new BigInteger("" + encodeTest));
//        System.out.println("\nleft_encode test using " + encodeTest + ": " + Arrays.toString(leftTest) + "\n");
//
//        byte[] rightTest = Shake.right_encode(new BigInteger("" + encodeTest));
//        System.out.println("right_encode test using " + encodeTest + ": " + Arrays.toString(rightTest) + "\n");
//
//        String encodeStringTest = "Email Signature";
//        byte[] encodeStringTestBytes = Shake.encode_string(encodeStringTest.getBytes());
//        System.out.println("encode_string test using string \"" + encodeStringTest + "\": " + Arrays.toString(encodeStringTestBytes) + "\n");
//
//        int bytepadInt = 5;
//        byte[] bytepadTest = Shake.bytepad(encodeStringTestBytes, new BigInteger("" + bytepadInt));
//        System.out.println("Using right_encode value with " + bytepadInt + " for bytepad: " + Arrays.toString(bytepadTest) + "\n");
//
//
//        System.out.println(Shake.bytesToHex(Shake.KMACXOF256("".getBytes(), "Secret".getBytes(), 1024, "D".getBytes())));
//        System.out.println(Shake.bytesToHex(Shake.encode_string("Email Signature".getBytes())));
    }
}
