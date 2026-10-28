package idojaras.szolgaltatas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import idojaras.model.Idojaras;
import idojaras.model.IdojarasKivetel;

/**
 * JSON parsing tests from fixed strings -> no network needed.
 */
public class OpenMeteoKliensTest {

    // a real answer copied from the API
    private static final String JO_JSON = """
            {"latitude":46.54601,"longitude":24.56224,"generationtime_ms":0.037,
             "utc_offset_seconds":0,"timezone":"GMT","timezone_abbreviation":"GMT","elevation":330.0,
             "current_units":{"time":"iso8601","interval":"seconds","temperature_2m":"°C","wind_speed_10m":"km/h"},
             "current":{"time":"2026-10-28T20:15","interval":900,"temperature_2m":10.2,"wind_speed_10m":2.0}}
            """;

    @Test
    public void parsesValidJson() throws IdojarasKivetel {
        Idojaras i = OpenMeteoKliens.feldolgoz(JO_JSON);
        assertEquals(10.2, i.homerseklet(), 0.001);
        assertEquals(2.0, i.szel(), 0.001);
        assertEquals("2026-10-28T20:15", i.idopont());
    }

    @Test
    public void missingCurrentPartThrows() {
        String json = "{\"latitude\":46.5,\"longitude\":24.5}";
        assertThrows(IdojarasKivetel.class, () -> OpenMeteoKliens.feldolgoz(json));
    }

    @Test
    public void missingFieldThrows() {
        String json = "{\"current\":{\"time\":\"2026-10-28T20:15\",\"temperature_2m\":10.2}}"; // no wind
        assertThrows(IdojarasKivetel.class, () -> OpenMeteoKliens.feldolgoz(json));
    }

    @Test
    public void brokenJsonIsWrappedInOwnException() {
        IdojarasKivetel e = assertThrows(IdojarasKivetel.class,
                () -> OpenMeteoKliens.feldolgoz("ez nem json {{{"));
        assertEquals("Hibás formátumú válasz érkezett.", e.getMessage());
    }

    @Test
    public void wrongTypeIsWrappedInOwnException() {
        String json = "{\"current\":{\"time\":\"x\",\"temperature_2m\":\"meleg\",\"wind_speed_10m\":2.0}}";
        assertThrows(IdojarasKivetel.class, () -> OpenMeteoKliens.feldolgoz(json));
    }

    @Test
    public void urlUsesDecimalPoint() {
        // must be "46.54" even on a Hungarian system (not "46,54")
        String url = OpenMeteoKliens.urlKeszit(46.54, 24.56);
        assertEquals("https://api.open-meteo.com/v1/forecast?latitude=46.54&longitude=24.56"
                + "&current=temperature_2m,wind_speed_10m", url);
    }
}
