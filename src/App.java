import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 */
public class App {
    private static JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please choose an option:");
        System.out.print("1. Open a file \n"
                        + "2. Give a string\n"
                        + "3. Encrypt a given data file symmetrically under a given passphrase\n"
                        + "4. Decrypt a given symmetric cryptogram under a given passphrase\n"
                        + "5. Exit\n"
                        + "Your choice: ");
        int choice = 0;
        byte[] S; //diversification string
        byte[] K; //key
        byte[] M = null; //message
        byte[] result;
        String passphrase = "";
        Encryption enc = new Encryption();
        SymmetricCryptogram sym = null;
        S = "D".getBytes();
        K = "".getBytes();
        String str = "";
        while (choice != 5){
            if (sc.hasNextInt()){
                choice = sc.nextInt();
                if (choice == 1){
                    M = getFile();
                    result = Shake.KMACXOF256(K, M, 512, S);
                    System.out.println("Plain cryptographic hash result: \n"
                            + Shake.bytesToHex(result)
                            + "\n");

                    }
                else if (choice == 2){
                    System.out.print("Your string: ");
                    str = sc.next();
                    M = str.getBytes();
                    result = Shake.KMACXOF256(K, M, 512, S);
                    System.out.println("Plain cryptographic hash result: \n"
                            + Shake.bytesToHex(result)
                            + "\n");
                }
                else if (choice == 3){
                    System.out.println("Please choose a file");
                    M = getFile();
                    System.out.print("Please enter you passphrase: ");
                    passphrase = sc.next();
                    sym = enc.encrypt(M,passphrase);
                    saveFile(sym);
                }
                else if (choice == 4){
                    Object obj = openFile();
                    if (obj != null){
                        SymmetricCryptogram sym2 = (SymmetricCryptogram) obj;
                        System.out.print("Please enter you passphrase: ");
                        passphrase = sc.next();
                        byte[] decryptObj = enc.decrypt(sym2,passphrase);
                        if (decryptObj != null){
                            System.out.println("Your byte code:");
                            System.out.println(Shake.bytesToHex(decryptObj));
                        }
                        else{
                            System.out.println("Maybe you choose a wrong file or a wrong pass phrase.");
                        }
                    }
                }
                else if (choice == 5) {
                    System.out.println("Exiting...");
                    System.exit(0);
                }
//              break;
                System.out.println("Please choose an option:");
                System.out.print("1. Open a file \n"
                        + "2. Give a string\n"
                        + "3. Encrypt a given data file symmetrically under a given passphrase\n"
                        + "4. Decrypt a given symmetric cryptogram under a given passphrase\n"
                        + "5. Exit\n"
                        + "Your choice: ");
            }
            else {
                System.out.println("\nMust choose 1, 2, 3, 4, or 5.");
                System.out.print("1. Open a file \n"
                        + "2. Give a string\n"
                        + "3. Encrypt a given data file symmetrically under a given passphrase\n"
                        + "4. Decrypt a given symmetric cryptogram under a given passphrase\n"
                        + "5. Exit\n"
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
    private static void saveFile(final SymmetricCryptogram obj){
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
                if (obj.getClass() != SymmetricCryptogram.class){
                    System.out.println("Not a encryption file.");
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
