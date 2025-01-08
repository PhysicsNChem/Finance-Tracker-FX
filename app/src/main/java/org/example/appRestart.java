package org.example;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.*;

public class appRestart {
    public void restartApplication() {
       try{
        String java = System.getProperty("java.home") + "/bin/java";

        // Check how the application was launched (via Gradle or directly)
        String command = System.getProperty("sun.java.command");
        String classpath = System.getProperty("java.class.path");

        // If run via Gradle, command will point to the main class; use it
        ProcessBuilder processBuilder;
        if (command != null && command.contains(" ")) {
            // Gradle might pass arguments, handle accordingly
            String[] commandParts = command.split(" ");
            String mainClass = commandParts[0];
            String[] args = new String[commandParts.length - 1];
            System.arraycopy(commandParts, 1, args, 0, args.length);

            processBuilder = new ProcessBuilder(java, "-cp", classpath, mainClass);
            processBuilder.command().addAll(List.of(args));
        } else {
            // Otherwise, fall back to the basic restart logic
            processBuilder = new ProcessBuilder(java, "-cp", classpath, command);
        }

        // Start the process
        processBuilder.start();

        // Exit the current application
        System.exit(0);
    } catch (Exception ex) {
        System.out.println("Error: " + ex.getMessage());
    }
}
}

