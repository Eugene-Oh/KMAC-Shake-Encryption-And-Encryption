import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please choose an option:");
        System.out.println("1. Open a file");
        System.out.println("2. Give a string");
        System.out.print("Your choice: ");
        int choice = 0;
        byte[] S; //diversification string
        byte[] K; //key
        byte[] M; //message
        byte[] result;
        S = "D".getBytes();
        K = "".getBytes();
        String str = "";
        while (sc.hasNext()){
            if (sc.hasNextInt()){
                choice = sc.nextInt();
                if (choice == 1){

                    JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                    int retValue = chooser.showOpenDialog(null);
                    File selectedFile = null;
                    if (retValue == JFileChooser.APPROVE_OPTION) {
                        selectedFile = chooser.getSelectedFile();
                        Path path = Paths.get(selectedFile.getAbsolutePath());
                        try {
                            M = Files.readAllBytes(path);

                            result = TCSS487Project.KMACXOF256(K, M, 512, S);
                            System.out.println("Plain cryptographic hash result: " + TCSS487Project.bytesToHex(result));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    }
                else {
                    System.out.print("Your string: ");
                    str = sc.next();
                    M = str.getBytes();
                    result = TCSS487Project.KMACXOF256(K, M, 512, S);
                    System.out.println("Plain cryptographic hash result: " + TCSS487Project.bytesToHex(result));
                }
                break;
            }
            else {
                System.out.println("Must choose 1 or 2.");
                sc.next("Your choice: ");
            }
        }
    }


}
