package com.flourish.cryptomafia.api;


import com.flourish.cryptomafia.models.CryptoCompare;

import retrofit2.Call;
import retrofit2.http.GET;


public interface APIService {

    //getting latest currency exchange rates
    @GET("pricemulti?fsyms=ETH,BTC&tsyms=NGN,KES,USD,GBP,CNY,CHF,EUR,MXN,GLD,JOD,OMR,BHD,KWD,JPY,CAD,AED,CLP,BRL,INR,ILS")
    Call<CryptoCompare> getLatestExchangeRates();

}
