package gbem.com.ar.estacionamientos.reservations;

import gbem.com.ar.estacionamientos.dashboard.ReservationDTO;

/**
 * @author pielreloj
 * Created: 04/11/18.
 */
public interface SolicitudListener {

    void onConfirmar(ReservationDTO reservation);

    void onRechazar(ReservationDTO reservation);

}
