package cl.duoc.aurasacrapp_api.guia;

import cl.duoc.aurasacrapp_api.recinto.Recinto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Guía voluntario de un recinto. No es un usuario de la app: lo administra el encargado (HU-32). */
@Entity
@Table(name = "guia")
@Getter
@Setter
@NoArgsConstructor
public class Guia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_guia")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_recinto", nullable = false)
    private Recinto recinto;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 50)
    private String apellido;

    // Borrado lógico: un guía desactivado no se puede asignar, pero queda en el historial.
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    public String nombreCompleto() {
        return nombre + " " + apellido;
    }
}
