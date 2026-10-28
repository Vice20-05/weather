package idojaras.model;

/**
 * A city with its coordinates (for the JComboBox).
 */
public record Varos(String nev, double lat, double lon) {

    // the JComboBox shows toString(), so only the name is returned
    @Override
    public String toString() {
        return nev;
    }
}
