package org.wispcrm.excepciones;

public class NotFoundException extends RuntimeException {

    /**
     * 
     */

    private static final long serialVersionUID = 1L;
    private String MENSAJE;

    public NotFoundException(String MENSAJE) {
        this.MENSAJE = MENSAJE;
    }
}
