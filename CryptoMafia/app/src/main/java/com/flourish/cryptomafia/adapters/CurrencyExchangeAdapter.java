package com.flourish.cryptomafia.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flourish.cryptomafia.R;
import com.flourish.cryptomafia.activities.ConversionActivity;
import com.flourish.cryptomafia.models.CurrencyExchange;

import java.util.List;

public class CurrencyExchangeAdapter extends RecyclerView.Adapter<CurrencyExchangeAdapter.MyViewHolder> {

    private Context mContext;
    private List<CurrencyExchange> currencyExchangeList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView country, btcPrice, ethPrice;
        ImageView countryFlag, overflow;

        public MyViewHolder(View view) {
            super(view);
            country = view.findViewById(R.id.title);
            btcPrice = view.findViewById(R.id.tv_btcPrice);
            ethPrice = view.findViewById(R.id.tv_ethPrice);
            countryFlag = view.findViewById(R.id.iv_flag);
            overflow = view.findViewById(R.id.overflow);
        }
    }

    public CurrencyExchangeAdapter(Context mContext, List<CurrencyExchange> currencyExchangeList) {
        this.mContext = mContext;
        this.currencyExchangeList = currencyExchangeList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.currency_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final CurrencyExchange currencyExchange = currencyExchangeList.get(position);
        holder.country.setText(currencyExchange.getCurrencyName());
        holder.btcPrice.setText(currencyExchange.getBitcoinPrice());
        holder.ethPrice.setText(currencyExchange.getEtherPrice());

        // loading currencyExchange cover using Glide library
        Glide.with(mContext).load(currencyExchange.getThumbnail()).into(holder.countryFlag);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendIntentForConversion(currencyExchange);
            }
        });

        holder.countryFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendIntentForConversion(currencyExchange);
            }
        });
    }

    private void sendIntentForConversion(CurrencyExchange currencyExchange) {
        Intent conversionActivity = new Intent(mContext, ConversionActivity.class);
        conversionActivity.putExtra("btcRate",currencyExchange.getBitcoinPrice());
        conversionActivity.putExtra("ethRate",currencyExchange.getEtherPrice());
        conversionActivity.putExtra("currencyName",currencyExchange.getCurrencyName());
        conversionActivity.putExtra("countryFlag",currencyExchange.getThumbnail());
        conversionActivity.putExtra("shortCode",currencyExchange.getCurrencyShortCode());
        mContext.startActivity(conversionActivity);
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.card_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_delete_card:
                    showDeleteConfirmationDialog();
                    return true;
                default:
            }
            return false;
        }

        private void showDeleteConfirmationDialog() {
            // Create an AlertDialog.Builder and set the message, and clickListeners
            // for the positive and negative buttons on the dialog.
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("Delete this country?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(mContext, "Country will be deleted", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // User clicked the "Cancel" button, so dismiss the dialog
                    if (dialog == null) {
                        dialog.dismiss();
                    }
                }
            });

            // Create and show the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }
    }

    @Override
    public int getItemCount() {
        return currencyExchangeList.size();
    }
}
