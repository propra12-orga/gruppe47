package einstellung;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class einstellung extends JDialog
implements ActionListener {
    private int[][] keys = null;
    /** tasten werden gesetzt */
    private int[] keysBeingSet = { -1, -1 };
    /** warte auf tasten */
    private boolean waitingForKey = false;
    /** Button das erlaubt die Taste zubelegen */
    private JButton[][] buttons = null;
    /** Textfeld das die Taste anzeigt */
    private JTextField[][] keyFields = null;

    /**
     * Konstruiert das Dialog.
     */
    public einstellung(JFrame owner) {
        super(owner, "Bomberman Tasten Einstellung", true);

        /** erstellt die temporäre tasten */
        keys = new int[4][5];
        for (int i = 0; i < 4; i++) for (int j = 0; j < 5; j++)
            keys[i][j] = tasten.keys[i][j];

        /** Panel Konfiguration aufrufen */
        JPanel centerPanel = new JPanel(new GridLayout(2, 2));
        JPanel[] panels = new JPanel[4];
        keyFields = new JTextField[4][];
        buttons = new JButton[4][5];
        for (int i = 0; i < 4; i++) {
            keyFields[i] = new JTextField[5];
            setupPanel(i, centerPanel, panels[i], keyFields[i]);
        }

        /** Panel für die Hilfenachricht */
        JPanel helpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        helpPanel.setBorder(BorderFactory.createEtchedBorder());

        helpPanel.add(new JLabel("Zum Ändern, klick auf die buttons.",
        JLabel.CENTER));

        getContentPane().add(helpPanel, "North");
        getContentPane().add(centerPanel, "Center");
        JPanel buttonsP = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonsP.setBorder(BorderFactory.createEtchedBorder());
        JButton saveButton = new JButton("Einstellung speichern");
        saveButton.addActionListener(this);
        buttonsP.add(saveButton);
        /** erstelle Schliessen */
        JButton closeButton = new JButton("Schließen");
        closeButton.addActionListener(this);
        buttonsP.add(closeButton);
        getContentPane().add(buttonsP, "South");

        setResizable(false);
        pack();

        int x = owner.getLocation().x + (owner.getSize().width -
                getSize().width) / 2;
        int y = owner.getLocation().y + (owner.getSize().height -
                getSize().height) / 2;

        setLocation(x, y);
        show();
    }

    /**
     * Erstellt ein panel für jeden Spieler.
     * @param pn Spielernummer
     * @param m master panel
     * @param p Spieler's panel
     * @param fields tasten felder
     */
    private void setupPanel(int pn, JPanel m, JPanel p, JTextField[] fields)
    {
        JPanel left = new JPanel(new GridLayout(5, 1));
        JPanel right = new JPanel(new GridLayout(5, 1));

        /** erstelle button und textfeld */
        for (int i = 0; i < 5; i++) {
            buttons[pn][i] = new JButton();
            fields[i] = new JTextField(10);
            switch (i) {
                case 0: buttons[pn][i].setText("Hoch"); break;
                case 1: buttons[pn][i].setText("Runter"); break;
                case 2: buttons[pn][i].setText("Links"); break;
                case 3: buttons[pn][i].setText("Rechts"); break;
                case 4: buttons[pn][i].setText("Bombe"); break;
            }
            buttons[pn][i].addActionListener(this);
            fields[i].setText(KeyEvent.getKeyText(keys[pn][i]));
            fields[i].setEditable(false);
            left.add(buttons[pn][i]);
            right.add(fields[i]);
        }

        /** Erstellt Spieler panel */
        p = new JPanel(new GridLayout(1, 2));
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.
        createEtchedBorder(), "Spieler " + (pn + 1) + " Tasten-Konfiguration"));
        p.add(left);
        p.add(right);
        m.add(p);
    }

    public void actionPerformed(ActionEvent evt) {
        /** wenn speichern gedrückt wird */
        if (evt.getActionCommand().equals("Speichere Konfiguration")) {
            /** kopiere die neuen tasten */
            for (int i = 0; i < 4; i++) for (int j = 0; j < 5; j++)
                tasten.keys[i][j] = keys[i][j];
            /** schreibe in die datei */
            tasten.writeFile();
            dispose();
        }
        /** wenn button schliessen gedrückt wurde */
        else if (evt.getActionCommand().equals("Schließen")) dispose();
        /**wenn andere buttons gedrückt werden */
        else {
            /** finde welche tasten einstellung gedrückt wurde */
            int i = 0, j = 0;
            boolean found = false;
            for (i = 0; i < 4; ++i) {
                for (j = 0; j < 5; ++j) {
                    /** wenn taste gefunden */
                    if (evt.getSource().equals(buttons[i][j])) found = true;
                    if (found) break;
                }
                if (found) break;
            }
            keysBeingSet[0] = i;
            keysBeingSet[1] = j;
            waitingForKey = true;
            new GetKeyDialog(this, "Drücke eine Taste...", true);
        }
    }

    /**
     * In der Klasse wird eine neue taste bekommen
     */
    private class GetKeyDialog extends JDialog {
        private JDialog me = null;
        public GetKeyDialog(JDialog owner, String title, boolean modal) {
            setTitle(title);
            setModal(modal);
            me = this;

            addKeyListener(new KeyAdapter() {
                /**
                 * @param evt keyboard event
                 */
                public void keyPressed(KeyEvent evt) {
                    if (waitingForKey) {
                        int i = keysBeingSet[0];
                        int j = keysBeingSet[1];
                        int newKey = evt.getKeyCode();
                        boolean keyUsed = false;
                        for (int p = 0; p < 4; ++p) {
                            for (int k = 0; k < 5; ++k) {
                                if (keys[p][k] == newKey) {
                                    if (!(p == i && j == k))
                                       keyUsed = true;
                                }
                                if (keyUsed) break;
                            }
                            if (keyUsed) break;
                        }
                        if (!keyUsed) {
                            keys[i][j] = newKey;
                            keyFields[i][j].setText(
                            KeyEvent.getKeyText(keys[i][j]));
                            waitingForKey = false;
                            dispose();
                        }
                        /** wenn taste benutzt wird */
                        else {
                            JOptionPane pane = new JOptionPane(
                            "Taste: [" + KeyEvent.getKeyText(newKey) +
                            "] wird bereits benutzt.  Eine andere Taste waehlen.");
                            pane.setOptionType(-JOptionPane.NO_OPTION);
                            pane.setMessageType(JOptionPane.ERROR_MESSAGE);
                            JDialog dialog = pane.createDialog(me, "Fehler");
                            dialog.setResizable(false);
                            dialog.show();
                        }
                    }
                }
            });

            setResizable(false);
            setSize(300, 0);

            int x = owner.getLocation().x + (owner.getSize().width -
            getSize().width) / 2;
            int y = owner.getLocation().y + (owner.getSize().width -
            getSize().height) / 2;
            setLocation(x, y);

            show();
        }
    }
}
