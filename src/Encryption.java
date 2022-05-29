import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 *
 */
public class Encryption {

    private SecureRandom sr;
    byte[] EMPTYSTRING = "".getBytes();

    public Encryption(){
        sr = new SecureRandom();
    }

    public EllipticCurvePoint GenerateKeyPair(byte[] passphrase) {
        byte[] passphraseTemp = (Shake.KMACXOF256(passphrase, "".getBytes(), 512, "K".getBytes()));
        BigInteger s = new BigInteger(passphraseTemp);
        s = s.multiply(BigInteger.valueOf(4));
        EllipticCurvePoint V = EllipticCurvePoint.scalarMultiplication(EllipticCurvePoint.G, s);
        return V;
    }

    /**
     *
     * @param m
     * @param passPhrase
     * @return
     */
    public SymmetricCryptogram Encryption(byte[] m, String passPhrase){
        byte[] z = new byte[512];

        //z <- Random(512)
        sr.nextBytes(z);
        byte[] pp = passPhrase.getBytes();

        //(ke || ka) <- KMACXOF256(z || pw, “”, 1024, “S”)
        byte[] keka = Shake.KMACXOF256(Shake.concat(z,pp), EMPTYSTRING,1024,"S".getBytes());
        byte[] ke = Arrays.copyOfRange(keka, 0, keka.length/2);
        byte[] ka = Arrays.copyOfRange(keka, keka.length/2, keka.length);

        //c <- KMACXOF256(ke, “”, |m|, “SKE”) xor m
        byte[] c = xor(Shake.KMACXOF256(ke, EMPTYSTRING, m.length * 8, "SKE".getBytes()),m);

        //t <- KMACXOF256(ka, m, 512, “SKA”)
        byte[] t = Shake.KMACXOF256(ka, m, 512, "SKA".getBytes());

        //symmetric cryptogram: (z, c, t)
        return new SymmetricCryptogram(z,c,t);
    }

    public PointCryptogram PointEncryption(byte[] m, EllipticCurvePoint V) {
        // k <- Random(512); k <- 4k
        byte[] kbytes = new byte[512];
        sr.nextBytes(kbytes);
        BigInteger k = new BigInteger(kbytes);
        k = k.multiply(BigInteger.valueOf(4));

        // W  k*V; Z <- k*G
        EllipticCurvePoint W = EllipticCurvePoint.scalarMultiplication(V, k);
        EllipticCurvePoint Z = EllipticCurvePoint.scalarMultiplication(EllipticCurvePoint.G, k);

        // (ke || ka) <- KMACXOF256(Wx, “”, 1024, “P”)
        byte[] keka = Shake.KMACXOF256(W.getX().toByteArray(), ("").getBytes(), 1024, "P".getBytes());
        byte[] ke = Arrays.copyOfRange(keka, 0, keka.length/2);
        byte[] ka = Arrays.copyOfRange(keka, keka.length/2, keka.length);

        // c <- KMACXOF256(ke, “”, |m|, “PKE”) xor m
        byte[] c = xor(Shake.KMACXOF256(ke, EMPTYSTRING, m.length * 8, "PKE".getBytes()),m);

        // t <- KMACXOF256(ka, m, 512, “PKA”)
        byte[] t = Shake.KMACXOF256(ka, m, 512, "PKA".getBytes());

        // cryptogram: (Z, c, t)
        return new PointCryptogram(Z,c,t);
    }

    /**
     *
     * @param sym
     * @param passPhrase
     * @return byte array or null.
     */
    public byte[] Decryption(SymmetricCryptogram sym, String passPhrase){
        byte[] z = sym.getZ();
        byte[] c = sym.getC();
        byte[] t = sym.getT();
        byte[] pp = passPhrase.getBytes();

        //(ke || ka) <- KMACXOF256(z || pw, “”, 1024, “S”)
        byte[] keka = Shake.KMACXOF256(Shake.concat(z,pp), EMPTYSTRING,1024,"S".getBytes());
        byte[] ke = Arrays.copyOfRange(keka, 0, keka.length/2);
        byte[] ka = Arrays.copyOfRange(keka, keka.length/2, keka.length);

        //m <- KMACXOF256(ke, “”, |c|, “SKE”) xor c
        byte[] m = xor(Shake.KMACXOF256(ke, EMPTYSTRING, c.length * 8,"SKE".getBytes()),c);

        //t’ <- KMACXOF256(ka, m, 512, “SKA”)
        byte[] t_2 = Shake.KMACXOF256(ka, m, 512, "SKA".getBytes());

        // accept if, and only if, t’ = t
        if (Arrays.equals(t,t_2)){
            return m;
        } else {
            return null;
        }
    }

    public byte[] PointDecryption(PointCryptogram point, byte[] pw) {
        EllipticCurvePoint Z = point.getZ();
        byte[] c = point.getC();
        byte[] t = point.getT();

        // s <- KMACXOF256(pw, “”, 512, “K”); s <- 4s
        byte[] sArray = Shake.KMACXOF256(pw, EMPTYSTRING,512,"K".getBytes());
        BigInteger s = new BigInteger(sArray).multiply(BigInteger.valueOf(4));

        // W <- s*Z
        EllipticCurvePoint W = EllipticCurvePoint.scalarMultiplication(Z, s);

        // (ke || ka) <- KMACXOF256(Wx, “”, 1024, “P”)
        byte[] keka = Shake.KMACXOF256(W.getX().toByteArray(), EMPTYSTRING,1024,"P".getBytes());
        byte[] ke = Arrays.copyOfRange(keka, 0, keka.length/2);
        byte[] ka = Arrays.copyOfRange(keka, keka.length/2, keka.length);

        // m <- KMACXOF256(ke, “”, |c|, “PKE”) xor c
        byte[] m = xor(Shake.KMACXOF256(ke, EMPTYSTRING, c.length * 8, "PKE".getBytes()),c);

        // t’ <- KMACXOF256(ka, m, 512, “PKA”)
        byte[] t_2 = Shake.KMACXOF256(ka, m, 512, "PKA".getBytes());

        // accept if, and only if, t’ = t
        if (Arrays.equals(t,t_2)){
            return m;
        } else {
            return null;
        }
    }

    public BigInteger[] GenerateSignature(byte[] m, byte[] pw) {
        // s <- KMACXOF256(pw, “”, 512, “K”); s <- 4s
        byte[] sArray = Shake.KMACXOF256(pw, EMPTYSTRING,512,"K".getBytes());
        BigInteger s = new BigInteger(sArray).multiply(BigInteger.valueOf(4));

        // k <- KMACXOF256(s, m, 512, “N”); k <- 4k
        byte[] kArray = Shake.KMACXOF256(s.toByteArray(), EMPTYSTRING,512,"N".getBytes());
        BigInteger k = new BigInteger(kArray).multiply(BigInteger.valueOf(4));

        // U <- k*G;
        EllipticCurvePoint U = EllipticCurvePoint.scalarMultiplication(EllipticCurvePoint.G, k);

        // h <- KMACXOF256(Ux, m, 512, “T”); z <- (k – hs) mod r
        byte[] hArray = Shake.KMACXOF256(U.getX().toByteArray(), m,512,"T".getBytes());
        BigInteger h = new BigInteger(hArray);
        BigInteger z = (k.subtract(h.multiply(s)).mod(EllipticCurvePoint.r));

        // signature: (h, z)
        BigInteger[] result = {h, z};
        return result;
    }

    public byte[] VerifySignature(BigInteger[] hz, byte[] m, EllipticCurvePoint V) {
        // U <- z*G + h*V
        EllipticCurvePoint zG = EllipticCurvePoint.scalarMultiplication(V, hz[0]);
        EllipticCurvePoint hV = EllipticCurvePoint.scalarMultiplication(EllipticCurvePoint.G, hz[1]);
        EllipticCurvePoint U = zG.addPoints(hV);

        // accept if, and only if, KMACXOF256(Ux, m, 512, “T”) = h
        byte[] result = Shake.KMACXOF256(U.getX().toByteArray(), m,512,"T".getBytes());
        if (Arrays.equals(result, hz[0].toByteArray())){
            return result;
        } else {
            return null;
        }
    }

    private byte[] xor(byte[] a, byte[] b){
        int len = a.length < b.length ? a.length : b.length;
        byte[] c = new byte[len];
        for (int i = 0; i < len; i++)
            c[i] = (byte) (0xff & a[i] ^ b[i]);
        return c;
    }
}
