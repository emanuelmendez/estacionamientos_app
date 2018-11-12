package gbem.com.ar.estacionamientos.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import gbem.com.ar.estacionamientos.R;
import gbem.com.ar.estacionamientos.api.dtos.UserDataDTO;
import gbem.com.ar.estacionamientos.dashboard.lender.SolicitudListener;
import gbem.com.ar.estacionamientos.dashboard.lender.SolicitudesAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static gbem.com.ar.estacionamientos.utils.Utils.RESERVATION_LOCATION;
import static gbem.com.ar.estacionamientos.utils.Utils.USER_DATA_KEY;
import static gbem.com.ar.estacionamientos.utils.Utils.getApp;
import static gbem.com.ar.estacionamientos.utils.Utils.getIdToken;

public class HomeFragment extends Fragment implements SolicitudListener {

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
    @BindView(R.id.btnCancelarReserva)
    Button btnCancelarReserva;
    @BindView(R.id.cv_driver_reservations)
    CardView cvDriverReservations;
    @BindView(R.id.cv_lender_lots)
    CardView cvLenderLots;
    @BindView(R.id.rvSolicitudes)
    RecyclerView rvSolicitudes;

    private SolicitudesAdapter adapter;
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

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);

        Objects.requireNonNull(getActivity());

        rvSolicitudes.setFocusable(false);
        rvSolicitudes.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvSolicitudes.setItemAnimator(new DefaultItemAnimator());
        rvSolicitudes.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        adapter = new SolicitudesAdapter(occupiedLots, this);
        rvSolicitudes.setAdapter(adapter);

        if (dashboardService == null)
            dashboardService = getApp(getActivity()).getService(DashboardService.class);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getActivity());

        dashboardService.getDriverCurrentReservation(getIdToken(getActivity())).enqueue(new DriverLastReservationCallback());
        dashboardService.getLenderReservations(getIdToken(getActivity())).enqueue(new LenderReservationsCallback());
    }

    @OnClick(R.id.btnVerEnMapa)
    public void onClickVerEnMapa() {
        final Intent intent = new Intent(getContext(), ReservationInMapActivity.class);
        intent.putExtra(RESERVATION_LOCATION, currentReservation);
        startActivity(intent);
    }

    @OnClick(R.id.btnCancelarReserva)
    public void onClickCancelarReserva() {
        Objects.requireNonNull(getActivity());
        dashboardService.cancelCurrentReservation(getIdToken(getActivity()), currentReservation.getId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful())
                            Toast.makeText(getActivity(), "Cancelada", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getActivity(), "Error al cancelar", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(getActivity(), "Cancelar Failure", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @OnClick(R.id.cv_driver_reservations)
    public void onClickDriverCard() {
        if (btnVerEnMapa.getVisibility() == GONE) {
            btnVerEnMapa.setVisibility(VISIBLE);
            btnCancelarReserva.setVisibility(VISIBLE);
        } else {
            btnVerEnMapa.setVisibility(GONE);
            btnCancelarReserva.setVisibility(GONE);
        }
    }

    @Override
    public void onConfirmar(ReservationDTO reservation) {
        Log.d(TAG, "onConfirmar: " + reservation);
        Objects.requireNonNull(getActivity());
        dashboardService.acceptReservation(getIdToken(getActivity()), reservation.getId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful())
                            Toast.makeText(getActivity(), "Accepted", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getActivity(), "Confirmation error", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(getActivity(), "Confirmation Failure", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onCancelar(ReservationDTO reservation) {
        Log.d(TAG, "onCancelar: " + reservation);
        Objects.requireNonNull(getActivity());
        dashboardService.rejectOrCancelLenderReservation(getIdToken(getActivity()), reservation.getId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful())
                            Toast.makeText(getActivity(), "Rejected...", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getActivity(), "Rejection error", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(getActivity(), "Rejection Failure", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private class DriverLastReservationCallback implements Callback<ReservationDTO> {
        @Override
        public void onResponse(@NonNull Call<ReservationDTO> call, @NonNull Response<ReservationDTO> response) {
            if (response.isSuccessful()) {
                if (response.code() == 200) {
                    cvDriverReservations.setVisibility(VISIBLE);
                    currentReservation = response.body();

                    if (currentReservation == null) {
                        cvDriverReservations.setVisibility(GONE);
                    }

                    txtReservaEn.setText(
                            getString(R.string.txt_reserva_en, currentReservation.getParkingLot().getStreetAddress()));
                    txtFechaReserva.setText(SimpleDateFormat.getDateTimeInstance().format(currentReservation.getFrom()));

                    txtUsuarioReserva.setText(getString(R.string.txt_usuario_reserva, currentReservation.getLenderName()));
                } else {
                    cvDriverReservations.setVisibility(GONE);
                }
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
                    cvLenderLots.setVisibility(VISIBLE);
                    occupiedLots.addAll(response.body());

                    adapter.setData(occupiedLots);
                } else {
                    cvLenderLots.setVisibility(GONE);
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
