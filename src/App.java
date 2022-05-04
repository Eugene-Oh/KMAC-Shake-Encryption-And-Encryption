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
        int choice = 0;
        byte[] S; //diversification string
        byte[] K; //key
        byte[] M; //message
        String str = "";
        while (sc.hasNext()){
            if (sc.hasNextInt()){
                choice = sc.nextInt();
                if (choice == 1){
                    S = "D".getBytes();
                    K = "".getBytes();
                    JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                    int retValue = chooser.showOpenDialog(null);
                    File selectedFile = null;
                    if (retValue == JFileChooser.APPROVE_OPTION) {
                        selectedFile = chooser.getSelectedFile();
                        Path path = Paths.get(selectedFile.getAbsolutePath());
                        try {
                            M = Files.readAllBytes(path);

                            byte[] message = TCSS487Project.KMACXOF256(K, M, 512, S);
                            System.out.println("SHA3 result: " + TCSS487Project.bytesToHex(message));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                    }
                else {
                    System.out.println("2");
                    break;
                }
            }
            else {
                System.out.println("Must choose 1 or 2.");
                sc.next("Your choice: ");
            }
        }
    }


}
