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
        System.out.println(Shake.bytesToHex(cryptographic_hash));

        //authentication tag test
        S = "T".getBytes();
        K = passPhrase.getBytes();
        authentication_tag =  Shake.KMACXOF256(K, M, 512, S);
        System.out.println(Shake.bytesToHex(authentication_tag));

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
        System.out.println(Arrays.equals(decrypt_symmetric_cryptogram, M));
        System.out.println(wrong_decrypt_symmetric_cryptogram);


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
