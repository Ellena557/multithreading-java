package ru.ellen;


import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * В данном тесте происходит практически то же самое, что и
 * в MainApp. Просто проверяется, как работает программа при разном количестве потоков.
 * Плюс, используется другой подход: с помощью ExecutorService.
 */
public class MainAppTest {
    private static int tunnelSize = 5;
    private static ArrayBlockingQueue<Ship> tunnel
            = new ArrayBlockingQueue<>(tunnelSize);

    private static ArrayList<Dock> docks = new ArrayList<>(
            Arrays.asList(new Dock(0),
                    new Dock(1), new Dock(2)));
    private static ArrayList<Integer> capacities =
            new ArrayList<>(Arrays.asList(10, 50, 100));

    @Test
    public void fewShips() throws InterruptedException {
        int numShips = 3;

        ArrayList<Ship> ships = new ArrayList<>();
        for (int i = 0; i < numShips; i++) {
            int type = (int) (Math.random() * 500) % 3;
            ships.add(new Ship(capacities.get(type),
                    docks.get(type),
                    tunnel,
                    i + 1));
        }

        ExecutorService service = Executors.newCachedThreadPool();
        for (int i = 0; i < numShips; i++)
            service.execute(ships.get(i));
        service.shutdown();
        boolean finished = service.awaitTermination(5, TimeUnit.MINUTES);
        Assert.assertTrue(finished);
    }

    @Test
    public void middleShips() throws InterruptedException {
        int numShips = 15;

        ArrayList<Ship> ships = new ArrayList<>();
        for (int i = 0; i < numShips; i++) {
            int type = (int) (Math.random() * 500) % 3;
            ships.add(new Ship(capacities.get(type),
                    docks.get(type),
                    tunnel,
                    i + 1));
        }

        ExecutorService service = Executors.newCachedThreadPool();
        for (int i = 0; i < numShips; i++)
            service.execute(ships.get(i));
        service.shutdown();
        boolean finished = service.awaitTermination(5, TimeUnit.MINUTES);
        Assert.assertTrue(finished);
    }

    @Test
    public void manyShips() throws InterruptedException {
        int numShips = 35;

        ArrayList<Ship> ships = new ArrayList<>();
        for (int i = 0; i < numShips; i++) {
            int type = (int) (Math.random() * 500) % 3;
            ships.add(new Ship(capacities.get(type),
                    docks.get(type),
                    tunnel,
                    i + 1));
        }

        ExecutorService service = Executors.newCachedThreadPool();
        for (int i = 0; i < numShips; i++)
            service.execute(ships.get(i));
        service.shutdown();
        boolean finished = service.awaitTermination(10, TimeUnit.MINUTES);
        Assert.assertTrue(finished);
    }
}
