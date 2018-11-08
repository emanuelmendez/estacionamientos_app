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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.api.dtos.ParkingLotResultDTO;
import gbem.com.ar.estacionamientos.api.rest.ISearchService;
import gbem.com.ar.estacionamientos.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static gbem.com.ar.estacionamientos.utils.Utils.getIdToken;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private ISearchService searchService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        searchService = Utils.getApp(this).getService(ISearchService.class);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(new LatLng(-34.625, -58.485), 15.0F)));

        mMap.setOnMapClickListener(latLng -> searchNear(latLng, 1.0));
    }

    private void searchNear(final LatLng latLng, double ratio) {
        searchService
                .searchNear(getIdToken(this), latLng.latitude, latLng.longitude, ratio)
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

}
