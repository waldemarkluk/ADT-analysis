package pl.edu.agh.controllers;

import com.datastax.spark.connector.japi.CassandraRow;
import com.datastax.spark.connector.japi.rdd.CassandraTableScanJavaRDD;
import org.apache.spark.SparkContext;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Wiktor on 2017-01-20.
 */
@RestController
@RequestMapping("/wiktortest")
public class CorrelationController {

    @Autowired
    private CassandraTableScanJavaRDD<CassandraRow> cassandraTable;


    /*
        W ten sposób dostanę współczynnik korelacji między A i B dla konkretnego dnia
        Można by to zbijać/grupować/wyznaczyć linię trendu/whatevs

        X - liczba pojazdów, które przejechały przez czujkę A w ciągu dnia
        Y - liczba pojazdów, które przejechały przez czujkę B w ciągu dnia
        Xi - liczba pojazdów, która przejechała przez czujkę A w czasie godziny i
        Yi - liczba pojazdów, która przejechała przez czujkę B w czasie godziny i
        śr.ar x - liczba pojazdów, która przejechała przez czujkę A w ciągu dnia/24
        śr.ar y - liczba pojazdów, która przejechała przez czujkę B w ciągu dnia/24

        r(x, y) = cov(x, y) / (odch_x * odch_y)
        cov(x, y) = sum((xi - śr.ar x) * (yi - śr.ar y))
        (odch_x * odch_y) = sqrt(sum((xi - śr.ar x)^2) * sum((yi - śr.ar y)^2))
    */
    @RequestMapping(method = RequestMethod.GET, value = "/correlation")
    public ResponseEntity<String> calculateCorrelationBetweenSensors(@RequestParam("first") String first, @RequestParam("second") String second, @RequestParam("when") String dateString) {
        Date date = new Date();
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            date = format.parse(dateString);
        } catch (ParseException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!sensorIdExists(first) || !sensorIdExists(second)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return null;
    }

    private boolean sensorIdExists(String sensorId) {
        return cassandraTable.where("sensorId = ?", sensorId).select("sensorId").limit(1L).collect().size() == 1;
    }
}


