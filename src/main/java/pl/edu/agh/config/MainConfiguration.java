package pl.edu.agh.config;

import com.datastax.driver.core.Session;
import com.datastax.spark.connector.cql.CassandraConnector;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MainConfiguration {

    @Value("${spark.master.endpoint}")
    private String sparkMasterEndpoint;

    @Value("${cassandra.contact.endpoints}")
    private String cassandraContactPoints;

   @Bean
    public SparkContext javaSparkContext() {
        SparkConf conf = new SparkConf();
        conf.setAppName("ADT Analysis");
       conf.set("spark.io.compression.codec", "org.apache.spark.io.LZ4CompressionCodec");
        conf.setMaster("spark://" + sparkMasterEndpoint);
       conf.set("spark.executor.memory", "471859200");
       conf.set("spark.driver.cores", "1");
//        conf.setMaster("local[4]");
        conf.set("spark.cassandra.connection.host", cassandraContactPoints);
        return new SparkContext(conf);
    }
}
