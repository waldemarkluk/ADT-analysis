package pl.edu.agh.controllers;

import org.testng.annotations.Test;
import pl.edu.agh.logic.PearsonCorrelation;
import pl.edu.agh.model.SensorEntry;
import pl.edu.agh.utils.SensorEntriesBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

public class CorrelationControllerTest {
    public static final long START_DATE = 1446332400000L; // 2015/11/01 00:00:00
    private static final String X_SENSOR_ID = "X sensor id";
    private static final String Y_SENSOR_ID = "Y sensor id";
    public static final int QUANTITY_OF_ENTRIES = (25 * 60 * 60) / 90;
    private static final double NO_CORRELATION_THRESHOLD = 0.2;

    @Test
    public void shouldFindCorrelationValues1WhenDataAreTheSame() {
        //given
        List<SensorEntry> xSensorEntries = new SensorEntriesBuilder()
                .addEntriesForSensor(X_SENSOR_ID, START_DATE, 1, QUANTITY_OF_ENTRIES)
                .build();

        List<SensorEntry> ySensorEntries = new SensorEntriesBuilder()
                .addEntriesForSensor(Y_SENSOR_ID, START_DATE, 1, QUANTITY_OF_ENTRIES)
                .build();


        long xDailySum = sumUpEntries(xSensorEntries);
        long yDailySum = sumUpEntries(ySensorEntries);
        Map<Integer, Long> xSumPerHourCache = cacheSumPerHour(xSensorEntries);
        Map<Integer, Long> ySumPerHourCache = cacheSumPerHour(ySensorEntries);

        //when
        double coefficient = PearsonCorrelation.computeCorrelation(xDailySum, yDailySum, xSumPerHourCache, ySumPerHourCache);

        //then
        assertEquals(coefficient, 1.0);
    }

    @Test
    public void shouldFindNoCorrelationWhenDataAreRandom() {
        //given
        List<SensorEntry> xSensorEntries = new SensorEntriesBuilder()
                .addRandomEntriesForSensor(X_SENSOR_ID, START_DATE, QUANTITY_OF_ENTRIES, 1, 10000)
                .build();

        List<SensorEntry> ySensorEntries = new SensorEntriesBuilder()
                .addRandomEntriesForSensor(Y_SENSOR_ID, START_DATE, QUANTITY_OF_ENTRIES, 1, 10000)
                .build();


        long xDailySum = sumUpEntries(xSensorEntries);
        long yDailySum = sumUpEntries(ySensorEntries);
        Map<Integer, Long> xSumPerHourCache = cacheSumPerHour(xSensorEntries);
        Map<Integer, Long> ySumPerHourCache = cacheSumPerHour(ySensorEntries);

        //when
        double coefficient = PearsonCorrelation.computeCorrelation(xDailySum, yDailySum, xSumPerHourCache, ySumPerHourCache);

        //then
        assertTrue(coefficient < NO_CORRELATION_THRESHOLD);

    }


    @Test
    public void shouldFindCorrelation() {
        //given
        List<SensorEntry> xSensorEntries = new SensorEntriesBuilder()
                .addRandomEntriesForSensor(X_SENSOR_ID, START_DATE, QUANTITY_OF_ENTRIES, 1, 2)
                .build();

        List<SensorEntry> ySensorEntries = new SensorEntriesBuilder()
                .addRandomEntriesForSensor(Y_SENSOR_ID, START_DATE, QUANTITY_OF_ENTRIES, 12, 13)
                .build();


        long xDailySum = sumUpEntries(xSensorEntries);
        long yDailySum = sumUpEntries(ySensorEntries);
        Map<Integer, Long> xSumPerHourCache = cacheSumPerHour(xSensorEntries);
        Map<Integer, Long> ySumPerHourCache = cacheSumPerHour(ySensorEntries);

        //when
        double coefficient = PearsonCorrelation.computeCorrelation(xDailySum, yDailySum, xSumPerHourCache, ySumPerHourCache);

        //then
        assertTrue(coefficient > NO_CORRELATION_THRESHOLD);
    }

    private Map<Integer, Long> cacheSumPerHour(List<SensorEntry> sensorEntries) {
        Map<Integer, Long> sumPerHourCache = new HashMap<>();

        int currentHour = sensorEntries.get(0).getTimestamp().getHours();
        long hourlSum = 0L;
        for (SensorEntry entry : sensorEntries) {
            if (currentHour != entry.getTimestamp().getHours()) {
                sumPerHourCache.put(currentHour, hourlSum);
                hourlSum = 0L;
                currentHour = entry.getTimestamp().getHours();
            }
            hourlSum += entry.getValue();
        }

        return sumPerHourCache;
    }

    private Integer sumUpEntries(List<SensorEntry> sensorEntries) {
        return sensorEntries.stream()
                .map(SensorEntry::getValue)
                .reduce((a, b) -> a + b)
                .get();
    }


}