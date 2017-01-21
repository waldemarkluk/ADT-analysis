package pl.edu.agh.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalyticsController extends CassandraTableScanBasedController {

/*    @RequestMapping("/correlation")
    public Object getCorrelation() {
//        Dataset<Row> dataset = new SQLContext(sparkContext).read()
//                .format("org.apache.spark.sql.cassandra")
//                .option("table", "measurements")
//                .option("keyspace", "measurements")
//                .


        ZoneId zone = ZoneId.systemDefault();
        DateTimeIndex dtIndex = DateTimeIndexFactory.uniformFromInterval(
                ZonedDateTime.of(LocalDateTime.parse("2015-08-03T00:00:00"), zone),
                ZonedDateTime.of(LocalDateTime.parse("2015-09-22T00:00:00"), zone),
                new BusinessDayFrequency(1, 0));

//        JavaTimeSeriesRDD<Object> objectJavaTimeSeriesRDD = JavaTimeSeriesRDDFactory.timeSeriesRDD()
        return "Boop";
//                .map(CassandraRow::toString).collect();
    }*/

    @RequestMapping("/measurements/count")
    public long getCount() {
        return cassandraTable.cassandraCount();
    }

    @RequestMapping("/measurements/count/{sensorId}")
    public Long getCountBetweenDates(
            @PathVariable String sensorId,
            @RequestParam(value = "from") Long from,
            @RequestParam(value = "to") Long to
    ) {
        return getMeasurementsBetween(sensorId, from, to)
                .cassandraCount();
    }
}
