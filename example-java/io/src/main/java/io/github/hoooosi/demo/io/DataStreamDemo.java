package io.github.hoooosi.demo.io;

import java.io.*;

public class DataStreamDemo {

    private static final String FILE_NAME = "data_primitives.bin";

    public static void main(String[] args) throws IOException {
        writeData();
        readData();
    }

    /**
     * Writes primitive data types to a file
     */
    private static void writeData() throws IOException {
        // DataOutputStream wraps FileOutputStream
        try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
             DataOutputStream dos = new DataOutputStream(fos)) {

            // Write three different types of data
            dos.writeInt(1024);
            dos.writeBoolean(true);
            dos.writeUTF("Hello, Data Stream!");

            System.out.println("Write complete: Int(1024), Boolean(true), String(\"Hello, Data Stream!\")");

        }
    }

    /**
     * Reads primitive data types from the file
     */
    private static void readData() throws IOException {
        // DataInputStream wraps FileInputStream
        try (FileInputStream fis = new FileInputStream(FILE_NAME);
             DataInputStream dis = new DataInputStream(fis)) {

            // !! IMPORTANT: Must read in the EXACT same order and type as they were written
            int num = dis.readInt();
            boolean flag = dis.readBoolean();
            String text = dis.readUTF();

            System.out.println("\nRead successful:");
            System.out.println("Read Integer: " + num);
            System.out.println("Read Boolean: " + flag);
            System.out.println("Read String: " + text);
        }
    }
}