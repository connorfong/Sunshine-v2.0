package com.connorfong.sunshine3;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.connorfong.sunshine3.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    public static final String DETAIL_URI = "URI";

    private static final String FORECAST_SHARE_HASHTAG = " #ProjectSunshine";

    private ShareActionProvider mShareActionProvider;
    private String mForecast;
    private Uri mUri;

    private static final int DETAIL_LOADER = 0;

    private final String[] DETAIL_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    // corresponding content columns
    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WEATHER_PRESSURE = 6;
    private static final int COL_WEATHER_WIND_SPEED = 7;
    private static final int COL_WEATHER_DEGREES = 8;
    private static final int COL_WEATHER_CONDITION_ID = 9;

    // View holders
    ImageView mIconView;
    TextView mDateView;
    TextView mFriendlyDateView;
    TextView mDescriptionView;
    TextView mHighTempView;
    TextView mLowTempView;
    TextView mHumidityView;
    TextView mPressureView;
    TextView mWindView;

    private Compass mCompass;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mIconView = (ImageView) rootView.findViewById(R.id.detail_icon);
        mDateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        mFriendlyDateView = (TextView) rootView.findViewById(R.id.detail_day_textview);
        mDescriptionView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
        mHighTempView = (TextView) rootView.findViewById(R.id.detail_high_textview);
        mLowTempView = (TextView) rootView.findViewById(R.id.detail_low_textview);
        mHumidityView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        mPressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);
        mWindView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        mCompass = (Compass) rootView.findViewById(R.id.detail_compassview);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate menu, add items to action bar if present
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hod it to set/change share intent
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an intent to ShareActionProvider.
        if (mForecast != null) {
            //mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            // Create and return a CursorLoader for the data to display
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            // Read weather condition id from cursor
            int weatherId = data.getInt(COL_WEATHER_CONDITION_ID);
            // Use placeholder image
            mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

            // Read date from cursor and update views
            long date = data.getLong(COL_WEATHER_DATE);
            String friendlyDate = Utility.getDayName(getActivity(), date);
            String dateText = Utility.getFormattedMonthDay(getActivity(), date);
            mFriendlyDateView.setText(friendlyDate);
            mDateView.setText(dateText);

            // Read and set description
            String description = data.getString(COL_WEATHER_DESC);
            mDescriptionView.setText(description);
            mIconView.setContentDescription(description);

            boolean isMetric = Utility.isMetric(getActivity());

            // Set high and low
            double high = data.getDouble(COL_WEATHER_MAX_TEMP);
            double low = data.getDouble(COL_WEATHER_MIN_TEMP);
            String highString = Utility.formatTemperature(getActivity(), high, isMetric);
            String lowString = Utility.formatTemperature(getActivity(), low, isMetric);
            mHighTempView.setText(highString);
            mLowTempView.setText(lowString);

            // Set humidity
            float humidity = data.getFloat(COL_WEATHER_HUMIDITY);
            mHumidityView.setText(getActivity().getString(R.string.format_humidity, humidity));

            // Set wind speed and compass
            float windSpeed = data.getFloat(COL_WEATHER_WIND_SPEED);
            float windDir = data.getFloat(COL_WEATHER_DEGREES);
            mWindView.setText(Utility.getFormattedWind(getActivity(), windSpeed, windDir));
            mCompass.update(windDir);



            // Set pressure
            float pressure = data.getFloat(COL_WEATHER_PRESSURE);
            mPressureView.setText(getActivity().getString(R.string.format_pressure, pressure));

            // Share Intent
            mForecast = String.format("%s - %s - %s/%s", dateText, description, high, low);

            if (mShareActionProvider != null) {
                //mShareActionProvider.setShareIntent(createShareForecastIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void onLocationChanged(String newLocation) {
        // Replace Uri
        Uri uri = mUri;
        if (uri != null) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }
}
