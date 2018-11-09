package gbem.com.ar.estacionamientos.search;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import gbem.com.ar.estacionamientos.R;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SearchFragment extends FragmentActivity {

    private static final String TAG = SearchFragment.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 4312;
    @BindView(R.id.etDireccion)
    TextInputEditText etDireccion;

    @BindView(R.id.btn_search)
    Button btnSearch;

    @BindView(R.id.swUseMyLocation)
    Switch swUseMyLocation;

    @BindView(R.id.txtDireccion)
    TextInputLayout txtDireccion;

    @BindView(R.id.txtDia)
    Button txtDia;

    @BindView(R.id.txtHora)
    Button txtHora;

    @BindView(R.id.etCantidadHoras)
    EditText etCantidadHoras;

    private Address address;
    private LatLng latLng;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search);
        ButterKnife.bind(this);
    }

    @OnCheckedChanged(R.id.swUseMyLocation)
    public void onUseMyLocationChanged(boolean checked) {
        if (checked) {
            if (isLocationPermissionGranted()) {
                etDireccion.setVisibility(View.INVISIBLE);
                txtDireccion.setVisibility(View.INVISIBLE);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            txtDireccion.setVisibility(View.VISIBLE);
            etDireccion.setVisibility(View.VISIBLE);
        }
    }

    public boolean isLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(
                this.getApplicationContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            txtDireccion.setVisibility(View.INVISIBLE);
            etDireccion.setVisibility(View.INVISIBLE);
        } else {
            txtDireccion.setVisibility(View.VISIBLE);
            etDireccion.setVisibility(View.VISIBLE);
            swUseMyLocation.setChecked(false);
        }

    }

    @OnClick(R.id.btn_search)
    public void sendSearchParameters() {

        if (swUseMyLocation.isChecked()) {

        } else {

        }

        if (etDireccion.getText().length() == 0) {
            etDireccion.setError("Este campo es obligatorio");
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
            latLng = new LatLng(address.getLatitude(), address.getLongitude());
        } else {
            etDireccion.setError(getString(R.string.location_search_not_found));
        }

    }

    @OnClick({R.id.txtDia, R.id.txtHora})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txtDia:
                break;
            case R.id.txtHora:
                break;
        }
    }
}
