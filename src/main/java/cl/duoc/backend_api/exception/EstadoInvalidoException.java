package cl.duoc.backend_api.exception;

public class EstadoInvalidoException extends RuntimeException {

    public EstadoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
