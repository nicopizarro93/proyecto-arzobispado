package cl.duoc.aurasacrapp_api.multimedia.dto;

import cl.duoc.aurasacrapp_api.multimedia.RecursoMultimedia;
import cl.duoc.aurasacrapp_api.multimedia.TipoRecurso;

/** Un elemento de la galería: la app lo muestra según su tipo (imagen, video o audio). */
public record RecursoMultimediaDto(Long id, TipoRecurso tipo, String url, String titulo) {

    public static RecursoMultimediaDto desde(RecursoMultimedia r) {
        return new RecursoMultimediaDto(r.getId(), r.getTipo(), r.getUrl(), r.getTitulo());
    }
}