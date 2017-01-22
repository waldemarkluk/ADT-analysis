package pl.edu.agh.controllers;

import com.sun.tools.javac.util.Pair;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.model.AnomalyReport;
import pl.edu.agh.model.HourSensorEntryList;
import pl.edu.agh.model.SensorEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Wiktor on 2017-01-21.
 */

@RestController
public class AnomalyController extends CassandraTableScanBasedController {
    public final double TOLERANCE = 0.1;

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
    public ResponseEntity<AnomalyReport> checkForPauseAnomalies(@PathVariable("sensorId") String sensorId, @RequestParam("from") Long fromDate, @RequestParam("to") Long toDate) {
        AnomalyReport anomalyReport = new AnomalyReport();

        List<SensorEntry> sensorEntryList = getEntryList(sensorId, fromDate, toDate);
        List<Pair<Date, Date>> anomalies = getAnomalies(sensorEntryList);

        anomalyReport.setEntries(sensorEntryList);
        anomalyReport.setAnomaliesDates(anomalies);

        return new ResponseEntity<AnomalyReport>(anomalyReport, HttpStatus.OK);
    }


    /*
    Sprawdzanie anomalii - niesprawności czujnika
    Rozpatrywane w zadanym okresie czasu preferowalnie w kontekście danego dnia
    ---
    Proponowany output
    {
    entries: [
    {
        timestamp: "",
        count: 0
    }
    ],
    anomalies: []
    }
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
     * @param sensorId
     * @param fromDate - in seconds, inclusive
     * @param toDate   - in seconds, exclusive
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/sensors/{sensorId}/anomalies")
    public ResponseEntity<AnomalyReport> checkForAnomalies(@PathVariable("sensorId") String sensorId, @RequestParam("from") Long fromDate, @RequestParam("to") Long toDate) {
        AnomalyReport anomalyReport = new AnomalyReport();

        List<SensorEntry> sensorEntryList = getEntryList(sensorId, fromDate, toDate);
        List<HourSensorEntryList> hourSensorEntryLists = splitByHours(sensorEntryList);
        List<Date> anomalies = getAnomaliesTemp(hourSensorEntryLists);

        anomalyReport.setEntries(sensorEntryList);
        anomalyReport.setAnomalies(anomalies);

        return new ResponseEntity<AnomalyReport>(anomalyReport, HttpStatus.OK);
    }

    private List<Date> getAnomaliesTemp(List<HourSensorEntryList> hourSensorEntryLists) {
        List<Date> anomalies = new ArrayList<>();

        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (int i = 0; i < hourSensorEntryLists.size(); i++) {
            stats.addValue(hourSensorEntryLists.get(i).getSensorEntryList().size());
        }

        double q1 = stats.getPercentile(25);
        double q3 = stats.getPercentile(75);
        double iqr = q3 - q1;
        double lowerFence = q1 - 1.5 * iqr;

        for (HourSensorEntryList hourSensorEntryList : hourSensorEntryLists) {
            if (hourSensorEntryList.getSensorEntryList().size() < lowerFence)
                anomalies.add(hourSensorEntryList.getDate());
        }

        return anomalies;
    }

    private List<HourSensorEntryList> splitByHours(List<SensorEntry> sensorEntryList) {
        List<HourSensorEntryList> hourSensorEntryList = new ArrayList<>();

        Date lastDate = null;
        List<SensorEntry> tempSensorEntryList = new ArrayList<>();
        if (sensorEntryList.size() > 0)
            lastDate = sensorEntryList.get(0).getTimestamp();

        Date currentDate;
        for (SensorEntry sensorEntry : sensorEntryList) {
            currentDate = sensorEntry.getTimestamp();
            if (lastDate.getHours() != currentDate.getHours()) {
                hourSensorEntryList.add(new HourSensorEntryList(lastDate, tempSensorEntryList));
                tempSensorEntryList = new ArrayList<>();
            }
            lastDate = currentDate;
            tempSensorEntryList.add(sensorEntry);
        }

        return hourSensorEntryList;
    }


    private List<SensorEntry> getEntryList(String sensorId, Long fromDate, Long toDate) {
        List<SensorEntry> sensorEntryList;
        sensorEntryList = getMeasurementsBetween(sensorId, fromDate, toDate).map(
                row -> new SensorEntry(
                        row.getString("sensorid"),
                        row.getDateTime("time").toDate(),
                        row.getInt("value")
                )
        ).collect();

        return sensorEntryList;
    }

    private List<Pair<Date, Date>> getAnomalies(List<SensorEntry> sensorEntryList) {
        List<Pair<Date, Date>> anomalies = new ArrayList<>();

        double standardTime = 90; // SECONDS
        double toleranceTime = 90 + standardTime * TOLERANCE;

        for (int i = 1; i < sensorEntryList.size(); i++) {
            if (Math.abs(sensorEntryList.get(i).getTimestamp().getTime() - sensorEntryList.get(i).getTimestamp().getTime()) > toleranceTime)
                anomalies.add(new Pair(sensorEntryList.get(i - 1).getTimestamp(), sensorEntryList.get(i).getTimestamp()));
        }

        return anomalies;
    }
}
