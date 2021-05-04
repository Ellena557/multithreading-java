package ru.ellen;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс узла кольца. В конструкторе он инициализируется.
 * Одновременно обрабатываеться может не более 3 пакетов данных.
 * Остальные пакеты ждут начала выполнения в буфере.
 * Если узел является пунктом назначения данных, они пишутся
 * на координатор. В противном случае, передаются дальше по часовой
 * стрелке.
 */
public class Node implements Runnable {
    private final int nodeId;
    private Node next;
    private ConcurrentLinkedQueue<DataPackage> bufferStack;
    private List<DataPackage> allData;
    private AtomicInteger numPackages;
    private DataCounter dataCounter;
    private Logger logger;
    private Node coordinator;

    Node(int nodeId, DataCounter dataCounter, Logger logger) {
        this.nodeId = nodeId;
        this.dataCounter = dataCounter;
        this.logger = logger;
        allData = new ArrayList<>();
        bufferStack = new ConcurrentLinkedQueue();
        numPackages = new AtomicInteger(0);
    }

    public void addData(DataPackage dataPackage) {
        bufferStack.add(dataPackage);

        // время последнего добавления в буфер
        dataPackage.setBufferStart(System.nanoTime());
    }

    public List<DataPackage> getData() {
        return allData;
    }

    public Collection getBuffer() {
        return bufferStack;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setCoordinator(Node coordinator) {
        this.coordinator = coordinator;
    }

    public void writeDate(DataPackage data) {
        logger.info(data.getData() + " written to node " + nodeId);
        data.setEndTime(System.nanoTime());
        allData.add(data);
    }

    /**
     * Начало работы узла. То есть из Node.bufferStack берётся пакет с данными
     * и отправляется на обработку, после чего передаётся следующему узлу.
     * Тут заключена логика, согласно которой обрабатываться может только 3 пакета данных одновременно.
     */
    @Override
    public void run() {
        while (!dataCounter.isAllDataReceived()) {
            // ждем, пока не появится возможность обработать
            int packages = numPackages.get();
            if (!bufferStack.isEmpty() && packages < 3) {
                if (numPackages.compareAndSet(packages, packages + 1)) {
                    processData();
                    numPackages.decrementAndGet();
                }
            }
        }
    }

    private void processData() {
        DataPackage data = bufferStack.poll();
        // убираем из буфера
        data.addTotalBufferTime(System.nanoTime() - data.getBufferStart());
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            System.out.println("Interrupted!");
        }
        if (data.getDestinationNode() == this.nodeId) {
            // log
            long received = System.nanoTime();
            logger.log(Level.INFO, "Data received on node " + nodeId +
                    ". Tame taken: " + (received - data.getStartTime()) / 1_000_000 + " ms");
            coordinator.writeDate(data);
            dataCounter.incrementCounter();
        } else {
            next.addData(data);
            logger.log(Level.INFO, data.getData() + " sent from "
                    + nodeId + " to " + next.getNodeId());
        }
    }
}
