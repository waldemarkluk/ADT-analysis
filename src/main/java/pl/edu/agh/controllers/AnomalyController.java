package pl.edu.agh.controllers;

import com.datastax.spark.connector.japi.CassandraRow;
import com.datastax.spark.connector.japi.rdd.CassandraTableScanJavaRDD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.model.AnomalyReport;

import java.util.Date;

/**
 * Created by Wiktor on 2017-01-21.
 */

@RestController
public class AnomalyController {

    @Autowired
    private CassandraTableScanJavaRDD<CassandraRow> cassandraTable;


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
    @RequestMapping(method = RequestMethod.GET, value = "/sensors/{sensorId}/anomalies")
    public ResponseEntity<AnomalyReport> checkForAnomalies(@PathVariable("sensorId") String sensorId, @RequestParam("from") Date from, @RequestParam("from") Date to) {
        return null;
    }
}
