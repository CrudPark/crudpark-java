// exceptions/BusinessException.java
package exceptios; // O en el paquete que prefieras

/**
 * Excepción no verificada (RuntimeException) para errores de negocio.
 * Permite que el Controller propague mensajes de error detallados sin
 * obligar a la View a declarar 'throws'.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}