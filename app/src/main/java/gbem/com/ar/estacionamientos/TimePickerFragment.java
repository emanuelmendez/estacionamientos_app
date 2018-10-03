package gbem.com.ar.estacionamientos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class TimePickerFragment extends DialogFragment{
    TimePickerDialog.OnTimeSetListener ontimeSet;
    private int hour, minute;

    public TimePickerFragment() {}

    public void setCallBack(TimePickerDialog.OnTimeSetListener ontime) {
        this.ontimeSet = ontime;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        hour = args.getInt("hour");
        minute = args.getInt("minute");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(),AlertDialog.THEME_HOLO_LIGHT, ontimeSet, hour, minute, true);
    }
}