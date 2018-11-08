package gbem.com.ar.estacionamientos.search;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;
import java.util.Objects;

public class DatePickerFragment extends DialogFragment {

    DatePickerDialog.OnDateSetListener ondateSet;

    private int year, monthOfYear, dayOfMonth;

    public DatePickerFragment() {
    }

    public void setCallBack(DatePickerDialog.OnDateSetListener ondate) {
        this.ondateSet = ondate;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);

        if (args != null &&
                args.containsKey("year") && args.containsKey("month") && args.containsKey("day")) {
            year = args.getInt("year");
            monthOfYear = args.getInt("month");
            dayOfMonth = args.getInt("day");
        } else {
            Calendar calender = Calendar.getInstance();
            year = calender.get(Calendar.YEAR);
            monthOfYear = calender.get(Calendar.MONTH);
            dayOfMonth = calender.get(Calendar.DAY_OF_MONTH);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getActivity()),
                AlertDialog.THEME_HOLO_LIGHT, ondateSet, year, monthOfYear, dayOfMonth);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        return datePickerDialog;
    }

}