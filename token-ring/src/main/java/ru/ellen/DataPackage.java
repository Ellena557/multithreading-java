package ru.ellen;


/**
 * Пакет данных. В конструкторе он инициализируется.
 * Задаются время создания, узел-пункт назначения и сами данные.
 * Кроме того, подсчитывается суммарное время, проведенное в буфере.
 */
public class DataPackage {
    private final int destinationNode;
    private final String data;
    private final long startTime;
    private long endTime;

    // для подсчета средней задержки в буфере
    private long totalBufferTime;
    private long bufferStart;

    public DataPackage(int destinationNode, String data) {
        this.destinationNode = destinationNode;
        this.data = data;
        this.totalBufferTime = 0;

        // Фиксируется время, когда создаётся пакет данных. Необходимо для
        // вычисления времени доставки до узла назначения.
        startTime = System.nanoTime();
    }

    public int getDestinationNode() {
        return destinationNode;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public String getData() {
        return this.data;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getBufferStart() {
        return bufferStart;
    }

    public void setBufferStart(long bufferStart) {
        this.bufferStart = bufferStart;
    }

    public long getTotalBufferTime() {
        return totalBufferTime;
    }

    public void addTotalBufferTime(long added) {
        this.totalBufferTime += added;
    }
}
