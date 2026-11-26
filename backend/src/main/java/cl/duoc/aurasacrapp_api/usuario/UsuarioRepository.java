package cl.duoc.aurasacrapp_api.usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByCorreo(String correo);

    Optional<Usuario> findByCorreo(String correo);

    // Cuentas de un rol, por orden alfabético (HU-34: el administrador lista a los encargados).
    List<Usuario> findByRolOrderByApellidoAscNombreAsc(Rol rol);

    // ¿Ese recinto ya tiene una cuenta activa de ese rol? Un recinto no tiene dos encargados activos.
    boolean existsByRolAndRecintoIdAndActivoTrue(Rol rol, Long idRecinto);
}
