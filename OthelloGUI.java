import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/*
 * OthelloGUI.java
 *
 * Interfaccia grafica del gioco Othello.
 * E' un "front-end" alternativo: la logica di gioco resta tutta in Othello.java
 * (controllaMossa, eseguiMossa, InizializzaScacchiera, dimensione_griglia).
 *
 * Pedine:  'X' = bianca (Giocatore A, inizia)   'O' = nera (Giocatore B)
 * Celle:   ' ' = vuota
 *
 * Tutto il programma e' in questa sola classe (niente classi interne, niente
 * classi anonime): la compilazione produce un solo OthelloGUI.class.
 * Le immagini si leggono dalla cartella del progetto.
 */
public class OthelloGUI extends JPanel implements ActionListener, MouseListener {

	/* ===================== COSTANTI ===================== */

	// dimensioni dell'immagine originale della scacchiera (1216 x 1216 px)
	static final int LATO_IMMAGINE = 1216;
	static final int MARGINE_IMMAGINE = 80;   // margine prima della griglia verde
	static final int CELLA_IMMAGINE = 132;    // lato di una cella nell'immagine

	// se lo metti a false le mosse valide non vengono evidenziate
	static final boolean MOSTRA_MOSSE_VALIDE = true;

	// durata di un fotogramma dell'animazione (millisecondi)
	static final int RITARDO_ANIMAZIONE = 60;

	// attesa prima che giochi il computer (millisecondi)
	static final int ATTESA_COMPUTER = 500;

	// colori delle pedine, come in Othello.java
	static final char BIANCO = 'X';
	static final char NERO = 'O';

	// nomi dei file immagine, nella stessa cartella di questo file .java
	//static final String NOME_SFONDO = "griglia_coordinate.png";
	URL urlSfondo = getClass().getResource("/Immagini/Sfondo/griglia_coordinate.png");

	// nomi originali dei 9 fotogrammi della moneta
	static final String[] NOMI_FOTOGRAMMI = {
			"F1.png",
			"F2.png",
			"F3.png",
			"F4.png",
			"F5.png",
			"F6.png",
			"F7.png",
			"F8.png",
			"F9.png"
	};

	/* ===================== IMMAGINI ===================== */

	BufferedImage sfondo = null;             // griglia_coordinate.png
	BufferedImage[] fotogrammi;       // fotogrammi[0] = F1 (bianca) ... fotogrammi[8] = F9 (nera)

	/* ===================== STATO DELLA PARTITA ===================== */

	char[][] griglia = new char[Othello.dimensione_griglia][Othello.dimensione_griglia];

	boolean turnoBianco = true;       // true = tocca a X (bianco), false = tocca a O (nero)
	boolean controComputer = false;   // true nella modalita' Giocatore vs Computer
	boolean animazioneInCorso = false;

	/*
	 * Pedine che stanno girando, salvate in 4 array paralleli per non usare
	 * altre classi. animPassi va da 0 a 8 (fotogramma corrente).
	 */
	int[] animRighe = new int[64];
	int[] animColonne = new int[64];
	int[] animVersi = new int[64];    // 1 = verso nero (F1->F9), 0 = verso bianco (F9->F1)
	int[] animPassi = new int[64];
	int animQuante = 0;

	// timer per l'animazione e per la mossa del computer
	Timer timerAnimazione;
	Timer timerComputer;

	/* ===================== COMPONENTI DELLA FINESTRA ===================== */

	JFrame finestra;
	JPanel pannelloMenu;
	JPanel pannelloGioco;
	JLabel etichettaTurno;
	JLabel etichettaPunteggio;

	// posizione e lato della scacchiera disegnata (calcolati ad ogni disegno)
	int scacchieraX = 0;
	int scacchieraY = 0;
	int scacchieraLato = 0;

	/* ================================================================
	 * COSTRUTTORE
	 * ================================================================ */
	public OthelloGUI() {
		caricaImmagini();

		setBackground(new Color(232, 232, 232));
		addMouseListener(this);

		finestra = new JFrame("Othello");
		finestra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		finestra.setSize(860, 930);
		finestra.setMinimumSize(new Dimension(620, 700));
		finestra.setLocationRelativeTo(null);

		pannelloMenu = creaPannelloMenu();
		pannelloGioco = creaPannelloGioco();

		mostraMenu();
		finestra.setVisible(true);
	}

	/* ================================================================
	 * CARICAMENTO DELLE IMMAGINI
	 * ================================================================ */

	// carica un'immagine cercandola nella cartella del progetto
	BufferedImage caricaRisorsa(String percorso) {
    	// percorso assoluto nel classpath: "/Immagini/Pedine/F1.png"
    	try (InputStream in = getClass().getResourceAsStream(percorso)) {
        	if (in == null) {
            	return null;               // risorsa assente: decide il chiamante
        	}
        	return ImageIO.read(in);
    	} catch (IOException e) {
        	System.err.println("Errore leggendo " + percorso + ": " + e.getMessage());
        	return null;
    	}
	}

	void caricaImmagini() {
		sfondo = caricaRisorsa("/Immagini/Sfondo/griglia_coordinate.png");

		if (sfondo == null) {
    		System.err.println("ATTENZIONE: griglia_coordinate.png non trovata, uso uno sfondo provvisorio.");
    		sfondo = creaSfondoFinto();
		}

		fotogrammi = new BufferedImage[9];
		for (int i = 0; i < 9; i++) {
    		fotogrammi[i] = caricaRisorsa("/Immagini/Pedine/F" + (i + 1) + ".png");
    		if (fotogrammi[i] == null) {
        		fotogrammi[i] = creaMonetaFinta(i);   // fallback già presente
    		}
		}
	/*
		if (mancanti) {
			System.err.println("ATTENZIONE: mancano alcune immagini, uso monete provvisorie.");
		}
	*/
	}
	// sfondo provvisorio, usato solo se griglia_coordinate.png non c'e'
	BufferedImage creaSfondoFinto() {
		BufferedImage immagine = new BufferedImage(LATO_IMMAGINE, LATO_IMMAGINE,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = immagine.createGraphics();

		g2.setColor(new Color(245, 245, 245));
		g2.fillRect(0, 0, LATO_IMMAGINE, LATO_IMMAGINE);

		g2.setColor(new Color(20, 120, 60));
		g2.fillRect(MARGINE_IMMAGINE, MARGINE_IMMAGINE,
				8 * CELLA_IMMAGINE, 8 * CELLA_IMMAGINE);

		g2.setColor(new Color(0, 70, 30));
		for (int i = 0; i <= 8; i++) {
			int posizione = MARGINE_IMMAGINE + i * CELLA_IMMAGINE;
			g2.drawLine(posizione, MARGINE_IMMAGINE, posizione,
					MARGINE_IMMAGINE + 8 * CELLA_IMMAGINE);
			g2.drawLine(MARGINE_IMMAGINE, posizione,
					MARGINE_IMMAGINE + 8 * CELLA_IMMAGINE, posizione);
		}

		g2.dispose();
		return immagine;
	}

	// moneta provvisoria, usata solo se mancano i fotogrammi
	BufferedImage creaMonetaFinta(int indice) {
		BufferedImage immagine = new BufferedImage(CELLA_IMMAGINE, CELLA_IMMAGINE,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = immagine.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// piu' la moneta e' "di taglio", piu' e' stretta
		double larghezzaVista = Math.abs(Math.cos(Math.PI * indice / 8.0));
		int larghezza = (int) Math.round(112 * larghezzaVista);
		if (larghezza < 10) {
			larghezza = 10;
		}

		int grigio = 255 - (int) Math.round(indice * 220 / 8.0);

		g2.setColor(new Color(20, 20, 20));
		g2.fillOval((CELLA_IMMAGINE - larghezza) / 2, 8, larghezza, 116);

		g2.setColor(new Color(grigio, grigio, grigio));
		g2.fillOval((CELLA_IMMAGINE - larghezza) / 2 + 2, 10,
				Math.max(2, larghezza - 4), 112);

		g2.dispose();
		return immagine;
	}

	/* ================================================================
	 * MENU INIZIALE
	 * ================================================================ */
	JPanel creaPannelloMenu() {
		JPanel pannello = new JPanel();
		pannello.setLayout(new BoxLayout(pannello, BoxLayout.Y_AXIS));
		pannello.setBackground(new Color(238, 244, 238));
		pannello.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

		JLabel titolo = new JLabel("OTHELLO");
		titolo.setFont(new Font("SansSerif", Font.BOLD, 60));
		titolo.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel sottotitolo = new JLabel("Scegli la modalita' di gioco");
		sottotitolo.setFont(new Font("SansSerif", Font.PLAIN, 18));
		sottotitolo.setAlignmentX(Component.CENTER_ALIGNMENT);

		pannello.add(Box.createVerticalGlue());
		pannello.add(titolo);
		pannello.add(Box.createVerticalStrut(8));
		pannello.add(sottotitolo);
		pannello.add(Box.createVerticalStrut(40));
		pannello.add(creaBottoneMenu("Giocatore vs Giocatore", "giocatori"));
		pannello.add(Box.createVerticalStrut(14));
		pannello.add(creaBottoneMenu("Giocatore vs Computer", "computer"));
		pannello.add(Box.createVerticalStrut(14));
		pannello.add(creaBottoneMenu("Regole", "regole"));
		pannello.add(Box.createVerticalStrut(14));
		pannello.add(creaBottoneMenu("Esci", "esci"));
		pannello.add(Box.createVerticalGlue());

		return pannello;
	}

	JButton creaBottoneMenu(String testo, String comando) {
		JButton bottone = new JButton(testo);
		bottone.setFont(new Font("SansSerif", Font.BOLD, 20));
		bottone.setAlignmentX(Component.CENTER_ALIGNMENT);
		bottone.setMaximumSize(new Dimension(340, 52));
		bottone.setPreferredSize(new Dimension(340, 52));
		bottone.setActionCommand(comando);
		bottone.addActionListener(this);
		return bottone;
	}

	// pannello con la barra di stato in alto e la scacchiera (this) al centro
	JPanel creaPannelloGioco() {
		JPanel pannello = new JPanel(new BorderLayout());
		pannello.add(creaBarraStato(), BorderLayout.NORTH);
		pannello.add(this, BorderLayout.CENTER);
		return pannello;
	}

	void mostraMenu() {
		fermaTimer();
		animQuante = 0;

		finestra.setContentPane(pannelloMenu);
		finestra.revalidate();
		finestra.repaint();
	}

	/* ================================================================
	 * REGOLE
	 *
	 * Il testo e' lo stesso di Othello.mostraRegole(). Li' viene stampato
	 * a console e interrotto da "premi invio", quindi qui lo ricopio in una
	 * finestra di dialogo.
	 * ================================================================ */
	void mostraRegoleDialog() {
		JTextArea area = new JTextArea(testoRegole());
		area.setEditable(false);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setFont(new Font("SansSerif", Font.PLAIN, 14));
		area.setBackground(new Color(250, 250, 250));
		area.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		JScrollPane scorrimento = new JScrollPane(area);
		scorrimento.setPreferredSize(new Dimension(520, 480));

		JOptionPane.showMessageDialog(finestra, scorrimento, "Regole Othello",
				JOptionPane.INFORMATION_MESSAGE);
	}

	String testoRegole() {
		return "REGOLE OTHELLO\n\n"
				+ "1. L'obiettivo del gioco\n"
				+ "Vince chi ha piu' pedine del proprio colore sulla scacchiera alla fine della partita.\n\n"
				+ "2. Inizio della partita\n"
				+ "Si gioca su una scacchiera 8x8.\n"
				+ "Disposizione iniziale:\n"
				+ "X in D4 ed E5, O in E4 e D5.\n\n"
				+ "3. Come si muove\n"
				+ "Ogni giocatore piazza una pedina del proprio colore su una casella vuota.\n"
				+ "La mossa e' valida solo se imprigiona almeno una pedina avversaria.\n\n"
				+ "4. Cattura delle pedine\n"
				+ "Una pedina imprigiona quelle avversarie comprese tra se' e un'altra pedina dello stesso colore.\n"
				+ "Le direzioni possibili sono: orizzontale, verticale e diagonale.\n"
				+ "Le pedine imprigionate vengono capovolte e diventano del colore di chi ha mosso.\n\n"
				+ "5. Obbligo di muovere e passo\n"
				+ "Non si puo' piazzare una pedina senza girare almeno una avversaria.\n"
				+ "Se non ci sono mosse legali, il giocatore passa il turno.\n\n"
				+ "6. Fine della partita\n"
				+ "La partita termina quando nessuno dei due giocatori ha mosse legali.\n"
				+ "Vince chi ha piu' pedine del proprio colore.\n"
				+ "Se il numero e' uguale, la partita e' pari.";
	}

	/* ================================================================
	 * AVVIO DI UNA PARTITA
	 * ================================================================ */
	void avviaGioco(boolean modalitaComputer) {
		fermaTimer();
		animQuante = 0;

		controComputer = modalitaComputer;
		turnoBianco = true;              // inizia sempre il bianco (X)

		Othello.InizializzaScacchiera(griglia);

		finestra.setContentPane(pannelloGioco);
		finestra.revalidate();
		aggiornaBarraStato();
		repaint();
	}

	/* ================================================================
	 * BARRA DI STATO (turno e punteggio)
	 * ================================================================ */
	JPanel creaBarraStato() {
		etichettaTurno = new JLabel();
		etichettaTurno.setFont(new Font("SansSerif", Font.BOLD, 18));

		etichettaPunteggio = new JLabel();
		etichettaPunteggio.setFont(new Font("SansSerif", Font.PLAIN, 16));
		etichettaPunteggio.setHorizontalAlignment(SwingConstants.CENTER);

		JButton bottoneMenu = new JButton("Menu");
		bottoneMenu.setFont(new Font("SansSerif", Font.PLAIN, 14));
		bottoneMenu.setActionCommand("menu");
		bottoneMenu.addActionListener(this);

		JPanel barra = new JPanel(new BorderLayout(10, 0));
		barra.setBackground(new Color(235, 241, 235));
		barra.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
		barra.add(etichettaTurno, BorderLayout.WEST);
		barra.add(etichettaPunteggio, BorderLayout.CENTER);
		barra.add(bottoneMenu, BorderLayout.EAST);

		return barra;
	}

	void aggiornaBarraStato() {
		if (etichettaTurno == null || etichettaPunteggio == null) {
			return;
		}

		etichettaTurno.setText("Turno: " + nomeGiocatore(coloreDiTurno()));
		etichettaPunteggio.setText("Bianche (X): " + contaPedine(griglia, BIANCO)
				+ "     Nere (O): " + contaPedine(griglia, NERO));
	}

	String nomeGiocatore(char colore) {
		if (colore == BIANCO) {
			return "Giocatore A (bianco)";
		}
		if (controComputer) {
			return "Computer (nero)";
		}
		return "Giocatore B (nero)";
	}

	char coloreDiTurno() {
		if (turnoBianco) {
			return BIANCO;
		}
		return NERO;
	}

	/* ================================================================
	 * DISEGNO DELLA SCACCHIERA (questo pannello e' la scacchiera)
	 * ================================================================ */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		// interpolazione NEAREST_NEIGHBOR: la pixel art resta nitida
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

		calcolaGeometria();

		// 1) sfondo (griglia_coordinate.png ridimensionata mantenendo le proporzioni)
		if (sfondo != null) {
			g2.drawImage(sfondo, scacchieraX, scacchieraY, scacchieraLato, scacchieraLato, null);
		}

		// 2) evidenzio le mosse valide del giocatore di turno
		if (MOSTRA_MOSSE_VALIDE && !animazioneInCorso) {
			disegnaMosseValide(g2);
		}

		// 3) disegno le pedine
		for (int riga = 0; riga < Othello.dimensione_griglia; riga++) {
			for (int colonna = 0; colonna < Othello.dimensione_griglia; colonna++) {
				char colore = griglia[riga][colonna];
				if (colore == ' ') {
					continue;
				}

				BufferedImage immagine;

				int indiceAnimata = trovaPedinaAnimata(riga, colonna);
				if (indiceAnimata >= 0) {
					// pedina che sta girando: uso il fotogramma corrente
					immagine = fotogrammi[indiceFotogramma(indiceAnimata)];
				} else if (colore == BIANCO) {
					immagine = fotogrammi[0];    // F1
				} else {
					immagine = fotogrammi[8];    // F9
				}

				disegnaPedina(g2, immagine, riga, colonna);
			}
		}
	}

	// disegna una pedina al centro della cella indicata
	void disegnaPedina(Graphics2D g2, BufferedImage immagine, int riga, int colonna) {
		double fattore = scacchieraLato / (double) LATO_IMMAGINE;

		int x = scacchieraX + (int) Math.round((MARGINE_IMMAGINE + colonna * CELLA_IMMAGINE) * fattore);
		int y = scacchieraY + (int) Math.round((MARGINE_IMMAGINE + riga * CELLA_IMMAGINE) * fattore);
		int latoCella = (int) Math.round(CELLA_IMMAGINE * fattore);

		g2.drawImage(immagine, x, y, latoCella, latoCella, null);
	}

	// disegna un piccolo cerchio semitrasparente su ogni mossa valida
	void disegnaMosseValide(Graphics2D g2) {
		double fattore = scacchieraLato / (double) LATO_IMMAGINE;
		char coloreTurno = coloreDiTurno();

		for (int riga = 0; riga < Othello.dimensione_griglia; riga++) {
			for (int colonna = 0; colonna < Othello.dimensione_griglia; colonna++) {

				if (!Othello.controllaMossa(griglia, riga, colonna, coloreTurno)) {
					continue;
				}

				double centroX = scacchieraX
						+ (MARGINE_IMMAGINE + (colonna + 0.5) * CELLA_IMMAGINE) * fattore;
				double centroY = scacchieraY
						+ (MARGINE_IMMAGINE + (riga + 0.5) * CELLA_IMMAGINE) * fattore;
				double raggio = Math.max(3.0, CELLA_IMMAGINE * fattore * 0.12);

				g2.setColor(new Color(20, 20, 20, 110));
				g2.fill(new Ellipse2D.Double(centroX - raggio, centroY - raggio,
						raggio * 2, raggio * 2));
			}
		}
	}

	// calcola posizione e lato della scacchiera dentro a questo pannello
	void calcolaGeometria() {
		int lato = Math.min(getWidth(), getHeight());

		scacchieraLato = lato;
		scacchieraX = (getWidth() - lato) / 2;
		scacchieraY = (getHeight() - lato) / 2;
	}

	/* ================================================================
	 * CLICK DEL MOUSE
	 * ================================================================ */
	public void mousePressed(MouseEvent e) {
		gestisciClick(e.getX(), e.getY());
	}

	public void mouseClicked(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }

	void gestisciClick(int x, int y) {
		// durante l'animazione i click vengono ignorati
		if (animazioneInCorso) {
			return;
		}

		// se sta giocando il computer, il giocatore non puo' cliccare
		if (controComputer && !turnoBianco) {
			return;
		}

		if (scacchieraLato <= 0) {
			return;
		}

		// trasformo le coordinate del click in coordinate della griglia
		double fattore = scacchieraLato / (double) LATO_IMMAGINE;
		double relativoX = (x - scacchieraX) / fattore - MARGINE_IMMAGINE;
		double relativoY = (y - scacchieraY) / fattore - MARGINE_IMMAGINE;

		if (relativoX < 0 || relativoY < 0) {
			return;   // click sul margine
		}

		int colonna = (int) (relativoX / CELLA_IMMAGINE);
		int riga = (int) (relativoY / CELLA_IMMAGINE);

		if (riga >= Othello.dimensione_griglia || colonna >= Othello.dimensione_griglia) {
			return;   // click fuori dalla griglia
		}

		char colore = coloreDiTurno();

		// mossa non valida: il click viene semplicemente ignorato
		if (!Othello.controllaMossa(griglia, riga, colonna, colore)) {
			return;
		}

		eseguiMossaConAnimazione(riga, colonna, colore);
	}

	/* ================================================================
	 * ESECUZIONE DI UNA MOSSA + ANIMAZIONE
	 * ================================================================ */
	void eseguiMossaConAnimazione(int riga, int colonna, char colore) {
		// copia della griglia PRIMA della mossa: serve per capire quali pedine girano
		char[][] grigliaPrima = copiaGriglia(griglia);

		// la logica di gioco resta in Othello.java
		Othello.eseguiMossa(griglia, riga, colonna, colore);

		preparaAnimazione(grigliaPrima, colore);
		aggiornaBarraStato();
		repaint();

		if (animQuante == 0) {
			fineTurno();
			return;
		}

		animazioneInCorso = true;
		timerAnimazione = new Timer(RITARDO_ANIMAZIONE, this);
		timerAnimazione.start();
	}

	// confronta la griglia prima e dopo la mossa e trova le pedine girate
	void preparaAnimazione(char[][] grigliaPrima, char colore) {
		animQuante = 0;

		for (int riga = 0; riga < Othello.dimensione_griglia; riga++) {
			for (int colonna = 0; colonna < Othello.dimensione_griglia; colonna++) {

				char prima = grigliaPrima[riga][colonna];
				char dopo = griglia[riga][colonna];

				// pedina che e' cambiata di colore (la pedina appena messa era vuota)
				if (prima != ' ' && prima != dopo) {
					animRighe[animQuante] = riga;
					animColonne[animQuante] = colonna;

					if (colore == NERO) {
						animVersi[animQuante] = 1;   // F1 -> F9
					} else {
						animVersi[animQuante] = 0;   // F9 -> F1
					}

					animPassi[animQuante] = 0;
					animQuante++;
				}
			}
		}
	}

	// fa avanzare di un fotogramma tutte le pedine in animazione
	void avanzaAnimazione() {
		for (int i = animQuante - 1; i >= 0; i--) {
			animPassi[i]++;

			if (animPassi[i] > 8) {
				// tolgo questa pedina: copio l'ultima al suo posto
				animQuante--;
				animRighe[i] = animRighe[animQuante];
				animColonne[i] = animColonne[animQuante];
				animVersi[i] = animVersi[animQuante];
				animPassi[i] = animPassi[animQuante];
			}
		}

		repaint();

		if (animQuante == 0) {
			timerAnimazione.stop();
			timerAnimazione = null;
			animazioneInCorso = false;
			fineTurno();
		}
	}

	// restituisce la posizione nella lista delle pedine in animazione, oppure -1
	int trovaPedinaAnimata(int riga, int colonna) {
		for (int i = 0; i < animQuante; i++) {
			if (animRighe[i] == riga && animColonne[i] == colonna) {
				return i;
			}
		}
		return -1;
	}

	// fotogramma da disegnare: 0 = F1, 8 = F9
	int indiceFotogramma(int indicePedina) {
		if (animVersi[indicePedina] == 1) {
			return animPassi[indicePedina];        // F1 -> F9
		}
		return 8 - animPassi[indicePedina];        // F9 -> F1
	}

	/* ================================================================
	 * FINE DEL TURNO, PASSO E FINE PARTITA
	 * ================================================================ */
	void fineTurno() {
		// cambio giocatore
		turnoBianco = !turnoBianco;
		aggiornaBarraStato();

		// controllo se il giocatore di turno ha almeno una mossa valida
		if (!haMosseValide(coloreDiTurno())) {

			char coloreAvversario;
			if (coloreDiTurno() == BIANCO) {
				coloreAvversario = NERO;
			} else {
				coloreAvversario = BIANCO;
			}

			if (haMosseValide(coloreAvversario)) {
				// l'avversario ha mosse: si passa il turno
				JOptionPane.showMessageDialog(finestra,
						nomeGiocatore(coloreDiTurno()) + " non ha mosse valide.\nPassa il turno.",
						"Passo", JOptionPane.INFORMATION_MESSAGE);

				turnoBianco = !turnoBianco;
				aggiornaBarraStato();
			} else {
				// nessuno dei due puo' muovere: partita finita
				mostraFinePartita();
				return;
			}
		}

		// se ora deve giocare il computer, aspetto mezzo secondo e faccio la sua mossa
		if (controComputer && !turnoBianco) {
			avviaMossaComputer();
		}
	}

	// true se il colore ha almeno una mossa valida
	boolean haMosseValide(char colore) {
		for (int riga = 0; riga < Othello.dimensione_griglia; riga++) {
			for (int colonna = 0; colonna < Othello.dimensione_griglia; colonna++) {
				if (Othello.controllaMossa(griglia, riga, colonna, colore)) {
					return true;
				}
			}
		}
		return false;
	}

	void mostraFinePartita() {
		fermaTimer();

		int bianche = contaPedine(griglia, BIANCO);
		int nere = contaPedine(griglia, NERO);

		String risultato;
		if (bianche > nere) {
			risultato = "Vince il Giocatore A (bianco)!";
		} else if (nere > bianche) {
			if (controComputer) {
				risultato = "Vince il computer (nero)!";
			} else {
				risultato = "Vince il Giocatore B (nero)!";
			}
		} else {
			risultato = "Pareggio!";
		}

		String messaggio = risultato
				+ "\n\nPunteggio finale"
				+ "\nBianche (X): " + bianche
				+ "\nNere (O): " + nere;

		Object[] opzioni = { "Rigioca", "Torna al menu", "Esci" };
		int scelta = JOptionPane.showOptionDialog(finestra, messaggio, "Fine partita",
				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
				null, opzioni, opzioni[0]);

		if (scelta == 0) {
			avviaGioco(controComputer);        // rigioca stessa modalita'
		} else if (scelta == 2) {
			System.exit(0);
		} else {
			mostraMenu();
		}
	}

	/* ================================================================
	 * COMPUTER
	 * ================================================================ */
	void avviaMossaComputer() {
		timerComputer = new Timer(ATTESA_COMPUTER, this);
		timerComputer.setRepeats(false);
		timerComputer.start();
	}

	void eseguiMossaComputer() {
		if (!controComputer || turnoBianco || animazioneInCorso) {
			return;
		}

		int[] mossa = scegliMossaComputer();
		if (mossa != null) {
			eseguiMossaConAnimazione(mossa[0], mossa[1], NERO);
		}
	}

	// sceglie la mossa che gira piu' pedine (simulando la mossa su una copia)
	int[] scegliMossaComputer() {
		int[] mossaMigliore = null;
		int massimoGirate = -1;

		for (int riga = 0; riga < Othello.dimensione_griglia; riga++) {
			for (int colonna = 0; colonna < Othello.dimensione_griglia; colonna++) {

				if (!Othello.controllaMossa(griglia, riga, colonna, NERO)) {
					continue;
				}

				char[][] copia = copiaGriglia(griglia);
				Othello.eseguiMossa(copia, riga, colonna, NERO);

				int girate = contaPedine(copia, NERO) - contaPedine(griglia, NERO);

				if (girate > massimoGirate) {
					massimoGirate = girate;
					mossaMigliore = new int[] { riga, colonna };
				}
			}
		}

		return mossaMigliore;
	}

	/* ================================================================
	 * GESTIONE DEI PULSANTI E DEI TIMER
	 * ================================================================ */
	public void actionPerformed(ActionEvent e) {
		// eventi dei timer
		if (e.getSource() == timerAnimazione) {
			avanzaAnimazione();
			return;
		}

		if (e.getSource() == timerComputer) {
			timerComputer.stop();
			timerComputer = null;
			eseguiMossaComputer();
			return;
		}

		// eventi dei pulsanti
		String comando = e.getActionCommand();

		if ("giocatori".equals(comando)) {
			avviaGioco(false);
		} else if ("computer".equals(comando)) {
			avviaGioco(true);
		} else if ("regole".equals(comando)) {
			mostraRegoleDialog();
		} else if ("menu".equals(comando)) {
			mostraMenu();
		} else if ("esci".equals(comando)) {
			System.exit(0);
		}
	}

	/* ================================================================
	 * METODI DI SUPPORTO
	 * ================================================================ */
	char[][] copiaGriglia(char[][] originale) {
		char[][] copia = new char[Othello.dimensione_griglia][Othello.dimensione_griglia];

		for (int riga = 0; riga < Othello.dimensione_griglia; riga++) {
			for (int colonna = 0; colonna < Othello.dimensione_griglia; colonna++) {
				copia[riga][colonna] = originale[riga][colonna];
			}
		}
		return copia;
	}

	int contaPedine(char[][] matrice, char colore) {
		int contatore = 0;

		for (int riga = 0; riga < Othello.dimensione_griglia; riga++) {
			for (int colonna = 0; colonna < Othello.dimensione_griglia; colonna++) {
				if (matrice[riga][colonna] == colore) {
					contatore++;
				}
			}
		}
		return contatore;
	}

	void fermaTimer() {
		if (timerAnimazione != null) {
			timerAnimazione.stop();
			timerAnimazione = null;
		}
		if (timerComputer != null) {
			timerComputer.stop();
			timerComputer = null;
		}
		animazioneInCorso = false;
	}

	/* ================================================================
	 * MAIN
	 *
	 * Creo la finestra direttamente nel main per non usare classi anonime.
	 * ================================================================ */
	public static void main(String[] args) {
		new OthelloGUI();
	}
}
