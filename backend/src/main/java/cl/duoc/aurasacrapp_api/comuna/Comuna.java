package cl.duoc.aurasacrapp_api.comuna;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "comuna")
@Getter
@Setter
@NoArgsConstructor
public class Comuna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comuna")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 60)
    private String nombre;
}
