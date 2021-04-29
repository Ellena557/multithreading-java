package ru.ellen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;

public class MainApp {
    private static int tunnelSize = 5;
    private static ArrayBlockingQueue<Ship> tunnel
            = new ArrayBlockingQueue<>(tunnelSize);

    // Типы: 0 - Хлеб, 1 - Банан, 2 - Одежда
    private static ArrayList<Dock> docks = new ArrayList<>(
            Arrays.asList(new Dock(0),
                    new Dock(1), new Dock(2)));
    private static ArrayList<Integer> capacities =
            new ArrayList<>(Arrays.asList(10, 50, 100));

    /**
     * Генерируется 20 кораблей, затем все они запускаются.
     */
    public static void main(String[] args) {
        int numShips = 20;

        ArrayList<Ship> ships = new ArrayList<>();
        for (int i = 0; i < numShips; i++) {
            int type = (int) (Math.random() * 500) % 3;
            ships.add(new Ship(capacities.get(type),
                    docks.get(type),
                    tunnel,
                    i + 1));
        }

        for (Ship ship : ships) {
            new Thread(ship).start();
        }
    }
}
