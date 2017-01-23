package pl.edu.agh.logic;


import java.util.Map;

public class PearsonCorrelation {
    public static double computeCorrelation(long xDailySum, long yDailySum, Map<Integer, Long> xSumPerHourCache, Map<Integer, Long> ySumPerHourCache) {
        double xDailyAvg = xDailySum / 24.0;
        double yDailyAvg = yDailySum / 24.0;

        long numerator = 0;
        long partialSumX = 0;
        long partialSumY = 0;
        for (int i = 0; i < 24; i++) {
            numerator += (xSumPerHourCache.get(i) - xDailyAvg) * (ySumPerHourCache.get(i) - yDailyAvg);
            partialSumX += Math.pow(xSumPerHourCache.get(i) - xDailyAvg, 2);
            partialSumY += Math.pow(ySumPerHourCache.get(i) - yDailyAvg, 2);
        }

        double denominator = Math.sqrt(partialSumX * partialSumY);

        return numerator / denominator;
    }
}
