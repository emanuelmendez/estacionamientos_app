package gbem.com.ar.estacionamientos;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;


public class ParkingSearchFragment extends Fragment {

    LinearLayout weekdays;
    LinearLayout sameTime;
    Button btnDateTo;
    Button btnDateTimeFrom;
    Button btnTime;
    TextView txtDateTimeFrom;
    TextView txtDateTo;
    TextView txtTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parking_search, null, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Radio spinner
        Spinner spinnerRadio = view.findViewById(R.id.spinner_radio);
        ArrayAdapter<CharSequence> adapterRadio = ArrayAdapter.createFromResource(getActivity(),
                R.array.radio_array, android.R.layout.simple_spinner_item);
        adapterRadio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRadio.setAdapter(adapterRadio);

        //Checkbox same time for other dates
        sameTime = (LinearLayout)view.findViewById( R.id.linearLayout_date_to);
        weekdays = (LinearLayout)view.findViewById( R.id.weekdays);
        CheckBox chkSameDate = (CheckBox)view.findViewById( R.id.chk_same_time_search);

        chkSameDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(isChecked){
                    sameTime.setVisibility(View.VISIBLE);
                    weekdays.setVisibility(View.VISIBLE);
                }else{
                    sameTime.setVisibility(View.GONE);
                    weekdays.setVisibility(View.GONE);
                }
            }
        });

        //Date and Time picker
        btnDateTimeFrom = (Button)view.findViewById( R.id.btn_search_start_datetime);
        txtDateTimeFrom = (TextView)view.findViewById( R.id.txt_start_datetime);
        btnDateTo = (Button)view.findViewById( R.id.btn_search_to_datetime);
        txtDateTo = (TextView)view.findViewById( R.id.txt_to_datetime);
        btnTime = (Button)view.findViewById( R.id.btn_search_start_time);
        txtTime = (TextView)view.findViewById( R.id.txt_start_time);

        btnDateTimeFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(true);
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });

        btnDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(false);
            }
        });
    }

    private void showDatePicker(Boolean from) {
        DatePickerFragment date = new DatePickerFragment();
        //Set Up Current Date Into dialog
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);

        //Set Call back to capture selected date/
        if(from){
            date.setCallBack(ondateFrom);
        }else{
            date.setCallBack(ondateTo);
        }
        date.show(getFragmentManager(), "Date Picker");
    }

    private void showTimePicker() {
        TimePickerFragment time = new TimePickerFragment();
        //Set Up Current Date Into dialog
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("hour", calender.get(Calendar.HOUR));
        args.putInt("minute", calender.get(Calendar.MINUTE));
        time.setArguments(args);

        //Set Call back to capture selected time/
        time.setCallBack(ontime);
        time.show(getFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener ondateFrom = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            txtDateTimeFrom.setText(String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear+1)
                    + "/" + String.valueOf(year));
        }
    };
    DatePickerDialog.OnDateSetListener ondateTo = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            txtDateTo.setText(String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear+1)
                    + "/" + String.valueOf(year));
        }
    };

    TimePickerDialog.OnTimeSetListener ontime = new TimePickerDialog.OnTimeSetListener() {

        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            String fullMin = String.valueOf(minute);
            fullMin = fullMin.length() == 1 ? String.valueOf(0)+fullMin : fullMin;
            txtTime.setText(String.valueOf(hour) + ":" + fullMin);
        }
    };
}
