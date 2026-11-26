package cl.duoc.aurasacrapp_api.guia;

import cl.duoc.aurasacrapp_api.recinto.Recinto;
import cl.duoc.aurasacrapp_api.usuario.Usuario;
import cl.duoc.aurasacrapp_api.visita.Visita;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/** Solicitud de un guía para un recinto de una visita; la resuelve el encargado de ese recinto (HU-23, HU-33). */
@Entity
@Table(name = "solicitud_guia")
@Getter
@Setter
@NoArgsConstructor
public class SolicitudGuia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_visita", nullable = false)
    private Visita visita;

    // Recinto de la visita donde se necesita el guía: a su encargado le llega la solicitud.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_recinto", nullable = false)
    private Recinto recinto;

    // Null mientras está pendiente.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_guia")
    private Guia guia;

    // Encargado que asignó o rechazó.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_resuelve")
    private Usuario resueltaPor;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "estado", nullable = false, length = 10)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    @Column(name = "motivo_rechazo", length = 300)
    private String motivoRechazo;

    @Column(name = "fecha_solicitud", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;
}
