package ru.ellen;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Класс корабля.
 */
public class Ship implements Runnable {
    private int capacity;
    private Dock dock;
    private ArrayBlockingQueue<Ship> tunnel;
    private int shipNumber;
    private final int tunnelTime = 1;
    private AtomicBoolean success = new AtomicBoolean(false);

    public Ship(int capacity, Dock dock, ArrayBlockingQueue<Ship> tunnel, int shipNumber) {
        this.capacity = capacity;
        this.dock = dock;
        this.tunnel = tunnel;
        this.shipNumber = shipNumber;
        success.set(false);
    }

    @Override
    public void run() {
        System.out.println("Ship " + shipNumber + " created");

        try {
            tunnel.put(this);
            System.out.println("Ship " + shipNumber + " entered the tunnel");
            Thread.sleep(tunnelTime * 1000);
            // ждем, когда можно будет загрузить
            while (!success.get()) {
                // если причал свободен, то пытаемся загрузить
                // использую CAS, чтобы два корабля одновременно не зашли на причал
                if (dock.getFree().compareAndSet(true, false)) {
                    // выходим из тоннеля
                    System.out.println("Ship " + shipNumber + " left the tunnel");
                    tunnel.take();

                    // заходим на причал
                    dock.discharge(this);

                    // завершаем
                    success.set(true);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getShipNumber() {
        return shipNumber;
    }

    public void setShipNumber(int shipNumber) {
        this.shipNumber = shipNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
