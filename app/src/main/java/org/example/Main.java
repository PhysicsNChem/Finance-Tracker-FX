package org.example;

import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {
        Setup.main(args);
        fileWriter.main(args);
        try {
            Thread.sleep(5000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        fileReader.main(args);

    }
}
