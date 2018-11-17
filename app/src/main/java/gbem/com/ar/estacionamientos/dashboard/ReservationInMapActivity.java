package gbem.com.ar.estacionamientos.dashboard;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.utils.Utils;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static gbem.com.ar.estacionamientos.utils.Utils.RESERVATION_LOCATION;

public class ReservationInMapActivity extends FragmentActivity {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 4314;
    private static final float DEFAULT_ZOOM = 16.0f;

    private ReservationDTO reservation;
    private LatLng reservationLocation;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted = false;
    private Location mLastKnownLocation;
    private boolean blockedPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_in_map);

        final Button btnCenterAtParkingLot = findViewById(R.id.btnCenterAtParkingLot);
        btnCenterAtParkingLot.setOnClickListener(view -> centerCameraAt(reservationLocation));

        reservation = (ReservationDTO) Objects.requireNonNull(getIntent().getExtras()).get(RESERVATION_LOCATION);

        getLocationPermission();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this::onMapReady);
    }

    public final void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this::getDeviceLocation);
        mMap.setOnInfoWindowClickListener(m -> onInfoWindowClick());

        reservationLocation = Utils.createLatLng(reservation.getParkingLot().getCoordinates());
        final MarkerOptions markerOptions = new MarkerOptions()
                .position(reservationLocation)
                .title(reservation.getLenderName())
                .visible(true)
                .snippet(reservation.getParkingLot().getStreetAddress());

        final Marker marker = mMap.addMarker(markerOptions);
        marker.showInfoWindow();

        centerCameraAt(reservationLocation);

        updateLocationUI();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.getApplicationContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else if (!blockedPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted =
                requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                        && grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        if (mLocationPermissionGranted)
            updateLocationUI();
        else
            blockedPermission = true;
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }

        mMap.setMyLocationEnabled(mLocationPermissionGranted);
        mMap.getUiSettings().setMyLocationButtonEnabled(mLocationPermissionGranted);

        if (!mLocationPermissionGranted) {
            mLastKnownLocation = null;
            getLocationPermission();
        }
    }

    private boolean getDeviceLocation() {
        if (mLocationPermissionGranted) {
            mFusedLocationProviderClient
                    .getLastLocation()
                    .addOnCompleteListener(this, this::onComplete);
        }
        return true;
    }

    private void centerCameraAt(final LatLng latlng) {
        mMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(latlng, DEFAULT_ZOOM)));
    }

    private void onComplete(Task<Location> task) {
        if (task.isSuccessful() && task.getResult() != null) {
            mLastKnownLocation = task.getResult();

            centerCameraAt(new LatLng(
                    mLastKnownLocation.getLatitude(),
                    mLastKnownLocation.getLongitude()));

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            centerCameraAt(reservationLocation);
        }
    }

    public void onInfoWindowClick() {
        // TODO accion
    }

}
