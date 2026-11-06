public class Othello {
/*-------------------------------------------------------------------------------------------------------------------------------------------------------*/
//metodi eric
	static int dimensione_griglia = 8;

	//le 8 direzioni attorno a una casella (usate da controllaMossa ed eseguiMossa)
	static int[] direzioneRiga    = {-1, -1, -1,  0, 0,  1, 1, 1};
	static int[] direzioneColonna = {-1,  0,  1, -1, 1, -1, 0, 1};

    public static void main(String[] args){
        avviaPartita();
    }

    static void avviaPartita(){

		System.out.println("Menu di gioco Othello");
		System.out.println("Scegli tra la seguenti opzioni (1/2/3/0) :");
		System.out.println("1. Giocatore vs Giocatore");
		System.out.println("2. Giocatore vs Computer");
		System.out.println("3. Regole");
		System.out.println("0. Esci");
		System.out.print("Scegli modalità: ");

		int scelta = Leggi.unInt();

		switch (scelta) {
			case 1 -> scegliModalita(1);
			case 2 -> scegliModalita(2);
			case 3 -> {
				mostraRegole();
				avviaPartita();
			}
			case 0 -> System.out.println("Grazie per aver giocato al nostro gioco");
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
		char[][] griglia = new char[dimensione_griglia][dimensione_griglia];
		InizializzaScacchiera(griglia);

		gestisciTurno(griglia, scelta);
    }

	static boolean finePartita(char[][] griglia){
		for(int i = 0; i < dimensione_griglia; i++){
			for(int j = 0; j < dimensione_griglia; j++){
				if(griglia[i][j] == ' '){
					return false;
				}
			}
		}
		return true;
	}
		
    static void gestisciTurno(char[][] griglia, int modalita){
		int[] mossa = new int[2];
		int riga; 
		int colonna; 
		String giocatoreA = "Giocatore A";
		String giocatoreB = "Giocatore B";

		char bianco = 'X';
		char nero = 'O';

		String turno = giocatoreA;

		while (true) {

			StampaScacchiera(griglia);
			System.out.println("Turno di: " + turno);

			char coloreTurno;
			if(turno.equals(giocatoreA)){
				coloreTurno = bianco;
			} else {
				coloreTurno = nero;
			}

			if (modalita == 2 && turno.equals(giocatoreB)) {
				riga = (int)(Math.random() * dimensione_griglia);
				colonna = (int)(Math.random() * dimensione_griglia);
				posizionaPedina(griglia, riga, colonna, coloreTurno);
			} else {
				do {
					chiediMossa(mossa);
				} while (!controllaMossa(griglia, mossa[0], mossa[1], coloreTurno));
				eseguiMossa(griglia, mossa[0], mossa[1], coloreTurno);
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
			System.out.print("Inserisci un valore per la riga da 1 a 8: ");
			righe = Leggi.unInt();
		} while (righe < 0 || righe > dimensione_griglia);

		do {
			System.out.print("Inserisci un valore per la colonna da 1 a 8: ");
			colonne = Leggi.unInt();
		} while (colonne < 0 || colonne > dimensione_griglia);

		mossa[0] = righe - 1;
		mossa[1] = colonne - 1;
    }

    static void determinaVincitore(char[][] griglia){
		int contX = 0;
		int contO = 0;

		for (int i = 0; i < dimensione_griglia; i++) {
			for (int j = 0; j < dimensione_griglia; j++) {
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
    static void InizializzaScacchiera(char[][] griglia){
        for(int i = 0; i < dimensione_griglia; i++){
            for(int j = 0; j < dimensione_griglia; j++){
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
    
    static void StampaScacchiera(char[][] griglia){
		for(int i = 0; i < 	dimensione_griglia; i++){
			if(i == 0){
				System.out.print("   ");
			}
			System.out.print("  "+ (i+1)+ " ");
		}
		System.out.println("");
        for(int i = 0; i < dimensione_griglia; i++){
            System.out.println("   +---+---+---+---+---+---+---+---+");
            for(int j = 0; j < dimensione_griglia; j++){
                if(j == 0){
                    System.out.print(" " + (i+1) + " | " + griglia[i][j]);
                } else if(j == dimensione_griglia - 1){
                    System.out.print(" | " + griglia[i][j] + " |");
                } else {
                    System.out.print(" | " + griglia[i][j]);
                }
            }
            System.out.println();
        }
        System.out.println("   +---+---+---+---+---+---+---+---+");
    }

    static void posizionaPedina(char[][] griglia, int riga, int colonna, char colore){
		if(colore == 'X'){
			griglia[riga][colonna] = 'X';
		} else {
			griglia[riga][colonna] = 'O';
		}
    }

    //piazza la pedina e gira tutte le pedine avversarie imprigionate
    //da chiamare SOLO dopo aver verificato la mossa con controllaMossa
    static void eseguiMossa(char[][] griglia, int riga, int colonna, char colore){
		char coloreAvversario = (colore == 'X') ? 'O' : 'X';

		//piazzo la pedina
		griglia[riga][colonna] = colore;

		for(int i = 0; i < direzioneRiga.length; i++){
			int r = riga + direzioneRiga[i];
			int c = colonna + direzioneColonna[i];
			int contaPedineAvversarie = 0;

			//cammino lungo la direzione finché trovo pedine avversarie
			while(r >= 0 && r < dimensione_griglia && c >= 0 && c < dimensione_griglia && griglia[r][c] == coloreAvversario){
				r += direzioneRiga[i];
				c += direzioneColonna[i];
				contaPedineAvversarie++;
			}

			//se la fila è chiusa da una mia pedina, giro quelle in mezzo
			if(contaPedineAvversarie > 0 && r >= 0 && r < dimensione_griglia && c >= 0 && c < dimensione_griglia && griglia[r][c] == colore){
				for(int k = 1; k <= contaPedineAvversarie; k++){
					griglia[riga + k * direzioneRiga[i]][colonna + k * direzioneColonna[i]] = colore;
				}
			}
		}
    }

/*-------------------------------------------------------------------------------------------------------------------------------*/
    //metodi gabriele
    static boolean controllaMossa(char[][] matrice, int riga, int colonna, char colore){
        //controllo validità della mossa da parte dell'utente
		//controllo limiti delle coordinate della casella
		if(riga < 0 || riga >= dimensione_griglia || colonna < 0 || colonna >= dimensione_griglia){
			return false;
		}
		//controllo che la casella sia vuota
		if(matrice[riga][colonna] != ' '){
			return false;
		}
		
		//determino il colore dell'avversario
		char coloreAvversario = (colore == 'X') ? 'O' : 'X';
		
		for(int i = 0; i < direzioneRiga.length; i++){
			int r = riga + direzioneRiga[i];
			int c = colonna + direzioneColonna[i];
			int contaPedineAvversarie = 0;

			while(r >= 0 && r < dimensione_griglia && c >= 0 && c < dimensione_griglia && matrice[r][c] == coloreAvversario){
				r += direzioneRiga[i];
				c += direzioneColonna[i];
				contaPedineAvversarie++; 
			}

			//valida solo se ho scavalcato almeno una pedina avversaria
			//e subito dopo c'è una pedina del MIO colore (la fila è "chiusa")
			if(contaPedineAvversarie > 0 && r >= 0 && r < dimensione_griglia && c >= 0 && c < dimensione_griglia && matrice[r][c] == colore){
				return true;
			}
		}
		return false;
    }
	/*
    static int valutaMossa(int riga, int colonna, char colorePC){
        //conta quante pedine verrebbero girate: usato dal PC per scegliere la mossa migliore
		
    }

    static int gestisciTurnoPC(int[][] matrice, int riga, int colonna, char colorePC){
        //sceglie la mossa migliore per il computer (quella che gira piu' pedine)
        int pedineGirate = valutaMossa(riga, colonna, colorePC);
		int max = pedineGirate;
		//ciclo per determinare la mossa migliore per il PC

    }

    static void mosseDisponibili(char colore){
        //restituisce tutte le mosse legali del giocatore/PC
		int[] mosse_valide = new int[100];
		
    }
		*/
}