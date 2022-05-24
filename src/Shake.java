import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

// TCSS 487 Project - Alex Trinh, Eugene Oh.
// All functions were based off of mjorsaarinen's tiny_sha3 implementation on GitHub
// and the SHA-3 NIST documentation.
public class Shake {

    private static final int KECCAKF_ROUNDS = 24;
    private static int mdlen, rsiz, pt;
    private static boolean KMACMode = false;
    private static boolean XOFCheck = false;
    private static byte[] state = new byte[200];

    private static final long[] keccakf_rndc = {
            0x0000000000000001L, 0x0000000000008082L, 0x800000000000808aL,
            0x8000000080008000L, 0x000000000000808bL, 0x0000000080000001L,
            0x8000000080008081L, 0x8000000000008009L, 0x000000000000008aL,
            0x0000000000000088L, 0x0000000080008009L, 0x000000008000000aL,
            0x000000008000808bL, 0x800000000000008bL, 0x8000000000008089L,
            0x8000000000008003L, 0x8000000000008002L, 0x8000000000000080L,
            0x000000000000800aL, 0x800000008000000aL, 0x8000000080008081L,
            0x8000000000008080L, 0x0000000080000001L, 0x8000000080008008L
    };

    private static final int[] keccakf_rotc = {
            1, 3, 6, 10, 15, 21, 28, 36, 45, 55, 2, 14,
            27, 41, 56, 8,  25, 43, 62, 18, 39, 61, 20, 44
    };

    private static final int[] keccakf_piln = {
            10, 7, 11, 17, 18, 3, 5, 16, 8, 21, 24, 4,
            15, 23, 19, 13, 12, 2, 20, 14, 22, 9, 6, 1
    };

    // Simulates bit rotation.
    private static long RotateLeft(long x, int y) {
        return (x << y) | (x >>> (64 - y));
    }

    // The very easy to understand keccak algorithm.
    static void sha3_keccakf(byte[] stateArg) {
        long[] q = new long[25];
        long[] bc = new long[5];

        // Endian conversion.
        int counter = 0;
        for (int i = 0; i < q.length; i++) {
            q[i] =  (((long) stateArg[counter + 0] & 0xFFL)) | (((long) stateArg[counter + 1] & 0xFFL) << 8) |
                    (((long) stateArg[counter + 2] & 0xFFL) << 16) | (((long) stateArg[counter + 3] & 0xFFL) << 24) |
                    (((long) stateArg[counter + 4] & 0xFFL) << 32) | (((long) stateArg[counter + 5] & 0xFFL) << 40) |
                    (((long) stateArg[counter + 6] & 0xFFL) << 48) | (((long) stateArg[counter + 7] & 0xFFL) << 56);
            counter += 8;
        }

        // The iteration part of Keccak.
        for (int r = 0; r < KECCAKF_ROUNDS; r++) {

            // Theta
            for (int i = 0; i < 5; i++) {
                bc[i] = q[i] ^ q[i + 5] ^ q[i + 10] ^ q[i + 15] ^ q[i + 20];
            }
            for (int i = 0; i < 5; i++) {
                long t = bc[(i + 4) % 5] ^ RotateLeft(bc[(i + 1) % 5], 1);
                for (int j = 0; j < 25; j += 5) {
                    q[j + i] ^= t;
                }
            }

            // Rho Pi
            long t = q[1];
            for (int i = 0; i < 24; i++) {
                int j = keccakf_piln[i];
                bc[0] = q[j];
                q[j] = RotateLeft(t, keccakf_rotc[i]);
                t = bc[0];
            }

            // Chi
            for (int j = 0; j < 25; j += 5) {
                for (int i = 0; i < 5; i++) {
                    bc[i] = q[j + i];
                }
                for (int i = 0; i < 5; i++) {
                    q[j + i] ^= (~bc[(i + 1) % 5]) & bc[(i + 2) % 5];
                }
            }

            // Iota
            q[0] ^= keccakf_rndc[r];
        }

        // Endian conversion again.
        int counter2 = 0;
        for (int i = 0; i < q.length; i++) {
            long t = q[i];
            stateArg[counter2 + 0] = (byte)((t) & 0xFF);
            stateArg[counter2 + 1] = (byte)((t >> 8) & 0xFF);
            stateArg[counter2 + 2] = (byte)((t >> 16) & 0xFF);
            stateArg[counter2 + 3] = (byte)((t >> 24) & 0xFF);
            stateArg[counter2 + 4] = (byte)((t >> 32) & 0xFF);
            stateArg[counter2 + 5] = (byte)((t >> 40) & 0xFF);
            stateArg[counter2 + 6] = (byte)((t >> 48) & 0xFF);
            stateArg[counter2 + 7] = (byte)((t >> 56) & 0xFF);
            counter2 += 8;
        }
        state = stateArg;
    }

    // Initialization for SHA3.
    public static void sha3_init() {
        Arrays.fill(state, (byte) 0);
        mdlen = 32;
        rsiz = 200 - (2 * mdlen);
        pt = 0;
    }

    // Initialization for KMAC.
    public static void KMAC_init(byte[] K, byte[] S, byte[] KMAC) {
        byte[] encK = bytepad(encode_string(K), BigInteger.valueOf(136));
        sha3_init();
        if ((KMAC != null && KMAC.length != 0) || (S != null && S.length != 0)){
            XOFCheck = true;
            byte[] combination = bytepad(concat(encode_string(KMAC),encode_string(S)), BigInteger.valueOf(136));
            sha3_update(combination, combination.length);
        }
        sha3_update(encK, encK.length);
    }

    // Updating the state with more data.
    public static void sha3_update(byte[] data, int len) {
        int j = pt;
        for (int i = 0; i < len; i++) {
            state[j++] ^= data[i];
            if (j >= rsiz) {
                sha3_keccakf(state);
                j = 0;
            }
        }
        pt = j;
    }

    // Finalize and output a hash.
    public static byte[] sha3_final() {
        state[pt] ^= 0x06;
        state[rsiz - 1] ^= 0x80;
        sha3_keccakf(state);
        byte[] md = new byte[mdlen];
        for (int i = 0; i < mdlen; i++) {
            md[i] = state[i];
        }
        return md;
    }

    // The SHA-3 Hash which returns a hash from a given byte array.
    public static byte[] sha3(byte[] in, int inlen, int mdlen) {
        sha3_init();
        sha3_update(in, inlen);
        return sha3_final();
    }

    // SHAKE128 and SHAKE256 extensible-output functionality.
    public static void shake_xof() {
        if (KMACMode) {
            byte[] right_encode = right_encode(0);
            sha3_update(right_encode, right_encode.length);
        }
        if (XOFCheck) {
            state[pt] ^= (byte)0x04;
        } else {
            state[pt] ^= (byte)0x1F;
        }
        state[rsiz - 1] ^= (byte)0x80;

        sha3_keccakf(state);
        pt = 0;
    }

    // SHAKE128 and SHAKE256 extensible-output functionality.
    public static void shake_out(byte[] out, int len) {
        int j = pt;
        for (int i = 0; i < len; i++) {
            if (j >= rsiz) {
                sha3_keccakf(state);
                j = 0;
            }
            out[i] = state[j++];
        }
        pt = j;
    }

    /**
     * Computes a byte array from a given integer.
     * @param x The integer to be used to to encode the byte array.
     * @return The computed byte array.
     */
    public static byte[] right_encode(final int x){
        int n = 1;
        while (1<<(8*n) <= x){
            n++;
        }
        byte[] x_list = new byte[n+1];
        for (int i = 1; i <= n; i++){
            x_list[i] = (byte) (x>>>(8*(n-i)));
        }
        x_list[0] = (byte) n;

        // Swapping for right encode.
        byte[] swapped_result = new byte[n+1];
        for (int i = 1; i <= n; i++) {
            swapped_result[i - 1] = x_list[i];
        }
        swapped_result[n] = x_list[0];
        return swapped_result;
    }

    /**
     * Computes a byte array from a given integer.
     * @param x The integer to be used to to encode the byte array.
     * @return The computed byte array.
     */
    public static byte[] left_encode(final int x)  {
        int n = 1;
        while (1<<(8*n) <= x){
            n++;
        }
        byte[] x_list = new byte[n+1];
        for (int i = 1; i <= n; i++){
            x_list[i] = (byte) (x>>>(8*(n-i)));
        }
        x_list[0] = (byte) n;
        return x_list;
    }

    /**
     * This code is from Professor Barreto's Week 2 Slides.
     * Apply the NIST bytepad primitive to a byte array X with encoding factor w.
     * @param X the byte array to bytepad
     * @param w the encoding factor (the output length must be a multiple of w)
     * @return the byte-padded byte array X with encoding factor w.
     */
    static byte[] bytepad(byte[] X, BigInteger w) {
        // Validity Conditions: w > 0
        assert w.intValue() > 0;
        // 1. z = left_encode(w) || X.
        byte[] wenc = left_encode(w.intValue());
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
    static byte[] encode_string(byte[] bitString) {
        BigInteger bitStringLength = BigInteger.valueOf(bitString.length);
        byte[] leftEncodeResult = left_encode(bitString.length << 3);

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

    /**
     * Concatenates a and b to a new byte array
     * @param a the first array
     * @param b the second array
     * @return A byte array combined from two given a and b array
     */
    public static byte[] concat(final byte[] a, final byte[] b){
        int alen = (a != null) ? a.length : 0;
        int blen = (b != null) ? b.length : 0;
        byte[] c = new byte[alen + blen];
        System.arraycopy(a, 0, c, 0, alen);
        System.arraycopy(b, 0, c, alen, blen);
        return c;
    }

    /**
     *
     * @param X The main input bit string. May be of any length, including 0.
     * @param L Integer representing the requested output length in bits.
     * @param N Function-name bit string to define function used.
     * @param S Bit string that defines the variant of the function that is desired.
     * @return
     */
    static byte[] cShake256(byte[] X, int L, byte[] N, byte[] S){
        Shake shake = new Shake();
        byte[] result = new byte[L];
        shake.sha3_init();
        if ((N != null && N.length != 0) || (S != null && S.length != 0)){
            XOFCheck = true;
            byte[] combination = bytepad(concat(encode_string(N),encode_string(S)), BigInteger.valueOf(136));
            shake.sha3_update(combination, combination.length);
        }
        shake.sha3_update(X, X.length);

        shake.shake_xof();
        byte[] result_cut = new byte[result.length >>> 3];
        shake.shake_out(result_cut, result.length >>> 3);
        return result_cut;
    }

    /**
     *
     * @param K Key bit string of any length.
     * @param X Main input bit string on any length.
     * @param L Requested output length.
     * @param S Optional customization bit string.
     * @return
     */
    static byte[] KMACXOF256(byte[] K, byte[] X, int L, byte[] S){
        Shake shake = new Shake();
        KMACMode = true;
        byte[] result = new byte[L];
        shake.KMAC_init(K, S, "KMAC".getBytes());
        shake.sha3_update(X, X.length);
        shake.shake_xof();
        // HERE IS THE CAUSE OF THE ENCRYPTION/DECRYPTION ERROR ([result.length >>> 3] vs [result.length]) ---------------------
        // Having it here causes enc/dec to stop functioning, but it passes all of the tests.
        // Not having it allows enc/dec to work, but it does not pass all of the tests because
        // the outputted byte array is too long. It is the correct input, but not trimmed.
        // May be something wrong inside of enc/dec functions.
        byte[] result_cut = new byte[result.length >>> 3]; // vs. [result.length];
        shake.shake_out(result_cut, result.length >>> 3); // vs. result.length);
        // HERE IS THE CAUSE OF THE ENCRYPTION/DECRYPTION ERROR ([result.length >>> 3] vs [result.length]) ---------------------
        return result_cut;
    }

    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);

    /**
     * @param bytes To be converted.
     * @return Hex representation of byte array.
     */
    static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8).replaceAll("..", "$0 ");
    }
}
