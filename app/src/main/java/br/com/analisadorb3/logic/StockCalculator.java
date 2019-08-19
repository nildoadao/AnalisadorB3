package br.com.analisadorb3.logic;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import br.com.analisadorb3.models.StockQuote;

public class StockCalculator {

    public static double getAverageVariation(List<StockQuote> list, int months) {

        double totalVariation = 0;
        int valuesCount = 0;
        Collections.sort(list);
        LocalDate currentDate = LocalDate.now();

        for(int i = list.size() - 1 ; i >= 0; i--) {

            if(list.get(i).getDate().isAfter(currentDate.minusMonths(months))) {
                totalVariation += list.get(i).getHigh() - list.get(i).getLow();
                valuesCount++;
            }
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
