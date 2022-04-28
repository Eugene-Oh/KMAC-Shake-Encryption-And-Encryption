import java.math.BigInteger;
import java.util.Arrays;

// TCSS 487 Project - Alex Trinh, Eugene Oh.
public class TCSS487Project {
    public static void main(String[] args) throws Exception { 
        // Testing the different required functions.
        int encodeTest = 0;
        byte[] leftTest = left_encode(new BigInteger("" + encodeTest));
        System.out.println("\nleft_encode test using " + encodeTest + ": " + Arrays.toString(leftTest) + "\n");

        byte[] rightTest = right_encode(new BigInteger("" + encodeTest));
        System.out.println("right_encode test using " + encodeTest + ": " + Arrays.toString(rightTest) + "\n");

        int bytepadInt = 11;
        byte[] bytepadTest = bytepad(rightTest, new BigInteger("" + bytepadInt));
        System.out.println("Using right_encode value with " + bytepadInt + " for bytepad: " + Arrays.toString(bytepadTest) + "\n");

        String encodeStringTest = "asd";
        byte[] encodeStringTestBytes = encode_string(encodeStringTest.getBytes());
        System.out.println("encode_string test using string \"" + encodeStringTest + "\": " + Arrays.toString(encodeStringTestBytes) + "\n");
    }

    /**
     * Computes a byte array from a given BigInteger based of encoding from the right.
     * It achieves this by computing a byte array that represents the actual number and
     * another byte array that represents the number of bytes in the previous byte array
     * and combines them.
     * @param x The BigInteger to be used to to encode the byte array.
     * @return The computed byte array.
     */
	public static byte[] right_encode(BigInteger x){

        // Step 1 is not needed from the NIST pseudocode.
        // 2. Let x1, x2,…, xn be the base-256 encoding of x satisfying:
        // x = ∑ 28(n-i) xi, for i = 1 to n.
        byte[] bytes = x.toByteArray();
        int lengthOfByteArray = bytes.length;
        byte[] padding = BigInteger.valueOf(lengthOfByteArray).toByteArray();
        int lengthOfPaddingByteArray = padding.length;
        byte[] result = new byte[lengthOfByteArray + lengthOfPaddingByteArray];

        // 4. Let Oi = enc8(xi), for i = 1 to n.
        int i = 0;
        while (i < lengthOfByteArray) {
            result[i] = bytes[i];
            i++;
        }

        // 3. Let On+1 = enc8(n).
        int j = 0;
        while (j < lengthOfPaddingByteArray) {
            result[j + lengthOfByteArray] = padding[j];
            j++;
        }
        return result;
    }

    /**
     * Computes a byte array from a given BigInteger based of encoding from the left.
     * It achieves this by computing a byte array that represents the actual number and
     * another byte array that represents the number of bytes in the previous byte array
     * and combines them.
     * @param x The BigInteger to be used to to encode the byte array.
     * @return The computed byte array.
     */
	public static byte[] left_encode(BigInteger x){

        // Step 1 is not needed from the NIST pseudocode.
        // 2. Let x1, x2,…, xn be the base-256 encoding of x satisfying:
        // x = ∑ 28(n-i) xi, for i = 1 to n.
        byte[] bytes = x.toByteArray();
        int lengthOfByteArray = bytes.length;
        byte[] padding = BigInteger.valueOf(lengthOfByteArray).toByteArray();
        int lengthOfPaddingByteArray = padding.length;
        byte[] result = new byte[lengthOfByteArray + lengthOfPaddingByteArray];

        // 4. Let O0 = enc8(n).
        int i = 0;
        while (i < lengthOfPaddingByteArray) {
            result[i] = padding[i];
            i++;
        }

        // 3. Let Oi = enc8(xi), for i = 1 to n.
        int j = 0;
        while (j < lengthOfByteArray) {
            result[j + lengthOfPaddingByteArray] = bytes[j];
            j++;
        }
        return result;
    }



    /**
    * This code is from Professor Barreto's Week 2 Slides.
    * Apply the NIST bytepad primitive to a byte array X with encoding factor w.
    * @param X the byte array to bytepad
    * @param w the encoding factor (the output length must be a multiple of w)
    * @return the byte-padded byte array X with encoding factor w.
    */
    private static byte[] bytepad(byte[] X, BigInteger w) {
        // Validity Conditions: w > 0
        assert w.intValue() > 0;
        // 1. z = left_encode(w) || X.
        byte[] wenc = left_encode(w);
        byte[] z = new byte[w.intValue()*((wenc.length + X.length + w.intValue() - 1)/w.intValue())];
        // NB: z.length is the smallest multiple of w that fits wenc.length + X.length
        System.arraycopy(wenc, 0, z, 0, wenc.length);
        System.arraycopy(X, 0, z, wenc.length, X.length);
        // 2. (nothing to do: len(z) mod 8 = 0 in this byte-oriented implementation)
        // 3. while (len(z)/8) mod w ≠ 0: z = z || 00000000
        for (int i = wenc.length + X.length; i < z.length; i++) {
        z[i] = (byte)0;
        }
        // 4. return z
        return z;
        }

    /**
     * Computes two byte arrays from the given bit strings.
     * @param bitString The given bit string.
     * @return A byte array combined from the two computed byte arrays.
     */
    private static byte[] encode_string(byte[] bitString) {
        BigInteger bitStringLength = BigInteger.valueOf(bitString.length);
        byte[] leftEncodeResult = left_encode(bitStringLength);

        // An empty bit string is passed.
        if (bitString.length == 0) {
            return leftEncodeResult;
        }
        
        // Computing the byte array from the length of the given bit string.
        int resultLength = bitString.length + leftEncodeResult.length;
        byte[] result = new byte[resultLength];

        // Combine the result with the given bit string.
        System.arraycopy(leftEncodeResult, 0, result, 0, leftEncodeResult.length);
        System.arraycopy(bitString, 0, result, leftEncodeResult.length, bitString.length);
        return result;
    }
}
