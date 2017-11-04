package com.bethel.eyo.kryptobash.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apptakk.http_request.HttpRequest;
import com.apptakk.http_request.HttpRequestTask;
import com.apptakk.http_request.HttpResponse;
import com.bethel.eyo.kryptobash.R;
import com.bethel.eyo.kryptobash.utilities.Keys;

public class ConversionScreenActivity extends AppCompatActivity {
    Button doneBtn;
    EditText quoteCurr, baseCurr;
    int CRYPTO_INDEX =0;
    ImageView cryptoIcon;
    LinearLayout conversionLayout;
    Resources res;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String CRYPTO_TYPE= "BTC";
    String[] currencyArray;
    String BASE_CURRENCY = "BTC";
    String QUOTE_CURRENCY = "USD";
    String CURRENCY_SYMBOL = "";
    String CRYPTO_URL;
    TextView crtCrdHint;
    TextView tryAgain;
    View emptyScreen;
    Toolbar toolbar;
    private final String LOG_TAG = ConversionScreenActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion_screen);

        quoteCurr = (EditText) findViewById(R.id.quote_curr);
        baseCurr = (EditText) findViewById(R.id.base_curr);
        doneBtn = (Button) findViewById(R.id.done_btn);
        emptyScreen = findViewById(R.id.error_screen);
        conversionLayout = (LinearLayout) findViewById(R.id.conversion_lay);
        crtCrdHint = (TextView) findViewById(R.id.crt_crd_hint);
        tryAgain = (TextView) findViewById(R.id.try_again);
        cryptoIcon = (ImageView) findViewById(R.id.pink_btc);

        // to set up the toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_conv);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Perform Conversion");
        toolbar.setTitleTextColor(getResources().getColor(R.color.icons));

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        Bundle extras = getIntent().getExtras();

        res = getResources();
        currencyArray = res.getStringArray(R.array.currency_array);

        if (extras != null) {
            CRYPTO_INDEX = extras.getInt("currency_position");
            if (Integer.parseInt(preferences.getString("list_" + CRYPTO_INDEX, null).split("#")[2]) == (R.drawable.ic_btc_white)){
                CRYPTO_TYPE = "BTC";
                baseCurr.setHint("1 " + CRYPTO_TYPE);
            }else if (Integer.parseInt(preferences.getString("list_" + CRYPTO_INDEX, null).split("#")[2]) == (R.drawable.ic_ehtereum_white)){
                CRYPTO_TYPE = "ETH";
                baseCurr.setHint("1 " + CRYPTO_TYPE);
            }
            BASE_CURRENCY = CRYPTO_TYPE;
            CURRENCY_SYMBOL = preferences.getString("list_" + CRYPTO_INDEX, null).split("#")[1];

            for (int i = 0; i < currencyArray.length; i++) {
                if (currencyArray[i].split(",")[1].contains(CURRENCY_SYMBOL)) {
                    QUOTE_CURRENCY = currencyArray[i].split(",")[0];
                }
            }
            CRYPTO_URL = Keys.URLS.CRYPTO_URL + BASE_CURRENCY + "&tsyms=" + QUOTE_CURRENCY;

            new HttpRequestTask(
                    new HttpRequest(CRYPTO_URL, HttpRequest.POST, "{ \"currency\": \"value\" }"),
                    new HttpRequest.Handler() {
                        @Override
                        public void response(HttpResponse response) {
                            if (response.code == 200) {
                                String feedback = response.body.replaceAll("\"", "")
                                        .replace("{", "").replace("}", "").split(":")[1];
                                if (baseCurr.getText().length() == 0) {
                                    quoteCurr.setText(CURRENCY_SYMBOL + " " + feedback);
                                } else if (baseCurr.getText().length() > 0) {
                                    float rawFeedback = Float.parseFloat(String.valueOf(feedback));
                                    float refinedFeedback = rawFeedback * Float.parseFloat(String.valueOf(baseCurr.getText()));

                                    quoteCurr.setText(CURRENCY_SYMBOL + " " + refinedFeedback);
                                }
                            } else {
                                conversionLayout.setVisibility(View.INVISIBLE);
                                crtCrdHint.setVisibility(View.INVISIBLE);
                                emptyScreen.setVisibility(View.VISIBLE);
                                Log.e(LOG_TAG, "Conversion Request wasn't successful: " + response);
                            }
                        }
                    }).execute();

        }
        if (CRYPTO_TYPE.contentEquals("BTC")){
            cryptoIcon.setImageResource(R.drawable.ic_pink_btc);
        }else if (CRYPTO_TYPE.contentEquals("ETH")){
            cryptoIcon.setImageResource(R.drawable.ic_ethereum_black);
        }

        baseCurr.addTextChangedListener(editTextWatcher);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emptyScreen.setVisibility(View.INVISIBLE);
                conversionLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private TextWatcher editTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            new HttpRequestTask(
                    new HttpRequest(CRYPTO_URL, HttpRequest.POST, "{ \"currency\": \"value\" }"),
                    new HttpRequest.Handler() {
                        @Override
                        public void response(HttpResponse response) {
                            if (response.code == 200) {
                                String feedback = response.body.replaceAll("\"", "")
                                        .replace("{", "").replace("}", "").split(":")[1];
                                if (baseCurr.getText().length() == 0) {
                                    quoteCurr.setText(CURRENCY_SYMBOL + " " + feedback);
                                } else if (baseCurr.getText().length() > 0) {
                                    float rawFeedback = Float.parseFloat(String.valueOf(feedback));
                                    float refinedFeedback = rawFeedback * Float.parseFloat(String.valueOf(baseCurr.getText()));

                                    quoteCurr.setText(CURRENCY_SYMBOL + " " + refinedFeedback);
                                }
                            } else {
                                conversionLayout.setVisibility(View.INVISIBLE);
                                crtCrdHint.setVisibility(View.INVISIBLE);
                                emptyScreen.setVisibility(View.VISIBLE);
                                Log.e(LOG_TAG, "Conversion Request wasn't successful: " + response);
                            }
                        }
                    }).execute();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id ){
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                super.onOptionsItemSelected(item);
        }

        return  true;
    }
}
