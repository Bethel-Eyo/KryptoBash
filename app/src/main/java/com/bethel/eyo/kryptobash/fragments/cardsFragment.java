package com.bethel.eyo.kryptobash.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.bethel.eyo.kryptobash.data.Currency;
import com.bethel.eyo.kryptobash.adapters.CurrencyListAdapter;
import com.bethel.eyo.kryptobash.R;
import com.bethel.eyo.kryptobash.activities.CardsActivity;
import com.bethel.eyo.kryptobash.activities.MainActivity;

import static android.support.v7.widget.RecyclerView.*;

public class cardsFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    CurrencyListAdapter adapter;
    SharedPreferences sharedPreferences;
    Bundle mBundle;
    View emptyScreen;
    TextView tryAgain;
    private RecyclerView recycleCardView;
    private List<Currency> currencyList;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cards_frag, container, false);
        recycleCardView = view.findViewById(R.id.recycler_card_view);
        emptyScreen = view.findViewById(R.id.error_screen);
        tryAgain = view.findViewById(R.id.try_again);
        currencyList = new ArrayList<>();
        adapter = new CurrencyListAdapter(getContext(), currencyList);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        loadList();

        //Implement a listener to monitor changes in shared preferences
        //sharedPreferences.registerOnSharedPreferenceChangeListener(this);


        //To check if any card has been created has been created before
        if (sharedPreferences.getAll().isEmpty() || sharedPreferences.getInt("list_size", 0)==0){
            emptyScreen.setVisibility(VISIBLE);

            tryAgain.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    CardsActivity cardsActivity = new CardsActivity();
                    cardsActivity.finish();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    getContext().startActivity(intent);
                }
            });
        }

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recycleCardView.setLayoutManager(layoutManager);
        recycleCardView.addItemDecoration(new cardItemDecoration(2, convertDpToPx(10), true));
        recycleCardView.setItemAnimator(new DefaultItemAnimator());
        recycleCardView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null){
            mBundle = savedInstanceState;
        }else {

        }
    }

    public void loadList(){
        //to load items from sharedPreference to currencyList
        currencyList.clear();
        int size = sharedPreferences.getInt("list_size", 0);
        for (int i = 0; i<size; i++){
            if (!sharedPreferences.getString("list_"+i, null).equals(null)){
                Currency newCurr = new Currency(sharedPreferences.getString("list_"+i,null).split("#")[0],
                        sharedPreferences.getString("list_"+i, null).split("#")[1],
                        Integer.parseInt(sharedPreferences.getString("list_"+i,null).split("#")[2]));
                currencyList.add(newCurr);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        loadList();
        ((MainActivity)getActivity()).saveList();
        adapter.notifyDataSetChanged();
    }


    public class cardItemDecoration extends ItemDecoration {
        private int area, spacing;
        private boolean containBorder;

        public cardItemDecoration(int area, int spacing, boolean containBorder) {
            this.area = area;
            this.spacing = spacing;
            this.containBorder = containBorder;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            int adapterPosition = parent.getChildAdapterPosition(view);
            int lineIndex = adapterPosition % area;

            if (containBorder) {
                outRect.left = spacing - lineIndex * spacing / area;
                outRect.right = (lineIndex + 1) * spacing / area;

                if (adapterPosition < area) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            }else {
                outRect.left = lineIndex * spacing / area;
                outRect.right = spacing - (lineIndex + 1) * spacing / area;
                if (adapterPosition >= area){
                    outRect.top = spacing;
                }
            }
        }
    }

    private int convertDpToPx(int dp) {
        Resources res = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics()));
    }
}
