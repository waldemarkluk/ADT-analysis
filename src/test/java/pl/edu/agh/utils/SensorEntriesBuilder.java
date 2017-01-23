package pl.edu.agh.utils;

import pl.edu.agh.model.SensorEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static pl.edu.agh.controllers.AnomalyControllerTest.*;

public class SensorEntriesBuilder {
    List<SensorEntry> entries;

    public SensorEntriesBuilder() {
        entries = new ArrayList<>();
    }

    public SensorEntriesBuilder addSensorEntry(String sensorId, long milisecSinceEpoch, int value) {
        entries.add(new SensorEntry(sensorId, new Date(milisecSinceEpoch), value));
        return this;
    }

    public List<SensorEntry> build() {
        return entries;
    }

    public SensorEntriesBuilder addEntriesForSensor(String sensorId, long startDate, int value, int quantityOfEntries) {
        for (int i = 0; i < quantityOfEntries; i++) {
            addSensorEntry(sensorId, startDate + i * MILISECONDS_90_SEC, value);
        }
        return this;
    }

    public SensorEntriesBuilder addEntriesForSensorWithSpecifiedInterval(String sensorId, long startDate, int value, int quantityOfEntries, long interval) {
        for (int i = 0; i < quantityOfEntries; i++) {
            addSensorEntry(sensorId, startDate + i * interval, value);
        }
        return this;
    }

    public SensorEntriesBuilder addRandomEntriesForSensor(String sensorId, long startDate, int quantityOfEntries, int randomMin, int randomMax) {
        Random rand = new Random();
        for (int i = 0; i < quantityOfEntries; i++) {
            int value = rand.nextInt((randomMax -randomMin) +1) + randomMin;
            addSensorEntry(sensorId, startDate + i * MILISECONDS_90_SEC, value);
        }
        return this;
    }
}
