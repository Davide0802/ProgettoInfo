public class Othello {
/*-------------------------------------------------------------------------------------------------------------------------------------------------------*/
//metodi eric
    public static void main(String[] args){
        char colore_giocatore;
        char colorePC;
        int righe_griglia = 8; 
        int colonne_griglia = 8; 
        char[][] griglia = new char[righe_griglia][colonne_griglia]; 
        InizializzaScacchiera(griglia, righe_griglia, colonne_griglia);
        StampaScacchiera(griglia, righe_griglia, colonne_griglia);

        avviaPartita();
    }

    static void avviaPartita(){

		System.out.println("Menu di gioco Othello");
		System.out.println("Scegli tra la seguenti opzioni (1/2/3) :");
		System.out.println("1. Giocatore vs Giocatore");
		System.out.println("2. Giocatore vs Computer");
		System.out.println("3. Regole");
		System.out.println("4. Esci");
		System.out.print("Scegli modalità: ");

		int scelta = Leggi.unInt();

		switch (scelta) {
			case 1 -> scegliModalita(1);
			case 2 -> scegliModalita(2);
			case 3 -> {
				mostraRegole();
				avviaPartita();
			}
			case 4 -> System.out.println("Grazie per aver giocato al nostro gioco");
			default -> {
				System.out.println("Scelta non valida");
				avviaPartita();
			}
		}
	}

    static void mostraRegole(){

	    System.out.println("REGOLE OTHELLO");
	    
	    System.out.println("Premi invio per vedere la 1 regola ");
	    Leggi.unoString();

	    System.out.println("1.L'obiettivo del gioco");
	    System.out.println("Vince chi ha più pedine del proprio colore sulla scacchiera alla fine della partita.\n");
	    
	    System.out.println("Premi invio per vedere la 2 regola ");
	    Leggi.unoString();
	    
	    System.out.println("2. Inizio della partita");
	    System.out.println("Si gioca su una scacchiera 8x8.");
	    System.out.println("Nero (O) inizia, Bianco (X) è l'avversario.");
	    System.out.println("Disposizione iniziale:");
	    System.out.println("X in D4 ed E5, O in E4 e D5.\n");
	    
	    System.out.println("Premi invio per vedere la 3 regola ");
	    Leggi.unoString();
	    
	    System.out.println("3. Come si muove");
	    System.out.println("Ogni giocatore piazza una pedina del proprio colore su una casella vuota.");
	    System.out.println("La mossa è valida solo se imprigiona almeno una pedina avversaria.\n");
	    
	    System.out.println("Premi invio per vedere la 4 regola ");
	    Leggi.unoString();
	    
	    System.out.println("4. Cattura delle pedine");
	    System.out.println("Una pedina imprigiona quelle avversarie comprese tra sé e un'altra pedina dello stesso colore.");
	    System.out.println("Le direzioni possibili sono: orizzontale, verticale e diagonale.");
	    System.out.println("Le pedine imprigionate vengono capovolte e diventano del colore di chi ha mosso.\n");
	    
	    System.out.println("Premi invio per vedere la 5 regola ");
	    Leggi.unoString();
	    
	    System.out.println("5. Obbligo di muovere e passo");
	    System.out.println("Non si può piazzare una pedina senza girare almeno una avversaria.");
	    System.out.println("Se non ci sono mosse legali, il giocatore passa il turno.\n");
	    
	    System.out.println("Premi invio per vedere la 6 regola ");
	    Leggi.unoString();
	    
	    System.out.println("6. Fine della partita");
	    System.out.println("La partita termina quando nessuno dei due giocatori ha mosse legali.");
	    System.out.println("Vince chi ha più pedine del proprio colore.");
	    System.out.println("Se il numero è uguale, la partita è pari.\n");

	    System.out.println("Premi invio per tornare al menu...");
	    Leggi.unoString();
	}
        
    static void scegliModalita(int scelta){
		char[][] griglia = new char[8][8];
		InizializzaScacchiera(griglia, 8, 8);

		gestisciTurno(griglia, scelta, 8, 8);
    }

	static boolean finePartita(char[][] griglia){
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				if(griglia[i][j] == ' '){
					return false;
				}
			}
		}
		return true;
	}
		
    static void gestisciTurno(char[][] griglia, int modalita, int righe , int colonne ){
		int[] mossa = new int[2];

		String giocatoreA = "Giocatore A";
		String giocatoreB = "Giocatore B";

		char bianco = 'O';
		char nero = 'X';

		String turno = giocatoreA;

		while (true) {

			StampaScacchiera(griglia, righe, colonne);
			System.out.println("Turno di: " + turno);

			char coloreTurno;
			if(turno.equals(giocatoreA)){
				coloreTurno = bianco;
			} else {
				coloreTurno = nero;
			}

			if (modalita == 2 && turno.equals(giocatoreB)) {
				righe = (int)(Math.random() * 8);
				colonne = (int)(Math.random() * 8);
				posizionaPedina(griglia, righe, colonne, coloreTurno);

			} else {
				chiediMossa(mossa);
				posizionaPedina(griglia, mossa[0], mossa[1], coloreTurno);
			}

			if (turno.equals(giocatoreA)) {
				turno = giocatoreB;
			} else {
				turno = giocatoreA;
			}

			if (finePartita(griglia)) {
				determinaVincitore(griglia);
				return;
			}
		}
    }

    static void chiediMossa(int[] mossa){
		int righe, colonne;

		do {
			System.out.print("Inserisci un valore per la riga da 0 a 7: ");
			righe = Leggi.unInt();
		} while (righe < 0 || righe > 7);

		do {
			System.out.print("Inserisci un valore per la colonna da 0 a 7: ");
			colonne = Leggi.unInt();
		} while (colonne < 0 || colonne > 7);

		mossa[0] = righe;
		mossa[1] = colonne;
    }

    static void determinaVincitore(char[][] griglia){
		int contX = 0;
		int contO = 0;

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (griglia[i][j] == 'X') {
					contX++;
				}
				if (griglia[i][j] == 'O') {
					contO++;
				}
			}
		}

		stampaRisultato(contX, contO);
    }
	
    static void stampaRisultato(int x , int o){
			if (x > o) {
			System.out.println("Vince il Giocatore 1 (X) ");
		} else if (o > x) {
			System.out.println("Vince il Giocatore 2 (O)");
		} else {
			System.out.println("Non ha vinto nessuno, c'è un pareggio!");
		}
    }

/*-------------------------------------------------------------------------------------------------------------------------------------------------------*/
    //metodi davide
    static void InizializzaScacchiera(char[][] griglia, int righe, int colonne){
        for(int i = 0; i < righe; i++){
            for(int j = 0; j < colonne; j++){
                if(i == 3 && j == 3 || i == 4 && j == 4){
                    griglia[i][j] = 'X'; 
                } else if(i == 3 && j == 4 || i == 4 && j == 3){
                    griglia[i][j] = 'O'; 
                } else {
                griglia[i][j] = ' '; 
                }
            }
        }
    }
    
    static void StampaScacchiera(char[][] griglia, int righe, int colonne){
        for(int i = 0; i < righe; i++){
            System.out.println("+---+---+---+---+---+---+---+---+");
            for(int j = 0; j < colonne; j++){
                if(j == 0){
                    System.out.print("| " + griglia[i][j]);
                } else if(j == colonne - 1){
                    System.out.print(" | " + griglia[i][j] + " |");
                } else {
                    System.out.print(" | " + griglia[i][j]);
                }
            }
            System.out.println();
        }
        System.out.println("+---+---+---+---+---+---+---+---+");
    }

    static  void posizionaPedina(char[][] griglia, int riga, int colonna, char colore){
		if(colore == 'X'){
			griglia[riga][colonna] = 'O';
		} else {
			griglia[riga][colonna] = 'X';
		}
    }

    static void eseguiMossa(){

    }

/*-------------------------------------------------------------------------------------------------------------------------------*/
    //metodi gabriele
	//metto tutto in commento se no da errore scusa londi
    /*static boolean controllaMossa(int[][] matrice, int riga, int colonna, char colore){
        //controlla se la posizione e' valida
        if(matrice[riga][colonna] == ' '){
            return true;
        }else{
            return false;
        }
    }

    static int valutaMossa(int riga, int colonna, char colorePC){
        //conta quante pedine verrebbero girate: usato dal PC per scegliere la mossa migliore
    }

    static int gestisciTurnoPC(int[][] matrice, int riga, int colonna, char colorePC){
        //sceglie la mossa migliore per il computer (quella che gira piu' pedine)
        int pedineGirate = valutaMossa(riga, colonna, colorePC);
    }

    static void mosseDisponibili(char colore){
        //restituisce tutte le mosse legali del giocatore/PC
    }*/
}
