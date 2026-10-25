package cl.duoc.aurasacrapp_api.visita;

import cl.duoc.aurasacrapp_api.recinto.Recinto;
import cl.duoc.aurasacrapp_api.ruta.Ruta;
import cl.duoc.aurasacrapp_api.usuario.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "visita")
@Getter
@Setter
@NoArgsConstructor
public class Visita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_visita")
    private Long id;

    // Visitante o docente que agenda.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    // Destino: un recinto o una ruta, nunca los dos (CHECK ck_visita_destino).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_recinto")
    private Recinto recinto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ruta")
    private Ruta ruta;

    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime inicio;

    // En una salida pedagógica es la cantidad de estudiantes: no se guardan sus datos (HU-22).
    @Column(name = "cantidad_personas", nullable = false)
    private Integer cantidadPersonas;

    // Solo en salidas pedagógicas de docentes, por ejemplo "3° medio".
    @Column(name = "nivel_curso", length = 50)
    private String nivelCurso;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "estado", nullable = false, length = 10)
    private EstadoVisita estado = EstadoVisita.AGENDADA;

    @Column(name = "fecha_creacion", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", insertable = false, updatable = false)
    private LocalDateTime fechaActualizacion;
}