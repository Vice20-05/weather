package idojaras.ui;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import idojaras.model.Idojaras;
import idojaras.model.IdojarasKivetel;
import idojaras.model.Varos;
import idojaras.szolgaltatas.IdojarasSzolgaltatas;

/**
 * Controller test with Mockito: both the service and the view are mocks,
 * so no window opens and there is no network.
 *
 * The SwingWorker finishes on another thread, that's why we use
 * verify(..., timeout(2000)) -> "wait max 2 seconds for this call".
 */
public class IdojarasControllerTest {

    private IdojarasSzolgaltatas szolgaltatas;
    private IdojarasNezet nezet;
    private IdojarasController controller;
    private final Varos budapest = new Varos("Budapest", 47.50, 19.04);

    @BeforeEach
    public void setUp() {
        szolgaltatas = mock(IdojarasSzolgaltatas.class);
        nezet = mock(IdojarasNezet.class);
        when(nezet.getValasztottVaros()).thenReturn(budapest);
        controller = new IdojarasController(szolgaltatas, nezet);
    }

    @Test
    public void successfulRefreshShowsWeather() throws IdojarasKivetel {
        Idojaras adat = new Idojaras(12.3, 4.5, "2026-10-28T12:00");
        when(szolgaltatas.aktualis(47.50, 19.04)).thenReturn(adat);

        controller.frissit();

        verify(nezet, timeout(2000)).mutatIdojaras(budapest, adat);
        verify(nezet, never()).mutatHiba(any());
    }

    @Test
    public void errorIsShownToTheUser() throws IdojarasKivetel {
        when(szolgaltatas.aktualis(anyDouble(), anyDouble()))
                .thenThrow(new IdojarasKivetel("Hálózati hiba"));

        controller.frissit();

        verify(nezet, timeout(2000)).mutatHiba("Hálózati hiba");
        verify(nezet, never()).mutatIdojaras(any(), any());
    }

    @Test
    public void buttonIsDisabledThenEnabledAgain() throws IdojarasKivetel {
        when(szolgaltatas.aktualis(anyDouble(), anyDouble()))
                .thenReturn(new Idojaras(1, 1, "t"));

        controller.frissit();

        // wait until the button is turned back on
        verify(nezet, timeout(2000)).setFrissitesEngedelyezve(true);

        // order: disable -> show data -> enable
        InOrder sorrend = inOrder(nezet);
        sorrend.verify(nezet).setFrissitesEngedelyezve(false);
        sorrend.verify(nezet).mutatIdojaras(any(), any());
        sorrend.verify(nezet).setFrissitesEngedelyezve(true);
    }

    @Test
    public void buttonIsEnabledAgainAfterError() throws IdojarasKivetel {
        when(szolgaltatas.aktualis(anyDouble(), anyDouble()))
                .thenThrow(new IdojarasKivetel("hiba"));

        controller.frissit();

        verify(nezet, timeout(2000)).setFrissitesEngedelyezve(true);
    }
}
