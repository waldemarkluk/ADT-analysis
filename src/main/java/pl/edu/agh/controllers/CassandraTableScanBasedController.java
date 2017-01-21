package pl.edu.agh.controllers;

import com.datastax.spark.connector.japi.CassandraRow;
import com.datastax.spark.connector.japi.rdd.CassandraTableScanJavaRDD;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static java.util.concurrent.TimeUnit.SECONDS;

class CassandraTableScanBasedController {
    @Autowired
    CassandraTableScanJavaRDD<CassandraRow> cassandraTable;

    /**
     * Gets measurements for sensorId in provided time span.
     *
     * @param sensorId
     * @param fromTime - inclusive, in seconds
     * @param toTime   - exclusive, in seconds
     */
    CassandraTableScanJavaRDD<CassandraRow> getMeasurementsBetween(String sensorId, Long fromTime, Long toTime) {
        return cassandraTable
                .where("sensorid = ? AND time >= ? AND time < ?", sensorId,
                        new Date(SECONDS.toMillis(fromTime)),
                        new Date(SECONDS.toMillis(toTime)));
    }
}
