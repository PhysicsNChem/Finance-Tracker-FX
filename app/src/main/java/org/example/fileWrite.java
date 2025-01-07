package org.example;
import java.io.*;
import javax.swing.*;


public class fileWrite implements fileWriteMethods {

    public void createFile(){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
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
    public void writeInformation(boolean theme, boolean english, String user) {
        try {
            FileWriter fw = new FileWriter("src/main/resources/info/info.txt");
            fw.write("darkMode = " + theme + "\nenglishLanguage = " + english + "\nuserName = " + user);
            fw.close();
            System.out.println("File written successfully.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An error occurred. Please restart FBLA project", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}