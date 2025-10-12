package io.github.hoooosi.demo.io;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class BufferDemo {
    public static void main(String[] args) {
        // Use BufferedInputStream to wrap a file stream
        try (FileInputStream fis = new FileInputStream("data.txt");
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            // Create a byte array as a buffer
            byte[] buffer = new byte[1024];

            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                // Convert to a string and print it
                System.out.println(new String(buffer, 0, bytesRead));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
