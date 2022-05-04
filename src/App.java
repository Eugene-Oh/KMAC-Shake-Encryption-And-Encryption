import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please choose an option:");
        System.out.println("1. Open a file");
        System.out.println("2. Give a string");
        System.out.println("3. Encrypt a given data file symmetrically under a given passphrase");
        System.out.println("4. Decrypt a given symmetric cryptogram under a given passphrase");
        System.out.print("Your choice: ");
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
        while (sc.hasNext()){
            if (sc.hasNextInt()){
                choice = sc.nextInt();
                if (choice == 1){
                    M = getFile();
                    result = TCSS487Project.KMACXOF256(K, M, 512, S);
                    System.out.println("Plain cryptographic hash result: " + TCSS487Project.bytesToHex(result));

                    }
                else if (choice == 2){
                    System.out.print("Your string: ");
                    str = sc.next();
                    M = str.getBytes();
                    result = TCSS487Project.KMACXOF256(K, M, 512, S);
                    System.out.println("Plain cryptographic hash result: " + TCSS487Project.bytesToHex(result));
                }
                else if (choice == 3){
                    System.out.println("Please choose a file");
                    M = getFile();
                    System.out.print("Please enter you passphrase:");
                    passphrase = sc.next();
                    sym = enc.encrypt(M,passphrase);
                }
                else if (choice == 4){
                    System.out.println(4);
                }
                break;
            }
            else {
                System.out.println("Must choose 1 or 2.");
                sc.next("Your choice: ");
            }
        }
        // Test encryption
        byte[] decrypt_byte = enc.decrypt(sym, passphrase);
        System.out.println(Arrays.toString(sym.getZ()));

        System.out.println();
        System.out.println(Arrays.equals(M,decrypt_byte));
        System.out.println(Arrays.toString(M));
        System.out.println(Arrays.toString(decrypt_byte));
//        String decrypt_string = new String(decrypt_byte);
//        System.out.println(original.equals(decrypt_string));

    }

    /**
     * Convert a file to byte array
     * @return converted byte array from a chosen file
     */
    private static byte[] getFile(){
        JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        int retValue = chooser.showOpenDialog(null);
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


}
