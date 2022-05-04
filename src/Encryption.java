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

    /**
     *
     * @param m
     * @param passPhrase
     * @return
     */
    public SymmetricCryptogram encrypt(byte[] m, String passPhrase){
        byte[] z = new byte[512];
        //z <- Random(512)
        sr.nextBytes(z);
        byte[] pp = passPhrase.getBytes();
        //(ke || ka) <- KMACXOF256(z || pw, “”, 1024, “S”)
        byte[] keka = Shake.KMACXOF256(Shake.concat(z,pp), EMPTYSTRING,1024,"S".getBytes());
        byte[] ke = Arrays.copyOfRange(keka, 0, keka.length/2);
        byte[] ka = Arrays.copyOfRange(keka, keka.length/2, keka.length);
        //c <- KMACXOF256(ke, “”, |m|, “SKE”) xor m
        byte[] c = xor(Shake.KMACXOF256(ke, EMPTYSTRING, m.length, "SKE".getBytes()),m);
        //t <- KMACXOF256(ka, m, 512, “SKA”)
        byte[] t = Shake.KMACXOF256(ka, m, 512, "SKA".getBytes());
        //symmetric cryptogram: (z, c, t)
        return new SymmetricCryptogram(z,c,t);
    }

    /**
     *
     * @param sym
     * @param passPhrase
     * @return byte array or null.
     */
    public byte[] decrypt(SymmetricCryptogram sym, String passPhrase){
        byte[] z = sym.getZ();
        byte[] c = sym.getC();
        byte[] t = sym.getT();
        byte[] pp = passPhrase.getBytes();
        //(ke || ka) <- KMACXOF256(z || pw, “”, 1024, “S”)
        byte[] keka = Shake.KMACXOF256(Shake.concat(z,pp), EMPTYSTRING,1024,"S".getBytes());
        byte[] ke = Arrays.copyOfRange(keka, 0, keka.length/2);
        byte[] ka = Arrays.copyOfRange(keka, keka.length/2, keka.length);
        //m <- KMACXOF256(ke, “”, |c|, “SKE”) xor c
        byte[] m = xor(Shake.KMACXOF256(ke, EMPTYSTRING, c.length,"SKE".getBytes()),c);
        //t’ <- KMACXOF256(ka, m, 512, “SKA”)
        byte[] t_2 = Shake.KMACXOF256(ka, m, 512, "SKA".getBytes());
        //accept if, and only if, t’ = t
        if (Arrays.equals(t,t_2)){
            return m;
        }else
            return null;
    }

    /**
     *
     * @param a
     * @param b
     * @return
     */
    private byte[] xor(byte[] a, byte[] b){
        int len = a.length < b.length ? a.length : b.length;
        byte[] c = new byte[len];
        for (int i = 0; i < len; i++)
            c[i] = (byte) (0xff & a[i] ^ b[i]);
        return c;
    }
}
