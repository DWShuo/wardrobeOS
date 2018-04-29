package com.dws.wardrobeos.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.dws.GPSTracker;
import com.dws.wardrobeos.R;
import com.dws.wardrobeos.api.GlobalBus;
import com.dws.wardrobeos.fragments.MainFragment;
import com.dws.wardrobeos.fragments.SuggestionFragment;
import com.dws.wardrobeos.views.CustomViewPager;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @BindView(R.id.pager)
    CustomViewPager pager;

    ClothPagerAdapter mAdapter;

    private static int REQUEST_PERMISSION = 1883;
    private LocationManager mLocationManager;
    private static int LOCATION_REFRESH_TIME = 1;
    private static int LOCATION_REFRESH_DISTANCE = 1;

    private GPSTracker mGpsTracker;
    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        GlobalBus.getBus().register(this);
        setSupportActionBar(toolbar);
        tabs.setVisibility(View.INVISIBLE);
        pager.setPagingEnabled(false);
        String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (!checkPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION);
        } else {
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (mLocation != null) {
                mAdapter = new ClothPagerAdapter(getSupportFragmentManager());
                pager.setAdapter(mAdapter);
                tabs.setViewPager(pager);
                tabs.setVisibility(View.VISIBLE);
            } else {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, mLocationListener);
            }
        }

        tabs.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int position) {

            }
        });

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    GlobalBus.getBus().post("update");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private boolean checkPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission:permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public class ClothPagerAdapter extends FragmentPagerAdapter {

        final String[] TITLES = {"Wardrobe", "Suggestion"};

        ClothPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return MainFragment.newInstance(position);
            } else {
                return SuggestionFragment.newInstance(position);
            }
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (mLocation != null) {
                    ClothPagerAdapter mAdapter = new ClothPagerAdapter(getSupportFragmentManager());
                    pager.setAdapter(mAdapter);
                    tabs.setViewPager(pager);
                    tabs.setVisibility(View.VISIBLE);
                } else {
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, mLocationListener);
                }

            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mGpsTracker = new GPSTracker(getApplicationContext());
            if (mGpsTracker.canGetLocation()) {
                mLocation = mGpsTracker.getLocation();
                ClothPagerAdapter mAdapter = new ClothPagerAdapter(getSupportFragmentManager());
                pager.setAdapter(mAdapter);
                tabs.setViewPager(pager);
                tabs.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public Location getCurrentLocation() {
        return mLocation;
    }

    @Override
    protected void onStop() {
        super.onStop();
        GlobalBus.getBus().unregister(this);
    }

    @Subscribe
    public void getMessage(String message) {

    }
}
