package idojaras.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import idojaras.model.Idojaras;
import idojaras.model.Varos;

/**
 * The Swing window. Only shows things and forwards the button click.
 * It doesn't know the service at all (no HTTP, no IdojarasSzolgaltatas).
 */
public class IdojarasAblak extends JFrame implements IdojarasNezet {

    // at least 5 cities with coordinates
    private static final Varos[] VAROSOK = {
            new Varos("Marosvásárhely", 46.54, 24.56),
            new Varos("Kolozsvár", 46.77, 23.60),
            new Varos("Budapest", 47.50, 19.04),
            new Varos("Debrecen", 47.53, 21.63),
            new Varos("Szeged", 46.25, 20.15),
            new Varos("Bukarest", 44.43, 26.10),
    };

    private final JComboBox<Varos> varosValaszto = new JComboBox<>(VAROSOK);
    private final JButton frissitesGomb = new JButton("FRISSÍTÉS");
    private final JLabel varosCimke = new JLabel("-");
    private final JLabel homersekletCimke = new JLabel("-");
    private final JLabel szelCimke = new JLabel("-");
    private final JLabel idopontCimke = new JLabel("-");

    public IdojarasAblak(String cim) {
        super(cim);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        CyberpunkTheme.neonButton(frissitesGomb, CyberpunkTheme.CYAN);

        // top: title line + city chooser + button
        JLabel fejlec = new JLabel(">> WEATHER_LINK // ONLINE", JLabel.CENTER);
        fejlec.setForeground(CyberpunkTheme.MAGENTA);
        fejlec.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
        fejlec.setBorder(BorderFactory.createEmptyBorder(12, 0, 4, 0));

        JPanel valaszto = new JPanel();
        valaszto.add(new JLabel("Város:"));
        valaszto.add(varosValaszto);
        valaszto.add(frissitesGomb);

        JPanel felso = new JPanel(new BorderLayout());
        felso.add(fejlec, BorderLayout.NORTH);
        felso.add(valaszto, BorderLayout.CENTER);

        // middle: result labels in a 2-column grid
        JPanel eredmeny = new JPanel(new GridLayout(4, 2, 10, 8));
        // neon frame around the data, with a little space inside
        eredmeny.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(10, 20, 20, 20),
                        BorderFactory.createLineBorder(CyberpunkTheme.PURPLE, 2)),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));
        eredmeny.add(new JLabel("Város:"));
        eredmeny.add(varosCimke);
        eredmeny.add(new JLabel("Hőmérséklet:"));
        eredmeny.add(homersekletCimke);
        eredmeny.add(new JLabel("Szél:"));
        eredmeny.add(szelCimke);
        eredmeny.add(new JLabel("Időpont:"));
        eredmeny.add(idopontCimke);

        homersekletCimke.setFont(homersekletCimke.getFont().deriveFont(Font.BOLD, 22f));
        homersekletCimke.setForeground(CyberpunkTheme.YELLOW);
        varosCimke.setForeground(CyberpunkTheme.CYAN);
        szelCimke.setForeground(CyberpunkTheme.CYAN);
        idopontCimke.setForeground(CyberpunkTheme.DIM);

        // whole window: dark background + magenta neon edge
        JPanel tartalom = new JPanel(new BorderLayout());
        tartalom.setBorder(BorderFactory.createLineBorder(CyberpunkTheme.MAGENTA, 2));
        setContentPane(tartalom);

        add(felso, BorderLayout.NORTH);
        add(eredmeny, BorderLayout.CENTER);

        pack();
        setSize(560, 340);
        setLocationRelativeTo(null); // center of the screen
    }

    /**
     * The controller is set from Main. The window only knows that
     * "when the button is pressed, call controller.frissit()".
     */
    public void setController(IdojarasController controller) {
        frissitesGomb.addActionListener(e -> controller.frissit());
    }

    // ---------- IdojarasNezet methods ----------

    @Override
    public Varos getValasztottVaros() {
        return (Varos) varosValaszto.getSelectedItem();
    }

    @Override
    public void setFrissitesEngedelyezve(boolean engedelyezve) {
        frissitesGomb.setEnabled(engedelyezve);
        frissitesGomb.setText(engedelyezve ? "FRISSÍTÉS" : "BETÖLTÉS...");
    }

    @Override
    public void mutatIdojaras(Varos varos, Idojaras idojaras) {
        varosCimke.setText(varos.nev());
        homersekletCimke.setText(idojaras.homerseklet() + " °C");
        szelCimke.setText(idojaras.szel() + " km/h");
        idopontCimke.setText(idojaras.idopont().replace("T", " "));
    }

    @Override
    public void mutatHiba(String uzenet) {
        JOptionPane.showMessageDialog(this, uzenet, "Hiba", JOptionPane.ERROR_MESSAGE);
    }
}
