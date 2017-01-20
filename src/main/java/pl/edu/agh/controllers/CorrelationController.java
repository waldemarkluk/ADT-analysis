package pl.edu.agh.controllers;

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
    private SparkContext sparkContext;

    @RequestMapping(method = RequestMethod.GET, value = "/correlation")
    public String calculateCorrelationBetweenDrivers(@RequestParam("first") String first, @RequestParam("second") String second) {
        return null;
    }
}
