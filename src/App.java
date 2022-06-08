import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * TCSS 487 - Final Cryptography Project - Alex Trinh, Eugene Oh
 *
 * The main driver class behind the cryptography program.
 */
public class App {
    private static JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

    // Used for enc/dec using user inputs.
    private static EllipticCurvePoint currentKey = null;
    private static byte[] currentData = null;
    private static PointCryptogram currentEncryptedData;

    /**
     * Runs the entirety of the program.
     * @param args Typical args.
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n------------------------------------");
        System.out.println("Welcome to our cryptography program!");
        System.out.println("------------------------------------\n");
        System.out.println("PLEASE CHOOSE THE RIGHT FILE TYPES WHEN PROMPTED OR THE PROGRAM MAY MALFUNCTION!\n");
        System.out.println("Please type in a number to choose an option:");
        PrintMainMenu();
        // The loop for the menu.
        int choice = 0;
        while (choice != 9) {
            if (sc.hasNextInt()) {
                choice = sc.nextInt();
                sc.nextLine(); // Consume the entire line.
                // Choice for either part 1 or part 2 functionality or exit.
                if (choice == 1) {
                    PartOneMenu(sc);
                    PrintMainMenu();
                } else if (choice == 2) {
                    PartTwoMenu(sc);
                    PrintMainMenu();
                } else if (choice == 3) {
                    System.out.println("Exiting...");
                    System.exit(0);
                } else {
                    System.out.println("Please type in a VALID number:");
                    PrintMainMenu();
                }
            } else {
                System.out.println("Please type in a NUMBER:");
                PrintMainMenu();
                sc.nextLine(); // Consume the entire line.
            }
        }
    }

    /**
     * Prints the main menu.
     */
    private static void PrintMainMenu() {
        System.out.println(
                "=================== MAIN MENU ===================\n" +
                "1 - Bring up Menu One for Part One Functionality.\n" +
                "2 - Bring up Menu Two for Part Two Functionality.\n" +
                "3 - Exit the program.\n" +
                "=================================================");
    }

    /**
     * Handles the entire functionality for part 1 menu.
     * @param sc Used for input from user.
     */
    private static void PartOneMenu(Scanner sc) {
        // Initialization for required variables.
        byte[] S; //diversification string
        byte[] K; //key
        byte[] dataFile = null; //message
        byte[] result;
        String passphrase = "";
        Encryption enc = new Encryption();
        SymmetricCryptogram sym = null;
        S = "D".getBytes();
        K = "".getBytes();
        String str = "";
        int choice = 0;
        PrintPartOneMenu();
        while (choice != 1 && choice != 2 && choice != 3 && choice != 4 && choice != 5 && choice != 6) {
            if (sc.hasNextInt()) {
                choice = sc.nextInt();
                sc.nextLine();
                if (choice == 1) { // Computing hash from file.
                    System.out.println("Please choose the data file:");
                    dataFile = getFile();
                    if (dataFile == null) {
                        System.out.println("Please choose a file next time.");
                    } else {
                        result = Shake.KMACXOF256(K, dataFile, 512, S);
                        System.out.println("Plain cryptographic hash result: \n"
                                + Shake.bytesToHex(result));
                    }
                } else if (choice == 2) { // Computing hash from string.
                    System.out.println("Your string: ");
                    str = sc.next();
                    sc.nextLine();
                    dataFile = str.getBytes();
                    result = Shake.KMACXOF256(K, dataFile, 512, S);
                    System.out.println("Plain cryptographic hash result: \n"
                            + Shake.bytesToHex(result)
                            + "\n");
                } else if (choice == 3) { // Computing MAC from file and pass.
                    System.out.println("Please choose the data file:");
                    dataFile = getFile();
                    if (dataFile == null) {
                        System.out.println("Please choose a file next time.");
                    } else {
                        System.out.println("Please enter a passphrase:");
                        passphrase = sc.next();
                        sc.nextLine();
                        result = Shake.KMACXOF256(passphrase.getBytes(), dataFile, 512, "T".getBytes());
                        System.out.println("The computed authentication tag (MAC): \n"
                                + Shake.bytesToHex(result)
                                + "\n");
                    }
                } else if (choice == 4) {  // Encrypting Data Symmetrically.
                    System.out.println("Please choose the data file:");
                    dataFile = getFile();
                    if (dataFile == null) {
                        System.out.println("Please choose a file next time.");
                    } else {
                        System.out.println("Please enter a passphrase:");
                        passphrase = sc.next();
                        sc.nextLine();
                        sym = enc.Encryption(dataFile, passphrase);
                        System.out.println("Enter the name of the output file with .txt at the end:");
                        saveFile(sym);
                    }
                } else if (choice == 5) { // Decrypting Data Symmetrically.
                    System.out.println("Open the encrypted file:");
                    Object obj = openFile();
                    if (obj != null) {
                        SymmetricCryptogram sym2 = (SymmetricCryptogram) obj;
                        System.out.println("Please enter a passphrase:");
                        passphrase = sc.next();
                        sc.nextLine();
                        byte[] decryptObj = enc.Decryption(sym2, passphrase);
                        if (decryptObj != null) {
                            System.out.println("Decrypted File Contents in Byte Code Format:");
                            System.out.println(Shake.bytesToHex(decryptObj));
                            System.out.println("Decrypted File Contents in String Format:");
                            System.out.println(new String(decryptObj, StandardCharsets.UTF_8));
                        } else {
                            System.out.println("You either chose the wrong file or entered the wrong pass phrase.");
                        }
                    } else {
                        System.out.println("Please choose a file next time.");
                    }
                } else if (choice == 6) { // Exit back to main menu.
                    break;
                } else {
                    System.out.println("Please type in a VALID number:");
                }
                choice = 0;
                PrintPartOneMenu();
            } else {
                System.out.println("Please type in a NUMBER:");
                PrintPartOneMenu();
                sc.nextLine(); // Consume the entire line.
            }
        }
    }

    /**
     * Prints the first menu.
     */
    private static void PrintPartOneMenu() {
        System.out.println(
                  "=================================== MENU 1 ===================================\n"
                + "1. Compute cryptographic hash of a given file.\n"
                + "2. Compute cryptographic hash of a given string.\n"
                + "3. Compute the authentication tag (MAC) from a given data file and passphrase.\n"
                + "4. Encrypt a given data file symmetrically under a given passphrase.\n"
                + "5. Decrypt a given symmetric cryptogram under a given passphrase.\n"
                + "6. Go back to the main menu.\n"
                + "==============================================================================");
    }

    /**
     * Handles the entire functionality for part 2 menu.
     * @param sc Used for input from user.
     */
    private static void PartTwoMenu(Scanner sc) {
        // Initialization for required variables.
        byte[] dataFile = null; //message
        String passphrase = "";
        Encryption enc = new Encryption();
        EllipticCurvePoint pointsym = null;
        int choice = 0;
        PrintPartTwoMenu();
        while (choice != 1 && choice != 2 && choice != 3 && choice != 4 && choice != 5 && choice != 6 && choice != 7 && choice != 8) {
            if (sc.hasNextInt()) {
                choice = sc.nextInt();
                sc.nextLine();
                if (choice == 1) { // Compute key pair from pass and output to file.
                    System.out.println("Please enter a passphrase:");
                    passphrase = sc.next();
                    sc.nextLine();
                    pointsym = enc.GenerateKeyPair(passphrase.getBytes());
                    System.out.println("Enter the name of the output file with .txt at the end: ");
                    saveFile(pointsym);
                } else if (choice == 2) { // Encrypt from data file using key file.
                    System.out.println("Please choose the data file:");
                    dataFile = getFile();
                    if (dataFile == null) {
                        System.out.println("Please choose a file next time.");
                    } else {
                        System.out.println("Please choose the elliptic public key file: ");
                        Object obj = openFile();
                        if (obj != null) {
                            EllipticCurvePoint point = (EllipticCurvePoint) obj;
                            PointCryptogram cryptogram = enc.PointEncryption(dataFile, point);
                            System.out.println("Enter the name of the output file with .txt at the end: ");
                            saveFile(cryptogram);
                        } else {
                            System.out.println("Please choose a file next time.");
                        }
                    }
                } else if (choice == 3) { // Decrypt from data file using pass.
                    System.out.println("Open the encrypted file: ");
                    Object obj = openFile();
                    if (obj != null){
                        PointCryptogram sym2 = (PointCryptogram) obj;
                        System.out.println("Please enter a passphrase:");
                        passphrase = sc.next();
                        sc.nextLine();
                        byte[] decryptObj = enc.PointDecryption(sym2,passphrase.getBytes());
                        if (decryptObj != null){
                            System.out.println("Decrypted File Contents in Byte Code Format:");
                            System.out.println(Shake.bytesToHex(decryptObj));
                            System.out.println("Decrypted File Contents in String Format:");
                            System.out.println(new String(decryptObj, StandardCharsets.UTF_8));
                        }
                        else {
                            System.out.println("You either chose the wrong file or entered the wrong pass phrase.");
                        }
                    }
                } else if (choice == 4) { // Encryption from user input.
                    // Getting an elliptic key.
                    System.out.println("Please enter a passphrase used for generating an elliptic key:");
                    passphrase = sc.next();
                    sc.nextLine();
                    currentKey = enc.GenerateKeyPair(passphrase.getBytes());
                    // Getting user input.
                    System.out.println("Enter in data to be encrypted: ");
                    String str = sc.nextLine();
                    currentData = str.getBytes();
                    currentEncryptedData = enc.PointEncryption(currentData, currentKey);
                    System.out.println("The data has been encrypted and saved to the program!");
                } else if (choice == 5) { // Decryption from user input.
                    // Making sure the user has encrypted and saved to program beforehand.
                    if (currentKey == null || currentData == null || currentEncryptedData == null) {
                        System.out.println("You must do encryption directly to the program first before!");
                    } else {
                        System.out.println("Please enter a passphrase:");
                        passphrase = sc.next();
                        sc.nextLine();
                        byte[] decryptObj = enc.PointDecryption(currentEncryptedData,passphrase.getBytes());
                        if (decryptObj != null){
                            System.out.println("Decrypted User-Input Contents in Byte Code Format:");
                            System.out.println(Shake.bytesToHex(decryptObj));
                            System.out.println("Decrypted User-Input Contents in String Format:");
                            System.out.println(new String(decryptObj, StandardCharsets.UTF_8));
                        } else {
                            System.out.println("You entered the wrong pass phrase.");
                        }
                    }
                } else if (choice == 6) { // Signing a file.
                    System.out.println("Please choose the data file:");
                    dataFile = getFile();
                    if (dataFile == null) {
                        System.out.println("Please choose a file next time.");
                    } else {
                        System.out.println("Please enter a passphrase:");
                        passphrase = sc.next();
                        sc.nextLine();
                        BigInteger[] signature = enc.GenerateSignature(dataFile, passphrase.getBytes());
                        System.out.println("Enter the desired name of the signature output file with .txt at the end: ");
                        saveFile(signature);
                    }
                } else if (choice == 7) { // Verifying a file's signature.
                    System.out.println("Please choose the data file:");
                    dataFile = getFile();
                    if (dataFile == null) {
                        System.out.println("Please choose a file next time.");
                    } else {
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
                            } else {
                                System.out.println("Please choose a file next time.");
                            }
                        } else {
                            System.out.println("Please choose a file next time.");
                        }
                    }

                } else if (choice == 8) { // Exit back to main menu.
                    break;
                } else {
                    System.out.println("Please type in a VALID number:");
                }
                choice = 0;
                PrintPartTwoMenu();
            } else {
                System.out.println("Please type in a NUMBER:");
                PrintPartTwoMenu();
                sc.nextLine(); // Consume the entire line.
            }
        }
    }

    /**
     * Prints the second menu.
     */
    private static void PrintPartTwoMenu() {
        System.out.println(
                  "================================== MENU 2 ================================\n"
                + "1. Generate an elliptic key pair from a given passphrase\n"
                + "2. Encrypt a data file under a given elliptic public key file\n"
                + "3. Decrypt a given elliptic-encrypted file from a given passphrase\n"
                + "4. Encrypt user input under a generated key pair from a given passphrase\n"
                + "5. Decrypt previously encrypted user input from a given passphrase\n"
                + "6. Sign a file from a given password and write the signature to the file\n"
                + "7. Verify a given data file and its signature file under a public key file\n"
                + "8. Go back to the main menu\n"
                + "==========================================================================");
    }

    /**
     * Convert a file to byte array
     * @return converted byte array from a chosen file
     */
    private static byte[] getFile(){
        JDialog dialog = new JDialog();
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
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
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
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
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
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
