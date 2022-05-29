import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 *
 */
public class App {
    private static JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please choose an option:");
        System.out.println("\n------------------------------------------------------------------------\n"+
                        "0. Open a file \n"
                        + "1. Give a string\n"
                        + "2. Encrypt a given data file symmetrically under a given passphrase\n"
                        + "3. Decrypt a given symmetric cryptogram under a given passphrase\n"
                        + "------------------------------------------------------------------------\n"
                        + "4. Generate an elliptic key pair from a given passphrase\n"
                        + "5. Encrypt a data file under a given elliptic public key file\n"
                        + "6. Decrypt a given elliptic-encrypted file from a given password\n"
                        + "7. Sign a file from a given password and write the signature to the file\n"
                        + "8. Verify a given data file and its signature file under a public key file\n"
                        + "------------------------------------------------------------------------\n"
                        + "9. Exit\n"
                        + "------------------------------------------------------------------------\n\n"
                        + "Your choice: ");
        int choice = 0;
        byte[] S; //diversification string
        byte[] K; //key
        byte[] dataFile = null; //message
        byte[] result;
        String passphrase = "";
        Encryption enc = new Encryption();
        SymmetricCryptogram sym = null;
        EllipticCurvePoint pointsym = null;
        S = "D".getBytes();
        K = "".getBytes();
        String str = "";
        while (choice != 9){
            if (sc.hasNextInt()){
                choice = sc.nextInt();
                if (choice == 0){
                    dataFile = getFile();
                    result = Shake.KMACXOF256(K, dataFile, 512, S);
                    System.out.println("Plain cryptographic hash result: \n"
                            + Shake.bytesToHex(result)
                            + "\n");
                    }
                else if (choice == 1){
                    System.out.println("Your string: ");
                    str = sc.next();
                    dataFile = str.getBytes();
                    result = Shake.KMACXOF256(K, dataFile, 512, S);
                    System.out.println("Plain cryptographic hash result: \n"
                            + Shake.bytesToHex(result)
                            + "\n");
                }
                else if (choice == 2){
                    System.out.println("Please choose a file");
                    dataFile = getFile();
                    System.out.println("Please enter you passphrase: ");
                    passphrase = sc.next();
                    sym = enc.Encryption(dataFile,passphrase);
                    System.out.println("Enter the name of the output file with .txt at the end: ");
                    saveFile(sym);
                }
                else if (choice == 3){
                    System.out.println("Open the encrypted file: ");
                    Object obj = openFile();
                    if (obj != null){
                        SymmetricCryptogram sym2 = (SymmetricCryptogram) obj;
                        System.out.println("Please enter you passphrase: ");
                        passphrase = sc.next();
                        byte[] decryptObj = enc.Decryption(sym2,passphrase);
                        if (decryptObj != null){
                            System.out.println("Your byte code:");
                            System.out.println(Shake.bytesToHex(decryptObj));
                        }
                        else{
                            System.out.println("Maybe you choose a wrong file or a wrong pass phrase.");
                        }
                    }
                }
                else if (choice == 4) {
                    System.out.println("Please enter you passphrase: ");
                    passphrase = sc.next();
                    pointsym = enc.GenerateKeyPair(passphrase.getBytes());
                    System.out.println("Enter the name of the output file with .txt at the end: ");
                    saveFile(pointsym);
                }
                else if (choice == 5) {
                    System.out.println("Please choose the data file");
                    dataFile = getFile();
                    System.out.println("Please choose the elliptic public key file: ");
                    Object obj = openFile();
                    if (obj != null) {
                        EllipticCurvePoint point = (EllipticCurvePoint) obj;
                        PointCryptogram cryptogram = enc.PointEncryption(dataFile, point);
                        System.out.println("Enter the name of the output file with .txt at the end: ");
                        saveFile(cryptogram);
                    }
                }
                else if (choice == 6) {
                    System.out.println("Open the encrypted file: ");
                    Object obj = openFile();
                    if (obj != null){
                        PointCryptogram sym2 = (PointCryptogram) obj;
                        System.out.println("Please enter you passphrase: ");
                        passphrase = sc.next();
                        byte[] decryptObj = enc.PointDecryption(sym2,passphrase.getBytes());
                        if (decryptObj != null){
                            System.out.println("Your byte code:");
                            System.out.println(Shake.bytesToHex(decryptObj));
                        }
                        else{
                            System.out.println("Maybe you choose a wrong file or a wrong pass phrase.");
                        }
                    }
                }
                else if (choice == 7) {
                    System.out.println("Please choose the data file");
                    dataFile = getFile();
                    System.out.println("Please enter you passphrase: ");
                    passphrase = sc.next();
                    BigInteger[] signature = enc.GenerateSignature(dataFile, passphrase.getBytes());
                    System.out.println("Enter the name of the output file with .txt at the end: ");
                    saveFile(signature);
                }
                else if (choice == 8) {
                    System.out.println("Please choose the data file");
                    dataFile = getFile();
                    System.out.println("Open the signature file: ");
                    Object obj = openFile();
                    if (obj != null) {
                        BigInteger[] signature = (BigInteger[]) obj;
                        System.out.println("Please choose the elliptic public key file: ");
                        Object obj2 = openFile();
                        if (obj2 != null) {
                            EllipticCurvePoint point = (EllipticCurvePoint) obj2;
                            byte[] signatureResult = enc.VerifySignature(signature, dataFile, point);
                            if (signatureResult != null) {
                                System.out.println("The signature is valid!");
                            } else {
                                System.out.println("The signature is NOT valid!");
                            }
                        }
                    }
                }
                else if (choice == 9) {
                    System.out.println("Exiting...");
                    System.exit(0);
                }
//              break;
                System.out.println("Please choose an option:");
                System.out.println("\n------------------------------------------------------------------------\n"+
                        "0. Open a file \n"
                        + "1. Give a string\n"
                        + "2. Encrypt a given data file symmetrically under a given passphrase\n"
                        + "3. Decrypt a given symmetric cryptogram under a given passphrase\n"
                        + "------------------------------------------------------------------------\n"
                        + "4. Generate an elliptic key pair from a given passphrase\n"
                        + "5. Encrypt a data file under a given elliptic public key file\n"
                        + "6. Decrypt a given elliptic-encrypted file from a given password\n"
                        + "7. Sign a file from a given password and write the signature to the file\n"
                        + "8. Verify a given data file and its signature file under a public key file\n"
                        + "------------------------------------------------------------------------\n"
                        + "9. Exit\n"
                        + "------------------------------------------------------------------------\n\n"
                        + "Your choice: ");
            }
            else {
                System.out.println("\nMust choose a valid option.\n");
                System.out.println("\n------------------------------------------------------------------------\n"+
                        "0. Open a file \n"
                        + "1. Give a string\n"
                        + "2. Encrypt a given data file symmetrically under a given passphrase\n"
                        + "3. Decrypt a given symmetric cryptogram under a given passphrase\n"
                        + "------------------------------------------------------------------------\n"
                        + "4. Generate an elliptic key pair from a given passphrase\n"
                        + "5. Encrypt a data file under a given elliptic public key file\n"
                        + "6. Decrypt a given elliptic-encrypted file from a given password\n"
                        + "7. Sign a file from a given password and write the signature to the file\n"
                        + "8. Verify a given data file and its signature file under a public key file\n"
                        + "------------------------------------------------------------------------\n"
                        + "9. Exit\n"
                        + "------------------------------------------------------------------------\n\n"
                        + "Your choice: ");
                sc.next();
            }
        }
    }

    /**
     * Convert a file to byte array
     * @return converted byte array from a chosen file
     */
    private static byte[] getFile(){
        JDialog dialog = new JDialog();
        chooser.setCurrentDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
        int retValue = chooser.showOpenDialog(dialog);
        File selectedFile = null;
        byte[] result = null;
        if (retValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            Path path = Paths.get(selectedFile.getAbsolutePath());
            try {
                result = Files.readAllBytes(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Save the encryption file to a specific location
     * @param obj SymmetricCryptogram file
     */
    private static void saveFile(final Object obj){
        JDialog dialog = new JDialog();
        chooser.setCurrentDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
        chooser.setDialogTitle("Save");
        int retValue = chooser.showSaveDialog(dialog);
        if (retValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            try (FileOutputStream fos = new FileOutputStream(selectedFile.getAbsolutePath());
                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(obj);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Open an encrypted file to decrypt
     * @return null if not an Java Object file
     *         otherwise return a Java object
     */
    private static Object openFile(){
        JDialog dialog = new JDialog();
        chooser.setCurrentDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
        int retValue = chooser.showOpenDialog(dialog);
        File selectedFile = null;
        if (retValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            try {
                FileInputStream fileIn = new FileInputStream(selectedFile.getAbsolutePath());
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                Object obj = objectIn.readObject();
                if (obj.getClass() != SymmetricCryptogram.class && obj.getClass() != EllipticCurvePoint.class &&
                    obj.getClass() != PointCryptogram.class && obj.getClass() != BigInteger[].class){
                    System.out.println("Not a valid file.");
                    return null;
                }
                objectIn.close();
                return obj;
            } catch (Exception e) {
                System.out.println("Must be an Java object file");
                e.printStackTrace();
            }
        }
        return null;
    }
}
