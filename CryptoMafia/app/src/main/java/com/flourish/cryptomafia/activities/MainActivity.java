package com.flourish.cryptomafia.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.flourish.cryptomafia.R;
import com.flourish.cryptomafia.adapters.CurrencyExchangeAdapter;
import com.flourish.cryptomafia.api.APIService;
import com.flourish.cryptomafia.api.APIUrl;
import com.flourish.cryptomafia.models.BTC;
import com.flourish.cryptomafia.models.CryptoCompare;
import com.flourish.cryptomafia.models.CurrencyExchange;
import com.flourish.cryptomafia.models.ETH;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CurrencyExchangeAdapter mAdapter;
    private List<CurrencyExchange> currencyExchangeList;

    // String array: For list of currencies and country names
    public String[] currencyNames;
    public String[] countryNames;

    public Spinner mCurrencySpinner;
    /*position of spinner selected item*/
    public int spinnerPosition;

    public ETH ether;
    public BTC bitcoin;
    public double[] BTCrates;
    public double[] ETHrates;

    public View mEmptyStateView;
    public Button retryButton;

    public FloatingActionButton fab;

    int[] flags = new int[]{
            R.drawable.flag_nigeria, R.drawable.flag_bahraini, R.drawable.flag_brazil,
            R.drawable.flag_british, R.drawable.flag_china, R.drawable.flag_euro,
            R.drawable.flag_canada, R.drawable.flag_chile, R.drawable.flag_india,
            R.drawable.flag_gilbraltar, R.drawable.flag_israel, R.drawable.flag_japan,
            R.drawable.flag_jordan, R.drawable.flag_kuwait, R.drawable.flag_kenya,
            R.drawable.flag_mexico, R.drawable.flag_oman, R.drawable.flag_swiss,
            R.drawable.flag_uae, R.drawable.flag_usa
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        // Initialize the arrays
        currencyNames = getResources().getStringArray(R.array.array_currency_options);
        countryNames = getResources().getStringArray(R.array.array_country_options);

        //initialize the recyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        currencyExchangeList = new ArrayList<>();
        mAdapter = new CurrencyExchangeAdapter(this, currencyExchangeList);

        // Set the number of grids to be displayed to 2
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setAdapter(mAdapter);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.layout_add_country, null);
                mBuilder.setTitle("Select a country:");
                //mBuilder.setIcon(R.mipmap.ic_launcher);

                mCurrencySpinner = mView.findViewById(R.id.fullname_text);

                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<String> currencyArrayAdapter = new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_spinner_item, countryNames);

                // Layout of the spinner items
                currencyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // apply the mAdapter to the spinner
                mCurrencySpinner.setAdapter(currencyArrayAdapter);
                mCurrencySpinner.setOnItemSelectedListener(new onSpinnerItemClicked());
                mBuilder.setPositiveButton("Add Country", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        addCountryCard();
                        dialog.dismiss();
                    }
                });
                mBuilder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
                dialog.setCanceledOnTouchOutside(false);
            }
        });


        // Get a reference to the ConnectivityManager to check the state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        mEmptyStateView = findViewById(R.id.empty_view);
        retryButton = (Button) findViewById(R.id.empty_view_retry);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchLatestExchangeRates();
            }
        });

        // Hide the empty state views
        mEmptyStateView.setVisibility(View.GONE);

        //If there is a connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            //Fetch the exchange rates for 20 countries from CryptoCompare Api using Retrofit
            fetchLatestExchangeRates();
            // Hide the empty state views
            mEmptyStateView.setVisibility(View.GONE);
            // Show the FAB button
            fab.setVisibility(View.VISIBLE);
        } else {
            // Show the empty state views
            mEmptyStateView.setVisibility(View.VISIBLE);
            // Hide the FAB button
            fab.setVisibility(View.GONE);
        }
    }

    /*method to handle user card creation*/
    public void addCountryCard() {

        currencyExchangeList.add(new CurrencyExchange(countryNames[spinnerPosition],
                currencyNames[spinnerPosition], String.valueOf(BTCrates[spinnerPosition]),
                flags[spinnerPosition],String.valueOf(ETHrates[spinnerPosition])));

        mAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);

        Toast.makeText(getApplicationContext(), String.valueOf(countryNames[spinnerPosition]) +
                " has been added", Toast.LENGTH_LONG).show();

    }

    /*method to handle dialogue spinner selection*/
    private class onSpinnerItemClicked implements AdapterView.OnItemSelectedListener {
        // This method is supposed to call the on item selected listener on the spinner class
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //this method gets called automatically when the user selects an item so we need to
            // retrieve what the user has clicked
            spinnerPosition = position;
            parent.getItemAtPosition(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //Do nothing
        }
    }


    //Fetch latest exchange rates from cryptoCompare API using retrofit
    private void fetchLatestExchangeRates() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading latest Exchange Rates...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIService service = retrofit.create(APIService.class);

        Call<CryptoCompare> call = service.getLatestExchangeRates();

        call.enqueue(new Callback<CryptoCompare>() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onResponse(Call<CryptoCompare> call, Response<CryptoCompare> response) {
                progressDialog.dismiss();

                if (response.code()==200){
                    try{
                        fab.setVisibility(View.VISIBLE);
                        mEmptyStateView.setVisibility(View.GONE);

                        // Get ether and btc objects from the response body
                        ether = response.body().getETH();
                        bitcoin = response.body().getBTC();

                        // Extract Exchange rates
                        extractExchangeRates(ether,bitcoin);

                    } catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CryptoCompare> call, Throwable t) {
                progressDialog.dismiss();
                mEmptyStateView.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Please check your internet connection and try again",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void extractExchangeRates(ETH ether, BTC bitcoin) {
        // Extract BTC exchange rates into an array
        BTCrates = new double[]{
                bitcoin.getNGN(), bitcoin.getBHD(), bitcoin.getBRL(),
                bitcoin.getGBP(), bitcoin.getCNY(), bitcoin.getEUR(),
                bitcoin.getCAD(), bitcoin.getCLP(), bitcoin.getINR(),
                bitcoin.getGLD(), bitcoin.getILS(), bitcoin.getJPY(),
                bitcoin.getJOD(), bitcoin.getKWD(), bitcoin.getKES(),
                bitcoin.getMXN(), bitcoin.getOMR(), bitcoin.getCHF(),
                bitcoin.getAED(), bitcoin.getUSD()
        };

        // Extract ETH exchange rates into an array
        ETHrates = new double[]{
                ether.getNGN(), ether.getBHD(), ether.getBRL(),
                ether.getGBP(), ether.getCNY(), ether.getEUR(),
                ether.getCAD(), ether.getCLP(), ether.getINR(),
                ether.getGLD(), ether.getILS(), ether.getJPY(),
                ether.getJOD(), ether.getKWD(), ether.getKES(),
                ether.getMXN(), ether.getOMR(), ether.getCHF(),
                ether.getAED(), ether.getUSD()
        };

        // Initialize the list of countries with Nigera
        CurrencyExchange NGNdata = new CurrencyExchange("Nigeria", "NGN",
                String.valueOf(bitcoin.getNGN()),
                flags[0],String.valueOf(ether.getNGN()));

        if(currencyExchangeList.size() == 1) {
            currencyExchangeList.remove(0);
            currencyExchangeList.add(NGNdata);
        } else if(currencyExchangeList.size() == 0) {
            currencyExchangeList.add(NGNdata);
        }

        mAdapter.notifyDataSetChanged();
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_refresh:
                fetchLatestExchangeRates();
                return true;
            case R.id.menu_about:
                final Dialog d = new Dialog(MainActivity.this);
                d.setContentView(R.layout.layout_about);
                d.setTitle("About");
                Button okayButton = (Button) d.findViewById(R.id.dialogButtonOK);
                okayButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                d.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    private class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

}
