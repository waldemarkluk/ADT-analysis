package pl.edu.agh.config;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.datastax.spark.connector.japi.CassandraRow;
import com.datastax.spark.connector.japi.rdd.CassandraTableScanJavaRDD;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MainConfiguration {

    @Value("${spark.master.endpoint}")
    private String sparkMasterEndpoint;

    @Value("${cassandra.contact.endpoints}")
    private String cassandraContactPoints;

    @Value("${cassandra.keyspace}")
    private String keyspace = "measurements";

    @Value("${cassandra.table.name}")
    private String tableName = "measurements";

    @Value("${spark.settings}")
    private String sparkSettings;

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public SparkContext sparkContext() {
        SparkConf conf = new SparkConf();
        conf.setAppName(applicationName + "SparkContext")
        .setMaster(sparkMasterEndpoint)
        .set("spark.cassandra.connection.host", cassandraContactPoints);

        for(String option:sparkSettings.split(",")) {
            String[] keyVal = option.trim().split("=");
            conf.set(keyVal[0], keyVal[1]);
        }

        return new SparkContext(conf);
    }

    @Bean
    public CassandraTableScanJavaRDD<CassandraRow> cassandraTable() {
        return CassandraJavaUtil.javaFunctions(sparkContext())
                .cassandraTable(keyspace, tableName);
    }
}
