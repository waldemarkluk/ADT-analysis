package pl.edu.agh.controllers;


import org.testng.annotations.Test;

public class AnomalyControllerTest {
    @Test
    public void shouldNotFindAnomalyWhenInputIsIdeallyOnTimed(){}

    @Test
    public void shouldNotFindAnomalyWhenInputIsWithinTolerance(){}

    @Test
    public void shouldNotFindAnomalyWhenNumberOfCarsIsStable(){}

    @Test
    public void shouldFindAnomalyWhenLackOneMeasurement(){}

    @Test
    public void shouldFindAnomalyWhenThereIsEveryOtherSample(){}

    @Test
    public void shouldFindAnomalyWhenSampleIsALittleBitOutOfTolerance(){}

    @Test
    public void shouldFindAnomalyWhenNumberOfCarsHasStrangePeak(){}

}
