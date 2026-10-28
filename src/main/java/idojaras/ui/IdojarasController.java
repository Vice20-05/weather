package idojaras.ui;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import idojaras.model.Idojaras;
import idojaras.model.Varos;
import idojaras.szolgaltatas.IdojarasSzolgaltatas;

/**
 * Connects the view and the service.
 * Both come in through the constructor (dependency injection) and both are
 * interfaces -> the controller doesn't know about HTTP or about Swing components.
 */
public class IdojarasController {

    private final IdojarasSzolgaltatas szolgaltatas;
    private final IdojarasNezet nezet;

    public IdojarasController(IdojarasSzolgaltatas szolgaltatas, IdojarasNezet nezet) {
        this.szolgaltatas = szolgaltatas;
        this.nezet = nezet;
    }

    /**
     * Called when the Frissítés button is pressed (on the EDT).
     * The slow network call runs in a SwingWorker on a background thread,
     * so the window doesn't freeze.
     */
    public void frissit() {
        Varos varos = nezet.getValasztottVaros();
        nezet.setFrissitesEngedelyezve(false); // no double clicks while loading

        SwingWorker<Idojaras, Void> worker = new SwingWorker<>() {

            // background thread: allowed to be slow, must NOT touch the UI
            @Override
            protected Idojaras doInBackground() throws Exception {
                return szolgaltatas.aktualis(varos.lat(), varos.lon());
            }

            // back on the EDT: here we can update the UI
            @Override
            protected void done() {
                try {
                    nezet.mutatIdojaras(varos, get());
                } catch (ExecutionException e) {
                    // get() wraps the exception from doInBackground -> getCause() is our IdojarasKivetel
                    nezet.mutatHiba(e.getCause().getMessage());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    nezet.mutatHiba("A lekérdezés megszakadt.");
                } finally {
                    nezet.setFrissitesEngedelyezve(true); // always turn the button back on
                }
            }
        };
        worker.execute();
    }
}
