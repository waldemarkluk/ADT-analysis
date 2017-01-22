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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Wiktor on 2017-01-21.
 */

@RestController
public class AnomalyController extends CassandraTableScanBasedController {
    public final double TOLERANCE = 0.1;

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
        List<Pair<Date, Date>> anomaliesRange = getAnomaliesPeterMethod(sensorEntryList, fromDate, toDate);
        List<Date> anomalies = getAnomaliesVictorMethod(sensorEntryList);

        anomalyReport.setEntries(sensorEntryList);
        anomalyReport.setAnomaliesDates(anomaliesRange);
        anomalyReport.setAnomalies(anomalies);

        return new ResponseEntity<AnomalyReport>(anomalyReport, HttpStatus.OK);
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
    public ResponseEntity<AnomalyReport> checkForAnomalies(@PathVariable("sensorId") String sensorId, @RequestParam("from") Long fromDate, @RequestParam("to") Long toDate) {
        AnomalyReport anomalyReport = new AnomalyReport();

        List<SensorEntry> sensorEntryList = getEntryList(sensorId, trimDateToDay(fromDate), trimDateToDay(toDate));
        List<Date> anomalies = getAnomalies(sensorEntryList);

        anomalyReport.setEntries(sensorEntryList);
        anomalyReport.setAnomalies(anomalies);

        return new ResponseEntity<AnomalyReport>(anomalyReport, HttpStatus.OK);
    }

    private Long trimDateToDay(Long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date*1000);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis()/1000;
    }

    private List<Date> getAnomaliesVictorMethod(List<SensorEntry> sensorEntryList) {
        List<Date> anomalies = new ArrayList<>();
        List<HourSensorEntryList> hourSensorEntryLists = splitByHours(sensorEntryList);
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

    private List<Pair<Date, Date>> getAnomaliesPeterMethod(List<SensorEntry> sensorEntryList, Long fromDate, Long toDate) {
        List<Pair<Date, Date>> anomalies = new ArrayList<>();

        double standardTime = 90; // SECONDS
        double toleranceTime = 90 + standardTime * TOLERANCE;

        for (int i = 1; i < sensorEntryList.size(); i++) {
            if (Math.abs(sensorEntryList.get(i).getTimestamp().getTime() - sensorEntryList.get(i).getTimestamp().getTime()) > toleranceTime)
                anomalies.add(new Pair(sensorEntryList.get(i - 1).getTimestamp(), sensorEntryList.get(i).getTimestamp()));
        }

        // if there is no entry at all
        if(fromDate != toDate && sensorEntryList.size() != 0)
            anomalies.add(new Pair(new Date(fromDate), new Date(toDate)));

        return anomalies;
    }

    private List<Date> getAnomalies(List<SensorEntry> sensorEntryList) {
        List<Date> anomalies = new ArrayList<>();
        List<List<HourSensorEntryList>> daySensorEntryLists = splitByDays(sensorEntryList);
        DescriptiveStatistics stats = new DescriptiveStatistics();

        for(int i=0; i<24; i++) {
            for (int j = 0; j < daySensorEntryLists.size(); j++) {
                try {
                    if(daySensorEntryLists.get(j).get(i).getDate().getHours() == i)
                        stats.addValue(getValuesFromHour(daySensorEntryLists.get(j).get(i)));
                } catch (Exception e) {
                    stats.addValue(0);
                }
            }

            double mean = stats.getMean();
            double std = stats.getStandardDeviation();

            for (int j = 0; j < daySensorEntryLists.size(); j++) {
                try {
                    if(Math.abs(getValuesFromHour(daySensorEntryLists.get(j).get(i)) - mean) > 3 * std)
                        anomalies.add(daySensorEntryLists.get(j).get(i).getDate());
                } catch (Exception e) {
                    // You've caught me. What do you plan to do with me? ~ exception
                }
            }
        }

        return anomalies;
    }

    private double getValuesFromHour(HourSensorEntryList hourSensorEntryList) {
        double result = 0;

        for(SensorEntry sensorEntry : hourSensorEntryList.getSensorEntryList())
            result += sensorEntry.getValue();

        return result;
    }

    private List<List<HourSensorEntryList>> splitByDays(List<SensorEntry> sensorEntryList) {
        List<List<HourSensorEntryList>> daySensorEntryList = new ArrayList<>();
        List<HourSensorEntryList> hourSensorEntryList = new ArrayList<>();

        Date lastDate = null;
        List<SensorEntry> tempSensorEntryList = new ArrayList<>();
        if (sensorEntryList.size() > 0)
            lastDate = sensorEntryList.get(0).getTimestamp();

        Date currentDate;
        for (SensorEntry sensorEntry : sensorEntryList) {
            currentDate = sensorEntry.getTimestamp();
            if (lastDate.getDay() != currentDate.getDay()) {
                daySensorEntryList.add(splitByHours(tempSensorEntryList));
                tempSensorEntryList = new ArrayList<>();
            }
            lastDate = currentDate;
            tempSensorEntryList.add(sensorEntry);
        }

        return daySensorEntryList;
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

}
