package gbem.com.ar.estacionamientos.search;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {

    TimePickerDialog.OnTimeSetListener ontimeSet;
    private int hour, minute;

    public TimePickerFragment() {
    }

    public void setCallBack(TimePickerDialog.OnTimeSetListener ontime) {
        this.ontimeSet = ontime;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);

        if (args != null &&
                args.containsKey("hour") && args.containsKey("minute")) {
            hour = args.getInt("hour");
            minute = args.getInt("minute");
        } else {
            Calendar calender = Calendar.getInstance();
            hour = calender.get(Calendar.HOUR);
            minute = calender.get(Calendar.MINUTE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(),
                AlertDialog.THEME_HOLO_LIGHT, ontimeSet, hour, minute, true);
    }
}