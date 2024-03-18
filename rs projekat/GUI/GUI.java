package GUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import Logika.Logika;

/**
 * Klasa VisualUserInterface predstavlja grafički korisnički interfejs igre  2048.
 * GameFrame ekstenduje klasu JFrame i implementira interfejs KeyListener za obradu korisničkih ulaznih podataka.
 * Klasa sadrži metode za crtanje tablice igre i postavljanje pitanja korisniku o ponovnom pokretanju igre.
 */
public class GUI {

    /**
     * JFrame koji predstavlja prozor igre za igru  2048.
     * Implementira interfejs KeyListener za obradu korisničkih ulaznih podataka.
     */
    private static class GameFrame extends JFrame implements KeyListener {
    	
    	// za čuvanje igre
    	private JButton saveButton;
        // za učitavanje igre
        private JButton loadButton;
    	
        @Override
        public void paint(Graphics g) {
            // Poziva osnovnu metodu paint i crta  tablicu
            super.paint(g);
            draw(g);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            // Obavlja obradu pritisnutog tastera
            handleKeypress(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // Ne radi ništa
        }
        @Override
        public void keyTyped(KeyEvent e) {
            // Ne radi ništa
        }

        GameFrame() {
            super("2048");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(500,  600); 
            setLayout(null); 
            setVisible(true);
            addKeyListener(this);
            setFocusable(true);
            setFocusTraversalKeysEnabled(false);
            setLocationRelativeTo(null);

            // Kreira i pozicionira dugmadi
            saveButton = new JButton("Save");
            saveButton.setBounds(40,  510,  100,  30);
            saveButton.addActionListener(e -> {
            	// Čuva igru
            	game.saveGame();
            	// Osvježava tablicu igre (iz nekog razloga se ne moze nastaviti igrati ako ne pozovem ovu funkciju
            	board = game.getBoard();
                // Resetuje status igre
                gameStatus =  0;
                // Ponovo crta igračku tablicu
                frame.paint(frame.getGraphics());
                // Vraća fokus na prozor
                frame.requestFocus();
            }); 
            add(saveButton);

            loadButton = new JButton("Load");
            loadButton.setBounds(160,  510,  100,  30);
            loadButton.addActionListener(e -> {
            	// Učitava igru
            	game.loadGame();
                // Osvježava tablicu igre
                board = game.getBoard();
                // Resetuje status igre
                gameStatus =  0;
                // Ponovo crta igračku tablicu
                frame.paint(frame.getGraphics());
                // Vraća fokus na prozor
                frame.requestFocus();
            }); 
            add(loadButton);
        }
    }
    
    // Instanca prozora igre
    private static GameFrame frame = new GameFrame();
    // Tablica igre
    private static int[][] board = new int[4][4];
    // Konstanta za veličinu  ćelije
    private static final int CELL_SIZE =  100;
    // Instanca klase za logiku
    private static Logika game;
    // Status igre
    private static int gameStatus =  0;
    

    /**
     * Crta igračku tablicu i skor na ekranu.
     * @param g objekt Graphics na kojem se crta
     */
    private static void draw(Graphics g) {
        int x =  50;
        int y =  50;

        // Iterira kroz svaku  ćeliju tablice
        for (int i =  0; i <  4; i++) {
            for (int j =  0; j <  4; j++) {
                // Vrijednost  ćelije
                int value = board[i][j];
                // Crta  ćeliju
                drawCell(g, x, y, value);
                // Pomak za sljedeću  ćeliju
                x += CELL_SIZE;
            }
            // Resetuje x za novi red
            x =  50;
            // Pomak za sljedeći red
            y += CELL_SIZE;
        }
        
        // Skor i najviši skor
        int score = game.getScore();
        int highScore = game.getHighScore();
        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("Arial", Font.BOLD,   24));
        // Formatirani string za skor
        String scoreStr = "Score: " + score;
        int strWidth = g.getFontMetrics().stringWidth(scoreStr);
        // Crta string skora
        g.drawString(scoreStr,   50,   490);
        // Formatirani string za najviši skor
        String hScore = "High score: " + highScore;
        // Crta string najvišeg skora
        g.drawString(hScore,   50,   520);

        // Ako je igra završila
        if (gameStatus !=   0) {
            // Postavlja boju za preklapanje
            g.setColor(new Color(0,   0,   0,   0.5f));
            // Prekriva cijeli prozor
            g.fillRect(0,   0,   500,   500);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD,   24));
            // Ako je igra pobijedena
            if (gameStatus ==   1) {
                // Poruka za pobijedenu igru
                String prompt = "You won!";
                strWidth = g.getFontMetrics().stringWidth(prompt);
                g.drawString(prompt,   250 - strWidth /  2,   200);
                
                //Konačni skor
                String fscore = "Final score: " + score;
                strWidth = g.getFontMetrics().stringWidth(fscore);
                g.drawString(fscore,   250 - strWidth /  2,   225);

                // Pitanje za nastavak igre
                prompt = "Continue? (y/n)";
                strWidth = g.getFontMetrics().stringWidth(prompt);
                g.drawString(prompt,   250 - strWidth /  2,   250);
            } else {
                // Poruka za izgubljenu igru
                String prompt = "You lost!";
                strWidth = g.getFontMetrics().stringWidth(prompt);
                g.drawString(prompt,   250 - strWidth /  2,   200);
                
                String fscore = "Final score: " + score;
                strWidth = g.getFontMetrics().stringWidth(fscore);
                g.drawString(fscore,   250 - strWidth /  2,   225);

                prompt = "Play again? (y/n)";
                strWidth = g.getFontMetrics().stringWidth(prompt);
                g.drawString(prompt,   250 - strWidth /  2,   250);
            }
        }
    }

    /**
     * Crta jednu   ćeliju na igračkoj tablici s datom vrijednošću na određenim koordinatama.
     * @param g objekat Graphics na kojem se crta
     * @param x x-koordinata   ćelije
     * @param y y-koordinata   ćelije
     * @param value vrijednost   ćelije koja se treba crtati
     */
    private static void drawCell(Graphics g, int x, int y, int value) {
        Color bgColor = Color.LIGHT_GRAY;
        Color textColor = value >  4 ? Color.WHITE : Color.DARK_GRAY;
        // Niz boja za   ćelije
        final int[] colors = {0xeee4da,  0xede0c8,  0xf2b179,  0xf59563,  0xf67c5f,  0xf65e3b,  0xedcf72,  0xedcc61,  0xedc850,  0xedc53f,  0xedc22e};

        // Ako je vrijednost   ćelije veća od nula
        if (value !=   0) {
            // Izračunava logaritam vrijednosti
            int log = (int) (Math.log(value) / Math.log(2));
            // Postavlja boju pozadine na temelju logaritma
            bgColor = new Color(colors[log -  1]);
        }

        g.setColor(bgColor);
        // Popunjava   ćeliju bojom
        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        // Postavlja boju okvira
        g.setColor(Color.DARK_GRAY);
        // Crta okvir   ćelije
        g.drawRect(x, y, CELL_SIZE, CELL_SIZE);

        // Ako je vrijednost   ćelije nula, ne crta se ništa više
        if (value ==   0)
            return;

        g.setColor(textColor);
        g.setFont(new Font("Arial", Font.BOLD,   24));
        String valueStr = String.valueOf(value);
        int strWidth = g.getFontMetrics().stringWidth(valueStr);
        // Crta vrijednost   ćelije
        g.drawString(valueStr, x + CELL_SIZE /   2 - strWidth /   2, y + CELL_SIZE /   2 +  10);
    }
    
    /**
     * Rukuje sa događajem pritisnutog tastera i procesuje potez prema tome.
     * Ako je igra završila, postavlja pitanje korisniku o ponovnom pokretanju ili nastavku igre.
     *
     * @param e događaj pritisnutog tastera koji pokreće poziv metode
     */
    private static void handleKeypress(KeyEvent e) {
    	
    	// Ako je igra pobijedena
    	if (gameStatus ==   1) {
            // Nastavlja igru
            continuee(e);
            return;
        }
    	
    	// Ako je igra završila
    	else if (gameStatus !=   0) {
            // Postavlja pitanje o ponovnom pokretanju igre
            restartPrompt(e);
            return;
        }
        // Status poteza
        int status = -1;
        // Odigraj potez na osnovu pritisnute strelice
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                // Procesuje pokret lijevo
                status = game.processMove('u');
                break;
            case KeyEvent.VK_RIGHT:
                // Procesuje pokret desno
                status = game.processMove('d');
                break;
            case KeyEvent.VK_UP:
                // Procesuje pokret gore
                status = game.processMove('l');
                break;
            case KeyEvent.VK_DOWN:
                // Procesuje pokret dole
                status = game.processMove('r');
                break;
            default:
                break;
        }
        // Ako nije validan status, ne radi ništa
        if (status == -1)
            return;
        //Osvježava tablicu nakon svakog poteza
        board = game.getBoard();
        frame.repaint();
        if (status ==   0)
            return;
        gameStatus = status;
    }

    /**
     * Postavlja pitanje korisniku o ponovnom pokretanju igre ili izlasku iz aplikacije na temelju pritisnutog tastera.
     * Ako je pritisnut taster 'Y', igra se ponovno pokreće. Ako je pritisnut taster 'N', aplikacija se izlazi.
     * @param e KeyEvent koji je pokrenuo poziv metode
     */
    //Ovo je ako je igrač izgubio igru
    private static void restartPrompt(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_Y:
                // Pokreće igru
                startGame();
                break;
            case KeyEvent.VK_N:
                // Izlazi iz aplikacije
                System.exit(0);
                break;
            default:
                break;
        }
    }
    
    //Ovo je ako je igrač pobijedio
    private static void continuee(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_Y:
            	board = game.getBoard();
                gameStatus =   0;
                frame.paint(frame.getGraphics());
                frame.requestFocus();
                
            case KeyEvent.VK_N:
                // Izlazi iz aplikacije
                System.exit(0);
                break;
            default:
                break;
        }
    }

    
    /**
     * Inicijalizira igračku tablicu na sve nule.
     */
    public GUI() {
        // Iterira kroz svaki red i kolonu tablice
        for (int i =   0; i <   4; i++) {
            for (int j =   0; j <   4; j++)
                // Postavlja svaku   ćeliju na nulu
                board[i][j] =   0;
        }
    }

    /**
     * Pokreće igru tako što stvara novu instancu klase Game, pokreće igru, dobiva tablicu igre, postavlja status igre na nulu, crta grafiku i traži fokus.
     */
    public static void startGame() {
        // Stvara novu instancu klase Game
        game = new Logika();
        // Pokreće igru
        game.startGame();
        // Dobija tablicu igre
        board = game.getBoard();
        // Resetuje status igre
        gameStatus =   0;
        // Ponovo crta  tablicu
        frame.paint(frame.getGraphics());
        // Traži fokus na prozoru
        frame.requestFocus();
    }
}

