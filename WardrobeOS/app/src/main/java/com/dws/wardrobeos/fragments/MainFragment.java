package com.dws.wardrobeos.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.dws.wardrobeos.R;
import com.dws.wardrobeos.adapters.ClothesAdapter;
import com.dws.wardrobeos.api.GlobalBus;
import com.dws.wardrobeos.models.ClothItem;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;


public class MainFragment extends Fragment implements ActionSheet.ActionSheetListener {

    private static final String ARG_POSITION = "position";
    private int position;

    private List<ClothItem> mClothes;

    @BindView(R.id.items_view) RecyclerView recyclerView;
    @BindView(R.id.not_found) LinearLayout notFound;

    private int selectedIndex = -1;

    public MainFragment() {

    }

    public static MainFragment newInstance(int position) {
        MainFragment fragment = new MainFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalBus.getBus().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        recyclerView.setVisibility(View.GONE);
        notFound.setVisibility(View.GONE);
        getAllClothes();

    }

    public void getAllClothes() {

       Realm realm = Realm.getDefaultInstance();
        RealmResults<ClothItem> results = realm.where(ClothItem.class).findAll();
        if (results.size() > 0) {
            notFound.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            mClothes = new ArrayList<>();
            for (int i=0;i<results.size();i++) {
                mClothes.add(results.get(i));
            }
            ClothesAdapter adapter = new ClothesAdapter(getActivity(), mClothes, new ClothesAdapter.ClickListener() {
                @Override
                public void longPressed(int position) {
                    selectedIndex = position;
                    ActionSheet.createBuilder(getActivity(), getFragmentManager())
                            .setCancelButtonTitle("Cancel")
                            .setOtherButtonTitles("Delete")
                            .setCancelableOnTouchOutside(true)
                            .setListener(MainFragment.this).show();
                }
            });
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.setVisibility(View.GONE);
            notFound.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Subscribe
    public void getMessage(String message) {

        Realm realm = Realm.getDefaultInstance();
        RealmResults<ClothItem> results = realm.where(ClothItem.class).findAll();
        if (results.size() > 0) {
            notFound.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            mClothes = new ArrayList<>();
            for (int i=0;i<results.size();i++) {
                mClothes.add(results.get(i));
            }

            Handler handler = new Handler();
            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    ClothesAdapter adapter = new ClothesAdapter(getActivity(), mClothes, new ClothesAdapter.ClickListener() {
                        @Override
                        public void longPressed(int position) {
                            selectedIndex = position;
                            ActionSheet.createBuilder(getActivity(), getFragmentManager())
                                    .setCancelButtonTitle("Cancel")
                                    .setOtherButtonTitles("Delete")
                                    .setCancelableOnTouchOutside(true)
                                    .setListener(MainFragment.this).show();
                        }
                    });
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                }
            };

            handler.postDelayed(r, 150);

        } else {
            notFound.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {

        if (index == 0) {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(Realm realm) {
                    ClothItem item = mClothes.get(selectedIndex);
                    RealmResults<ClothItem> rows = realm.where(ClothItem.class)
                            .equalTo("type", item.getType())
                            .equalTo("brand", item.getBrand())
                            .equalTo("color", item.getColor())
                            .equalTo("info", item.getInfo())
                            .equalTo("photo", item.getPhoto())
                            .findAll();

                    if (rows.size() == 1) {
                        rows.deleteAllFromRealm();
                    }

                    RealmResults<ClothItem> results = realm.where(ClothItem.class).findAll();
                    if (results.size() > 0) {
                        notFound.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        mClothes = new ArrayList<>();
                        for (int i=0;i<results.size();i++) {
                            mClothes.add(results.get(i));
                        }
                        ClothesAdapter adapter = new ClothesAdapter(getActivity(), mClothes, new ClothesAdapter.ClickListener() {
                            @Override
                            public void longPressed(int position) {
                                Toast.makeText(getActivity(), "long pressed", Toast.LENGTH_SHORT).show();
                            }
                        });
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        notFound.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }
}
