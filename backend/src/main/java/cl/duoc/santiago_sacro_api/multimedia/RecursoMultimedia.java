package cl.duoc.santiago_sacro_api.multimedia;

import cl.duoc.santiago_sacro_api.recinto.Recinto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "recurso_multimedia")
@Getter
@Setter
@NoArgsConstructor
public class RecursoMultimedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recurso")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_recinto", nullable = false)
    private Recinto recinto;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "tipo_recurso", nullable = false, length = 10)
    private TipoRecurso tipo;

    // Dirección del archivo (http o https): el servidor no guarda imágenes, solo su enlace.
    @Column(name = "url", nullable = false, length = 500)
    private String url;

    @Column(name = "titulo", length = 150)
    private String titulo;

    // Posición en la galería (1, 2, 3...).
    @Column(name = "orden", nullable = false)
    private Integer orden;

    @Column(name = "fecha_creacion", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}
