package DJ.Exception;

public class ServerError extends RuntimeException {
    public ServerError(String serverName, String error) {
        super(serverName + ": " + error);
    }

    public ServerError(String serverName, String error, Throwable cause) {
        super(serverName + ": " + error, cause);
    }
}
