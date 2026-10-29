package idojaras;

import javax.swing.SwingUtilities;

import idojaras.szolgaltatas.CacheeloIdojaras;
import idojaras.szolgaltatas.IdojarasSzolgaltatas;
import idojaras.szolgaltatas.MockIdojaras;
import idojaras.szolgaltatas.OpenMeteoKliens;
import idojaras.ui.CyberpunkTheme;
import idojaras.ui.IdojarasAblak;
import idojaras.ui.IdojarasController;

/**
 * The ONLY place where the concrete classes are chosen and created
 * (composition root). Everything else works with the interface.
 *
 * Switches:
 *   (none)   -> real data from Open-Meteo
 *   --mock   -> fixed data, no internet
 *   --hiba   -> mock that always fails (to show the error message)
 */
public class Main {

    public static void main(String[] args) {
        boolean mock = false;
        boolean hiba = false;
        for (String arg : args) {
            if (arg.equals("--mock")) mock = true;
            if (arg.equals("--hiba")) hiba = true;
        }

        IdojarasSzolgaltatas szolgaltatas;
        String cim;
        if (mock || hiba) {
            MockIdojaras mockSzolg = new MockIdojaras();
            mockSzolg.setKesleltetesMs(700); // so the "Betöltés..." state is visible
            mockSzolg.setHibatDob(hiba);
            szolgaltatas = mockSzolg;
            cim = hiba ? "Időjárás (MOCK - hiba mód)" : "Időjárás (MOCK)";
        } else {
            szolgaltatas = new CacheeloIdojaras(new OpenMeteoKliens()); // decorator around the real one
            cim = "Időjárás (Open-Meteo)";
        }

        final IdojarasSzolgaltatas kesz = szolgaltatas;
        final String ablakCim = cim;

        // Swing components must be created on the EDT
        SwingUtilities.invokeLater(() -> {
            CyberpunkTheme.apply(); // dark neon look, before any window exists
            IdojarasAblak ablak = new IdojarasAblak(ablakCim);
            IdojarasController controller = new IdojarasController(kesz, ablak);
            ablak.setController(controller);
            ablak.setVisible(true);
        });
    }
}
