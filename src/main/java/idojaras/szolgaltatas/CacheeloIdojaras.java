package idojaras.szolgaltatas;

import java.util.HashMap;
import java.util.Map;

import idojaras.model.Idojaras;
import idojaras.model.IdojarasKivetel;

/**
 * Decorator (stretch task): wraps another IdojarasSzolgaltatas and
 * remembers the answer for 10 minutes for the same coordinates.
 * It implements the same interface, so nobody notices the difference.
 */
public class CacheeloIdojaras implements IdojarasSzolgaltatas {

    private static final long ERVENYESSEG_MS = 10 * 60 * 1000; // 10 minutes

    // one saved answer + when it was saved
    private record Mentett(Idojaras idojaras, long mikor) {
    }

    private final IdojarasSzolgaltatas belso; // the wrapped (real or mock) service
    private final Map<String, Mentett> cache = new HashMap<>();

    public CacheeloIdojaras(IdojarasSzolgaltatas belso) {
        this.belso = belso;
    }

    @Override
    public synchronized Idojaras aktualis(double lat, double lon) throws IdojarasKivetel {
        String kulcs = lat + "," + lon;
        long most = System.currentTimeMillis();

        Mentett mentett = cache.get(kulcs);
        if (mentett != null && most - mentett.mikor() < ERVENYESSEG_MS) {
            return mentett.idojaras(); // still fresh -> no call
        }

        // not in the cache or too old -> ask the wrapped service
        // (if it throws, nothing gets saved)
        Idojaras uj = belso.aktualis(lat, lon);
        cache.put(kulcs, new Mentett(uj, most));
        return uj;
    }
}
