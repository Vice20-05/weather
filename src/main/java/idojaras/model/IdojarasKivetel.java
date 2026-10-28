package idojaras.model;

/**
 * Our own exception. Network errors, bad HTTP status and broken JSON
 * are all wrapped into this, so the caller only has to handle one type.
 */
public class IdojarasKivetel extends Exception {

    public IdojarasKivetel(String uzenet) {
        super(uzenet);
    }

    // ok = the original exception (cause), so it is not lost
    public IdojarasKivetel(String uzenet, Throwable ok) {
        super(uzenet, ok);
    }
}
