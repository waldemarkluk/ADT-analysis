package pl.edu.agh.controllers;

import com.cloudera.sparkts.BusinessDayFrequency;
import com.cloudera.sparkts.DateTimeIndex;
import com.cloudera.sparkts.api.java.DateTimeIndexFactory;
import com.cloudera.sparkts.api.java.JavaTimeSeriesRDD;
import com.cloudera.sparkts.api.java.JavaTimeSeriesRDDFactory;
import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.datastax.spark.connector.japi.CassandraRow;
import com.datastax.spark.connector.japi.CassandraStreamingJavaUtil;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class AnalyticsController {

    @Autowired
    private SparkContext sparkContext;

    @RequestMapping("/correlation")
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


        return CassandraJavaUtil.javaFunctions(sparkContext)
                .cassandraTable("measurements", "measurements")
                .count();
//                .map(CassandraRow::toString).collect();
    }
}
