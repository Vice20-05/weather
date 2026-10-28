package idojaras.ui;

import idojaras.model.Idojaras;
import idojaras.model.Varos;

/**
 * What the controller needs from the view.
 * The Swing window implements it, in the tests Mockito fakes it.
 */
public interface IdojarasNezet {

    Varos getValasztottVaros();

    void setFrissitesEngedelyezve(boolean engedelyezve);

    void mutatIdojaras(Varos varos, Idojaras idojaras);

    void mutatHiba(String uzenet);
}
