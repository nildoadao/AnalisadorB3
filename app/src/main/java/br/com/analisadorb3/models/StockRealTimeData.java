package br.com.analisadorb3.models;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;

public class StockRealTimeData {

    private String symbol;
    private String name;
    private String price;
    private String currency;

    @SerializedName("price_open")
    private String priceOpen;

    @SerializedName("day_high")
    private String dayHigh;

    @SerializedName("day_low")
    private String dayLow;

    @SerializedName("52_week_high")
    private String yearHigh;

    @SerializedName("52_week_low")
    private String yearLow;

    @SerializedName("day_change")
    private String dayChange;

    @SerializedName("change_pct")
    private String changePercent;

    @SerializedName("close_yesterday")
    private String closeYesterday;

    @SerializedName("market_cap")
    private String marketCapital;

    private String volume;

    @SerializedName("volume_avg")
    private String averageVolume;

    private String shares;

    @SerializedName("stock_exchange_long")
    private String stockExchangeNameLong;

    @SerializedName("stock_exchange_short")
    private String stockExchangeNameShort;

    @SerializedName("timeZone")
    private String timeZone;

    @SerializedName("timeZone_name")
    private String timeZoneName;

    @SerializedName("gmt_offset")
    private String gmtOffset;

    @SerializedName("last_trade_time")
    private String lastTradingTime;

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public String getPriceOpen() {
        return priceOpen;
    }

    public String getDayHigh() {
        return dayHigh;
    }

    public String getDayLow() {
        return dayLow;
    }

    public String getDayChange() {
        return dayChange;
    }

    public String getChangePercent() {
        return changePercent;
    }

    public String getCloseYesterday() {
        return closeYesterday;
    }

    public String getMarketCapital() {
        return marketCapital;
    }

    public String getVolume() {
        return volume;
    }

    public String getLastTradingTime() {
        return lastTradingTime;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setPriceOpen(String priceOpen) {
        this.priceOpen = priceOpen;
    }

    public void setDayHigh(String dayHigh) {
        this.dayHigh = dayHigh;
    }

    public void setDayLow(String dayLow) {
        this.dayLow = dayLow;
    }

    public void setDayChange(String dayChange) {
        this.dayChange = dayChange;
    }

    public void setChangePercent(String changePercent) {
        this.changePercent = changePercent;
    }

    public void setCloseYesterday(String closeYesterday) {
        this.closeYesterday = closeYesterday;
    }

    public void setMarketCapital(String marketCapital) {
        this.marketCapital = marketCapital;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public void setLastTradingTime(String lastTradingTime) {
        this.lastTradingTime = lastTradingTime;
    }

    public String getYearHigh() {
        return yearHigh;
    }

    public void setYearHigh(String yearHigh) {
        this.yearHigh = yearHigh;
    }

    public String getYearLow() {
        return yearLow;
    }

    public void setYearLow(String yearLow) {
        this.yearLow = yearLow;
    }

    public String getAverageVolume() {
        return averageVolume;
    }

    public void setAverageVolume(String averageVolume) {
        this.averageVolume = averageVolume;
    }

    public String getShares() {
        return shares;
    }

    public void setShares(String shares) {
        this.shares = shares;
    }

    public String getStockExchangeNameLong() {
        return stockExchangeNameLong;
    }

    public void setStockExchangeNameLong(String stockExchangeNameLong) {
        this.stockExchangeNameLong = stockExchangeNameLong;
    }

    public String getStockExchangeNameShort() {
        return stockExchangeNameShort;
    }

    public void setStockExchangeNameShort(String stockExchangeNameShort) {
        this.stockExchangeNameShort = stockExchangeNameShort;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getTimeZoneName() {
        return timeZoneName;
    }

    public void setTimeZoneName(String timeZoneName) {
        this.timeZoneName = timeZoneName;
    }

    public String getGmtOffset() {
        return gmtOffset;
    }

    public void setGmtOffset(String gmtOffset) {
        this.gmtOffset = gmtOffset;
    }
}
