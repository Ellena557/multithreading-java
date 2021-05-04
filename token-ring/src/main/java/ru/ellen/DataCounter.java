package ru.ellen;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * Класс для подсчета доставленных данных.
 */
public class DataCounter {
    private AtomicInteger counter;
    private int amountData;

    public DataCounter(int amountData) {
        counter = new AtomicInteger(0);
        this.amountData = amountData;
    }

    public void incrementCounter() {
        this.counter.getAndIncrement();
    }

    public boolean isAllDataReceived() {
        return (counter.get() == amountData);
    }
}
