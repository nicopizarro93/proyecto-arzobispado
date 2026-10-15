package cl.duoc.santiago_sacro_api.ruta;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/** Llave de parada_ruta: la ruta y la posición de la parada (1, 2, 3...). */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class ParadaRutaId implements Serializable {

    @Column(name = "id_ruta")
    private Long idRuta;

    @Column(name = "orden")
    private Integer orden;
}