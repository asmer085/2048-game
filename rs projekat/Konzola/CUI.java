package Konzola;

import java.util.Scanner;

import Logika.Logika;

/**
 * Ova klasa predstavlja konzolni korisnički interfejs za igru
 * Omogućava korisnicima da igraju igru   2048 u konzoli, gdje se igračka tablica ispisuje na ekran,
 * a korisnik može unijeti pokrete za premještanje pločica.
 */
public class CUI {
    /**
     * Metoda printBoard ispravlja igračku tablicu na konzolu.
     * Za svaku   ćeliju na tablici ispisuje vrijednost   ćelije, a zatim prelazi u novi red.
     *
     * @param board dvodimenzionalno polje cijelih brojeva koje predstavlja igračku tablicu
     */
    private static void printBoard(int[][] board) {
        for (int y =   0; y <   4; y++) {
            for (int x =   0; x <   4; x++)
                System.out.print(board[x][y] + " "); // Ispisuje vrijednost   ćelije i razmake
            System.out.println(); // Prelazi u novi red
        }
    }

    /**
     * Metoda startGame pokreće igru.
     * Stvara novu instancu klase Logika koja predstavlja logiku igre,
     * a zatim koristi Scanner za čitanje korisničkih ulaznih podataka.
     * U petlji se ispisuje igračka tablica, korisniku se postavlja pitanje o unosu pokreta,
     * a zatim se obrađuje unos korisnika.
     * Ako je igra završila (korisnik pobijedio ili izgubio), ispisuje se konačni skor i postavlja se pitanje o nastavku igre.
     * Ako korisnik želi nastaviti, igra se nastavlja.
     * Ako korisnik odluči izaći, zatvara se Scanner i metoda se završava.
     */
    public static void startGame() {
        Logika game = new Logika(); // Stvara novu instancu klase Logika
        game.startGame(); // Pokreće igru
        Scanner scanner = new Scanner(System.in); // Stvara Scanner za čitanje korisničkih ulaznih podataka
        while (true) { // Petlja koja se izvršava dok igra nije završila
            int[][] board = game.getBoard(); // Dohvaća trenutno stanje igračke tablice
            printBoard(board); // Ispisuje igračku tablicu na konzolu

            char input; // Promenljiva za pohranu korisničkog ulaza
            int status; // Promenljiva za pohranu statusa igre nakon obrade pokreta
            while (true) { // Petlja koja se izvršava dok korisnik ne unese važeći pokret
                try {
                    input = scanner.nextLine().charAt(0); // Čita prvu karakteristiku unesenog teksta
                    try {
                        status = game.processMove(input); // Procesira pokret korisnika
                        break; // Ako je pokret uspješno procesiran, izlazi iz petlje
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage()); // Ako je pokret nevažeći, ispisuje se poruka o greški
                    }
                } catch (StringIndexOutOfBoundsException e) {
                    System.out.println("Nevažeći unos. Molimo unesite pokret (u/l/d/r)"); // Ako je unos prazan, ispisuje se poruka o greški
                }
            }

            if (status ==   0) // Ako je igra nastavljena bez promjene stanja (nema novih pločica)
                continue; // Nastavlja petlju bez ispisivanja nove tablice

            board = game.getBoard(); // Dohvaća ažurirano stanje igračke tablice
            printBoard(board); // Ispisuje ažuriranu igračku tablicu na konzolu
            
            if (status ==   1) { // Ako je igra završila s pobijedom
                System.out.println("Pobijedili ste!"); // Ispisuje poruku o pobijedi
            } else if (status ==   2) { // Ako je igra završila s porazom
                System.out.println("Izgubili ste!"); // Ispisuje poruku o porazu
            }
            System.out.println("Konačni skor: " + game.getScore()); // Ispisuje konačni skor
            System.out.println("Nastaviti? (y/n)"); // Postavlja pitanje o nastavku igre
            while (true) { // Petlja koja se izvršava dok korisnik ne unese važeći odgovor
                try {
                    input = scanner.nextLine().charAt(0); // Čita prvu karakteristiku unesenog teksta
                    break; // Ako je odgovor unesen, izlazi iz petlje
                } catch (StringIndexOutOfBoundsException e) {
                    System.out.println("Nevažeći unos. Molimo unesite 'y' ili 'n'"); // Ako je unos prazan, ispisuje se poruka o greški
                }
            }
            if (input == 'y') { // Ako korisnik želi nastaviti igru
                game = new Logika(); // Stvara novu instancu klase Logika
                game.startGame(); // Pokreće igru
            } else { // Ako korisnik odluči izaći
                scanner.close(); // Zatvara Scanner
                return; // Završava metodu
            }
        }
    }
}
