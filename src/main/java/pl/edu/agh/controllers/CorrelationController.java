package pl.edu.agh.controllers;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.logic.PearsonCorrelation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.HOURS;
import static pl.edu.agh.logic.PearsonCorrelation.computeCorrelation;

/**
 * Created by Wiktor on 2017-01-20.
 */
@RestController
public class CorrelationController extends CassandraTableScanBasedController {

    private final static Logger LOG = Logger.getLogger(CorrelationController.class);


    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

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

        long xDailySum = 0;
        long yDailySum = 0;

        Map<Integer, Long> xSumPerHourCache = new HashMap<>();
        Map<Integer, Long> ySumPerHourCache = new HashMap<>();
        for (int i = 0; i < 24; i++) {
            long xSumPerHour = sumValuesForPeriod(firstSensorId, startOfDay, i);
            xSumPerHourCache.put(i, xSumPerHour);
            long ySumPerHour = sumValuesForPeriod(secondSensorId, startOfDay, i);
            ySumPerHourCache.put(i, ySumPerHour);

            xDailySum += xSumPerHour;
            yDailySum += ySumPerHour;
        }

        validateSum(xDailySum, firstSensorId);
        validateSum(yDailySum, secondSensorId);

        return PearsonCorrelation.computeCorrelation(xDailySum, yDailySum, xSumPerHourCache, ySumPerHourCache);
    }

    private static void validateSum(long sum, String sensorId) {
        if (sum == 0) {
            throw new RuntimeException(sensorId + " did not register any value that day");
        }
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


