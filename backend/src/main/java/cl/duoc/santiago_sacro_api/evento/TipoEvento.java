package cl.duoc.santiago_sacro_api.evento;

public enum TipoEvento {
    CONCIERTO, EXPOSICION, HITO_LITURGICO, ACTIVIDAD_CULTURAL, AVISO,
    APERTURA_ESPECIAL, CIERRE_ESPECIAL;

    /** Las aperturas y los cierres excepcionales cambian el horario del recinto (HU-30). */
    public boolean esExcepcionDeHorario() {
        return this == APERTURA_ESPECIAL || this == CIERRE_ESPECIAL;
    }
}