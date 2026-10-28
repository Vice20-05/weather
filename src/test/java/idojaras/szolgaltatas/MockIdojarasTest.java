package idojaras.szolgaltatas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import idojaras.model.Idojaras;
import idojaras.model.IdojarasKivetel;

/**
 * Tests for the mock implementation (good branch + error branch).
 */
public class MockIdojarasTest {

    @Test
    public void returnsConfiguredAnswer() throws IdojarasKivetel {
        MockIdojaras mock = new MockIdojaras();
        Idojaras beallitott = new Idojaras(-5.0, 30.0, "2026-12-24T18:00");
        mock.setValasz(beallitott);

        assertEquals(beallitott, mock.aktualis(46.54, 24.56));
    }

    @Test
    public void throwsWhenErrorIsSwitchedOn() {
        MockIdojaras mock = new MockIdojaras();
        mock.setHibatDob(true);

        assertThrows(IdojarasKivetel.class, () -> mock.aktualis(46.54, 24.56));
    }
}
