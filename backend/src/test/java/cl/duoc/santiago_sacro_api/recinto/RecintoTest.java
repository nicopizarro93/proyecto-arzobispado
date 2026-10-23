package cl.duoc.santiago_sacro_api.recinto;

import cl.duoc.santiago_sacro_api.comuna.Comuna;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecintoTest {

    private Recinto recinto(String calle, String numero) {
        Comuna maipu = new Comuna();
        maipu.setNombre("Maipú");
        Recinto r = new Recinto();
        r.setCalle(calle);
        r.setNumeroCalle(numero);
        r.setComuna(maipu);
        return r;
    }

    @Test
    void direccionConNumero() {
        assertEquals("Pasaje El Llano 118, Maipú", recinto("Pasaje El Llano", "118").direccionCompleta());
    }

    @Test
    void direccionSinNumero() {
        assertEquals("Calle Los Talleres, Maipú", recinto("Calle Los Talleres", null).direccionCompleta());
    }
}
