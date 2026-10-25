package cl.duoc.aurasacrapp_api.usuario;

import cl.duoc.aurasacrapp_api.recinto.Recinto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "rol", nullable = false, length = 15)
    private Rol rol;

    // Solo los encargados tienen un recinto a cargo; para el resto queda vacío.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_recinto")
    private Recinto recinto;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 50)
    private String apellido;

    @Column(name = "correo", nullable = false, length = 120)
    private String correo;

    // Nunca la contraseña real: solo su hash BCrypt.
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "institucion", length = 120)
    private String institucion;

    // Valores iniciales en Java: si quedaran en null, MySQL rechazaría el INSERT.
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "intentos_fallidos", nullable = false)
    private Integer intentosFallidos = 0;

    @Column(name = "bloqueado_hasta")
    private LocalDateTime bloqueadoHasta;

    @Column(name = "codigo_recuperacion_hash", length = 255)
    private String codigoRecuperacionHash;

    @Column(name = "codigo_recuperacion_expira")
    private LocalDateTime codigoRecuperacionExpira;

    @Column(name = "fecha_acepta_privacidad")
    private LocalDateTime fechaAceptaPrivacidad;

    @Column(name = "fecha_creacion", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", insertable = false, updatable = false)
    private LocalDateTime fechaActualizacion;
}
