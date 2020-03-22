import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @date December 2019
 * @author jaimin
 */
public class SliderGame extends JFrame implements ActionListener {

    //jFrame components
    private JButton[][] buttons;
    private JButton startButton;
    private JLabel displayMoves, shufflesText, squaresText, scoreRulesText, highscoreinfo;
    private JPanel gamePanel;
    private JPanel scorePanel;
    private JPanel customizePanel;
    private JPanel panel;
    private JSlider boxamount, NofShuffles;
    private JButton customButton;

    //class variables
    private int boardSize = 4;
    private int emptyX, emptyY;
    private int moveCounter;
    private boolean boardExists = false;
    private int Shuffles;

    /**
     * Construct a slider game.
     */
    public SliderGame() {

        //Set up JFrame
        setSize(750, 750);
        setDefaultCloseOperation(3);

        Font g = new Font("Arial", 1, 16);
        Font p = new Font("Arial", 1, 32);

        //Set up the start button JButton
        this.startButton = new JButton("Start");
        this.startButton.setFont(g);
        this.startButton.addActionListener(this);
        this.startButton.setBackground(Color.white);

        //Set up the customize option JButton
        this.customButton = new JButton("Customize Board");
        this.customButton.setFont(g);
        this.customButton.addActionListener(this);
        this.customButton.setBackground(Color.white);

        //Set up move display JLabel
        this.displayMoves = new JLabel("Moves: " + this.moveCounter, 0);
        this.displayMoves.setFont(p);

        //Set up "Dimension of board" JLabel
        this.squaresText = new JLabel("Dimension of board");
        this.squaresText.setFont(p);

        //Set up "# of shuffles" JLabel
        this.shufflesText = new JLabel("# of shuffles");
        this.shufflesText.setFont(p);

        //Set up JLabel on how high scores are saved
        this.scoreRulesText = new JLabel("<html>High score is seperated based on <br><br> shuffles and board dimension<html>");
        this.scoreRulesText.setFont(g);

        //Set up JLabel on where to find high scores
        this.highscoreinfo = new JLabel("<html>High scores can be found in a text file<br><br> in the projects folder<html>");
        this.highscoreinfo.setFont(g);

        //Set up JSlider that controls the dimensions of the game.
        this.boxamount = new JSlider(3, 10, 4);
        boxamount.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                boxamountStateChanged(evt);
            }
        });
        this.boxamount.setMajorTickSpacing(1);
        this.boxamount.setPaintTicks(true);
        this.boxamount.setPaintLabels(true);

        //Set up JSlider that controls the number of (random) swaps before the game starts.
        this.NofShuffles = new JSlider(30, 100, 50);
        NofShuffles.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                boxamountStateChanged(evt);
            }
        });
        this.NofShuffles.setMajorTickSpacing(10);
        this.NofShuffles.setPaintTicks(true);
        this.NofShuffles.setPaintLabels(true);
        this.NofShuffles.setSnapToTicks(true);

        //Set up a JPanel that contains all score elements.
        this.scorePanel = new JPanel();
        this.scorePanel.setLayout(new GridLayout(1, 3, 5, 5));
        this.scorePanel.add(this.displayMoves);
        this.scorePanel.add(this.customButton);
        this.scorePanel.add(this.startButton);

        //Set up a JPanel that contains all customization elements.
        this.customizePanel = new JPanel();
        this.customizePanel.setLayout(new GridLayout(3, 2, 5, 5));
        this.customizePanel.add(this.squaresText);
        this.customizePanel.add(this.boxamount);
        this.customizePanel.add(this.shufflesText);
        this.customizePanel.add(this.NofShuffles);
        this.customizePanel.add(this.scoreRulesText);
        this.customizePanel.add(this.highscoreinfo);

        //Set up a JPanel that contains every element.
        this.panel = new JPanel();
        this.panel.setLayout(new BorderLayout());
        this.panel.add(this.customizePanel, "Center");
        this.panel.add(this.scorePanel, "South");

        add(this.panel);
        setVisible(true);
    }

    //Checks if an action has happened
    //The customizePanel and gamePanel overlap. It's setup so only one is visible at a time.
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.startButton) {
            boardSetup();
            this.customizePanel.setVisible(false);
            this.gamePanel.setVisible(true);
            scramble();
        }

        if (e.getSource() == this.customButton) {
            this.customizePanel.setVisible(true);
            if (boardExists) {
                this.gamePanel.setVisible(false);
            }
        }

        if (boardExists) {
            for (int i = 0; i < this.buttons.length; i++) {
                for (int j = 0; j < boardSize; j++) {
                    if (e.getSource() == this.buttons[i][j]) {

                        if (isNextToEmpty(i, j)) {

                            swapPieces(i, j);
                            this.moveCounter++;
                            this.displayMoves.setText("Moves: " + this.moveCounter);

                            if (win()) {
                                System.out.println("you win");
                                this.displayMoves.setText("YOU WIN!!!");

                                for (int o = 0; o < boardSize; o++) {
                                    for (int p = 0; p < boardSize; p++) {
                                        this.buttons[o][p].setEnabled(false);
                                    }
                                }

                                highscore();
                            }

                            break;
                        }
                    }
                }
            }
        }
    }

    //change event specifically for the jslider that adjusts the board size and shuffles
    private void boxamountStateChanged(ChangeEvent evt) {
        boardSize = boxamount.getValue();
        Shuffles = NofShuffles.getValue();
    }

    /**
     * Returns true if the piece clicked is next to an empty square
     *
     * @param x the JButton's x location
     * @param y the JButton's y location
     * @return true/false if the piece is next to an empty square
     */
    private boolean isNextToEmpty(int x, int y) {
        if (x + 1 < boardSize && y < boardSize) {
            if (this.buttons[x + 1][y] == this.buttons[emptyX][emptyY]) {
                return true;
            }
        }
        if (x < boardSize && y + 1 < boardSize) {
            if (this.buttons[x][y + 1] == this.buttons[emptyX][emptyY]) {
                return true;
            }
        }
        if (x - 1 >= 0 && y >= 0) {
            if (this.buttons[x - 1][y] == this.buttons[emptyX][emptyY]) {
                return true;
            }
        }
        if (x >= 0 && y - 1 >= 0) {
            if (this.buttons[x][y - 1] == this.buttons[emptyX][emptyY]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Swaps the location(really just labels) of two JButtons
     *
     * @param x the JButton's x location
     * @param y the JButton's y location
     */
    private void swapPieces(int x, int y) {
        this.buttons[this.emptyX][this.emptyY].setText(this.buttons[x][y].getText());
        this.buttons[x][y].setVisible(false);
        this.buttons[this.emptyX][this.emptyY].setVisible(true);
        this.emptyX = x;
        this.emptyY = y;
    }

    /**
     * Scrambles the board Only does legal moves, and has a counter to do a
     * specific amount of swaps
     */
    private void scramble() {
        Shuffles = NofShuffles.getValue();
        int i = 0;
        while (i < NofShuffles.getValue()) {
            int indexx = (int) (Math.random() * this.buttons.length);
            int indexy = (int) (Math.random() * this.buttons.length);
            if (isNextToEmpty(indexx, indexy)) {
                swapPieces(indexx, indexy);
                i++;
            }
        }
        this.moveCounter = 0;
        this.displayMoves.setText("Moves: " + this.moveCounter);
    }

    /**
     * Checks for all JButtons numbers to be in order.
     *
     * @return true/false depending on win state.
     */
    private boolean win() {
        int v = 1;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (!(this.buttons[i][j]).getText().equals(v + "") && (this.buttons[i][j] != this.buttons[boardSize - 1][boardSize - 1])) {
                    return false;
                }
                v++;

            }
        }
        if (!(((this.buttons[boardSize - 1][boardSize - 1])).isVisible())) {
            return true;
        }
        return false;
    }

    /**
     *
     */
    private void highscore() {

        //sets up a date formatter for tracking when a highscore was achieved.
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        try {
            PrintWriter fileOut2 = new PrintWriter(new FileWriter("SlidingGameScores.txt", true));
            //makes a new file and saves the file in NetbeansProject/ProjectName

            //makes a file reader object to read from
            FileReader fr = new FileReader("SlidingGameScores.txt");
            BufferedReader fileIn = new BufferedReader(fr);

            //makes a temporary file to write to.
            FileWriter fw2 = new FileWriter("temp.txt");
            PrintWriter pw2 = new PrintWriter(fw2);

            //copies everything over from main scores text file to the temp file.
            String str = fileIn.readLine();
            while (str != null) {
                pw2.println(str);
                str = fileIn.readLine();
            }
            pw2.close();
            fileOut2.close();

            //makes a file reader object to read from (For temp file).
            FileReader fileIn2 = new FileReader("temp.txt");
            BufferedReader tempread = new BufferedReader(fileIn2);

            //makes a new file (same name as old high score list) and ensures it's blank
            PrintWriter newScores = new PrintWriter(new FileWriter("SlidingGameScores.txt"));

            boolean scoreBeat = false;
            boolean exists = false;
            boolean matches = false;
            //holds the max number of high scores possible 8 dimensions. 8 shuffle amounts. +1 for good measure.
            String[] scores = new String[65];
            int count = 0;

            //starts reading from the temp file.
            scores[count] = tempread.readLine();
            while (scores[count] != null) {  // goes only if something was read

                //if the current line being read matches the parameters for this specific game.
                //otherwise just go ahead and readd the old score from the temp file
                if (scores[count].contains(boardSize + "x" + boardSize) && scores[count].contains(Shuffles + " shuffles")) {
                    exists = true;
                    matches = true;
                    //extracts highscore from line
                    int result = Integer.valueOf(scores[count].substring(scores[count].indexOf("[") + 1, scores[count].indexOf("]")));

                    //checks if the users score beats the highscore (ties go in the favour of the original high score)
                    if (result > moveCounter) {
                        scores[count] = (dtf.format(now) + " - Highscore for " + boardSize + "x" + boardSize + "puzzle with " + Shuffles + " shuffles: [" + moveCounter + "] moves");
                        newScores.println(scores[count]);
                        scoreBeat = true;
                        System.out.println("new highscore");
                        Font g = new Font("Arial", 1, 16);
                        this.displayMoves.setFont(g);
                        this.displayMoves.setText("NEW HIGHSCORE!!!");
                    }
                } else {
                    newScores.println(scores[count]);
                }

                count++;
                scores[count] = tempread.readLine(); // read the next line in oldFile.txt and assign to String
                scoreBeat = false;
                matches = false;
            } //repeat until we reach the end of the file

            //if that dimension and shuffle combination didn't exist, then add it with the users score as the highscore.
            if (!exists) {
                System.out.println("New dimension");
                newScores.println((dtf.format(now) + " - Highscore for " + boardSize + "x" + boardSize + "puzzle with " + Shuffles + " shuffles: [" + moveCounter + "] moves"));
            }

            newScores.close();

        } catch (IOException e) {
            System.out.println("File reading/writing error.");
        }
    }

    /**
     * Initializes the gamePanel Method allows for the board size to be changed
     * without resetting the whole JFrame.
     */
    private void boardSetup() {
        try {
            this.gamePanel.removeAll();
            this.gamePanel.revalidate();
            this.gamePanel.repaint();
        } catch (java.lang.NullPointerException v) {
            this.gamePanel = new JPanel();
            System.out.println("First startup");
            boardExists = true;
        }

        this.gamePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.gamePanel.setLayout(new GridLayout(boardSize, boardSize + 1, 5, 5));
        this.gamePanel.setBackground(Color.black);

        this.panel.add(this.gamePanel, "Center");

        boardSize = boxamount.getValue();
        buttons = new JButton[boardSize][boardSize];
        int v = 0;
        
        //initializes the 2d array of jbuttons
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                this.buttons[i][j] = new JButton((v + 1) + "");
                this.buttons[i][j].setFont(new Font("Arial", Font.PLAIN, 250 / boardSize));
                this.buttons[i][j].setForeground(Color.WHITE);

                //determines colour. allows it to be back and forth.
                //works with all dimensions
                Color red = new Color(255, 0, 18);
                Color green = new Color(0, 179, 44);

                if (boardSize % 2 == 0) {
                    if (i % 2 == 0) {
                        Color[] colors = {red, green};
                        this.buttons[i][j].setBackground(colors[v % 2]);
                    } else {
                        Color[] colors = {green, red};
                        this.buttons[i][j].setBackground(colors[v % 2]);
                    }
                } else {
                    Color[] colors = {red, green};
                    this.buttons[i][j].setBackground(colors[v % 2]);
                }

                this.buttons[i][j].addActionListener(this);
                this.gamePanel.add(this.buttons[i][j]);
                this.buttons[i][j].setEnabled(true);
                v++;
            }
        }
        this.gamePanel.repaint();

        this.buttons[boardSize - 1][boardSize - 1].setVisible(false);
        this.emptyX = boardSize - 1;
        this.emptyY = boardSize - 1;

    }

}