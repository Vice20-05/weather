package idojaras.szolgaltatas;

import idojaras.model.Idojaras;
import idojaras.model.IdojarasKivetel;

/**
 * Fake implementation: no internet, fixed data.
 * Can be configured to return a given answer or to throw an error on purpose.
 */
public class MockIdojaras implements IdojarasSzolgaltatas {

    private Idojaras valasz = new Idojaras(21.5, 12.0, "2026-01-01T12:00");
    private boolean hibatDob = false;
    private int kesleltetesMs = 0; // fake "network" delay, so the disabled button can be seen

    public void setValasz(Idojaras valasz) {
        this.valasz = valasz;
    }

    public void setHibatDob(boolean hibatDob) {
        this.hibatDob = hibatDob;
    }

    public void setKesleltetesMs(int kesleltetesMs) {
        this.kesleltetesMs = kesleltetesMs;
    }

    @Override
    public Idojaras aktualis(double lat, double lon) throws IdojarasKivetel {
        if (kesleltetesMs > 0) {
            try {
                Thread.sleep(kesleltetesMs); // OK here: this runs in the SwingWorker, not on the EDT
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (hibatDob) {
            throw new IdojarasKivetel("Szimulált hiba (mock): a szolgáltatás nem elérhető.");
        }
        return valasz;
    }
}
