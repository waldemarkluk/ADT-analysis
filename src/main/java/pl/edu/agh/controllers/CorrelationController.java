package pl.edu.agh.controllers;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;

/**
 * Created by Wiktor on 2017-01-20.
 */
@RestController
public class CorrelationController extends CassandraTableScanBasedController {

    private final static Logger LOG = Logger.getLogger(CorrelationController.class);


    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    /*
                W ten sposób dostanę współczynnik korelacji między A i B dla konkretnego dnia
                Można by to zbijać/grupować/wyznaczyć linię trendu/whatevs

                X - liczba pojazdów, które przejechały przez czujkę A w ciągu dnia
                Y - liczba pojazdów, które przejechały przez czujkę B w ciągu dnia
                Xi - liczba pojazdów, która przejechała przez czujkę A w czasie godziny i
                Yi - liczba pojazdów, która przejechała przez czujkę B w czasie godziny i
                śr.ar x - liczba pojazdów, która przejechała przez czujkę A w ciągu dnia/24
                śr.ar y - liczba pojazdów, która przejechała przez czujkę B w ciągu dnia/24

                r(x, y) = cov(x, y) / (odch_x * odch_y)
                cov(x, y) = sum((xi - śr.ar x) * (yi - śr.ar y))
                (odch_x * odch_y) = sqrt(sum((xi - śr.ar x)^2) * sum((yi - śr.ar y)^2))
            */
    @RequestMapping(method = RequestMethod.GET, value = "/correlation/{when}/{firstSensorId}/{secondSensorId}")
    public Object calculateCorrelationBetweenSensors(
            @PathVariable("firstSensorId") String firstSensorId,
            @PathVariable("secondSensorId") String secondSensorId,
            @PathVariable("when") String dateString) throws ParseException {
        Date date = dateFormat.parse(dateString);
        long startOfDay = TimeUnit.MILLISECONDS.toSeconds(date.getTime());

        double dailyAvgX = sumValuesForDay(firstSensorId, startOfDay) / 24.0;
        double dailyAvgY = sumValuesForDay(secondSensorId, startOfDay) / 24.0;

        long numerator = 0;
        for (int i = 0; i < 24; i++) {
            numerator += (sumValuesForPeriod(firstSensorId, startOfDay, i) - dailyAvgX)
                    * (sumValuesForPeriod(secondSensorId, startOfDay, i) - dailyAvgY);
        }

        long partialSumX = 0;
        for (int i = 0; i < 24; i++) {
            partialSumX += Math.pow(sumValuesForPeriod(firstSensorId, startOfDay, i) - dailyAvgX, 2);
        }

        long partialSumY = 0;
        for (int i = 0; i < 24; i++) {
            partialSumY += Math.pow((sumValuesForPeriod(secondSensorId, startOfDay, i) - dailyAvgY), 2);
        }

        double denominator = Math.sqrt(partialSumX * partialSumY);
        if (denominator == 0) {
            return 0;
        } else {
            return numerator / denominator;
        }
    }

    private long sumValuesForDay(String sensorId, long fromTime) {
        return sumValuesForPeriod(sensorId, fromTime, fromTime + DAYS.toSeconds(1));
    }

    private long sumValuesForPeriod(String sensorId, long startTime, int hour) {
        return sumValuesForPeriod(sensorId, startTime + HOURS.toSeconds(hour), startTime + HOURS.toSeconds(hour + 1));
    }

    private long sumValuesForPeriod(String sensorId, long startTime, long endTime) {
        JavaRDD<Long> rdd = getMeasurementsBetween(sensorId, startTime, endTime)
                .map(row -> row.getLong("value"));
        if (rdd.isEmpty()) {
            return 0L;
        } else {
            return rdd.reduce((a, b) -> a + b);
        }
    }
}


