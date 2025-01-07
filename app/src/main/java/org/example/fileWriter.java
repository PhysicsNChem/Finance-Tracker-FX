package org.example;
import java.io.*;
import javax.swing.JOptionPane;
public class fileWriter {
    public static void main(String[] args) {
        try {
            File myObj = new File("src/main/resources/info/info.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An error occurred. Please restart FBLA project", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}