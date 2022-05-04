import java.security.SecureRandom;
import java.util.Arrays;

public class Encryption {
    private SecureRandom sr;
    byte[] EMPTYSTRING = "".getBytes();

    public Encryption(){
        sr = new SecureRandom();
    }

    public SymmetricCryptogram encrypt(byte[] m, String passPhrase){
        byte[] z = new byte[512];
        sr.nextBytes(z);
        byte[] pp = passPhrase.getBytes();
        byte[] keka = TCSS487Project.KMACXOF256(TCSS487Project.concat(z,pp), EMPTYSTRING,1024,"S".getBytes());
        byte[] ke = Arrays.copyOfRange(keka, 0, keka.length/2);
        byte[] ka = Arrays.copyOfRange(keka, keka.length/2, keka.length);
        //need to do t he exclusive-or with m here
        byte[] c = TCSS487Project.KMACXOF256(ke, EMPTYSTRING, m.length, "SKE".getBytes());
        byte[] t = TCSS487Project.KMACXOF256(ka, m, 512, "SKA".getBytes());
        return new SymmetricCryptogram(z,c,t);
    }

    public byte[] decrypt(SymmetricCryptogram sym, String passPhrase){
        byte[] z = sym.getZ();
        byte[] c = sym.getC();
        byte[] t = sym.getT();
        byte[] pp = passPhrase.getBytes();
        byte[] keka = TCSS487Project.KMACXOF256(TCSS487Project.concat(z,pp), EMPTYSTRING,1024,"S".getBytes());
        byte[] ke = Arrays.copyOfRange(keka, 0, keka.length/2);
        byte[] ka = Arrays.copyOfRange(keka, keka.length/2, keka.length);
        byte[] m = TCSS487Project.KMACXOF256(ke, EMPTYSTRING,c.length,"SKE".getBytes());
        byte[] t_2 = TCSS487Project.KMACXOF256(ka, m, 512, "SKA".getBytes());
        if (Arrays.equals(t,t_2)){
            return m;
        }else
            return null;
    }

}
