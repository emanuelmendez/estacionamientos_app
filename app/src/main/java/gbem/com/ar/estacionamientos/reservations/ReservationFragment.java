package gbem.com.ar.estacionamientos.reservations;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.dashboard.ReservationDTO;
import gbem.com.ar.estacionamientos.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static gbem.com.ar.estacionamientos.utils.Utils.getIdToken;

public class ReservationFragment extends Fragment implements SolicitudListener {

    public static final String TAG = ReservationFragment.class.getSimpleName();
    private final LenderReservationsService service;
    private SolicitudesAdapter adapter;
    private List<ReservationDTO> reservations = new ArrayList<>();
    private BroadcastReceiver updateUIReciver;

    public ReservationFragment() {
        service = Utils.getService(LenderReservationsService.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLenderReservations();
    }

    private void getLenderReservations() {
        service.getLenderReservations(
                Utils.getIdToken(Objects.requireNonNull(getActivity()))).enqueue(
                new Callback<List<ReservationDTO>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<ReservationDTO>> call,
                                           @NonNull Response<List<ReservationDTO>> response) {
                        if (response.code() == 200) {
                            reservations = response.body();
                        } else {
                            reservations.clear();
                        }

                        updateLayouts();
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<ReservationDTO>> call,
                                          @NonNull Throwable t) {

                    }
                });
    }

    private void updateLayouts() {
        if (adapter != null) {
            adapter.setData(reservations);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            adapter = new SolicitudesAdapter(reservations, this, context);
            recyclerView.setAdapter(adapter);

            IntentFilter filter = new IntentFilter();
            filter.addAction("gbem.com.ar.estacionamientos.notification");

            updateUIReciver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    getLenderReservations();
                }
            };
            Objects.requireNonNull(getActivity()).registerReceiver(updateUIReciver, filter);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Objects.requireNonNull(getActivity()).unregisterReceiver(updateUIReciver);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLenderReservations();
    }

    @Override
    public void onConfirmar(ReservationDTO reservation) {
        Log.d(TAG, "onConfirmar: " + reservation);
        Objects.requireNonNull(getActivity());
        service.acceptReservation(getIdToken(getActivity()), reservation.getId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getActivity(), "Aceptada", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Error code: " + response.code());
                            Toast.makeText(getActivity(), "Error al intentar confirmar la reserva", Toast.LENGTH_SHORT).show();
                        }
                        getLenderReservations(); // actualizamos la vista
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(getActivity(), "Confirmation Failure", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRechazar(ReservationDTO reservation) {
        Log.d(TAG, "onRechazar: " + reservation);
        Objects.requireNonNull(getActivity());
        service.rejectOrCancelLenderReservation(getIdToken(getActivity()), reservation.getId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getActivity(), "Rechazada", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Rejection error", Toast.LENGTH_SHORT).show();
                        }
                        getLenderReservations(); // actualizamos la vista
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(getActivity(), "Rejection Failure", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
