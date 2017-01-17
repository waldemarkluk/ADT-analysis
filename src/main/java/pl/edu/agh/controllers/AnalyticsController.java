package pl.edu.agh.controllers;

import com.cloudera.sparkts.api.java.JavaTimeSeriesRDDFactory;
import com.datastax.driver.core.Session;
import com.datastax.spark.connector.SparkContextFunctions;
import com.datastax.spark.connector.cql.CassandraConnector;
import com.datastax.spark.connector.rdd.ReadConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalyticsController {

    @Autowired
    private SparkContext sparkContext;

    @RequestMapping("/correlation")
    public Object getCorrelation() {
        SparkSession spark = new SparkSession(sparkContext);

        return spark.sqlContext().read()
                .format("org.apache.spark.sql.cassandra")
                .option("table", "measurements")
                .option("keyspace", "measurements")
                .load()
                .limit(10).collectAsList().toString();
    }
}
