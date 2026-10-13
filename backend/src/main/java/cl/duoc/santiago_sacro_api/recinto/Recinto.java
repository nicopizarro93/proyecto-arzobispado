package cl.duoc.santiago_sacro_api.recinto;

import cl.duoc.santiago_sacro_api.comuna.Comuna;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recinto")
@Getter
@Setter
@NoArgsConstructor
public class Recinto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recinto")
    private Long id;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "tipo_recinto", nullable = false, length = 12)
    private TipoRecinto tipoRecinto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_comuna", nullable = false)
    private Comuna comuna;

    @Column(name = "nombre_oficial", nullable = false, length = 150)
    private String nombreOficial;

    @Column(name = "calle", nullable = false, length = 150)
    private String calle;

    @Column(name = "numero_calle", length = 10)
    private String numeroCalle;

    @Column(name = "latitud", nullable = false, precision = 9, scale = 6)
    private BigDecimal latitud;

    @Column(name = "longitud", nullable = false, precision = 9, scale = 6)
    private BigDecimal longitud;

    @Column(name = "descripcion_historica", columnDefinition = "TEXT")
    private String descripcionHistorica;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "estado_apertura", nullable = false, length = 15)
    private EstadoApertura estadoApertura;

    @Column(name = "fecha_estado", nullable = false)
    private LocalDateTime fechaEstado;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    // Estas dos fechas las pone MySQL; Java solo las lee.
    @Column(name = "fecha_creacion", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", insertable = false, updatable = false)
    private LocalDateTime fechaActualizacion;

    /** Dirección legible, por ejemplo "Pasaje El Llano 118, Maipú". */
    public String direccionCompleta() {
        String numero = (numeroCalle == null || numeroCalle.isBlank()) ? "" : " " + numeroCalle;
        return calle + numero + ", " + comuna.getNombre();
    }
}

/*
@ManyToOne es la relación con Comuna: muchos recintos pertenecen a una comuna. LAZY significa que la comuna solo se carga cuando se pide.
@JdbcTypeCode(SqlTypes.VARCHAR) es necesario porque, sin esa línea, Hibernate espera en MySQL una columna de tipo ENUM, mientras que la nuestra es VARCHAR con CHECK. Sin ella, la validación del arranque falla.
@Getter, @Setter y @NoArgsConstructor son de Lombok y generan los getters, los setters y el constructor vacío que exige JPA.
 */
