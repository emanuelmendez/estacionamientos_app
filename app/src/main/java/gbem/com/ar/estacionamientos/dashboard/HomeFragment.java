package gbem.com.ar.estacionamientos.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import gbem.com.ar.estacionamientos.MapsActivity;
import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.api.dtos.UserDataDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static gbem.com.ar.estacionamientos.utils.Utils.USER_DATA_KEY;
import static gbem.com.ar.estacionamientos.utils.Utils.getApp;

public class HomeFragment extends Fragment {

    private final List<ReservationDTO> reservations = new ArrayList<>();
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

    private Unbinder unbinder;
    private UserDataDTO userData;

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
            Log.i("HOME_FRAGMENT", "onCreate: " + userData.getEmail());
        }

        final DashboardService service = getApp(Objects.requireNonNull(getActivity())).getService(DashboardService.class);
        final Call<List<ReservationDTO>> call =
                service.getDriverReservations(getApp(getActivity()).getLastSignedInAccount().getIdToken());

        Callback<List<ReservationDTO>> callback = new Callback<List<ReservationDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<ReservationDTO>> call, @NonNull Response<List<ReservationDTO>> response) {
                if (response.isSuccessful()) {
                    reservations.clear();
                    if (response.code() == 200) {
                        cvDriverReservations.setVisibility(View.VISIBLE);
                        reservations.addAll(response.body());
                        final ReservationDTO dto = reservations.get(0);
                        txtReservaEn.setText(getString(R.string.txt_reserva_en, dto.getParkingLot().getStreetAddress()));
                        txtFechaReserva.setText(
                                new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(dto.getFrom()));
                        txtUsuarioReserva.setText(getString(R.string.txt_usuario_reserva, dto.getLenderName()));
                    } else {
                        cvDriverReservations.setVisibility(View.GONE);
                    }
                    Log.i("HOME_FRAGMENT", "onResponse: " + reservations.toString());
                } else {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ReservationDTO>> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        };
        call.enqueue(callback);

        final Call<List<ReservationDTO>> call2 =
                service.getLenderReservations(getApp(getActivity()).getLastSignedInAccount().getIdToken());

        Callback<List<ReservationDTO>> callback2 = new Callback<List<ReservationDTO>>() {
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
                    Log.i("HOME_FRAGMENT", "onResponse: " + occupiedLots.toString());
                } else {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ReservationDTO>> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        };
        call2.enqueue(callback2);


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
        startActivity(new Intent(getContext(), MapsActivity.class));
    }

}
