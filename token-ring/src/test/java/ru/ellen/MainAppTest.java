package ru.ellen;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.logging.FileHandler;

/**
 * В данном классе просто прогоняется программа при разном количестве
 * узлов в кольце и разном количестве пакетов с данными.
 * Также считается и сохраняется статистика.
 */
public class MainAppTest {
    FileHandler fileHandler = null;

    @Before
    public void init() {
        try {
            // тут будут логи по всем 3 тестам
            fileHandler = new FileHandler("artifacts/testRing.txt");
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void simpleTest() {
        RingProcessor processor = new RingProcessor(6, 3, fileHandler);
        processor.startProcessing();

        // get statistics
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        processor.countAverageNetworkDelay();
        processor.countAverageBufferDelay();
    }

    @Test
    public void middleTest() {
        RingProcessor processor = new RingProcessor(10, 77, fileHandler);
        processor.startProcessing();

        // get statistics
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        processor.countAverageNetworkDelay();
        processor.countAverageBufferDelay();
    }

    @Test
    public void bigTest() {
        RingProcessor processor = new RingProcessor(14, 255, fileHandler);
        processor.startProcessing();

        // get statistics
        try {
            Thread.sleep(12000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        processor.countAverageNetworkDelay();
        processor.countAverageBufferDelay();
    }
}
