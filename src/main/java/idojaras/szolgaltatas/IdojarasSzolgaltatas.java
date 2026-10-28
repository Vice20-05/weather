package idojaras.szolgaltatas;

import idojaras.model.Idojaras;
import idojaras.model.IdojarasKivetel;

/**
 * Everybody depends on this interface, not on a concrete class (DIP).
 * The UI / controller has no idea if the data comes from HTTP or a mock.
 */
public interface IdojarasSzolgaltatas {

    Idojaras aktualis(double lat, double lon) throws IdojarasKivetel;
}
