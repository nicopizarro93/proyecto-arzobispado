package cl.duoc.aurasacrapp_api.ruta;

import cl.duoc.aurasacrapp_api.recinto.Recinto;
import cl.duoc.aurasacrapp_api.usuario.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ruta")
@Getter
@Setter
@NoArgsConstructor
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ruta")
    private Long id;

    // Administrador (rutas sugeridas) o usuario dueño (rutas personalizadas).
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario_creador", nullable = false)
    private Usuario creador;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 1000)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "tipo_ruta", nullable = false, length = 15)
    private TipoRuta tipo;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "modo_traslado", nullable = false, length = 20)
    private ModoTraslado modoTraslado = ModoTraslado.A_PIE;

    // Borrado lógico: una ruta desactivada no se muestra, pero no se elimina.
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", insertable = false, updatable = false)
    private LocalDateTime fechaActualizacion;

    // Paradas en orden. Al guardar o borrar la ruta se guardan o borran también sus paradas.
    @OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id.orden")
    private List<ParadaRuta> paradas = new ArrayList<>();

    /** Agrega una parada al final de la ruta. */
    public void agregarParada(Recinto recinto, int tiempoVisitaMin) {
        ParadaRuta parada = new ParadaRuta();
        parada.setRuta(this);
        parada.getId().setOrden(paradas.size() + 1);
        parada.setRecinto(recinto);
        parada.setTiempoVisitaMin(tiempoVisitaMin);
        paradas.add(parada);
    }
}
