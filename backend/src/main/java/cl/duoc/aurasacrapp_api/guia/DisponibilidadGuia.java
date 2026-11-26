package cl.duoc.aurasacrapp_api.guia;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Bloque de tiempo en que un guía está disponible (HU-32). */
@Entity
@Table(name = "disponibilidad_guia")
@Getter
@Setter
@NoArgsConstructor
public class DisponibilidadGuia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_disponibilidad")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_guia", nullable = false)
    private Guia guia;

    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime inicio;

    @Column(name = "fecha_hora_fin", nullable = false)
    private LocalDateTime fin;
}
