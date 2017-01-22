package pl.edu.agh.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Wiktor on 2017-01-21.
 */
public class SensorEntry implements Serializable{
    private String sensorId;
    private Date timestamp;
    private int value;

    public SensorEntry(String sensorId, Date timestamp, int value) {
        this.sensorId = sensorId;
        this.timestamp = timestamp;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String toString() {
        return sensorId + ", " + timestamp + ", " + value;
    }
}
