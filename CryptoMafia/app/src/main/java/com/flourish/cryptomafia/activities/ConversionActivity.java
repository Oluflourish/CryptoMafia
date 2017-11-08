package com.flourish.cryptomafia.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flourish.cryptomafia.R;

public class ConversionActivity extends AppCompatActivity {

    //Intent data
    String currencyName, bitcoinRate, etherRate, shortCode;
    boolean isBTCswapped = false;
    boolean isETHswapped = false;

    // BTC conversion variables
    private EditText cardBtcInput;
    private TextView btcTitle_tv, btcCurrencyShortCode1, btcCurrencyShortCode2, btcConversionResult;

    // ETH conversion variables
    private EditText cardEthInput;
    private TextView ethTitle_tv, ethCurrencyShortCode1, ethCurrencyShortCode2, ethConversionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Allows the back button to pop off the current activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView ethRate_tv = (TextView) findViewById(R.id.eth_rate);
        TextView btcRate_iv = (TextView) findViewById(R.id.btc_rate);
        ImageView flag_iv = (ImageView) findViewById(R.id.flag_thumbnail);

        // Initialize the BTC card view views
        initBTCViews();

        //initialize the Ether card view views
        initETHViews();

        //Read intent data passed from the MainActivity
        Intent incomingIntent = getIntent();

        if (incomingIntent.hasExtra("currencyName")) {
            currencyName = getIntent().getExtras().getString("currencyName");
            bitcoinRate = getIntent().getExtras().getString("btcRate");
            etherRate = getIntent().getExtras().getString("ethRate");
            shortCode = getIntent().getExtras().getString("shortCode");
            int thumbnail = getIntent().getExtras().getInt("countryFlag");

            //set the exchange rates for BTH and ETH
            btcRate_iv.setText(bitcoinRate);
            ethRate_tv.setText(etherRate);

            //Set the currency shortCodes to the view
            btcCurrencyShortCode1.setText(shortCode);
            ethCurrencyShortCode1.setText(shortCode);

            // loading currencyExchange cover using Glide library
            Glide.with(this).load(thumbnail).into(flag_iv);

        }
        //intent data was not received
        else {
            Toast.makeText(this, "Error Occurred. No API Data", Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    private void initBTCViews() {

        //finding and initializing the views
        btcCurrencyShortCode1 = (TextView) findViewById(R.id.btc_country_shortcode);
        btcCurrencyShortCode2 = (TextView) findViewById(R.id.btc_currency_shortcode);
        btcTitle_tv = (TextView) findViewById(R.id.btc_conversion_title);
        btcConversionResult = (TextView) findViewById(R.id.btc_conversion_result);
        cardBtcInput = (EditText) findViewById(R.id.btc_input);
        Button btcViceVersaBtn = (Button) findViewById(R.id.btc_currency_swap);

        //Setting input fields to vice versa to support both way conversion
        btcViceVersaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if conversion mode is vice versa then return to normal mode
                if (isBTCswapped) {
                    isBTCswapped = false;
                    btcTitle_tv.setText("Convert from " + shortCode + " to " + "BTC");
                    btcCurrencyShortCode1.setText(shortCode);
                    btcCurrencyShortCode2.setText(R.string.btc_shortcode);
                    //clear previous data
                    btcConversionResult.setText("");
                    cardBtcInput.setText("");
                } else {
                    //if conversion mode normal then return to vice versa mode
                    isBTCswapped = true;
                    btcTitle_tv.setText("Convert from " + "BTC to " + shortCode);
                    btcCurrencyShortCode1.setText(R.string.btc_shortcode);
                    btcCurrencyShortCode2.setText(shortCode);
                    //clear previous data
                    btcConversionResult.setText("");
                    cardBtcInput.setText("");
                }
            }
        });

        cardBtcInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = cardBtcInput.getText().toString();

                if (TextUtils.isEmpty(input)) {
                    btcConversionResult.setText("Enter a value");
                } else {
                    // parse the input from the string
                    double inputToBeConverted = Double.parseDouble(input);
                    // Do the conversion
                    btcConverter(bitcoinRate, inputToBeConverted);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }

    private void initETHViews() {
        //finding and initializing the views
        ethCurrencyShortCode1 = (TextView) findViewById(R.id.eth_country_shortcode);
        ethCurrencyShortCode2 = (TextView) findViewById(R.id.eth_currency_shortcode);
        ethTitle_tv = (TextView) findViewById(R.id.crdEth_conversionTitle);
        ethConversionResult = (TextView) findViewById(R.id.eth_conversion_result);
        cardEthInput = (EditText) findViewById(R.id.eth_input);
        Button ethViceVersaBtn = (Button) findViewById(R.id.crdEth_BtnViceVersa);

        //Setting input fields to vice versa to support both way conversion
        ethViceVersaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if conversion mode is vice versa then return to normal mode

                if (isETHswapped) {
                    isETHswapped = false;
                    ethTitle_tv.setText("Convert from " + shortCode + " to " + "ETH");
                    ethCurrencyShortCode1.setText(shortCode);
                    ethCurrencyShortCode2.setText(R.string.eth_shortcode);

                    // Clear Previous data
                    ethConversionResult.setText("");
                    cardEthInput.setText("");
                } else {
                    //if conversion mode normal then return to vice versa mode
                    isETHswapped = true;
                    ethTitle_tv.setText("Convert from " + "ETH to " + shortCode);
                    ethCurrencyShortCode1.setText(R.string.eth_shortcode);
                    ethCurrencyShortCode2.setText(shortCode);

                    //clear previous data
                    ethConversionResult.setText("");
                    cardEthInput.setText("");
                }
            }
        });

        cardEthInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = cardEthInput.getText().toString();

                if (TextUtils.isEmpty(input)) {
                    ethConversionResult.setText("Enter a value");
                } else {
                    // parse the input from the string
                    double inputToBeConverted = Double.parseDouble(input);
                    // Do the conversion
                    ethConverter(etherRate, inputToBeConverted);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }

    private void btcConverter(String btcRate, double value) {
        //change the bitcoin_logo rate into a double
        double btcPrice = Double.parseDouble(btcRate);

        if (!isBTCswapped) {
            //Convert from selected currency to BTC
            double answer = value / btcPrice;
            btcConversionResult.setText(String.valueOf(answer));
        } else {
            //Convert BTC to selected currency
            double answer = value * btcPrice;
            btcConversionResult.setText(String.valueOf(answer));
        }
    }

    private void ethConverter(String ethRate, double value) {
        //change the etherum Rate into a double
        double etherPrice = Double.parseDouble(ethRate);

        if (!isETHswapped) {
            //Convert Other selected currency to ETH
            double answer = value / etherPrice;
            ethConversionResult.setText(String.valueOf(answer));
        } else {
            //Convert ETH to Other selected currency
            double answer = value * etherPrice;
            ethConversionResult.setText(String.valueOf(answer));
        }
    }
}
