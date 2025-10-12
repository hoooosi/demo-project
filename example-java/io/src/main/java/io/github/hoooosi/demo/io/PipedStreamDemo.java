package io.github.hoooosi.demo.io;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class PipedStreamDemo {
    public static void main(String[] args) throws IOException, InterruptedException {
        try (PipedOutputStream output = new PipedOutputStream();
             PipedInputStream input = new PipedInputStream();) {

            output.connect(input);
            Thread writerThread = new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + ": Writing to PipedStream");
                try {
                    String data = "Hello from the Writer Thread!";
                    output.write(data.getBytes());
                    System.out.println("Writer: [" + data + "]");
                } catch (IOException e) {
                    System.err.println("Writer Error: " + e.getMessage());
                }
            });

            Thread readerThread = new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + ": Reading from PipedStream");
                try {
                    byte[] buffer = new byte[1024];
                    int len = input.read(buffer);
                    if (len != -1) {
                        String receivedData = new String(buffer, 0, len);
                        System.out.println("Reader: [" + receivedData + "]");
                    }
                } catch (IOException e) {
                    System.err.println("Reader Error: " + e.getMessage());
                }
            });

            writerThread.setName("Writer");
            readerThread.setName("Reader");
            writerThread.start();
            readerThread.start();
            writerThread.join();
            readerThread.join();
        }
    }
}
