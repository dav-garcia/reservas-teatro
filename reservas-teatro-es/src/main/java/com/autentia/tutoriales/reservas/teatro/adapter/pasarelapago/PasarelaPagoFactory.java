package com.autentia.tutoriales.reservas.teatro.adapter.pasarelapago;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Implementación totalmente dummy; no es una Abstract Factory de verdad
 */
public abstract class PasarelaPagoFactory {

    private static final PasarelaPago INSTANCE = (cliente, descripcion, valor) -> RandomStringUtils.randomAlphanumeric(8);

    public static PasarelaPago getPasarelaPago() {
        return INSTANCE;
    }
}
