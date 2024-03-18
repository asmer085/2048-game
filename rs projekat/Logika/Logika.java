package Logika;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;

/**
 * Ova klasa predstavlja logiku igre za   2048
 */
public class Logika {
    /**
     * Predstavlja   ćeliju na igračkoj tablici 
     */
    private static class GameCell implements Serializable {
        private static final long serialVersionUID =  1L; // Dodaje serialVersionUID kako bi se izbegle InvalidClassExceptions

        private int value =  0;

        /**
         * Vraća vrijednost   ćelije
         * Ako je   ćelija prazna, vraća  0
         * Inače, vraća vrijednost   ćelije
         *
         * @return vrijednost   ćelije
         */
        int getValue() {
            if (value ==  0)
                return  0;
            return  1 << value;
        }

        /**
         * Postavlja vrijednost   ćelije
         *  
         * @param value vrijednost koju treba postaviti   ćeliji
         */
        void setValue(int value) {
            if (value ==  0) {
                this.value =  0;
                return;
            }
            if (value <  2)
                throw new IllegalArgumentException("Vrijednost mora biti veća od 2");
            int newValue =  0;
            while (value >  1) {
                if (value %  2 !=  0)
                    throw new IllegalArgumentException("Vrijednost mora biti stepen broja  2");
                value /=  2;
                newValue++;
            }
            this.value = newValue;
        }
    }

    // Igračka tablica
    public GameCell[][] board = new GameCell[4][4];
    // Generator slučajnih brojeva
    private Random random = new Random();
    // Da li je dostignut broj  8
    private boolean hasReached8 = false;
    // Trenutni skor
    private int score =  0;
    // Najviši skor
    private int highScore = loadHighScore();

    // Ažurira najviši skor
    public void saveHighScore(int highScore) {
        try {
            FileOutputStream fileOut = new FileOutputStream("highscore.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeInt(highScore);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }
    
    // Učitava najviši skor
    public int loadHighScore() {
        int highScore =  0;
        try {
            FileInputStream fileIn = new FileInputStream("highscore.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            highScore = in.readInt();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } /*catch (ClassNotFoundException c) {
            System.out.println("Najviši skor nije pronađen");
            c.printStackTrace();
        }*/
        return highScore;
    }

    // Vraća najviši skor
    public int getHighScore() { return highScore;}
    

    // Dodaje slučajnu   ćeliju na igračku tablicu
    private void addRandomCell() {
        boolean hasEmptyCell = false;
        for (int y =  0; y <  4; y++) {
            for (int x =  0; x <  4 && !hasEmptyCell; x++) {
                if (board[x][y].getValue() ==  0) {
                    hasEmptyCell = true;
                    break;
                }
            }
        }
        if (!hasEmptyCell)
            return;
        
        int x, y, value;
        if (hasReached8) {
            int r = random.nextInt(100);
            if (r >=  90)
               value =  4;
            else
               value =  2;
        }
        else
            value =  2;
        do {
            x = random.nextInt(4);
            y = random.nextInt(4);
        } while (board[x][y].getValue() !=  0);
        board[x][y].setValue(value);
    }

    // Provjerava da li je dati broj unutar granica igračke tablice
    private static boolean isInBounds(int a) {
        return a >=  0 && a <  16;
    }

    // Provjerava da li su dati koordinati unutar granica igračke tablice
    private static boolean isInBounds(int x, int y) {
        return x >=  0 && x <  4 && y >=  0 && y <  4;
    }

    // Inicijalizira igračku tablicu stvaranjem novih objekata GameCell za svaku   ćeliju i dodavanjem slučajne   ćelije
    public void startGame() {
        for (int y =  0; y <  4; y++) {
            for (int x =  0; x <  4; x++)
                board[x][y] = new GameCell();
        }
        addRandomCell();
    }

 // Obradu pokreta u igri provodi pomicanjem pločica u navedenom smjeru i spajanjem susjednih pločica s istom vrijednošću.
	//  
	//@param move smjer pokreta ('u' za gore, 'd' za dolje, 'l' za lijevo, 'r' za desno)
	//@return cijeli broj koji predstavlja rezultat pokreta:  0 ako igra nastavlja,  1 ako igrač pobjeđuje,  2 ako je igra završila
	//@throws IllegalArgumentException ako je pokret nevažeći
    public int processMove(char move) {
        // Definisanje promjenljivih za pravac i pomak
        int dir, mx, my;
        
        // Određivanje pravca i pomaka na osnovu unijetog karaktera
        switch (move) {
            case 'u': // gore
                dir =   1; mx =   0; my = -1;
                break;
            case 'd': // dole
                dir = -1; mx =   0; my =   1;
                break;
            case 'l': // lijevo
                dir =   1; mx = -1; my =   0;
                break;
            case 'r': // desno
                dir = -1; mx =   1; my =   0;
                break;
            default: // nevažeći pokret
                throw new IllegalArgumentException("Nevažeći pokret");
        }
        
        // Inicijalizacija promjenljivih za skor poteza, spajanje ćelija
        int moveScore =   0;
        boolean[][] hasMerged = new boolean[4][4];
        for (int i =   0; i <   4; i++) {
            for (int j =   0; j <   4; j++) {
                hasMerged[i][j] = false;
            }
        }
        boolean reached2048 = false, hasChanged = false;

        // Procesiranje poteza
        for (int i = (dir ==   1) ?  0 :  15; isInBounds(i); i += dir) {
            int x = i %  4, y = i /  4;
            if (board[x][y].getValue() ==   0)
                continue;
            int nx = x + mx, ny = y + my;
            while (isInBounds(nx, ny) && board[nx][ny].getValue() ==   0) {
                nx += mx;
                ny += my;
            }
            // Spajanje ćelija
            if (isInBounds(nx, ny) && board[nx][ny].getValue() == board[x][y].getValue() && !hasMerged[nx][ny]) {
                board[nx][ny].setValue(board[nx][ny].getValue() <<  1);
                board[x][y].setValue(0);
                moveScore += board[nx][ny].getValue();
                if (board[nx][ny].getValue() ==   8)
                    hasReached8 = true;
                hasMerged[nx][ny] = true;
                if (board[nx][ny].getValue() ==   2048)
                    reached2048 = true;
                hasChanged = true;
            } else {
                nx -= mx; ny -= my;
                if (nx != x || ny != y) {
                    board[nx][ny].setValue(board[x][y].getValue());
                    board[x][y].setValue(0);
                    hasChanged = true;
                }
            }
        }

        // Ažuriranje skora
        score += moveScore;
        if (score > highScore) {
            saveHighScore(score);
        }
        if (reached2048)
            return  1;
        if (hasChanged)
            addRandomCell();
        // Provjera da li ima slobodnih ćelija ili mogućih poteza
        for (int i =   0; i <   4; i++) {
            for (int j =   0; j <   4; j++)
                if (board[i][j].getValue() ==   0)
                    return  0;
        }
        for (int i =   0; i <   4; i++) {
            for (int j =   0; j <   3; j++)
                if (board[i][j].getValue() == board[i][j +  1].getValue())
                    return  0;
        }
        for (int i =   0; i <   3; i++) {
            for (int j =   0; j <   4; j++)
                if (board[i][j].getValue() == board[i +  1][j].getValue())
                    return  0;
        }
        // Ako nema slobodnih ćelija ni mogućih poteza, igra je završena
        return  2;
    }

	
	/**
	* Vraća trenutni skor igre.
	*
	* @return trenutni skor igre
	*/
	public int getScore() {
	   if (score > highScore) {
	       saveHighScore(score);
	   }
	   return score;
	}
	
	/**
	* Vraća  2D polje cijelih brojeva koje predstavlja trenutno stanje igračke tablice.
	* Svaki element u polju predstavlja vrijednost odgovarajuće pločice na tablici.
	* @return  2D polje cijelih brojeva koje predstavlja trenutno stanje igračke tablice
	*/
	public int[][] getBoard() {
	   int[][] ret = new int[4][4];
	   for (int y =   0; y <   4; y++) {
	       for (int x =   0; x <   4; x++)
	           ret[x][y] = board[x][y].getValue();
	   }
	   return ret;
	}
	
	/**
	* Čuva trenutno stanje igre na disk koristeći serijalizaciju Java.
	*/
	public void saveGame() {
	   try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("game_state.ser"))) {
	       out.writeObject(board);
	       out.writeInt(score);
	   } catch (IOException e) {
	       e.printStackTrace();
	   }
	}
	
    /**
     * Učitava stanje igre s diska koristeći serijalizaciju Java.
     */
    public void loadGame() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("game_state.ser"))) {
            // Čita polje tablice
            Object loadedObject = inputStream.readObject();
            if (loadedObject instanceof GameCell[][]) {
                GameCell[][] loadedBoard = (GameCell[][]) loadedObject;
                // Čita skor
                if (inputStream.available() >  0) {
                    score = inputStream.readInt();
                } else {
                    // Rukovodi se slučajem gdje skor nije dostupan
                }
                board = loadedBoard;
            } else {
                throw new IOException("Nevažeći format datoteke stanja igre");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace(); 
        }
    }
}


