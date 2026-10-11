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
    }

    static void avviaPartita(){

    }
    static void scegliModalita(){

    }
    static void gestisciTurno(){

    }
    static void chiediMossa(){

    }
    static void determinaVincitore(){

    }
    static void stampaRisultato(){

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

    static  void posizionaPedina(int riga, int colonna, char colore){

    }

    static void eseguiMossa(){

    }

/*-------------------------------------------------------------------------------------------------------------------------------*/
    //metodi gabriele
    static boolean controllaMossa(int[][] matrice, int riga, int colonna, char colore){
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
    }
}