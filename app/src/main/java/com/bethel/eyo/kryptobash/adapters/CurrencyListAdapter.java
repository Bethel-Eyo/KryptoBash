package com.bethel.eyo.kryptobash.adapters;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apptakk.http_request.HttpRequest;
import com.apptakk.http_request.HttpRequestTask;
import com.apptakk.http_request.HttpResponse;
import com.bethel.eyo.kryptobash.R;
import com.bethel.eyo.kryptobash.activities.CardsActivity;
import com.bethel.eyo.kryptobash.activities.ConversionScreenActivity;
import com.bethel.eyo.kryptobash.data.Currency;
import com.bethel.eyo.kryptobash.utilities.Keys;
import java.util.ArrayList;
import java.util.List;

public class CurrencyListAdapter extends RecyclerView.Adapter<CurrencyListAdapter.currencyViewHolder> {

    private Context mContext;
    private List<Currency> currencyList = new ArrayList<>();
    private Currency mCurrency;
    private int card_index;
    public int itemPosition;
    private LayoutInflater inflater;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Resources resources;
    private Handler handler = new Handler();
    private final String LOG_TAG = CurrencyListAdapter.class.getSimpleName();

    public CurrencyListAdapter(Context mContext, List<Currency> currencyList) {
        this.mContext = mContext;
        this.currencyList = currencyList;
    }

    @Override
    public currencyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_card_view, parent, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = sharedPreferences.edit();
        resources = mContext.getResources();
        return new currencyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final currencyViewHolder holder, final int position) {
        mCurrency = currencyList.get(position);
        //initialize 0.00 text with currency_value for easy identification
        holder.currencyValue.setText(mCurrency.getName()+ " 0.00");
        card_index = 0;
        Runnable runner = new Runnable() {
            @Override
            public void run() {
                if (card_index != currencyList.size()) {
                    // setting a limit to requests made
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCurrency = currencyList.get(position);
                            String SELECT_BASE_CURR = "";
                            if (mCurrency.getThumbnail() == R.drawable.ic_btc_white) {
                                SELECT_BASE_CURR = Keys.URLS.BTC_URL;
                            } else {

                                SELECT_BASE_CURR = Keys.URLS.ETH_URL;
                            }
                            new HttpRequestTask(
                                    new HttpRequest(SELECT_BASE_CURR + mCurrency.getName(), HttpRequest.POST,
                                            "{ \"currency\": \"value\" }"), new HttpRequest.Handler() {
                                @Override
                                public void response(HttpResponse response) {
                                    if (response.code == 200) {
                                        String name = response.body.replaceAll("\"", "")
                                                .replace("{", "").replace("}", "").split(":")[0];
                                        String value = response.body.replaceAll("\"", "")
                                                .replace("{", "").replace("}", "").split(":")[1];
                                        holder.currencyValue.setText(name + " " + value);
                                        card_index++;
                                    } else {
                                        Log.e(LOG_TAG, "Request wasn't successful: " + response);
                                        Toast.makeText(mContext, "check connection settings", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).execute();

                        }

                    });
                }
            }
        };

        new Thread(runner).start();

        //To set the appropriate drawable for each card
        if (mCurrency.getThumbnail() == R.drawable.ic_btc_white){
            holder.coinIcon.setImageResource(R.drawable.ic_pink_btc);
        }else{
            holder.coinIcon.setImageResource(R.drawable.ic_ethereum_black);
        }


        //to set a clicklistener on each item to take u to the conversion screen
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ConversionScreenActivity.class);
                intent.putExtra("currency_position",position);
                mContext.startActivity(intent);
            }
        });

        // to set a clicklistener to the dropdownmenu icon and to show dropdownlist
        holder.dropDownMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDropDownList(holder.dropDownMenu);
                // get particular item position
                itemPosition = position;
            }
        });
    }

    public void showDropDownList(View view){
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.currency_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new CardMenuItemClickListener());
        popupMenu.show();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return currencyList.size();
    }

    public static class currencyViewHolder extends RecyclerView.ViewHolder {
        ImageView coinIcon, dropDownMenu;
        TextView currencyValue;

        public currencyViewHolder(View itemView) {
            super(itemView);
            coinIcon = itemView.findViewById(R.id.coin_icon);
            dropDownMenu = itemView.findViewById(R.id.drop_down_icon);
            currencyValue = itemView.findViewById(R.id.currency_value);
        }
    }

    class CardMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.ac_conversion:
                    Intent intent = new Intent(mContext, ConversionScreenActivity.class);
                    intent.putExtra("currency_postion", itemPosition);
                    mContext.startActivity(intent);
                    final CardsActivity cardsActivity = new CardsActivity();
                    cardsActivity.finish();
                    return true;

                case R.id.ac_delete:
                    // to delete that particular card and refresh the list
                    int itemAmount = currencyList.size()-1;
                    currencyList.remove(itemPosition);

                    for (int i = 0; i < currencyList.size(); i++){
                        editor.remove("list_" +i);
                        editor.putString("list_" +i, currencyList.get(i).getName()+ "#" + currencyList
                                .get(i).getSign()+ "#" + currencyList.get(i).getThumbnail());
                    }
                    editor.putInt("list_size", itemAmount);
                    editor.commit();
                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setMessage("You have Successfully deleted a card");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    new CardsActivity().finish();
                                    Intent i = new Intent(mContext, CardsActivity.class);
                                    mContext.startActivity(i);
                                }
                            });
                    alertDialog.show();

                    return true;
                default:
            }
            return false;
        }
    }

}
