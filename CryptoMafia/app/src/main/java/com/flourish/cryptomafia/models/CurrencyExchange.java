package com.flourish.cryptomafia.models;

/**
 *
 */
public class CurrencyExchange {
    private String currencyName;
    private String bitcoinPrice;
    private int thumbnail;
    private String etherPrice;
    private String currencyShortCode;

    public CurrencyExchange(String currencyName, String currencyShortCode, String bitcoinPrice, int thumbnail, String etherPrice) {
        this.currencyName = currencyName;
        this.bitcoinPrice = bitcoinPrice;
        this.thumbnail = thumbnail;
        this.etherPrice = etherPrice;
        this.currencyShortCode = currencyShortCode;
    }

    // Getters
    public String getCurrencyName() { return currencyName; }
    public String getBitcoinPrice() { return bitcoinPrice; }
    public int getThumbnail() { return thumbnail; }
    public String getEtherPrice() { return etherPrice; }
    public String getCurrencyShortCode() { return currencyShortCode; }

}