public class Othello {
    public static void main(String[] args){
        int righe_griglia = 8; 
        int colonne_griglia = 8; 
        int[][] griglia = new int[righe_griglia][colonne_griglia]; 
        InizializzaScacchiera(griglia, righe_griglia, colonne_griglia);
        StampaScacchiera(griglia, righe_griglia, colonne_griglia);
    }
    static void InizializzaScacchiera(int[][] griglia, int righe, int colonne){
        for(int i = 0; i < righe; i++){
            for(int j = 0; j < colonne; j++){
                griglia[i][j] = 'X'; 
            }
        }
    }
    static void StampaScacchiera(int[][] griglia, int righe, int colonne){
        for(int i = 0; i < righe; i++){
            for(int j = 0; j < colonne; j++){
                System.out.print(griglia[i][j] + " ");
            }
            System.out.println();
        }
    }
}