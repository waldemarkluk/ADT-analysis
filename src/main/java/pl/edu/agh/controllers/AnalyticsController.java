package pl.edu.agh.controllers;

import com.cloudera.sparkts.api.java.JavaTimeSeriesRDDFactory;
import com.datastax.driver.core.Session;
import com.datastax.spark.connector.cql.CassandraConnector;
import org.apache.spark.SparkContext;
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
        CassandraConnector connector = CassandraConnector.apply(spark.sparkContext().conf());
        Session session = connector.openSession();
        return session.execute("SELECT * FROM measurements.measurements LIMIT 1").one().toString();
    }
}
