package pl.edu.agh.controllers;

import com.datastax.spark.connector.japi.rdd.CassandraTableScanJavaRDD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalyticsController {

    @Autowired
    private CassandraTableScanJavaRDD cassandraTable;

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

    @RequestMapping("/measurements/count/bla")
    public long getCountBetweenDates() {
        return cassandraTable
                .where("sensorId = ?", "bla")
                .cassandraCount();
    }
}
