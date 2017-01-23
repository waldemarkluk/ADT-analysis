package pl.edu.agh.controllers;


import org.testng.annotations.Test;
import pl.edu.agh.model.SensorEntry;
import pl.edu.agh.utils.SensorEntriesBuilder;

import java.util.List;

import static pl.edu.agh.controllers.AnomalyController.*;

public class AnomalyControllerTest {
    @Test
    public void shouldNotFindAnomalyWhenInputIsIdeallyTimed() {
        //given
        List<SensorEntry> sensorEntries  = new SensorEntriesBuilder()
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375600000L, 1) // 2015/11/01 12:00:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375690000L, 1) // 2015/11/01 12:01:30
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375780000L, 1) // 2015/11/01 12:03:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375870000L, 1) // 2015/11/01 12:04:30
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375960000L, 1) // 2015/11/01 12:06:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376050000L, 1) // 2015/11/01 12:07:30
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376140000L, 1) // 2015/11/01 12:09:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376230000L, 1) // 2015/11/01 12:10:30
                .build();

        //when


        //then


    }

    @Test
    public void shouldNotFindAnomalyWhenInputIsWithinTolerance() {
        long toleranceEdge = ((long) TOLERANCE * 90000L) - 1L; // tolerance time - 1 miliseconds

        //given
        List<SensorEntry> sensorEntries  = new SensorEntriesBuilder()
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375600000L + toleranceEdge, 1) // 2015/11/01 12:00:00

                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375690000L, 1) // 2015/11/01 12:01:30
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375780000L, 1) // 2015/11/01 12:03:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375870000L, 1) // 2015/11/01 12:04:30
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375960000L, 1) // 2015/11/01 12:06:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376050000L, 1) // 2015/11/01 12:07:30
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376140000L, 1) // 2015/11/01 12:09:00

                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376230000L - toleranceEdge, 1) // 2015/11/01 12:10:30
                .build();

        //when


        //then
    }

    @Test
    public void shouldNotFindAnomalyWhenNumberOfCarsIsStable() {
        //given
        List<SensorEntry> sensorEntries  = new SensorEntriesBuilder()
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375600000L, 1) // 2015/11/01 12:00:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375690000L, 2) // 2015/11/01 12:01:30
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375780000L, 1) // 2015/11/01 12:03:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375870000L, 2) // 2015/11/01 12:04:30
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375960000L, 1) // 2015/11/01 12:06:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376050000L, 2) // 2015/11/01 12:07:30
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376140000L, 1) // 2015/11/01 12:09:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376230000L, 1) // 2015/11/01 12:10:30
                .build();
    }


    @Test
    public void shouldNotFindAnomalyWhenNumberOfCarsIsGrowingStable() {
        //given
        List<SensorEntry> sensorEntries  = new SensorEntriesBuilder()
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375600000L, 1) // 2015/11/01 12:00:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375690000L, 2) // 2015/11/01 12:01:30
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375780000L, 3) // 2015/11/01 12:03:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375870000L, 4) // 2015/11/01 12:04:30
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375960000L, 5) // 2015/11/01 12:06:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376050000L, 6) // 2015/11/01 12:07:30
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376140000L, 7) // 2015/11/01 12:09:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376230000L, 8) // 2015/11/01 12:10:30
                .build();
    }

    @Test
    public void shouldFindAnomalyWhenLackOneMeasurement() {
        //given
        List<SensorEntry> sensorEntries  = new SensorEntriesBuilder()
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375600000L, 1) // 2015/11/01 12:00:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375690000L, 1) // 2015/11/01 12:01:30
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375780000L, 1) // 2015/11/01 12:03:00

                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375960000L, 1) // 2015/11/01 12:06:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376050000L, 1) // 2015/11/01 12:07:30
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376140000L, 1) // 2015/11/01 12:09:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376230000L, 1) // 2015/11/01 12:10:30
                .build();

    }

    @Test
    public void shouldFindAnomalyWhenThereIsEveryOtherSample() {

        //given
        List<SensorEntry> sensorEntries  = new SensorEntriesBuilder()
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375600000L, 1) // 2015/11/01 12:00:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375780000L, 1) // 2015/11/01 12:03:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375960000L, 1) // 2015/11/01 12:06:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376140000L, 1) // 2015/11/01 12:09:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376320000L, 1) // 2015/11/01 12:12:00
                .build();
    }

    @Test
    public void shouldFindAnomalyWhenSampleIsALittleBitOutOfTolerance() {
        long toleranceEdge = ((long) TOLERANCE * 90000L) + 1L; // tolerance time + 1 miliseconds
        //given
        List<SensorEntry> sensorEntries  = new SensorEntriesBuilder()
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375600000L + toleranceEdge, 1) // 2015/11/01 12:00:00

                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375690000L, 1) // 2015/11/01 12:01:30
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375780000L, 1) // 2015/11/01 12:03:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375870000L, 1) // 2015/11/01 12:04:30
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375960000L, 1) // 2015/11/01 12:06:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376050000L, 1) // 2015/11/01 12:07:30
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376140000L, 1) // 2015/11/01 12:09:00

                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376230000L - toleranceEdge, 1) // 2015/11/01 12:10:30
                .build();
    }

    @Test
    public void shouldFindAnomalyWhenNumberOfCarsHasStrangePeak() {

        //given
        List<SensorEntry> sensorEntries  = new SensorEntriesBuilder()
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375600000L, 1) // 2015/11/01 12:00:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375690000L, 1) // 2015/11/01 12:01:30
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375780000L, 1) // 2015/11/01 12:03:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375870000L, 18) // 2015/11/01 12:04:30
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375960000L, 1) // 2015/11/01 12:06:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376050000L, 1) // 2015/11/01 12:07:30
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376140000L, 1) // 2015/11/01 12:09:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376230000L, 1) // 2015/11/01 12:10:30
                .build();
    }

}
