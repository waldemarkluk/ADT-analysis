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

    @Value("${spark.executor.memory}")
    private String sparkExecutorMemory;

    @Value("${cassandra.keyspace}")
    private String keyspace = "measurements";

    @Value("${cassandra.table.name}")
    private String tableName = "measurements";

    @Bean
    public SparkContext sparkContext() {
        SparkConf conf = new SparkConf();
        conf.setAppName("ADT Analysis");
        conf.set("spark.io.compression.codec", "org.apache.spark.io.LZ4CompressionCodec");
        conf.setMaster(sparkMasterEndpoint);
        conf.set("spark.executor.memory", sparkExecutorMemory);
        conf.set("spark.cassandra.connection.host", cassandraContactPoints);
        conf.set("spark.cassandra.read.timeout_ms", "1200000");
        conf.set("spark.cassandra.connection.timeout_ms", "1200000");
        conf.set("spark.cassandra.input.split.size_in_mb", "67108864");
        return new SparkContext(conf);
    }

    @Bean
    public CassandraTableScanJavaRDD<CassandraRow> cassandraTable() {
        return CassandraJavaUtil.javaFunctions(sparkContext())
                .cassandraTable(keyspace, tableName);
    }
}
