package gbem.com.ar.estacionamientos.search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.api.dtos.ParkingLotResultDTO;
import gbem.com.ar.estacionamientos.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static gbem.com.ar.estacionamientos.utils.Utils.getIdToken;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();

    private final Map<Marker, ParkingLotResultDTO> markers = new HashMap<>();

    @BindView(R.id.cl_maps_activity)
    ConstraintLayout layout;

    private IReservationService reservationService;
    private Snackbar snackbar;
    private GoogleMap mMap;
    private LatLng location;
    private double ratio;
    private Date fromDate;
    private Date toDate;
    private float zoom;
    private List<ParkingLotResultDTO> parkingLots = new ArrayList<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        final Bundle extras = Objects.requireNonNull(getIntent().getExtras());
        location = (LatLng) extras.get("location");
        ratio = extras.getDouble("ratio");
        fromDate = (Date) extras.get("date_from");
        toDate = (Date) extras.get("date_to");
        zoom = extras.getFloat("zoom");

        findPlaces();

        final SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(latLng -> {
            if (snackbar != null) {
                snackbar.dismiss();
            }
        });

        mMap.setOnMarkerClickListener(this::onMarkerClick);

        mMap.addMarker(new MarkerOptions()
                .title("Punto de búsqueda")
                .position(location)
                .visible(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        centerCameraAt(location);

        if (!parkingLots.isEmpty()) addMarkersToMap();
    }

    private boolean onMarkerClick(final Marker marker) {
        if (location.equals(marker.getPosition()) || !markers.containsKey(marker))
            return false;

        if (snackbar != null) snackbar.dismiss();

        final ParkingLotResultDTO parkingLot = markers.get(marker);

        snackbar = Snackbar.make(layout, "Valor por hora: $" + parkingLot.getValue(), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Reservar", v -> postReservation(parkingLot));
        snackbar.show();

        return false;
    }

    private void postReservation(ParkingLotResultDTO parkingLot) {
        if (reservationService == null) {
            reservationService = Utils.getService(IReservationService.class);
        }

        final ReservationOptionsDTO options = new ReservationOptionsDTO();
        options.setParkingLotId(parkingLot.getId());
        options.setDateFrom(fromDate.getTime());
        options.setDateTo(toDate.getTime());

        reservationService
                .makeReservation(Utils.getIdToken(this), options)
                .enqueue(new ReservationIntentCallback());
    }

    private void findPlaces() {
        Utils.getService(ISearchService.class)
                .searchNear(getIdToken(this),
                        location.latitude, location.longitude, ratio,
                        Utils.parse(fromDate), Utils.parse(toDate))
                .enqueue(new searchServiceCallback());
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
                        CameraPosition.fromLatLngZoom(latlng, zoom)));
    }

    private void addMarkersToMap() {
        for (final ParkingLotResultDTO dto : parkingLots) {
            final MarkerOptions marker = new MarkerOptions();
            final LatLng coordinates = Utils.createLatLng(dto.getCoordinates());
            marker.position(coordinates);

            String title = "";
            if (dto.getScore() != null) {
                title += dto.getScore() + getString(R.string.star) + " ";
            }

            title += dto.getDescription() + " en " + dto.getStreetAddress();

            marker.title(title);
            marker.snippet("Ofrecido por " + dto.getUserFullName());
            final Marker m = mMap.addMarker(marker);
            markers.put(m, dto);
        }
    }

    private final class searchServiceCallback implements Callback<List<ParkingLotResultDTO>> {

        @Override
        public void onResponse(@NonNull final Call<List<ParkingLotResultDTO>> call,
                               @NonNull final Response<List<ParkingLotResultDTO>> response) {
            if (response.code() == 200) {
                parkingLots.addAll(response.body());
                if (mMap != null) {
                    addMarkersToMap();
                }
            } else if (response.code() == 204) {
                Toast.makeText(MapsActivity.this,
                        R.string.parking_lots_not_found,
                        Toast.LENGTH_LONG).show();

                onChangeOptions();
            } else {
                Toast.makeText(MapsActivity.this, "Error de búsqueda", Toast.LENGTH_SHORT).show();
                onChangeOptions();
            }
        }

        @Override
        public void onFailure(@NonNull final Call<List<ParkingLotResultDTO>> call,
                              @NonNull final Throwable t) {
            Log.e(TAG, t.getMessage(), t);
            Toast.makeText(MapsActivity.this, "Error de búsqueda", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    private class ReservationIntentCallback implements Callback<Void> {

        @Override
        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
            if (response.code() == 201) {
                setResult(RESULT_OK);
                finish();
            } else if (response.code() == 409) {
                Toast.makeText(MapsActivity.this,
                        "Alguien lo reservó antes que vos, intentá con otro lugar", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MapsActivity.this, "Error de comunicaciones", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
            Log.e(TAG, "ReservationCallback failure: ", t);
            Toast.makeText(MapsActivity.this, "Error de comunicaciones", Toast.LENGTH_SHORT).show();
        }

    }
}
