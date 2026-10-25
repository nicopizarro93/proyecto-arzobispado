package cl.duoc.aurasacrapp_api.ruta;

import cl.duoc.aurasacrapp_api.recinto.Recinto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "parada_ruta")
@Getter
@Setter
@NoArgsConstructor
public class ParadaRuta {

    @EmbeddedId
    private ParadaRutaId id = new ParadaRutaId();

    // @MapsId: el id_ruta de la llave se toma de esta relación al guardar.
    @MapsId("idRuta")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ruta")
    private Ruta ruta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_recinto", nullable = false)
    private Recinto recinto;

    @Column(name = "tiempo_visita_min", nullable = false)
    private Integer tiempoVisitaMin;

    public Integer getOrden() {
        return id.getOrden();
    }
}