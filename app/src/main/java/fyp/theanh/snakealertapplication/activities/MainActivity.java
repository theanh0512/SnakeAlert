package fyp.theanh.snakealertapplication.activities;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

import fyp.theanh.snakealertapplication.fragments.FragmentMain;
import fyp.theanh.snakealertapplication.tasks.GetDataTask;
import fyp.theanh.snakealertapplication.fragments.MainPageFragment;
import fyp.theanh.snakealertapplication.R;


public class MainActivity extends Activity implements MainPageFragment.OnFragmentInteractionListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;
    String mLastUpdateTime;
    int ALERT_DISTANCE = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MainPageFragment())
                    .commit();
        }

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    public void searchSnakeArea() {
        if (mCurrentLocation != null) {
            for (int i = 0; i < FragmentMain.snakeList.size(); i++) {
                if (distanceFrom(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(),
                        FragmentMain.snakeList.get(i).getLat(),
                        FragmentMain.snakeList.get(i).getLng()) <= ALERT_DISTANCE) {
                    sendNotification();
                }
            }
        } else {
            Toast toast = Toast.makeText(this, "Please enable GPS to search near by snakes",
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        GetDataTask dataTask = new GetDataTask(this, FragmentMain.mSnakeAdapter,FragmentMain.mAdapter);
        dataTask.execute();
        GetDataTask dataTaskForSnakeKind = new GetDataTask(this,
                "http://www.snakealertapp.com/query_kind_table.php");
        dataTaskForSnakeKind.execute();
        super.onStart();
    }

    private void sendNotification() {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        CharSequence tickerText = "Snake Alert";
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(contentIntent)
                .setLights(0xff00ff00, 300, 1000)
                .setTicker(tickerText)
                .setSmallIcon(R.drawable.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Snake Alert")
                .setContentText("You are near snake area");
        Notification n = builder.build();
        n.defaults |= Notification.DEFAULT_VIBRATE;
        n.defaults |= Notification.DEFAULT_SOUND;

        mNotificationManager.notify(1, n);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //callback provided by GGAppClient
        createLocationRequest();
        startLocationUpdates();
        searchSnakeArea();
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(200000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        //updateUI();
        searchSnakeArea();
    }

//    private void updateUI() {
//        mLatitudeTextView.setText(String.valueOf(mCurrentLocation.getLatitude()));
//        mLongitudeTextView.setText(String.valueOf(mCurrentLocation.getLongitude()));
//        mLastUpdateTimeTextView.setText(mLastUpdateTime);
//    }

    public double distanceFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        return dist;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
