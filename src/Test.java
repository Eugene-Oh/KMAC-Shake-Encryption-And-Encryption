import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Testing for CShake and KMAC.
 */
public class Test {

    public static void main(String[] args) {
        ShakeTest();
        ShakeTest2();
        KMACTest();
        KMACTest2();
        KMACTest3();
    }

    // https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/cSHAKE_samples.pdf
    // From Sample #3
    public static void ShakeTest() {
        byte[] K  = {0, 1, 2, 3};
        int L = 512;
        byte[] N = "".getBytes();
        byte[] S = "Email Signature".getBytes();
        byte[] test = Shake.cShake256(K, L, N, S);

        String desiredResult = "D0 08 82 8E 2B 80 AC 9D 22 18 FF EE 1D 07 0C 48 " +
                "B8 E4 C8 7B FF 32 C9 69 9D 5B 68 96 EE E0 ED D1 " +
                "64 02 0E 2B E0 56 08 58 D9 C0 0C 03 7E 34 A9 69 " +
                "37 C5 61 A7 4C 41 2B B4 C7 46 46 95 27 28 1C 8C ";
        System.out.println("CShake Test");
        System.out.println("CShake DESIRED: " + desiredResult);
        System.out.println("CShake RESULT : " + Shake.bytesToHex(test));
        System.out.println("Are they equal: " + desiredResult.equals(Shake.bytesToHex(test)));
        System.out.println();
    }

    // https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/cSHAKE_samples.pdf
    // From Sample #4
    public static void ShakeTest2() {
        byte[] K  = hexToByte("000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F202122232425262728292A2B2C2D2E2F303132333435363738393A3B3C3D3E3F404142434445464748494A4B4C4D4E4F505152535455565758595A5B5C5D5E5F606162636465666768696A6B6C6D6E6F707172737475767778797A7B7C7D7E7F808182838485868788898A8B8C8D8E8F909192939495969798999A9B9C9D9E9FA0A1A2A3A4A5A6A7A8A9AAABACADAEAFB0B1B2B3B4B5B6B7B8B9BABBBCBDBEBFC0C1C2C3C4C5C6C7");
        int L = 512;
        byte[] N = "".getBytes();
        byte[] S = "Email Signature".getBytes();
        byte[] test = Shake.cShake256(K, L, N, S);

        String desiredResult = "07 DC 27 B1 1E 51 FB AC 75 BC 7B 3C 1D 98 3E 8B " +
                "4B 85 FB 1D EF AF 21 89 12 AC 86 43 02 73 09 17 " +
                "27 F4 2B 17 ED 1D F6 3E 8E C1 18 F0 4B 23 63 3C " +
                "1D FB 15 74 C8 FB 55 CB 45 DA 8E 25 AF B0 92 BB ";
        System.out.println("CShake2 Test");
        System.out.println("CShake DESIRED: " + desiredResult);
        System.out.println("CShake RESULT : " + Shake.bytesToHex(test));
        System.out.println("Are they equal: " + desiredResult.equals(Shake.bytesToHex(test)));
        System.out.println();
    }

    // https://csrc.nist.gov/csrc/media/projects/cryptographic-standards-and-guidelines/documents/examples/kmacxof_samples.pdf
    // From Sample #4
    public static void KMACTest () {
        System.out.println("KMAC Test");
        String s = "404142434445464748494A4B4C4D4E4F505152535455565758595A5B5C5D5E5F";
        byte[] K  = hexToByte(s);
        byte[] X = {0, 1, 2, 3};
        int L = 512;
        byte[] S = "My Tagged Application".getBytes();
        byte[] test2 = Shake.KMACXOF256(K, X, L, S);

        String desiredResult = "17 55 13 3F 15 34 75 2A AD 07 48 F2 C7 06 FB 5C " +
                "78 45 12 CA B8 35 CD 15 67 6B 16 C0 C6 64 7F A9 " +
                "6F AA 7A F6 34 A0 BF 8F F6 DF 39 37 4F A0 0F AD " +
                "9A 39 E3 22 A7 C9 20 65 A6 4E B1 FB 08 01 EB 2B ";
        System.out.println("KMAC DESIRED: " + desiredResult);
        System.out.println("KMAC RESULT : " + Shake.bytesToHex(test2));
        System.out.println("Are they equal: " + desiredResult.equals(Shake.bytesToHex(test2)));
        System.out.println();
    }

    // https://csrc.nist.gov/csrc/media/projects/cryptographic-standards-and-guidelines/documents/examples/kmacxof_samples.pdf
    // From Sample #5
    public static void KMACTest2 () {
        System.out.println("KMAC Test2");
        String s = "404142434445464748494A4B4C4D4E4F505152535455565758595A5B5C5D5E5F";
        byte[] K  = hexToByte(s);
        byte[] X = hexToByte("000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F202122232425262728292A2B2C2D2E2F303132333435363738393A3B3C3D3E3F404142434445464748494A4B4C4D4E4F505152535455565758595A5B5C5D5E5F606162636465666768696A6B6C6D6E6F707172737475767778797A7B7C7D7E7F808182838485868788898A8B8C8D8E8F909192939495969798999A9B9C9D9E9FA0A1A2A3A4A5A6A7A8A9AAABACADAEAFB0B1B2B3B4B5B6B7B8B9BABBBCBDBEBFC0C1C2C3C4C5C6C7");
        int L = 512;
        byte[] S = "".getBytes();
        byte[] test2 = Shake.KMACXOF256(K, X, L, S);

        String desiredResult = "FF 7B 17 1F 1E 8A 2B 24 68 3E ED 37 83 0E E7 97 " +
                "53 8B A8 DC 56 3F 6D A1 E6 67 39 1A 75 ED C0 2C " +
                "A6 33 07 9F 81 CE 12 A2 5F 45 61 5E C8 99 72 03 " +
                "1D 18 33 73 31 D2 4C EB 8F 8C A8 E6 A1 9F D9 8B ";
        System.out.println("KMAC DESIRED: " + desiredResult);
        System.out.println("KMAC RESULT : " + Shake.bytesToHex(test2));
        System.out.println("Are they equal: " + desiredResult.equals(Shake.bytesToHex(test2)));
        System.out.println();
    }

    // https://csrc.nist.gov/csrc/media/projects/cryptographic-standards-and-guidelines/documents/examples/kmacxof_samples.pdf
    // From Sample #6
    public static void KMACTest3 () {
        System.out.println("KMAC Test3");
        String s = "404142434445464748494A4B4C4D4E4F505152535455565758595A5B5C5D5E5F";
        byte[] K  = hexToByte(s);
        byte[] X = hexToByte("000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F202122232425262728292A2B2C2D2E2F303132333435363738393A3B3C3D3E3F404142434445464748494A4B4C4D4E4F505152535455565758595A5B5C5D5E5F606162636465666768696A6B6C6D6E6F707172737475767778797A7B7C7D7E7F808182838485868788898A8B8C8D8E8F909192939495969798999A9B9C9D9E9FA0A1A2A3A4A5A6A7A8A9AAABACADAEAFB0B1B2B3B4B5B6B7B8B9BABBBCBDBEBFC0C1C2C3C4C5C6C7");
        int L = 512;
        byte[] S = "My Tagged Application".getBytes();
        byte[] test2 = Shake.KMACXOF256(K, X, L, S);

        String desiredResult = "D5 BE 73 1C 95 4E D7 73 28 46 BB 59 DB E3 A8 E3 " +
                "0F 83 E7 7A 4B FF 44 59 F2 F1 C2 B4 EC EB B8 CE " +
                "67 BA 01 C6 2E 8A B8 57 8D 2D 49 9B D1 BB 27 67 " +
                "68 78 11 90 02 0A 30 6A 97 DE 28 1D CC 30 30 5D ";
        System.out.println("KMAC DESIRED: " + desiredResult);
        System.out.println("KMAC RESULT : " + Shake.bytesToHex(test2));
        System.out.println("Are they equal: " + desiredResult.equals(Shake.bytesToHex(test2)));
        System.out.println();
    }

    // Used to convert keys to byte arrays.
    public static byte[] hexToByte(String s) {
        byte[] result = new byte[s.length() / 2];
        for (int i = 0; i < result.length; i++) {
            int index = i * 2;
            // Using parseInt() method of Integer class
            int val = Integer.parseInt(s.substring(index, index + 2), 16);
            result[i] = (byte)val;
        }
        return result;
    }
}
