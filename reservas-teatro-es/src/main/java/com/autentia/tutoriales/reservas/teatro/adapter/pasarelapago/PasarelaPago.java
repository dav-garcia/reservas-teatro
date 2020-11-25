package com.autentia.tutoriales.reservas.teatro.adapter.pasarelapago;

public interface PasarelaPago {

    String iniciarPago(String cliente, String descripcion, int valor);
}
