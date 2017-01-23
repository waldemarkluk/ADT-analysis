package pl.edu.agh.controllers;


import org.testng.annotations.Test;
import pl.edu.agh.utils.SensorEntriesBuilder;

public class AnomalyControllerTest {
    @Test
    public void shouldNotFindAnomalyWhenInputIsIdeallyTimed() {
        //given
        new SensorEntriesBuilder()
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375600000L, 1) // 2015/11/01 12:00:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375690000L, 1) // 2015/11/01 12:01:30
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375780000L, 1) // 2015/11/01 12:03:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375870000L, 1) // 2015/11/01 12:04:30
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446375960000L, 1) // 2015/11/01 12:06:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376050000L, 1) // 2015/11/01 12:07:30
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376140000L, 1) // 2015/11/01 12:09:00
                .addSensorEntry("CAUTC11FD318_D11_D1_1", 1446376230000L, 1) // 2015/11/01 12:10:30
                .pushToDB();

        //when


        //then


    }

    @Test
    public void shouldNotFindAnomalyWhenInputIsWithinTolerance() {
    }

    @Test
    public void shouldNotFindAnomalyWhenNumberOfCarsIsStable() {
    }

    @Test
    public void shouldFindAnomalyWhenLackOneMeasurement() {
    }

    @Test
    public void shouldFindAnomalyWhenThereIsEveryOtherSample() {
    }

    @Test
    public void shouldFindAnomalyWhenSampleIsALittleBitOutOfTolerance() {
    }

    @Test
    public void shouldFindAnomalyWhenNumberOfCarsHasStrangePeak() {
    }

}
