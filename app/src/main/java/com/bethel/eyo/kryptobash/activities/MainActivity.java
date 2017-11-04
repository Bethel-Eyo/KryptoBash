package com.bethel.eyo.kryptobash.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.bethel.eyo.kryptobash.dialogs.BtcCardSuccessDialog;
import com.bethel.eyo.kryptobash.data.Currency;
import com.bethel.eyo.kryptobash.adapters.CurrencyListAdapter;
import com.bethel.eyo.kryptobash.dialogs.EthCardSuccessDialog;
import com.bethel.eyo.kryptobash.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Spinner cur_spinner;
    RadioButton radioBtnBtc, radioBtnEth;
    Button createCardBtn, viewCardsBtn;
    private List<Currency> currencyList;
    private RecyclerView recycleCardView;
    CurrencyListAdapter adapter;
    ImageView icCoin;
    SharedPreferences sharedPreferences = null;
    SharedPreferences.Editor editor = null;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize the radio buttons
        radioBtnBtc = (RadioButton) findViewById(R.id.radio_btn_btc);
        radioBtnEth = (RadioButton) findViewById(R.id.radio_btn_eth);

        //initialize buttons
        createCardBtn = (Button) findViewById(R.id.create_card_btn);
        viewCardsBtn = (Button) findViewById(R.id.view_cards_btn);

        recycleCardView = (RecyclerView) findViewById(R.id.recycler_card_view);

        icCoin = (ImageView) findViewById(R.id.ic_coin);

        currencyList = new ArrayList<>();
        adapter = new CurrencyListAdapter(this, currencyList);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();


        //initialize the spinner
        cur_spinner = (Spinner) findViewById(R.id.crypto_drop);
        // create an adapter for the spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.currency_array, android.R.layout.simple_spinner_item);
        // specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cur_spinner.setAdapter(spinnerAdapter);


        radioBtnBtc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radioBtnBtc.isChecked()){
                    icCoin.setImageResource(R.drawable.ic_btc_white);
                }
            }
        });

        radioBtnEth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radioBtnEth.isChecked()){
                    icCoin.setImageResource(R.drawable.ic_ehtereum_white);
                }
            }
        });


        createCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //To get the name and sign of the selected currency
                String name = cur_spinner.getSelectedItem().toString().split(",")[0].replace(" ", "");
                String currencySymbol = cur_spinner.getSelectedItem().toString().split(",")[1].replace(" ", "");
                if (radioBtnBtc.isChecked()){
                    Toast.makeText(getApplicationContext(), "BTC radioButton is checked", Toast.LENGTH_SHORT).show();
                    if (!ItemExists(name, R.drawable.ic_btc_white)){
                        createCard(radioBtnBtc.getText().toString(), name, currencySymbol);
                        BtcCardSuccessDialog btcDialog = new BtcCardSuccessDialog(MainActivity.this);
                        btcDialog.show();
                    }else {
                        Toast.makeText(getApplicationContext(), "This card already exists, Please view your cards!!",
                                Toast.LENGTH_LONG).show();
                    }
                }else if (radioBtnEth.isChecked()){
                    Toast.makeText(getApplicationContext(), "ETH radioButton is checked!!", Toast.LENGTH_SHORT).show();
                    if (!ItemExists(name, R.drawable.ic_ehtereum_white)){
                        createCard(radioBtnEth.getText().toString(), name, currencySymbol);
                        EthCardSuccessDialog ethDialog = new EthCardSuccessDialog(MainActivity.this);
                        ethDialog.show();
                    }else {
                        Toast.makeText(getApplicationContext(), "This card already exists, Please view your cards!!",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        viewCardsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CardsActivity.class);
                startActivity(intent);
            }
        });
    }


    //to save currencylist items
    public void saveList(){
        editor.putInt("list_size", currencyList.size());
        for (int i = 0; i < currencyList.size(); i++){
            editor.remove("list_" + i);
            editor.putString("list_" +i, currencyList.get(i).getName()+ "#" + currencyList
                    .get(i).getSign()+ "#" + currencyList.get(i).getThumbnail());
        }
        editor.commit();
    }

    public boolean ItemExists(String currencyValue, int thumbnail){
        // to avoid redundancy, we check if item exists already in the sharedpreference before saving
        boolean exists = false;
        int listSize = sharedPreferences.getInt("list_size", 0);
        for (int i = 0; i < listSize; i++){
            if (sharedPreferences.getString("list_"+i, null).split("#")[2].
                    contains(String.valueOf(thumbnail)) && sharedPreferences.getString("list_"+i, null)
                    .split("#")[0].contains(currencyValue)){
                exists = true;
            }
        }
        return exists;
    }

    private void createCard(String baseCurr, String countryCurrName, String currSign){
        int[] coinType = new int[]{
                R.drawable.ic_btc_white, R.drawable.ic_ehtereum_white };
        if (baseCurr.contains("BTC")){
            Currency currency = new Currency(countryCurrName, currSign, coinType[0]);
            currencyList.add(currency);
            saveList();
        }else if (baseCurr.contains("ETH")){
            Currency currency = new Currency(countryCurrName, currSign, coinType[1]);
            currencyList.add(currency);
            saveList();
        }
        adapter.notifyDataSetChanged();
    }

}
