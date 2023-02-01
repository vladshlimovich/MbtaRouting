package com.example.vlad.basic.Utils;

import org.apache.commons.io.FilenameUtils;

import java.net.MalformedURLException;
import java.net.URL;

public class MainRunner {
    public static void main(String[] argArgs) {
        System.out.println("Start:\n");
        doit();
        System.out.println("\nFinish");
    }
    private static void doit() {
        try {
            URL url = new URL("https://www.somehost.com/part1/part2/file.html?p1=blah&p2=blah-blah");
            System.out.println(FilenameUtils.getName(url.getPath()));
        } catch (MalformedURLException argE) {
            argE.printStackTrace();
        }
    }
}
