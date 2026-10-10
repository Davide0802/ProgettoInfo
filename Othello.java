public class Othello {
    public static void main(String[] args){
        int righe_griglia = 8; 
        int colonne_griglia = 8; 
        char[][] griglia = new char[righe_griglia][colonne_griglia]; 
        InizializzaScacchiera(griglia, righe_griglia, colonne_griglia);
        StampaScacchiera(griglia, righe_griglia, colonne_griglia);
    }

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
}