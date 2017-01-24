package pl.edu.agh.controllers;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.logic.AnomalyAlgorithms;
import pl.edu.agh.model.AnomalyReport;
import pl.edu.agh.model.SensorEntry;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Wiktor on 2017-01-21.
 */

@RestController
public class AnomalyController extends CassandraTableScanBasedController {

    /*
        Przerwy w działaniu licznika, metodologia:
        - Dane powinny występować do 90 sekund, jeżeli przerwa jest dłuższa niż ten czas( * tolerancja) to jest
         to anomalia która trafia do raportu
     */

    /**
     * @param sensorId
     * @param fromDate - in seconds, inclusive
     * @param toDate   - in seconds, exclusive
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/sensors/{sensorId}/pause_anomalies")
    public AnomalyReport checkForPauseAnomalies(@PathVariable("sensorId") String sensorId, @RequestParam("from") Long fromDate, @RequestParam("to") Long toDate) {
        AnomalyReport anomalyReport = new AnomalyReport();

        List<SensorEntry> sensorEntryList = getEntryList(sensorId, fromDate, toDate);
        List<Pair<Date, Date>> anomaliesRange = new AnomalyAlgorithms().getPauseAnomaliesPeterMethod(sensorEntryList, fromDate, toDate);

        anomalyReport.setEntries(sensorEntryList);
        anomalyReport.setAnomaliesDates(anomaliesRange);
        return anomalyReport;
    }


    /*
    ---
    Dla danego dnia policz liczbę wpisów dla danego sensora grupując godzinami.
    Wylicz 1. i 3. kwartyl
    Wylicz IQR
    Oblicz dolną wartość akceptowalną "lower fence"  1.Q - współczynnik * IQR
    ! Współczynnik też można by sparametryzować, ale słabo z czasem, więc zostańmy przy 1.5
    Każdą wartość poniżej tej wartości uznaj za anomalię.

    ---
    UWAGA
    Zdarzają się przypadki, że czujniki były martwe przez całe dni - w takich wypadkach ta metoda nie zadziała.
    Przed wykonaniem powyższych kroków proponuję policzyć analogicznie anomalie dla DNI w kontekście WSZYSTKICH danych
    (bądź tygodnia na przykład). Jeżeli w tym wypadku dzień zostanie wypluty jako anomalia, to po prostu wszystkie wpisy
    z tego dnia zakwalifikuj jako anomalię.

    http://datapigtechnologies.com/blog/index.php/highlighting-outliers-in-your-data-with-the-tukey-method/
     */
    /**
     * Searches for pause anomalies using Tukey method
     * @param sensorId
     * @param fromDate - in seconds, inclusive
     * @param toDate   - in seconds, exclusive
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/sensors/{sensorId}/pause_anomalies_tukey")
    public AnomalyReport checkForPauseAnomaliesTukey(@PathVariable("sensorId") String sensorId, @RequestParam("from") Long fromDate, @RequestParam("to") Long toDate) {
        AnomalyReport anomalyReport = new AnomalyReport();

        List<SensorEntry> sensorEntryList = getEntryList(sensorId, fromDate, toDate);
        List<Date> anomalies = new AnomalyAlgorithms().getPauseAnomaliesVictorMethod(sensorEntryList);

        anomalyReport.setEntries(sensorEntryList);
        anomalyReport.setAnomalies(anomalies);
        return anomalyReport;
    }

    /*
        Tutaj znajdujemy anomalie w przypadku wadliwego działania czujników, czyli np. gołąb siadł na czujniku
        i odczyt jest zły.

        Zastosuję tutaj metodę 3 sigma
     */

    /**
     * @param sensorId
     * @param fromDate - in seconds, inclusive
     * @param toDate   - in seconds, exclusive
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/sensors/{sensorId}/anomalies")
    public AnomalyReport checkForAnomalies(@PathVariable("sensorId") String sensorId, @RequestParam("from") Long fromDate, @RequestParam("to") Long toDate) {
        AnomalyReport anomalyReport = new AnomalyReport();

        List<SensorEntry> sensorEntryList = getEntryList(sensorId, trimDateToDay(fromDate), trimDateToDay(toDate));
        List<Date> anomalies = new AnomalyAlgorithms().getAnomalies(sensorEntryList);

        anomalyReport.setEntries(sensorEntryList);
        anomalyReport.setAnomalies(anomalies);

        return anomalyReport;
    }

    private Long trimDateToDay(Long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date * 1000);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis() / 1000;
    }

    private List<SensorEntry> getEntryList(String sensorId, Long fromDate, Long toDate) {
        List<SensorEntry> sensorEntryList;
        sensorEntryList = getMeasurementsBetween(sensorId, fromDate, toDate)
                .map(SensorEntry::fromCassandraRow)
                .collect();

        return sensorEntryList;
    }

}
