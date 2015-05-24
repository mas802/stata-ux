package istata.interact;

public class StataNotRunningException extends RuntimeException {

    /**
     * default serial
     */
    private static final long serialVersionUID = 1L;

    public StataNotRunningException( ) { 
        super();
    }

    public StataNotRunningException( String message ) {
        super( message);
    }
}
