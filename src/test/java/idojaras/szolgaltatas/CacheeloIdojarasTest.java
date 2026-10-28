package idojaras.szolgaltatas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import idojaras.model.Idojaras;
import idojaras.model.IdojarasKivetel;

/**
 * Tests for the cache decorator. The wrapped service is a Mockito mock,
 * so we can count how many times it was really called.
 */
public class CacheeloIdojarasTest {

    @Test
    public void secondCallComesFromCache() throws IdojarasKivetel {
        IdojarasSzolgaltatas belso = mock(IdojarasSzolgaltatas.class);
        Idojaras adat = new Idojaras(15.0, 5.0, "2026-10-28T12:00");
        when(belso.aktualis(46.54, 24.56)).thenReturn(adat);

        CacheeloIdojaras cache = new CacheeloIdojaras(belso);
        assertEquals(adat, cache.aktualis(46.54, 24.56));
        assertEquals(adat, cache.aktualis(46.54, 24.56));

        verify(belso, times(1)).aktualis(46.54, 24.56); // only once!
    }

    @Test
    public void differentCityIsAskedAgain() throws IdojarasKivetel {
        IdojarasSzolgaltatas belso = mock(IdojarasSzolgaltatas.class);
        when(belso.aktualis(46.54, 24.56)).thenReturn(new Idojaras(15.0, 5.0, "t"));
        when(belso.aktualis(47.50, 19.04)).thenReturn(new Idojaras(18.0, 3.0, "t"));

        CacheeloIdojaras cache = new CacheeloIdojaras(belso);
        cache.aktualis(46.54, 24.56);
        cache.aktualis(47.50, 19.04);

        verify(belso).aktualis(46.54, 24.56);
        verify(belso).aktualis(47.50, 19.04);
    }
}
