package gbem.com.ar.estacionamientos.search;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.utils.Utils;

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

    @BindView(R.id.spinner_radio)
    Spinner spinnerRadio;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private Location mLastKnowLocation;
    private CustomOnTimeSetListener timeSetListener;
    private CustomOnDateSetListener dateSetListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search);
        ButterKnife.bind(this);

        timeSetListener = new CustomOnTimeSetListener(txtHora);
        dateSetListener = new CustomOnDateSetListener(txtDia);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                mLastKnowLocation = locationResult.getLastLocation();
            }
        };

        mLocationRequest = new LocationRequest().setInterval(100).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @OnCheckedChanged(R.id.swUseMyLocation)
    public void onUseMyLocationChanged(boolean checked) {
        btnSearch.setEnabled(!checked);
        if (checked) {
            if (isLocationPermissionGranted()) {
                etDireccion.setVisibility(View.GONE);
                txtDireccion.setVisibility(View.GONE);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            txtDireccion.setVisibility(View.VISIBLE);
            etDireccion.setVisibility(View.VISIBLE);
            btnSearch.setEnabled(true);
        }
    }

    public boolean isLocationPermissionGranted() {
        final boolean granted = ContextCompat.checkSelfPermission(
                this.getApplicationContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (granted) {
            final Task<Location> lastLocationTask = mFusedLocationProviderClient.getLastLocation();
            lastLocationTask.addOnSuccessListener(this::onSuccess);
        }

        return granted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            txtDireccion.setVisibility(View.GONE);
            etDireccion.setVisibility(View.GONE);
        } else {
            txtDireccion.setVisibility(View.VISIBLE);
            etDireccion.setVisibility(View.VISIBLE);
            swUseMyLocation.setChecked(false);
            btnSearch.setEnabled(true);
        }

    }

    @OnClick(R.id.btn_search)
    public void sendSearchParameters() {
        if (!dateSetListener.isDateSetted) {
            txtDia.setError("Indica el día");
            return;
        } else if (!timeSetListener.isTimeSetted) {
            txtHora.setError("Indica la hora");
            return;
        } else if (etCantidadHoras.getText().length() == 0
                || etCantidadHoras.getText().toString().equals("0")) {
            etCantidadHoras.setError("Campo requerido");
            return;
        } else {
            txtHora.setError(null);
            txtDia.setError(null);
            etCantidadHoras.setError(null);
        }

        final Date dateFrom;
        final Date dateTo;
        try {
            dateFrom = Utils.parse(txtDia.getText() + " " + txtHora.getText() + ":00");
            final Calendar cal = Calendar.getInstance();
            cal.setTime(dateFrom);
            final int cantHoras = Integer.parseInt(etCantidadHoras.getText().toString());
            cal.add(Calendar.HOUR, cantHoras);
            dateTo = cal.getTime();
        } catch (ParseException e) {
            Log.e(TAG, "Parse: " + e.getMessage(), e);
            return;
        }

        LatLng centerLocation;
        if (swUseMyLocation.isChecked() && isLocationPermissionGranted()) {
            centerLocation = new LatLng(mLastKnowLocation.getLatitude(), mLastKnowLocation.getLongitude());
        } else {
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
            if (addresses.isEmpty()) {
                etDireccion.setError(getString(R.string.location_search_not_found));
                return;
            }

            final Address address = addresses.get(0);
            centerLocation = new LatLng(address.getLatitude(), address.getLongitude());
        }

        int ratioInMeters = Integer.parseInt(spinnerRadio.getSelectedItem().toString());
        double ratio = ratioInMeters / 1000.0d;
        final float zoom = getMapZoom(ratioInMeters);

        final Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("location", centerLocation);
        intent.putExtra("ratio", ratio);
        intent.putExtra("date_from", dateFrom);
        intent.putExtra("date_to", dateTo);
        intent.putExtra("zoom", zoom);
        startActivityForResult(intent, 531);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 531 && resultCode == RESULT_OK) {
            super.onBackPressed();
        }
    }

    private float getMapZoom(int ratioInMeters) {
        final float zoom;
        switch (ratioInMeters) {
            case 300:
                zoom = 16.0f;
                break;
            case 500:
                zoom = 15.5f;
                break;
            case 800:
                zoom = 14.5f;
                break;
            case 1000:
                zoom = 13.5f;
                break;
            case 1500:
                zoom = 13.0f;
                break;
            default:
                zoom = 16.0f;
                break;
        }
        return zoom;
    }

    private void onSuccess(Location location) {
        if (location != null) {
            mLastKnowLocation = location;
            btnSearch.setEnabled(true);
        }
    }

    @OnClick({R.id.txtDia, R.id.txtHora})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txtDia:
                showDatePicker();
                break;
            case R.id.txtHora:
                showTimePicker();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        // permission checked inside method
        if (isLocationPermissionGranted()) {
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, null);
        }
    }

    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();

        date.setCallBack(dateSetListener);

        date.show(Objects.requireNonNull(getSupportFragmentManager()), "Date Picker");
    }

    private void showTimePicker() {
        TimePickerFragment time = new TimePickerFragment();

        time.setCallBack(timeSetListener);
        time.show(Objects.requireNonNull(getSupportFragmentManager()), "Date Picker");
    }

    private class CustomOnTimeSetListener implements TimePickerDialog.OnTimeSetListener {

        private final TextView txtTime;
        boolean isTimeSetted = false;


        private CustomOnTimeSetListener(TextView txtTime) {
            this.txtTime = txtTime;
        }

        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            String fullMin = String.valueOf(minute);
            fullMin = fullMin.length() == 1 ? String.valueOf(0) + fullMin : fullMin;
            txtTime.setText(String.format("%s:%s", String.valueOf(hour), fullMin));
            isTimeSetted = true;
        }
    }

    private class CustomOnDateSetListener implements DatePickerDialog.OnDateSetListener {

        private final TextView txt;
        boolean isDateSetted = false;

        private CustomOnDateSetListener(TextView txt) {
            this.txt = txt;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            txt.setText(
                    String.format("%s/%s/%s", String.valueOf(month + 1), String.valueOf(dayOfMonth), String.valueOf(year))
            );
            isDateSetted = true;
        }

    }
}
