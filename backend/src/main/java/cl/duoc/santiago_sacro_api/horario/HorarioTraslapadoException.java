package cl.duoc.santiago_sacro_api.horario;

public class HorarioTraslapadoException extends RuntimeException {
    public HorarioTraslapadoException() {
        super("El bloque se cruza con otro horario del mismo día.");
    }
}
