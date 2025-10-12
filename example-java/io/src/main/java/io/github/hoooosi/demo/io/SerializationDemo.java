package io.github.hoooosi.demo.io;

import java.io.*;

public class SerializationDemo {
    public static void main(String[] args) throws IOException {

        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream();
             ObjectOutputStream output = new ObjectOutputStream(buffer)) {
            output.writeUTF("Hello World");
        }

        File file = new File("file.txt");
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis);) {
            String str = ois.readUTF();
        }
    }
}