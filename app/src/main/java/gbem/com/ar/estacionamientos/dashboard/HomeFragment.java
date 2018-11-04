package gbem.com.ar.estacionamientos.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.api.dtos.UserDataDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static gbem.com.ar.estacionamientos.utils.Utils.USER_DATA_KEY;
import static gbem.com.ar.estacionamientos.utils.Utils.getApp;

public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();
    private final List<ReservationDTO> occupiedLots = new ArrayList<>();
    @BindView(R.id.txtReservaEn)
    TextView txtReservaEn;
    @BindView(R.id.txtFechaReserva)
    TextView txtFechaReserva;
    @BindView(R.id.txtUsuarioReserva)
    TextView txtUsuarioReserva;
    @BindView(R.id.btnVerEnMapa)
    Button btnVerEnMapa;
    @BindView(R.id.cv_driver_reservations)
    CardView cvDriverReservations;
    @BindView(R.id.cv_lender_lots)
    CardView cvLenderLots;
    private ReservationDTO currentReservation;
    private Unbinder unbinder;
    private UserDataDTO userData;
    private DashboardService dashboardService;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(UserDataDTO userData) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putSerializable(USER_DATA_KEY, userData);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userData = (UserDataDTO) getArguments().getSerializable(USER_DATA_KEY);
            Objects.requireNonNull(userData);
            Log.i(TAG, "onCreate: " + userData.getEmail());
        }

        final FragmentActivity activity = Objects.requireNonNull(getActivity());

        if (dashboardService == null) {
            dashboardService = getApp(activity).getService(DashboardService.class);
        }

        final Call<ReservationDTO> call =
                dashboardService.getDriverCurrentReservation(getApp(activity).getLastSignedInAccount().getIdToken());
        call.enqueue(new DriverLastReservationCallback());

        final Call<List<ReservationDTO>> call2 =
                dashboardService.getLenderReservations(getApp(activity).getLastSignedInAccount().getIdToken());
        call2.enqueue(new LenderReservationsCallback());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnVerEnMapa)
    public void onClickVerEnMapa() {
        final Intent intent = new Intent(getContext(), ReservationInMapActivity.class);
        intent.putExtra("RESERVATION_LOCATION", currentReservation);
        startActivity(intent);
    }

    private class DriverLastReservationCallback implements Callback<ReservationDTO> {
        @Override
        public void onResponse(@NonNull Call<ReservationDTO> call, @NonNull Response<ReservationDTO> response) {
            if (response.isSuccessful()) {
                if (response.code() == 200) {
                    cvDriverReservations.setVisibility(View.VISIBLE);
                    currentReservation = response.body();

                    if (currentReservation == null) {
                        cvDriverReservations.setVisibility(View.GONE);
                    }

                    txtReservaEn.setText(
                            getString(R.string.txt_reserva_en, currentReservation.getParkingLot().getStreetAddress()));
                    txtFechaReserva.setText(SimpleDateFormat.getDateTimeInstance().format(currentReservation.getFrom()));

                    txtUsuarioReserva.setText(getString(R.string.txt_usuario_reserva, currentReservation.getLenderName()));
                } else {
                    cvDriverReservations.setVisibility(View.GONE);
                }
                Log.i(TAG, "onResponse: " + currentReservation.toString());
            } else {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(@NonNull Call<ReservationDTO> call, @NonNull Throwable t) {
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
        }

    }

    private class LenderReservationsCallback implements Callback<List<ReservationDTO>> {
        @Override
        public void onResponse(@NonNull Call<List<ReservationDTO>> call, @NonNull Response<List<ReservationDTO>> response) {
            if (response.isSuccessful()) {
                occupiedLots.clear();
                if (response.code() == 200) {
                    cvLenderLots.setVisibility(View.VISIBLE);
                    occupiedLots.addAll(response.body());
                } else {
                    cvLenderLots.setVisibility(View.GONE);
                }
                Log.i(TAG, "onResponse: " + occupiedLots.toString());
            } else {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(@NonNull Call<List<ReservationDTO>> call, @NonNull Throwable t) {
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
        }
    }
}
