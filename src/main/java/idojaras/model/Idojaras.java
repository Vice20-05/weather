package idojaras.model;

/**
 * The current weather. Record -> immutable, equals/hashCode/toString for free.
 *
 * @param homerseklet temperature in °C
 * @param szel        wind speed in km/h
 * @param idopont     time of the measurement, e.g. "2026-10-28T20:15"
 */
public record Idojaras(double homerseklet, double szel, String idopont) {
}
