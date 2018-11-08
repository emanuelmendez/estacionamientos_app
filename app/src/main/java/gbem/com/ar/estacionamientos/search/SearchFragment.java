package gbem.com.ar.estacionamientos.search;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gbem.com.ar.estacionamientos.R;

public class SearchFragment extends FragmentActivity {

    private static final String TAG = SearchFragment.class.getSimpleName();
    @BindView(R.id.etDireccion)
    TextInputEditText etDireccion;

    @BindView(R.id.btn_search)
    Button btnSearch;

    private GoogleMap mMap;
    private Marker marker;
    private Address address;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search);
        ButterKnife.bind(this);

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapSearchView);
        mapFragment.getMapAsync(this::onMapReady);
    }

    @OnClick(R.id.btn_search)
    public void textChanged() {
        if (etDireccion.getText().length() < 4) {
            return;
        }

        // limpiar el edit text en caso de error anterior
        etDireccion.setError(null);

        final Geocoder geocoder = new Geocoder(this);
        final List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(etDireccion.getText().toString(), 1);
        } catch (IOException e) {
            Log.e(TAG, "Geocoder exception: " + e.getMessage(), e);
            Toast.makeText(this, R.string.location_search_error, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!addresses.isEmpty()) {
            address = addresses.get(0);

            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));

            if (marker != null) {
                marker.remove();
            }

            marker = mMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)));
        } else {
            etDireccion.setError(getString(R.string.location_search_not_found));
        }

    }

    private void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }


}
