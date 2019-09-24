package br.com.analisadorb3.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RealTimeDataResponse {

    @SerializedName("symbols_requested")
    @Expose
    private int symbolsRequested;

    @SerializedName("symbols_returned")
    @Expose
    private int symbolsReturned;

    @SerializedName("data")
    @Expose
    private List<StockRealTimeData> data;

    public List<StockRealTimeData> getData() {
        return data;
    }

    public void setData(List<StockRealTimeData> data){
        this.data = data;
    }

    public int getSymbolsRequested() {
        return symbolsRequested;
    }

    public void setSymbolsRequested(int symbolsRequested) {
        this.symbolsRequested = symbolsRequested;
    }

    public int getSymbolsReturned() {
        return symbolsReturned;
    }

    public void setSymbolsReturned(int symbolsReturned) {
        this.symbolsReturned = symbolsReturned;
    }
}
