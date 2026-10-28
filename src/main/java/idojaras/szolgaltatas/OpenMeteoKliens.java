package idojaras.szolgaltatas;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import idojaras.model.Idojaras;
import idojaras.model.IdojarasKivetel;

/**
 * The real implementation: calls the free Open-Meteo API (no key needed).
 * Only Main is allowed to create it with new.
 */
public class OpenMeteoKliens implements IdojarasSzolgaltatas {

    private static final String ALAP_URL = "https://api.open-meteo.com/v1/forecast";

    // one HttpClient is enough for the whole app
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Override
    public Idojaras aktualis(double lat, double lon) throws IdojarasKivetel {
        String url = urlKeszit(lat, lon);
        HttpRequest keres = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        try {
            HttpResponse<String> valasz = http.send(keres, HttpResponse.BodyHandlers.ofString());
            if (valasz.statusCode() != 200) {
                throw new IdojarasKivetel("A szerver hibát adott vissza: HTTP " + valasz.statusCode());
            }
            return feldolgoz(valasz.body());
        } catch (IOException e) {
            // no internet, timeout, unknown host...
            throw new IdojarasKivetel("Hálózati hiba: nem sikerült elérni az időjárás szervert.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // keep the interrupted flag
            throw new IdojarasKivetel("A lekérdezés megszakadt.", e);
        }
    }

    /**
     * Builds the request URL. Locale.US -> decimal point is "." and not ","
     * (on a Hungarian system "%f" would give 46,54 which the API doesn't accept).
     */
    static String urlKeszit(double lat, double lon) {
        return String.format(Locale.US,
                "%s?latitude=%.2f&longitude=%.2f&current=temperature_2m,wind_speed_10m",
                ALAP_URL, lat, lon);
    }

    /**
     * Turns the JSON text into an Idojaras object.
     * Static and package-visible so it can be tested without network.
     *
     * Expected JSON (only the part we need):
     * { "current": { "time": "2026-10-28T20:15", "temperature_2m": 10.2, "wind_speed_10m": 2.0 } }
     */
    static Idojaras feldolgoz(String json) throws IdojarasKivetel {
        try {
            JsonObject gyoker = JsonParser.parseString(json).getAsJsonObject();
            JsonObject current = gyoker.getAsJsonObject("current");
            if (current == null) {
                throw new IdojarasKivetel("Hiányzik a \"current\" rész a válaszból.");
            }
            if (!current.has("temperature_2m") || !current.has("wind_speed_10m") || !current.has("time")) {
                throw new IdojarasKivetel("Hiányos adat a válaszban.");
            }
            double homerseklet = current.get("temperature_2m").getAsDouble();
            double szel = current.get("wind_speed_10m").getAsDouble();
            String idopont = current.get("time").getAsString();
            return new Idojaras(homerseklet, szel, idopont);
        } catch (RuntimeException e) {
            // Gson throws different RuntimeExceptions for broken JSON or wrong types
            throw new IdojarasKivetel("Hibás formátumú válasz érkezett.", e);
        }
    }
}
