package pl.edu.agh.controllers;


import org.apache.commons.lang3.tuple.Pair;
import org.testng.annotations.Test;
import pl.edu.agh.logic.AnomalyAlgorithms;
import pl.edu.agh.model.SensorEntry;
import pl.edu.agh.utils.SensorEntriesBuilder;

import java.util.Date;
import java.util.List;

import static org.testng.Assert.*;

public class AnomalyControllerTest {


    public static final long START_DATE = 1446375600000L; // 2015/11/01 12:00:00
    public static final long MILISECONDS_90_SEC = 90 * 1000;
    public static final String SENSOR_ID = "CAUTC11FD318_D11_D1_1";


    @Test
    public void shouldNotFindPauseAnomalyWhenInputIsIdeallyTimed() {
        //given
        List<SensorEntry> sensorEntries = new SensorEntriesBuilder()
                .addEntriesForSensor(SENSOR_ID, START_DATE, 1, 8)
                .build();

        //when
        AnomalyAlgorithms anomalyAlgorithms = new AnomalyAlgorithms();
        List<Pair<Date, Date>> pauseAnomaliesResult = anomalyAlgorithms.getPauseAnomaliesPeterMethod(sensorEntries, START_DATE, START_DATE + 7 * MILISECONDS_90_SEC);
        List<Date> anomalies = anomalyAlgorithms.getPauseAnomaliesVictorMethod(sensorEntries);

        //then
        assertEquals(pauseAnomaliesResult.isEmpty(), true);
        assertEquals(anomalies.isEmpty(), true);
    }

    @Test
    public void shouldNotFindPauseAnomalyWhenInputIsWithinTolerance() {
        long toleranceEdge = ((long) AnomalyAlgorithms.TOLERANCE * 90000L) - 1L; // tolerance time - 1 miliseconds

        //given
        List<SensorEntry> sensorEntries = new SensorEntriesBuilder()
                .addSensorEntry(SENSOR_ID, START_DATE + toleranceEdge, 1) // 2015/11/01 12:00:00
                .addEntriesForSensor(SENSOR_ID, (START_DATE + MILISECONDS_90_SEC) + toleranceEdge, 1, 6)
                .addSensorEntry(SENSOR_ID, (START_DATE + MILISECONDS_90_SEC * 7) - toleranceEdge, 1) // 2015/11/01 12:10:30
                .build();

        //when
        AnomalyAlgorithms anomalyAlgorithms = new AnomalyAlgorithms();
        List<Pair<Date, Date>> pauseAnomaliesResult = anomalyAlgorithms.getPauseAnomaliesPeterMethod(sensorEntries, START_DATE, START_DATE + 7 * MILISECONDS_90_SEC);
        List<Date> anomalies = anomalyAlgorithms.getPauseAnomaliesVictorMethod(sensorEntries);

        //then
        assertEquals(pauseAnomaliesResult.isEmpty(), true);
        assertEquals(anomalies.isEmpty(), true);
    }


    @Test
    public void shouldNotFindAnomalyWhenNumberOfCarsIsStable() {
        //given
        List<SensorEntry> sensorEntries = new SensorEntriesBuilder()
                .addSensorEntry(SENSOR_ID, 1446375600000L, 1) // 2015/11/01 12:00:00
                .addSensorEntry(SENSOR_ID, 1446375690000L, 2) // 2015/11/01 12:01:30
                .addSensorEntry(SENSOR_ID, 1446375780000L, 1) // 2015/11/01 12:03:00
                .addSensorEntry(SENSOR_ID, 1446375870000L, 2) // 2015/11/01 12:04:30
                .addSensorEntry(SENSOR_ID, 1446375960000L, 1) // 2015/11/01 12:06:00
                .addSensorEntry(SENSOR_ID, 1446376050000L, 2) // 2015/11/01 12:07:30
                .addSensorEntry(SENSOR_ID, 1446376140000L, 1) // 2015/11/01 12:09:00
                .addSensorEntry(SENSOR_ID, 1446376230000L, 1) // 2015/11/01 12:10:30
                .build();

        //when
        AnomalyAlgorithms anomalyAlgorithms = new AnomalyAlgorithms();
        List<Date> anomalies = anomalyAlgorithms.getAnomalies(sensorEntries);

        //then
        assertEquals(anomalies.isEmpty(), true);
    }


    @Test
    public void shouldNotFindAnomalyWhenNumberOfCarsIsGrowingStable() {
        //given
        List<SensorEntry> sensorEntries = new SensorEntriesBuilder()
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375600000L, 1) // 2015/11/01 12:00:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375690000L, 2) // 2015/11/01 12:01:30
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375780000L, 3) // 2015/11/01 12:03:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375870000L, 4) // 2015/11/01 12:04:30
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375960000L, 5) // 2015/11/01 12:06:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376050000L, 6) // 2015/11/01 12:07:30
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376140000L, 7) // 2015/11/01 12:09:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376230000L, 8) // 2015/11/01 12:10:30
                .build();

        //when
        AnomalyAlgorithms anomalyAlgorithms = new AnomalyAlgorithms();
        List<Date> anomalies = anomalyAlgorithms.getAnomalies(sensorEntries);

        //then
        assertEquals(anomalies.isEmpty(), true);
    }

    @Test
    public void shouldFindPauseAnomalyWhenLackOneMeasurement() {
        //given
        List<SensorEntry> sensorEntries = new SensorEntriesBuilder()
                .addEntriesForSensor(SENSOR_ID, START_DATE, 1, 3)
                // lack of entry for  Sun Nov 01 12:04:30 CET 2015
                .addEntriesForSensor(SENSOR_ID, START_DATE + 4 * MILISECONDS_90_SEC, 1, 80)
                .build();

        //when
        AnomalyAlgorithms anomalyAlgorithms = new AnomalyAlgorithms();
        List<Pair<Date, Date>> pauseAnomaliesResult = anomalyAlgorithms.getPauseAnomaliesPeterMethod(sensorEntries, START_DATE, START_DATE + 7 * MILISECONDS_90_SEC);
        List<Date> anomalies = anomalyAlgorithms.getPauseAnomaliesVictorMethod(sensorEntries);

        //then
        assertEquals(anomalies.size(), 0);
        assertEquals(pauseAnomaliesResult.size(), 1);
        //TODO object comparation
    }

    @Test
    public void shouldFindPauseAnomalyWhenThereIsEveryOtherSample() {

        //given
        List<SensorEntry> sensorEntries = new SensorEntriesBuilder()
                .addEntriesForSensorWithSpecifiedInterval(SENSOR_ID, START_DATE, 1, 30, MILISECONDS_90_SEC * 2)
                .build();

        //when
        AnomalyAlgorithms anomalyAlgorithms = new AnomalyAlgorithms();
        List<Pair<Date, Date>> pauseAnomaliesResult = anomalyAlgorithms.getPauseAnomaliesPeterMethod(sensorEntries, START_DATE, START_DATE + 7 * MILISECONDS_90_SEC);
        List<Date> anomalies = anomalyAlgorithms.getPauseAnomaliesVictorMethod(sensorEntries);

        //then
        assertEquals(anomalies.size(), 0);
        assertEquals(pauseAnomaliesResult.size(), 29);
    }

    @Test
    public void shouldFindPauseAnomalyWhenSampleIsALittleBitOutOfTolerance() {
        long toleranceEdge = (long)(AnomalyAlgorithms.TOLERANCE * 90000L) + 2000L; // tolerance time + 1 miliseconds
        // given
        List<SensorEntry> sensorEntries = new SensorEntriesBuilder()
                .addSensorEntry(SENSOR_ID, START_DATE, 1) // 2015/11/01 12:00:00
                .addSensorEntry(SENSOR_ID, 1446375690000L, 1) // 2015/11/01 12:01:30
                .addSensorEntry(SENSOR_ID, 1446375780000L, 1) // 2015/11/01 12:03:00
                .addSensorEntry(SENSOR_ID, 1446375870000L + toleranceEdge, 1) // 2015/11/01 12:04:41
                .addSensorEntry(SENSOR_ID, 1446375960000L, 1) // 2015/11/01 12:06:00
                .addSensorEntry(SENSOR_ID, 1446376050000L, 1) // 2015/11/01 12:07:30
                .addSensorEntry(SENSOR_ID, 1446376140000L, 1) // 2015/11/01 12:09:00
                .addSensorEntry(SENSOR_ID, 1446376230000L, 1) // 2015/11/01 12:10:30
                .build();

        //when
        AnomalyAlgorithms anomalyAlgorithms = new AnomalyAlgorithms();
        List<Pair<Date, Date>> pauseAnomaliesResult = anomalyAlgorithms.getPauseAnomaliesPeterMethod(sensorEntries, START_DATE, START_DATE + 7 * MILISECONDS_90_SEC);
        List<Date> anomalies = anomalyAlgorithms.getPauseAnomaliesVictorMethod(sensorEntries);

        //then
        assertEquals(anomalies.size(), 0);
        assertEquals(pauseAnomaliesResult.size(), 1);
    }

    @Test
    public void shouldFindAnomalyWhenNumberOfCarsHasStrangePeak() {
        //given
        List<SensorEntry> sensorEntries = new SensorEntriesBuilder()
                .addEntriesForSensorWithSpecifiedInterval(SENSOR_ID, START_DATE, 1, 48, 1000 * 60 * 60 ) // add 1 entry per hour
                .addSensorEntry(SENSOR_ID, 1446375870000L + 1000 * 60 * 60 * 48, 18) // 2015/11/03 12:04:30
                .addEntriesForSensorWithSpecifiedInterval(SENSOR_ID, START_DATE + 1000 * 60 * 60 * 49, 1, 48, 1000 * 60 * 60) // add 1 entry per hour
                .build();

        //when
        AnomalyAlgorithms anomalyAlgorithms = new AnomalyAlgorithms();
        List<Date> anomalies = anomalyAlgorithms.getAnomalies(sensorEntries);

        //then
        assertEquals(anomalies.size(), 1);
    }

}
