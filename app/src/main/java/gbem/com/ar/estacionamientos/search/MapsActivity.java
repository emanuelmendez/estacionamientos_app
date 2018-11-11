package gbem.com.ar.estacionamientos.search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;
import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.api.dtos.ParkingLotResultDTO;
import gbem.com.ar.estacionamientos.api.rest.ISearchService;
import gbem.com.ar.estacionamientos.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static gbem.com.ar.estacionamientos.utils.Utils.getIdToken;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final float DEFAULT_ZOOM = 16.0f;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private ISearchService searchService;
    private LatLng location;
    private double ratio;
    private Date fromDate;
    private Date toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        final Bundle extras = Objects.requireNonNull(getIntent().getExtras());
        location = (LatLng) extras.get("location");
        ratio = extras.getDouble("ratio");
        fromDate = (Date) extras.get("date_from");
        toDate = (Date) extras.get("date_to");

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        searchService = Utils.getApp(this).getService(ISearchService.class);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.addMarker(new MarkerOptions()
                .title("Punto de búsqueda")
                .position(location)
                .visible(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        centerCameraAt(location);

        findPlaces();
    }

    private void findPlaces() {
        searchService
                .searchNear(getIdToken(this),
                        location.latitude, location.longitude, ratio,
                        Utils.parse(fromDate), Utils.parse(toDate))
                .enqueue(new Callback<List<ParkingLotResultDTO>>() {

                    @Override
                    public void onResponse(@NonNull Call<List<ParkingLotResultDTO>> call,
                                           @NonNull Response<List<ParkingLotResultDTO>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            mMap.clear();

                            for (ParkingLotResultDTO dto : response.body()) {
                                MarkerOptions marker = new MarkerOptions();
                                LatLng coordinates = Utils.createLatLng(dto.getCoordinates());
                                marker.position(coordinates);
                                marker.title(dto.getStreetAddress());
                                marker.snippet(dto.getDescription());
                                mMap.addMarker(marker);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<ParkingLotResultDTO>> call, @NonNull Throwable t) {
                        Log.e(TAG, t.getMessage(), t);
                        Toast.makeText(MapsActivity.this, "Search Service error", Toast.LENGTH_SHORT).show();
                    }

                });
    }

    @OnClick(R.id.btn_options)
    public void onChangeOptions() {
        super.onBackPressed();
    }

    @OnClick(R.id.btn_center_on_location)
    public void onBtnCenterClick() {
        centerCameraAt(location);
    }

    private void centerCameraAt(final LatLng latlng) {
        mMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(latlng, DEFAULT_ZOOM)));
    }


}
