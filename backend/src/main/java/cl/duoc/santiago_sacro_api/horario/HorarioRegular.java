package cl.duoc.santiago_sacro_api.horario;

import cl.duoc.santiago_sacro_api.recinto.Recinto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "horario_regular")
@Getter
@Setter
@NoArgsConstructor
public class HorarioRegular {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_horario")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_recinto", nullable = false)
    private Recinto recinto;

    // 1 = lunes ... 7 = domingo (ISO 8601), igual que en la tabla.
    @Column(name = "dia_semana", nullable = false)
    private Integer diaSemana;

    @Column(name = "hora_apertura", nullable = false)
    private LocalTime horaApertura;

    @Column(name = "hora_cierre", nullable = false)
    private LocalTime horaCierre;
}
