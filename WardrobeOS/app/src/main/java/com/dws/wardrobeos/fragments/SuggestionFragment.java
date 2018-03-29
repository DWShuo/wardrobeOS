package com.dws.wardrobeos.fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.dws.wardrobeos.Constants;
import com.dws.wardrobeos.R;
import com.dws.wardrobeos.activities.MainActivity;
import com.dws.wardrobeos.activities.NewActivity;
import com.dws.wardrobeos.adapters.SuggestAdapter;
import com.dws.wardrobeos.models.ClothItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

public class SuggestionFragment extends Fragment {

    private static String TAG = "SuggestionFragment";

    private static final String ARG_POSITION = "position";
    private int position;
    private Location mLocation;

    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.btn_add) FloatingActionButton addBtn;
    @BindView(R.id.icon_weather) AppCompatImageView weatherIcon;
    @BindView(R.id.weather_description) AppCompatTextView weatherDesc;
    @BindView(R.id.weather_humidity) AppCompatTextView weatherHumidity;
    @BindView(R.id.weather_pressure) AppCompatTextView weatherPressure;
    @BindView(R.id.weather_temp) AppCompatTextView weatherTemp;
    @BindView(R.id.not_found_suggestion) AppCompatTextView notFound;

    @BindView(R.id.view_clothes) RecyclerView recyclerView;
    SuggestAdapter mAdapter;

    private List<ClothItem> mClothes;

    public SuggestionFragment() {

    }

    public static SuggestionFragment newInstance(int position) {
        SuggestionFragment fragment = new SuggestionFragment();

        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_suggestion, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mProgressBar.setVisibility(View.INVISIBLE);

        if (((MainActivity)getActivity()) != null) {
            mLocation = ((MainActivity)getActivity()).getCurrentLocation();
            getWeatherAndSuggestion(mLocation);
            getSuggestion();
        }
        getSuggestion();
    }

    private void getSuggestion() {
        mClothes = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ClothItem> results = realm.where(ClothItem.class).findAll();
        if (results.size() > 0) {

            notFound.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            mClothes.addAll(results);

            mAdapter = new SuggestAdapter(getActivity(), mClothes);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
        } else {
            notFound.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Test");
    }

    @OnClick(R.id.btn_add) void addClicked() {
        Intent intent = new Intent(getActivity(), NewActivity.class);
        startActivity(intent);
    }

    private void getWeatherAndSuggestion(Location location) {

        mProgressBar.setVisibility(View.VISIBLE);
        String url = "https://api.openweathermap.org/data/2.5/weather?";
        url = url + "lat=" + String.valueOf(location.getLatitude()) + "&lon=" + String.valueOf(location.getLongitude()) + "&appid=" + Constants.API_KEY;
        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        try {
                            JSONArray weatherObjArr = response.getJSONArray("weather");
                            if (weatherObjArr.length() > 0) {
                                JSONObject weatherObj = weatherObjArr.getJSONObject(0);
                                weatherDesc.setText(weatherObj.getString("description"));
                                JSONObject sysObj = response.getJSONObject("sys");
                                setWeatherIcon(weatherObj.getInt("id"), sysObj.getLong("sunrise") *1000, sysObj.getLong("sunset") * 1000);
                                if (response.has("main")) {
                                    JSONObject mainObj = response.getJSONObject("main");
                                    weatherHumidity.setText(String.format("Humidity: %s%%", String.valueOf(mainObj.getInt("humidity"))));
                                    weatherPressure.setText(String.format("Pressure: %shPa", String.valueOf(mainObj.getDouble("pressure"))));
                                    weatherTemp.setText(String.format("%s`C", String.format("%.2f", mainObj.getDouble("temp")/100)));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Log.d(TAG, anError.getErrorDetail());
                    }
                });
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >=sunrise && currentTime < sunset) {
                weatherIcon.setImageResource(R.drawable.weather_sunny);
            } else {
                weatherIcon.setImageResource(R.drawable.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2: weatherIcon.setImageResource(R.drawable.weather_thunder);
                break;
                case 3: weatherIcon.setImageResource(R.drawable.weather_drizzle);
                break;
                case 7: weatherIcon.setImageResource(R.drawable.weather_foggy);
                break;
                case 8: weatherIcon.setImageResource(R.drawable.weather_cloudy);
                break;
                case 6: weatherIcon.setImageResource(R.drawable.weather_snowy);
                break;
                case 5: weatherIcon.setImageResource(R.drawable.weather_rainy);
                break;
            }
        }
    }
}
