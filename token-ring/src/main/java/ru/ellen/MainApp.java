package ru.ellen;

import java.io.IOException;
import java.util.logging.FileHandler;

public class MainApp {
    public static void main(String[] args) {
        FileHandler fileHandler = null;
        try {
            fileHandler = new FileHandler("artifacts/logsRing.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        RingProcessor processor = new RingProcessor(10, 100, fileHandler);
        processor.startProcessing();

        // get statistics
        // С параметрами (nodes = 10, data = 100) хватает 5 секунд.
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            System.out.println("Interrupted!");
        }
        processor.countAverageNetworkDelay();
        processor.countAverageBufferDelay();
    }
}
