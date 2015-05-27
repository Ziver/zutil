package zutil.net;

/**
 * Created by Ziver Koc
 */
public class ThroughputCalculator {
    public static final float UPDATES_PER_SEC = 1;
    public static final double NANOSEC_PER_SECOND = 1000000000.0;

    private boolean updated;
    private double throughput;
    private long previousTimeStamp;
    private long data_amount;
    private long total_data_amount;
    private float frequency = UPDATES_PER_SEC;

    public void setTotalHandledData(long bytes){
        setHandledData(bytes - total_data_amount);
        total_data_amount = bytes;
    }
    public void setHandledData(long bytes){
        long currentTimeStamp = System.nanoTime();
        data_amount += bytes;
        if(currentTimeStamp - (NANOSEC_PER_SECOND/frequency) > previousTimeStamp) {
            throughput = data_amount / ((currentTimeStamp - previousTimeStamp) / NANOSEC_PER_SECOND);
            previousTimeStamp = currentTimeStamp;
            data_amount = 0;
            updated = true;
        }
    }

    public double getByteThroughput(){
        setHandledData(0); // Update throughput
        updated = false;
        return throughput;
    }
    public double getBitThroughput(){
        return getByteThroughput()*8;
    }

    public boolean isUpdated(){
        return updated;
    }

    public void setFrequency(float frequency) {
        if(frequency < 0)
            this.frequency = UPDATES_PER_SEC;
        else
            this.frequency = frequency;
    }
}
