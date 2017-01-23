package pl.edu.agh.logic;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import pl.edu.agh.model.HourSensorEntryList;
import pl.edu.agh.model.SensorEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AnomalyAlgorithms {
    public static final double TOLERANCE = 0.1;

    public List<Date> getPauseAnomaliesVictorMethod(List<SensorEntry> sensorEntryList) {
        List<Date> anomalies = new ArrayList<>();
        List<HourSensorEntryList> hourSensorEntryLists = splitByHours(sensorEntryList);
        DescriptiveStatistics stats = new DescriptiveStatistics();

        for (HourSensorEntryList hourSensorEntryList1 : hourSensorEntryLists) {
            stats.addValue(hourSensorEntryList1.getSensorEntryList().size());
        }

        double q1 = stats.getPercentile(25);
        double q3 = stats.getPercentile(75);
        double iqr = q3 - q1;
        double lowerFence = q1 - 1.5 * iqr;

        for (HourSensorEntryList hourSensorEntryList : hourSensorEntryLists) {
            if (hourSensorEntryList.getSensorEntryList().size() < lowerFence)
                anomalies.add(hourSensorEntryList.getDate());
        }

        return anomalies;
    }

    public List<Pair<Date, Date>> getPauseAnomaliesPeterMethod(List<SensorEntry> sensorEntryList, Long fromDate, Long toDate) {
        List<Pair<Date, Date>> anomalies = new ArrayList<>();

        double standardTime = 90; // SECONDS
        double toleranceTime = 90 + standardTime * AnomalyAlgorithms.TOLERANCE;

        for (int i = 1; i < sensorEntryList.size(); i++) {
            if (Math.abs(sensorEntryList.get(i).getTimestamp().getTime() - sensorEntryList.get(i).getTimestamp().getTime()) > toleranceTime)
                anomalies.add(new ImmutablePair<>(sensorEntryList.get(i - 1).getTimestamp(), sensorEntryList.get(i).getTimestamp()));
        }

        // if there is no entry at all
       if(!Objects.equals(fromDate, toDate) && sensorEntryList.size() == 0)
            anomalies.add(new ImmutablePair<>(new Date(fromDate), new Date(toDate)));

        return anomalies;
    }

    public List<Date> getAnomalies(List<SensorEntry> sensorEntryList) {
        List<Date> anomalies = new ArrayList<>();
        List<List<HourSensorEntryList>> daySensorEntryLists = splitByDays(sensorEntryList);
        DescriptiveStatistics stats = new DescriptiveStatistics();

        for(int i=0; i<24; i++) {
            for (List<HourSensorEntryList> daySensorEntryList : daySensorEntryLists) {
                try {
                    if (daySensorEntryList.get(i).getDate().getHours() == i)
                        stats.addValue(getValuesFromHour(daySensorEntryList.get(i)));
                } catch (Exception e) {
                    stats.addValue(0);
                }
            }

            double mean = stats.getMean();
            double std = stats.getStandardDeviation();

            int j = 0;
            while (j < daySensorEntryLists.size()) {
                try {
                    if(Math.abs(getValuesFromHour(daySensorEntryLists.get(j).get(i)) - mean) > 3 * std)
                        anomalies.add(daySensorEntryLists.get(j).get(i).getDate());
                } catch (Exception e) {
                    // You've caught me. What do you plan to do with me? ~ exception
                }
                j++;
            }
        }

        return anomalies;
    }

    private double getValuesFromHour(HourSensorEntryList hourSensorEntryList) {
        double result = 0;

        for(SensorEntry sensorEntry : hourSensorEntryList.getSensorEntryList())
            result += sensorEntry.getValue();

        return result;
    }

    private List<List<HourSensorEntryList>> splitByDays(List<SensorEntry> sensorEntryList) {
        List<List<HourSensorEntryList>> daySensorEntryList = new ArrayList<>();
        List<HourSensorEntryList> hourSensorEntryList = new ArrayList<>();

        Date lastDate = null;
        List<SensorEntry> tempSensorEntryList = new ArrayList<>();
        if (sensorEntryList.size() > 0)
            lastDate = sensorEntryList.get(0).getTimestamp();

        Date currentDate;
        for (SensorEntry sensorEntry : sensorEntryList) {
            currentDate = sensorEntry.getTimestamp();
            if (lastDate.getDay() != currentDate.getDay()) {
                daySensorEntryList.add(splitByHours(tempSensorEntryList));
                tempSensorEntryList = new ArrayList<>();
            }
            lastDate = currentDate;
            tempSensorEntryList.add(sensorEntry);
        }

        return daySensorEntryList;
    }

    private List<HourSensorEntryList> splitByHours(List<SensorEntry> sensorEntryList) {
        List<pl.edu.agh.model.HourSensorEntryList> hourSensorEntryList = new ArrayList<>();

        Date lastDate = null;
        List<SensorEntry> tempSensorEntryList = new ArrayList<>();
        if (sensorEntryList.size() > 0)
            lastDate = sensorEntryList.get(0).getTimestamp();

        Date currentDate;
        for (SensorEntry sensorEntry : sensorEntryList) {
            currentDate = sensorEntry.getTimestamp();
            if (lastDate.getHours() != currentDate.getHours()) {
                hourSensorEntryList.add(new pl.edu.agh.model.HourSensorEntryList(lastDate, tempSensorEntryList));
                tempSensorEntryList = new ArrayList<>();
            }
            lastDate = currentDate;
            tempSensorEntryList.add(sensorEntry);
        }

        return hourSensorEntryList;
    }
}
