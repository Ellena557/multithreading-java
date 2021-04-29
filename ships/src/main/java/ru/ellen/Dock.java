package ru.ellen;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Класс причала.
 */
public class Dock {
    private int dockType;
    private AtomicBoolean free = new AtomicBoolean(true);
    private final int speed = 10;

    public Dock(int dockType) {
        this.dockType = dockType;
        this.free.set(true);
    }

    /**
     * Загрузка корабля.
     */
    public void discharge(Ship ship) throws InterruptedException {
        // причал занят
        free.set(false);

        System.out.println("Dock " + dockType + ": discharge started for ship " + ship.getShipNumber());
        Thread.sleep(ship.getCapacity() * 1000 / speed);
        System.out.println("Dock " + dockType + ": discharge ended for ship " + ship.getShipNumber());

        // причал свободен
        free.set(true);
    }

    public AtomicBoolean getFree() {
        return free;
    }

    public void setFree(AtomicBoolean free) {
        this.free = free;
    }
}
