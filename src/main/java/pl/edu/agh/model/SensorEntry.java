package pl.edu.agh.model;

import java.util.Date;

/**
 * Created by Wiktor on 2017-01-21.
 */
public class SensorEntry {
    private String sensorId;
    private Date timestamp;
    private int value;


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
}
