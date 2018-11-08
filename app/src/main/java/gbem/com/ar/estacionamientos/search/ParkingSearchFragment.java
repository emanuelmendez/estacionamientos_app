package gbem.com.ar.estacionamientos.search;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;
import gbem.com.ar.estacionamientos.R;

public class ParkingSearchFragment extends Fragment {

    @BindView(R.id.btn_search_start_datetime)
    Button btnDateTimeFrom;

    @BindView(R.id.txt_start_datetime)
    TextView txtDateTimeFrom;

    @BindView(R.id.btn_search_to_datetime)
    Button btnDateTo;

    @BindView(R.id.txt_to_datetime)
    TextView txtDateTo;

    @BindView(R.id.btn_search_start_time)
    Button btnTime;

    @BindView(R.id.txt_start_time)
    TextView txtTime;

    @BindView(R.id.weekdays)
    LinearLayout weekdays;

    @BindView(R.id.linearLayout_date_to)
    LinearLayout sameTime;

    @BindView(R.id.chk_same_time_search)
    CheckBox chkSameDate;

    @BindView(R.id.spinner_radio)
    Spinner spinnerRadio;

    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parking_search, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prepareDistanceSpinner();
    }

    @OnCheckedChanged(R.id.chk_same_time_search)
    public void onCheckSameTime(boolean isChecked) {
        if (isChecked) {
            sameTime.setVisibility(View.VISIBLE);
            weekdays.setVisibility(View.VISIBLE);
        } else {
            sameTime.setVisibility(View.GONE);
            weekdays.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btn_search_start_datetime)
    public void onDateFromClicked() {
        showDatePicker(true);
    }

    @OnClick(R.id.btn_search_to_datetime)
    public void onDateToClicked() {
        showDatePicker(false);
    }

    @OnClick(R.id.btn_search_start_time)
    public void onTimeClicked() {
        showTimePicker();
    }

    private void prepareDistanceSpinner() {
        ArrayAdapter<CharSequence> adapterRadio =
                ArrayAdapter.createFromResource(
                        Objects.requireNonNull(getActivity()),
                        R.array.radio_array,
                        android.R.layout.simple_spinner_item);

        adapterRadio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRadio.setAdapter(adapterRadio);
    }

    private void showDatePicker(Boolean from) {
        DatePickerFragment date = new DatePickerFragment();

        date.setCallBack(from ? new CustomOnDateSetListener(txtDateTimeFrom) : new CustomOnDateSetListener(txtDateTo));

        date.show(Objects.requireNonNull(getFragmentManager()), "Date Picker");
    }

    private void showTimePicker() {
        TimePickerFragment time = new TimePickerFragment();

        time.setCallBack(new CustomOnTimeSetListener(txtTime));

        time.show(Objects.requireNonNull(getFragmentManager()), "Date Picker");
    }

    private class CustomOnTimeSetListener implements TimePickerDialog.OnTimeSetListener {

        private final TextView txtTime;

        private CustomOnTimeSetListener(TextView txtTime) {
            this.txtTime = txtTime;
        }

        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            String fullMin = String.valueOf(minute);
            fullMin = fullMin.length() == 1 ? String.valueOf(0) + fullMin : fullMin;
            txtTime.setText(String.format("%s:%s", String.valueOf(hour), fullMin));
        }
    }

    private class CustomOnDateSetListener implements DatePickerDialog.OnDateSetListener {

        private final TextView txt;

        private CustomOnDateSetListener(TextView txt) {
            this.txt = txt;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            txt.setText(
                    String.format("%s/%s/%s", String.valueOf(dayOfMonth), String.valueOf(month + 1), String.valueOf(year))
            );
        }

    }
}
