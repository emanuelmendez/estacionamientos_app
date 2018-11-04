package gbem.com.ar.estacionamientos.dashboard;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

import gbem.com.ar.estacionamientos.R;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ReservationInMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 4314;
    private static final String TAG = ReservationInMapActivity.class.getSimpleName();
    private static final float DEFAULT_ZOOM = 15.0f;
    private ReservationDTO reservation;
    private LatLng reservationLocation;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted = false;
    private Location mLastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_in_map);

        reservation = (ReservationDTO) Objects.requireNonNull(getIntent().getExtras()).get("RESERVATION_LOCATION");

        getLocationPermission();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this::getDeviceLocation);

        final String[] coordinates = reservation.getParkingLot().getCoordinates().split(",");
        final double lat = Double.parseDouble(coordinates[0]);
        final double lng = Double.parseDouble(coordinates[1]);

        reservationLocation = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(reservationLocation).title(reservation.getLenderName()).visible(true));
        mMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(reservationLocation, DEFAULT_ZOOM)));

        updateLocationUI();
        getDeviceLocation();
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private boolean getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();

                        LatLngBounds bounds = new LatLngBounds(
                                reservationLocation,
                                new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                        mMap.setMyLocationEnabled(true);
                        mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(reservationLocation, DEFAULT_ZOOM));
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return true;
    }

}
