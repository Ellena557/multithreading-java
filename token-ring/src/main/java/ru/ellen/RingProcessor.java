package ru.ellen;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * В конструкторе кольцо инициализируется, то есть создаются все узлы и данные на узлах.
 * В методе {@link RingProcessor#startProcessing()} запускается работа кольца - данные начинают
 * обрабатываться по часовой стрелке. Также производится логгирование.
 */
public class RingProcessor {

    private final int nodesAmount;
    private final int dataAmount;
    private List<Node> nodeList;
    private final Logger logger;
    private List<DataPackage> dataPackages;

    RingProcessor(int nodesAmount, int dataAmount, FileHandler logs) {
        this.nodesAmount = nodesAmount;
        this.dataAmount = dataAmount;
        this.nodeList = new ArrayList<>();
        this.dataPackages = new ArrayList<>();
        logger = Logger.getLogger("ringLogger");
        logger.addHandler(logs);

        init();
    }

    private void init() {
        DataCounter dataCounter = new DataCounter(dataAmount);
        // nodes
        for (int i = 0; i < nodesAmount; i++) {
            Node node = new Node(i, dataCounter, logger);
            nodeList.add(node);
            if (i > 0) {
                nodeList.get(i - 1).setNext(node);
            }
        }
        // add reference to next for the last node
        nodeList.get(nodesAmount - 1).setNext(nodeList.get(0));

        // create coordinator
        int coordinatorId = (int) (Math.random() * nodesAmount);
        // change "next" for the coordinator's previous node
        nodeList.get((nodesAmount + coordinatorId - 1) % nodesAmount)
                .setNext(nodeList.get((coordinatorId + 1) % nodesAmount));

        // set coordinator for all nodes
        for (int i = 0; i < nodesAmount; i++) {
            nodeList.get(i).setCoordinator(nodeList.get(coordinatorId));
        }

        // data
        for (int i = 0; i < dataAmount; i++) {
            // Тут можно вставить и рандомное значение, но так
            // проще отслеживать корректность поведения программы.
            String data = "Data_" + i;
            int dest = (int) ((Math.random() * nodesAmount * 19.753) % nodesAmount);

            // координатор не может быть пунктом назначения
            while (dest == coordinatorId) {
                dest = (int) ((Math.random() * nodesAmount * 19.753) % nodesAmount);
            }
            DataPackage dataPackage = new DataPackage(dest, data);
            // отправляем на какой-то узел (не координатор!)
            int distFromCoordinator = (coordinatorId + i) % (nodesAmount - 1) + 1;
            nodeList.get((coordinatorId + distFromCoordinator) % nodesAmount).addData(dataPackage);

            // список нужен для подсчета статистики
            dataPackages.add(dataPackage);
        }

        // log
        logger.log(Level.INFO, "Number of nodes: " + nodesAmount);
        logger.info("Coordinator node is number " + coordinatorId);
        for (int i = 0; i < nodesAmount; i++) {
            logger.log(Level.INFO, "Node " + i + " contains "
                    + nodeList.get(i).getBuffer().size() + " data packages");
        }
    }

    /**
     * Запуск работы кольца.
     */
    public void startProcessing() {
        ExecutorService service = Executors.newCachedThreadPool();
        for (int i = 0; i < nodesAmount; i++)
            service.execute(nodeList.get(i));
        service.shutdown();
    }

    /**
     * Считается среднее время задержки в сети
     */
    public void countAverageNetworkDelay() {
        DecimalFormat df = new DecimalFormat("#.###");
        Optional<Long> delay = dataPackages.stream()
                .map(it -> (it.getEndTime() - it.getStartTime()))
                .reduce(Long::sum);

        double res = (double) delay.get() / (double) (dataAmount * 1_000_000);
        logger.info("Average network delay is " + df.format(res) + " ms");
    }

    /**
     * Считается среднее время задержки в буфере
     */
    public void countAverageBufferDelay() {
        DecimalFormat df = new DecimalFormat("#.###");
        Optional<Long> delay = dataPackages.stream()
                .map(DataPackage::getTotalBufferTime)
                .reduce(Long::sum);

        double res = (double) delay.get() / (double) (dataAmount * 1_000_000);
        logger.info("Average buffer delay is " + df.format(res) + " ms");
    }
}
