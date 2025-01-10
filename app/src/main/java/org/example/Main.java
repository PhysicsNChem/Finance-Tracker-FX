package org.example;
import java.io.*;

import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {
        File check = new File("src/main/resources/info/info.txt");
        if(!check.exists()) {
            Setup setup = new Setup();
            setup.createLangPanel();

        }


        //fileReader.main(args);

    }
}
