package br.com.analisadorb3.models;

public class StockHistoricalData {
    private String open;
    private String close;
    private String high;
    private String low;
    private String volume;

    public String getOpen() {
        return open;
    }

    public String getClose() {
        return close;
    }

    public String getHigh() {
        return high;
    }

    public String getLow() {
        return low;
    }

    public String getVolume() {
        return volume;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }
}
