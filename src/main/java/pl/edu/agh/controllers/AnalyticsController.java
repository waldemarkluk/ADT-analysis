package pl.edu.agh.controllers;

import org.apache.spark.api.java.JavaRDD;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AnalyticsController extends CassandraTableScanBasedController {

    /**
     * @return Returns count of all measurements in the database
     */
    @RequestMapping("/measurements/count")
    public long getCount() {
        return cassandraTable.cassandraCount();
    }

    /**
     * @param sensorId
     * @param from     Unix epoch time value to start count from
     * @param to       Unix epoch time value to stop count at
     * @return Returns count of sensor entries between 'from' and 'to'
     */
    @RequestMapping("/measurements/count/{sensorId}")
    public Long getCountBetweenDates(
            @PathVariable String sensorId,
            @RequestParam("from") Long from,
            @RequestParam("to") Long to
    ) {
        return getMeasurementsBetween(sensorId, from, to)
                .cassandraCount();
    }

    /**
     * @return Returns list of sensor ids
     */
    @RequestMapping(method = RequestMethod.GET, value = "/sensors/names")
    public ResponseEntity<List<String>> getSensors() {
        List<String> ans = cassandraTable.select("sensorid").distinct().map(row -> row.getString("sensorid")).collect();
        if (ans.isEmpty()) {
            return new ResponseEntity<>(ans, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(ans, HttpStatus.OK);
        }
    }
}
