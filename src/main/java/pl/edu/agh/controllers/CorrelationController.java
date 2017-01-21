package pl.edu.agh.controllers;

import com.datastax.spark.connector.japi.CassandraRow;
import com.datastax.spark.connector.japi.rdd.CassandraTableScanJavaRDD;
import org.apache.spark.SparkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    cov(x, y) = (xi - śr.ar x) * (yi - śr.ar y)
    odch_x = sqrt(xi - śr.ar x)
    odch_y = sqrt(yi - śr. ar y)
*/
    @RequestMapping(method = RequestMethod.GET, value = "/correlation")
    public String calculateCorrelationBetweenDrivers(@RequestParam("first") String first, @RequestParam("second") String second) {

        return null;
    }
}


