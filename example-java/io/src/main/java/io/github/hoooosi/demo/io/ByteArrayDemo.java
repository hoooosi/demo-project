package io.github.hoooosi.demo.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ByteArrayDemo {
    public static void main(String[] args) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
            String data = "Hello, Memory!";
            baos.write(data.getBytes());
            System.out.println(baos.toString());
        }
    }

}
