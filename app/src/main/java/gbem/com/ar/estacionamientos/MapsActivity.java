package gbem.com.ar.estacionamientos;

import android.os.Bundle;
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

import gbem.com.ar.estacionamientos.api.dtos.ParkingLotResultDTO;
import gbem.com.ar.estacionamientos.api.rest.ISearchService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // FIXME parametrizar url base. Es la ip para usar a través del emulador
    private static final String API_BASE_URL = "http://10.0.2.2:8080/web/";

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

        // FIXME inyectar
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        searchService = retrofit.create(ISearchService.class);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(new LatLng(-34.625, -58.485), 15.0F)));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                final Call<List<ParkingLotResultDTO>> call =
                        searchService.searchNear(latLng.latitude, latLng.longitude, 1);

                call.enqueue(new Callback<List<ParkingLotResultDTO>>() {
                    @Override
                    public void onResponse(Call<List<ParkingLotResultDTO>> call,
                                           Response<List<ParkingLotResultDTO>> response) {
                        mMap.clear();

                        for (ParkingLotResultDTO dto : response.body()) {
                            MarkerOptions marker = new MarkerOptions();
                            String[] split = dto.getCoordinates().split(",");
                            double lat = Double.parseDouble(split[0]);
                            double lng = Double.parseDouble(split[1]);

                            marker.position(new LatLng(lat, lng));
                            marker.title(dto.getStreetAddress());
                            marker.snippet(dto.getDescription());
                            mMap.addMarker(marker);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ParkingLotResultDTO>> call, Throwable t) {
                        Log.e(TAG, t.getMessage(), t);
                        Toast.makeText(MapsActivity.this, "Search Service error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}
