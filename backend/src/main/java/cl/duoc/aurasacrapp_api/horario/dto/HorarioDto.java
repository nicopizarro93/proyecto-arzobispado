package cl.duoc.aurasacrapp_api.horario.dto;

import cl.duoc.aurasacrapp_api.horario.HorarioRegular;

import java.time.LocalTime;

/** Un bloque de horario: por ejemplo, lunes de 09:00 a 13:00. */
public record HorarioDto(Long id, int diaSemana, String dia, LocalTime horaApertura, LocalTime horaCierre) {

    private static final String[] DIAS = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};

    public static HorarioDto desde(HorarioRegular h) {
        return new HorarioDto(h.getId(), h.getDiaSemana(), DIAS[h.getDiaSemana() - 1],
                h.getHoraApertura(), h.getHoraCierre());
    }
}
