package fyp.theanh.snakealertapplication.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fyp.theanh.snakealertapplication.fragments.FragmentMain;
import fyp.theanh.snakealertapplication.tasks.GetDataTask;
import fyp.theanh.snakealertapplication.R;
import fyp.theanh.snakealertapplication.models.Snake;

public class MapsActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MapFragment())
                    .commit();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #//setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */


    public static class MapFragment extends Fragment implements OnMapReadyCallback {
        private Snake mSnake;
        private GoogleMap mMap; // Might be null if Google Play services APK is not available.

        public MapFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            if (FragmentMain.snakeList.size() == 0) {
                getData();
            }
        }

        private void getData() {
            GetDataTask dataTask = new GetDataTask(this.getActivity(),
                    FragmentMain.mSnakeAdapter);
            dataTask.execute();
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.menu_detail, menu);
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            //Put map initiation here
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Intent intent = getActivity().getIntent();
            View rootView = inflater.inflate(R.layout.fragment_map, container, false);

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        private Intent createShareForecastIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            shareIntent.setType("text/plain");
            return shareIntent;
        }

        @Override
        public void onMapReady(GoogleMap map) {
            List<LatLng> listOfSnakeLocations = new ArrayList<LatLng>();
            while(FragmentMain.snakeList.size()==0){
                getData();
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    //Handle exception
                }
            }
            for (int i = 0; i < FragmentMain.snakeList.size(); i++) {
                listOfSnakeLocations.add(new LatLng(FragmentMain.snakeList.get(i).getLat(),
                        FragmentMain.snakeList.get(i).getLng()));
            }
            for (int i = 0; i < FragmentMain.snakeList.size(); i++) {
                map.addMarker(new MarkerOptions().position(listOfSnakeLocations.get(i)).title
                        (separateName(FragmentMain.snakeList.get(i).getName())));
            }
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(listOfSnakeLocations.get(0), 12));
        }

        private String separateName(String name){
            String nameSeparated = name.replaceAll("(\\p{Ll})(\\p{Lu})","$1 $2");
            return nameSeparated;
        }
    }
}
