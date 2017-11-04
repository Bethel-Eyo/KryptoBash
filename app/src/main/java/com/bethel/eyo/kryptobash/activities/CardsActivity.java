package com.bethel.eyo.kryptobash.activities;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.bethel.eyo.kryptobash.R;
import com.bethel.eyo.kryptobash.fragments.cardsFragment;
import com.bethel.eyo.kryptobash.utilities.Keys;

public class CardsActivity extends AppCompatActivity {
    String FRAG_TAG;
    Fragment mFragment;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        Bundle bundle = getIntent().getExtras();

        toolbar = (Toolbar) findViewById(R.id.toolbar_cards);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Your cards");
        toolbar.setTitleTextColor(getResources().getColor(R.color.icons));

        FRAG_TAG = Keys.TAGs.CARD_FRAG_TAG;
        mFragment = new cardsFragment();
        mFragment.setArguments(bundle);
        setFragment(mFragment);
    }

    public void setFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment, FRAG_TAG).commit();
    }


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
