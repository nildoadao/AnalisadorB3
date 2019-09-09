package br.com.analisadorb3.models.logic;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import br.com.analisadorb3.models.StockHistorycalData;

public class StockCalculator {

    public static double getAverageVariation(List<StockHistorycalData> list, int months) throws IllegalArgumentException{

        double totalVariation = 0;
        int valuesCount = 0;
        //Collections.sort(list);
        LocalDate currentDate = LocalDate.now();

        for(int i = list.size() - 1 ; i >= 0; i--) {

            StockHistorycalData quote = list.get(i);
            Double dayHigh;
            Double dayLow;

            try {
                dayHigh = Double.parseDouble(quote.getHigh());
                dayLow = Double.parseDouble(quote.getLow());
            }
            catch (Exception e){
                dayHigh = null;
                dayLow = null;
            }

            if(dayHigh == null || dayLow == null){
                throw new IllegalArgumentException("Fail to get DayHigh, DayLow");
            }

            /*if(list.get(i).getDate().isAfter(currentDate.minusMonths(months))) {
                totalVariation += dayHigh - dayLow;
                valuesCount++;
            }*/
            else {
                break;
            }
        }
        return totalVariation/valuesCount;
    }

    public static String doublePrettify(double value){
        if(value < 1000)
            return Double.toString(value);
        if(value/1000 < 1000)
            return String.format("%.2fK", value/1000);
        if(value/1000000 < 1000)
            return String.format("%.2fM", value/1000000);
        if(value/1000000000 < 1000)
            return String.format("%.2fB", value/1000000000);
        return "NaN";
    }
}
