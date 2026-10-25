package cl.duoc.aurasacrapp_api.horario;

import cl.duoc.aurasacrapp_api.horario.dto.HorarioDto;
import cl.duoc.aurasacrapp_api.horario.dto.HorarioRequest;
import cl.duoc.aurasacrapp_api.recinto.Recinto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class HorarioService {

    private final HorarioRegularRepository horarioRepository;

    public HorarioService(HorarioRegularRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
    }

    @Transactional
    public HorarioDto crear(Recinto recinto, HorarioRequest datos) {
        validarSinTraslape(recinto.getId(), datos, null);
        HorarioRegular horario = new HorarioRegular();
        horario.setRecinto(recinto);
        copiar(datos, horario);
        return HorarioDto.desde(horarioRepository.save(horario));
    }

    @Transactional
    public HorarioDto actualizar(Recinto recinto, Long idHorario, HorarioRequest datos) {
        HorarioRegular horario = buscarDelRecinto(recinto.getId(), idHorario);
        validarSinTraslape(recinto.getId(), datos, idHorario);
        copiar(datos, horario);
        return HorarioDto.desde(horario);
    }

    @Transactional
    public void eliminar(Recinto recinto, Long idHorario) {
        horarioRepository.delete(buscarDelRecinto(recinto.getId(), idHorario));
    }

    // Un bloque de otro recinto responde 404, igual que uno que no existe.
    private HorarioRegular buscarDelRecinto(Long idRecinto, Long idHorario) {
        return horarioRepository.findByIdAndRecintoId(idHorario, idRecinto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe el bloque " + idHorario + " en tu recinto"));
    }

    // Dos bloques del mismo día se traslapan si uno empieza antes de que el otro termine.
    // Los bloques seguidos (09:00–13:00 y 13:00–16:00) sí se permiten.
    private void validarSinTraslape(Long idRecinto, HorarioRequest datos, Long idExcluir) {
        boolean traslapa = horarioRepository.findByRecintoIdAndDiaSemana(idRecinto, datos.diaSemana()).stream()
                .filter(h -> !h.getId().equals(idExcluir))
                .anyMatch(h -> h.getHoraApertura().isBefore(datos.horaCierre())
                        && datos.horaApertura().isBefore(h.getHoraCierre()));
        if (traslapa) {
            throw new HorarioTraslapadoException();
        }
    }

    private void copiar(HorarioRequest datos, HorarioRegular horario) {
        horario.setDiaSemana(datos.diaSemana());
        horario.setHoraApertura(datos.horaApertura());
        horario.setHoraCierre(datos.horaCierre());
    }
}
