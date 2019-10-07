package MyGame;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.Timer;

public class BoggleGame extends JFrame {

    // 游戏时间
    private static final int GAME_TIME = 180;
    private static final int TIME_TRANSMISSION = 60;
    private static final int FOUND_WORDS_DISPLAY_COUNT = 17;
    private static final int ALL_WORDS_DISPLAY_COUNT   = 7;
    // 输入框的尺寸
    private static final int DEF_COLUMNS = 10;
    private static final String MAX_WORD_SIZE = "INCONSEQUENTIALLY";

    // 窗口尺寸
    private static final int DEF_HEIGHT = 550;
    private static final int DEF_WIDTH = 700;
    private static final int WORD_PANEL_WIDTH  = 205;
    private static final int WORD_PANEL_HEIGHT = 325;

    private int BOARD_ROW;
    private int BOARD_COL;

    private int gameDifficulty = 1;
    private String[] difficultyName = {
            "Easy",
            "So-So",
            "Common",
            "Impossible",
            "Death"
    };
    private String diff;
    private boolean inGame = true;
    private Timer timer = new Timer();
    private int elapsedTime = 0;
    private int points = 0;
    private int oppPoint = 0;

    private JLabel currentPointsLabel;
    private JLabel allwordPoint;
    private JLabel opponentPoint;
    private JLabel clock;
    private JTextField textField;

    private LinkedHashSet<String> foundWords;
    private TreeSet<String> allValidWords;
    private TreeSet<String> opponentFoundWord;
    private JList foundList;
    private JList allValidList;
    private JList opponentFoundList;
    private String[] emptyList = new String[0];

    private BoardPanel bp;
    private BoggleBoard boggleBoard;
    private BoggleSolver boggleSolver;

    private TreeSet<String> dict1;
    private TreeSet<String> dict2;
    private TreeSet<String> dict3;
    private TreeSet<String> dict4;

    private JMenu gameMenu;
    private JMenuBar menuBar;
    private JRadioButtonMenuItem[] difficultySelection;


    public BoggleGame(int row, int col, String diff)  {
        // 如果这里的变量使用到了 内部类里面，那么久不能定义成不变的量了。
        BOARD_ROW = row;
        BOARD_COL = col;
        this.diff = diff;
        for (int i = 0; i < difficultyName.length; i++) {
            if (diff == difficultyName[i]) {
                gameDifficulty = i + 1;
                break;
            }
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Boggle_MyGame");
        setLocationRelativeTo(null);

        this.makeMenuBar();

        JPanel timePanel = new JPanel();
        JLabel timeLabel = new JLabel("Time：");
        String seconds = String.format("%02d", (GAME_TIME - elapsedTime) % TIME_TRANSMISSION);
        String minutes = String.format("%02d", (GAME_TIME - elapsedTime) / TIME_TRANSMISSION);
        String _time = minutes + " : " + seconds;
        clock = new JLabel(_time);
        timePanel.add(timeLabel);
        timePanel.add(clock);

        textField = new JTextField(DEF_COLUMNS);
        // 设置文本输入框的最大最小的地方
        textField.setMaximumSize(new Dimension(textField.getPreferredSize().width, textField.getPreferredSize().height));
        // 给文本输入框设置后面需要链接的地方
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkword();
            }
        });

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                JTextField tmp = (JTextField)e.getSource();
                String txt = tmp.getText().toLowerCase();
                bp.match(txt);
            }
        });

        // 接下来就是JList的初始化
        foundList = new JList();
        foundList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        foundList.setListData(emptyList);
        foundList.setVisibleRowCount(FOUND_WORDS_DISPLAY_COUNT);
        foundList.setLayoutOrientation(JList.VERTICAL_WRAP);


        // 设置列表中的渲染器，（样式）
        foundList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(list ,value , index, false, false);
                comp.setForeground(Color.BLACK);
                return comp;
            }
        });

        JScrollPane foundScrollPane = new JScrollPane(foundList);
        foundScrollPane.setPreferredSize(new Dimension(WORD_PANEL_WIDTH,WORD_PANEL_HEIGHT));
        foundScrollPane.setMaximumSize(foundScrollPane.getPreferredSize());
        foundScrollPane.setMinimumSize(foundScrollPane.getPreferredSize());

        JPanel currentPointPanel = new JPanel();
        currentPointsLabel = new JLabel("My Current Point is :");
        currentPointPanel.add(currentPointsLabel);
        JPanel leftPanel = new JPanel();

        GroupLayout leftLayout = new GroupLayout(leftPanel);
        leftPanel.setLayout(leftLayout);
        leftLayout.setAutoCreateGaps(true);
        leftLayout.setAutoCreateContainerGaps(true);

        leftLayout.setHorizontalGroup(
                leftLayout.createSequentialGroup().addGroup(
                        leftLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(timePanel)
                                .addComponent(textField)
                                .addComponent(foundScrollPane)
                                .addComponent(currentPointPanel)
                )
        );

        leftLayout.setVerticalGroup(
                leftLayout.createSequentialGroup()
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(timePanel) // 马勒戈壁的，找了三个多小时 应该写 timePanel，心态炸了
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addComponent(textField)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addComponent(foundScrollPane)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addComponent(currentPointPanel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        bp = new BoardPanel();
        allValidList = new JList();
        allValidList.setVisible(true);
        allValidList.setListData(emptyList);
        allValidList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        allValidList.setLayoutOrientation(JList.VERTICAL_WRAP);
        allValidList.setVisibleRowCount(ALL_WORDS_DISPLAY_COUNT);
        allValidList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(list,  value, index, false, false);
                String word = (String) value;
                if (!inGame) {
                    if (foundWords.contains(word)) {
                        comp.setBackground(Color.YELLOW);
                    }
                }
                comp.setForeground(Color.BLACK);
                return comp;
            }
        });


        JLabel gameLabel = new JLabel("BoggleGame");
        JPanel gameStuff = new JPanel();
        gameStuff.add(gameLabel);
        JScrollPane allwordScrollPane = new JScrollPane(allValidList);
        allwordScrollPane.setPreferredSize(new Dimension(300, 145));
        allwordScrollPane.setMinimumSize(allwordScrollPane.getPreferredSize());
        allwordScrollPane.setMaximumSize(allwordScrollPane.getPreferredSize());
        JPanel allwordPointPanel = new JPanel();
        allwordPoint = new JLabel();
        allwordPointPanel.add(allwordPoint);
        JPanel gamePanel = new JPanel();

        GroupLayout gameLayout = new GroupLayout(gamePanel);
        gamePanel.setLayout(gameLayout);
        gameLayout.setAutoCreateGaps(true);
        gameLayout.setAutoCreateContainerGaps(true);

        gameLayout.setHorizontalGroup(
                gameLayout.createSequentialGroup().addGroup(
                        gameLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(gameStuff)
                        .addComponent(bp)
                        .addComponent(allwordScrollPane)
                        .addComponent(allwordPointPanel)
                )
        );

        gameLayout.setVerticalGroup(
                gameLayout.createSequentialGroup()
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,        GroupLayout.DEFAULT_SIZE,   Short.MAX_VALUE)
                        .addComponent(gameStuff)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,        GroupLayout.DEFAULT_SIZE,   GroupLayout.DEFAULT_SIZE)
                        .addComponent(bp)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,        GroupLayout.DEFAULT_SIZE,   GroupLayout.DEFAULT_SIZE)
                        .addComponent(allwordScrollPane)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,        GroupLayout.DEFAULT_SIZE,   GroupLayout.DEFAULT_SIZE)
                        .addComponent(allwordPointPanel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,        GroupLayout.DEFAULT_SIZE,   Short.MAX_VALUE)
        );


        JLabel opponentLabel = new JLabel("Opponent's Words: ");
        JPanel opponentPanel = new JPanel();
        opponentPanel.add(opponentLabel);

        opponentFoundList = new JList();
        opponentFoundList.setVisibleRowCount(FOUND_WORDS_DISPLAY_COUNT);
        opponentFoundList.setLayoutOrientation(JList.VERTICAL_WRAP);
        opponentFoundList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        opponentFoundList.setListData(emptyList);
        opponentFoundList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(list, value, index, false, false);
                comp.setForeground(Color.BLACK);
                return comp;
            }
        });

        JScrollPane opponentScrollPane = new JScrollPane(opponentFoundList);
        opponentScrollPane.setPreferredSize(new Dimension(WORD_PANEL_WIDTH, WORD_PANEL_HEIGHT));
        opponentScrollPane.setMaximumSize(opponentScrollPane.getPreferredSize());
        opponentScrollPane.setMinimumSize(opponentScrollPane.getPreferredSize());

        JPanel rightPanel = new JPanel();
        JPanel oppoenntPointPanel = new JPanel();
        opponentPoint = new JLabel("Opponents");
        oppoenntPointPanel.add(opponentPoint);
        JPanel spacingPanel = new JPanel();
        spacingPanel.setPreferredSize(new Dimension(WORD_PANEL_WIDTH, 22));

        GroupLayout rightLayout = new GroupLayout(rightPanel);
        rightPanel.setLayout(rightLayout);
        rightLayout.setAutoCreateGaps(true);
        rightLayout.setAutoCreateContainerGaps(true);

        rightLayout.setHorizontalGroup(
                rightLayout.createSequentialGroup().addGroup(
                        rightLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(spacingPanel)
                        .addComponent(opponentPanel)
                        .addComponent(opponentScrollPane)
                        .addComponent(oppoenntPointPanel)
                )
        );

        rightLayout.setVerticalGroup(
                rightLayout.createSequentialGroup()
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(spacingPanel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addComponent(opponentPanel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addComponent(opponentScrollPane)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addComponent(oppoenntPointPanel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        Container container = getContentPane();
        GroupLayout layout = new GroupLayout(container);
        container.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(leftPanel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addComponent(gamePanel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addComponent(rightPanel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup().addGroup(
                        layout.createParallelGroup(GroupLayout.Alignment.CENTER, false)
                        .addComponent(leftPanel)
                        .addComponent(gamePanel)
                        .addComponent(rightPanel)
                )
        );



        In in1 = new In(new File("dict1.txt"));
        dict1 = new TreeSet<>();
        for (String s : in1.readAllStrings())
            dict1.add(s);

        In in2 = new In(new File("dict2.txt"));
        dict2 = new TreeSet<>();
        for (String s : in2.readAllStrings())
            dict2.add(s);

        In in3 = new In(new File("dict3.txt"));
        dict3 = new TreeSet<>();
        for (String s : in3.readAllStrings())
            dict3.add(s);

        In in4 = new In(new File("dict4.txt"));
        dict4 = new TreeSet<>();
        for (String s : in4.readAllStrings())
            dict4.add(s);

        In in = new In(new File("dict.txt"));
        String [] dictionary = in.readAllLines();
        boggleSolver = new BoggleSolver(dictionary);

        newGame();
        this.pack();
    }


    private void newGame() {
        if (BOARD_ROW == 4 && BOARD_COL == 4) {
            boggleBoard = new BoggleBoard();
        }
        else {
            boggleBoard = new BoggleBoard(BOARD_ROW, BOARD_COL);
        }
        clock.setForeground(Color.BLACK);
        textField.requestFocus();
        inGame = true;
        points = 0;
        currentPointsLabel.setText("Current Points: " + points);
        textField.setEnabled(true);

        foundWords = new LinkedHashSet<>();
        foundList.setListData(emptyList);
        allValidList.setListData(emptyList);
        opponentFoundList.setListData(emptyList);

        bp.setBoard();
        bp.unHighLightCube();
        bp.path = null;

        Iterable<String> words = boggleSolver.getAllValidWords(boggleBoard);
        allValidWords = new TreeSet<>();
        int allpoints = 0;
        for (String s: words) {
            allValidWords.add(s);
            allpoints += scoreWord(s);
        }

        allwordPoint.setText("Possible Points: " + allpoints);

        opponentFoundWord = new TreeSet<>();
        if (gameDifficulty == 1) {
            for (String word : words)
                if (dict1.contains(word) && StdRandom.uniform(4) == 0)
                    opponentFoundWord.add(word);
        }

        else if (gameDifficulty == 2) {
            for (String s : words) {
                if (dict2.contains(s) && StdRandom.uniform(3) == 0)
                    opponentFoundWord.add(s);
            }
        }

        else if (gameDifficulty == 3) {
            for (String s : words) {
                if (dict3.contains(s) && StdRandom.uniform(3) != 0)
                    opponentFoundWord.add(s);
            }
        }

        else if (gameDifficulty == 4) {
            for (String s : words) {
                if (dict4.contains(s) && StdRandom.uniform(5) != 0)
                    opponentFoundWord.add(s);
            }
        }

        else if (gameDifficulty == 5) {
            for (String s : words)
                opponentFoundWord.add(s);
        }
        oppPoint = 0;
        for (String s : opponentFoundWord)
            oppPoint += scoreWord(s);

        opponentPoint.setText("Opponent's Points: " + oppPoint);
        timer.cancel();
        elapsedTime = -1;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (elapsedTime < GAME_TIME - 1) {
                    elapsedTime ++;
                    String seconds = String.format("%02d", (GAME_TIME - elapsedTime) % 60);
                    String minutes = String.format("%02d", (GAME_TIME - elapsedTime) / 60);
                    String time = minutes + ":" + seconds;
                    clock.setText(time);
                }
                else endGame();
            }
        }, 0, 1000);
    }

    private void endGame() {

        clock.setText("00:00");
        clock.setForeground(Color.red);
        timer.cancel();
        textField.setText("");
        textField.setEnabled(false);
        bp.unHighLightCube();

        allValidList.setListData(allValidWords.toArray());


        int[] index = new int[foundWords.size()];
        int x = 0;
        int i = 0;
        for (String s : allValidWords) {
            if (foundWords.contains(s)) {
                index[x++] = i;
            }
            i++;
        }

        allValidList.setSelectedIndices(index);
        inGame = false;

        int playerScore = points, opponentScore = oppPoint;

        for (String s: foundWords) {
            if (opponentFoundWord.contains(s)) {
                playerScore -= scoreWord(s);
                opponentScore -= scoreWord(s);
            }
        }

        Object[] list1 = foundWords.toArray();
        for (int j = 0; j < list1.length; j++) {
            if (opponentFoundWord.contains(list1[j])) {
                list1[j] = "<html><em><strike>" + list1[j] + "</strike></em></html>";
            }
        }
        foundList.setListData(list1);

        Object[] list2 = opponentFoundWord.toArray();
        for (int j = 0; j < list2.length; j++) {
            if (foundWords.contains(list2[j])) {
                list2[j] = "<html><em><strike>" + list2[j] + "</strike></em></html>";
            }
        }
        opponentFoundList.setListData(list2);

        String winnerMessage = "";
        if      (playerScore > opponentScore) winnerMessage = "                   You win!\n\n";
        else if (playerScore < opponentScore) winnerMessage = "            The computer wins!\n\n";
        else                                  winnerMessage = "                     Tie!\n\n";
        String scoreMessage  = "                  Final score:\n          You: " +  playerScore + " - Computer: " + opponentScore;
        JOptionPane.showMessageDialog(this, winnerMessage + scoreMessage, "Game Over!", JOptionPane.PLAIN_MESSAGE);
    }

    private void makeMenuBar() {
        menuBar = new JMenuBar();
        gameMenu = new JMenu("Game");
        // 设置键盘助记符，这个后面再去弄清楚是什么意思。
        gameMenu.setMnemonic(KeyEvent.VK_G);
        // 这里就是对某个部件的一个简介，后面要弄清楚，这个究竟显示在什么地方
        gameMenu.getAccessibleContext().setAccessibleDescription("This menu contains game options");
        menuBar.add(gameMenu);
        JMenuItem newGameMenuItem = new JMenuItem("New...", KeyEvent.VK_N);
        // 这个就是确认菜单栏那一块的键盘快捷键确认，control 进行加速键
        newGameMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        newGameMenuItem.getAccessibleContext().setAccessibleDescription("Starts a new game");
        // 这里就相当于信号槽被触发之后的操作，前面都是关联怎么触发这个操作，所有操作都必须经过这个动作。
        newGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                newGame();
            }
        });


        JMenuItem endGameMenuItem = new JMenuItem("End Game", KeyEvent.VK_E);
        endGameMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        endGameMenuItem.getAccessibleContext().setAccessibleDescription("Ends the current game");
        endGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                endGame();
            }
        });
        gameMenu.add(newGameMenuItem);
        gameMenu.add(endGameMenuItem);
        // 菜单栏上面的分割建
        gameMenu.addSeparator();

        ButtonGroup difficultyGroup = new ButtonGroup();
        difficultySelection = new JRadioButtonMenuItem[5];
        for (int i = 0; i < 5; i++) {
            difficultySelection[i]  = new JRadioButtonMenuItem(difficultyName[i]); // mod as a check against mismatched sizes
            // 设置一开始的选中状态
            if (difficultyName[i] == diff) difficultySelection[i].setSelected(true);
            // 给每一个按钮写一个命令的获取，如果写完之后按下就可以直接获取命令，从而少写很多if 与 else
            difficultySelection[i].setActionCommand(difficultyName[i]);
            difficultySelection[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    for (int i = 0; i < difficultyName.length; i++) {
                        if (ae.getActionCommand().equals(difficultyName[i])) {
                            gameDifficulty = i + 1;
                            //endGame();
                            newGame();
                            break;
                        }
                    }
                }
            });
            difficultyGroup.add(difficultySelection[i]);
            gameMenu.add(difficultySelection[i]);
        }
        JMenuItem quitMenuItem = new JMenuItem("Quit", KeyEvent.VK_Q);
        quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        quitMenuItem.getAccessibleContext().setAccessibleDescription("Quits the program");
        quitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                timer.cancel();
                System.exit(0);
            }
        });
        gameMenu.addSeparator();
        gameMenu.add(quitMenuItem);
        setJMenuBar(menuBar);
    }

    private void checkword() {
        String s;
        if (textField.getText().length() >= bp.getString().length()) s = textField.getText().toUpperCase();
        else s = bp.getString().toUpperCase();
        s = s.trim();
        if (s.equals("")) return;
        if (!foundWords.contains(s) && allValidWords.contains(s)) {
            foundWords.add(s);
            foundList.setListData(foundWords.toArray());
            points += scoreWord(s);
            currentPointsLabel.setText("currentPoints : " + points);
            textField.setText("");
        }

        /*
            这个地方有两种写法，第一种就是下面用递归，仿照dfs进行判断，第二种就是这样
            else if (s.equals("GODMODE")) {
            foundWords.clear();
            points = 0;
            for (String str : solver.getAllValidWords(board)) {
                foundWords.add(str);
                points += scoreWord(str);
            }
            foundWordsList.setListData(foundWords.toArray());
            scoreLabel.setText("Current Points: " + points);
            entryField.setText("");
        }
        */
        else if (s.equals("GODMODE")) {
            for (String ks : boggleSolver.getAllValidWords(boggleBoard)) {
                textField.setText(ks);
                checkword();
            }
        }

        else {
            Toolkit.getDefaultToolkit().beep();
            textField.setText("");
        }
    }

    private class BoardPanel extends JPanel{

        private final int ALL_CUBES = BOARD_COL * BOARD_ROW;
        private JLabel [] cube = new JLabel[ALL_CUBES];
        private final Color cubeActive = new Color(232, 237, 76);
        private final Color cubeUnActive = new Color(146, 183, 219);
        private int [] path;
        private final int CUBE_WIDTH = 60;
        private boolean foundword;

        public BoardPanel() {
            GridLayout gridLayout = new GridLayout(BOARD_ROW, BOARD_COL);
            this.setPreferredSize(new Dimension(CUBE_WIDTH * BOARD_COL, CUBE_WIDTH * BOARD_ROW));
            this.setMaximumSize(getPreferredSize());
            this.setMinimumSize(getPreferredSize());
            this.setLayout(gridLayout);


            for (int i = 0; i < ALL_CUBES; i++) {
                final int pos = i;

                cube[i] = new JLabel("", JLabel.CENTER);
                cube[i].setPreferredSize(new Dimension(CUBE_WIDTH, CUBE_WIDTH));
                cube[i].setMaximumSize(getPreferredSize());
                cube[i].setMinimumSize(getPreferredSize());
                cube[i].setFont(new Font("SansSerif", Font.PLAIN, 28));
                cube[i].setBorder(BorderFactory.createRaisedBevelBorder());
                cube[i].setOpaque(true);
                cube[i].setBackground(cubeUnActive);

                cube[i].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (!inGame) return;
                        if (path == null) {
                            path = new int [ALL_CUBES];
                            for (int j = 0; j < ALL_CUBES; j++) {
                                path[j] = -1;
                            }
                            path[0] = pos;
                            highLightCube();
                            return;
                        }

                        // 当初看源码的时候还没有注意到，其实这个地方由于内部类的关系，前面必须定义一个final变量
                        for (int j = 0; j < path.length; j++) {
                            if (j == 0 && path[j] == -1) {
                                path[j] = pos;
                                break;
                            }
                            else if (path[j] == pos) {
                                // 现在觉得写程序的人思维真的特别缜密，看源码的时候还没有觉得。
                                // 真正自己写的时候发现，为了防止特殊情况，真的考虑了很多东西
                                if (j == path.length - 1|| path[j + 1] == -1) {
                                    cube[pos].setBackground(cubeUnActive);
                                    path[j] = -1;
                                }
                                break;
                            }
                            else if (path[j] == -1) {

                                if (Math.abs(path[j - 1] / BOARD_COL - pos / BOARD_COL) <= 1 &&
                                        Math.abs(path[j - 1] % BOARD_COL - pos % BOARD_COL) <= 1) {
                                    path[j] = pos;
                                }
                                break;
                            }
                        }
                        highLightCube();
                    }
                });

                cube[i].addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        int keyCode = e.getKeyCode();
                        if (keyCode == KeyEvent.VK_ENTER) {
                            checkword();
                        }
                    }
                });

                this.add(cube[i]);
            }
        }

        private void setBoard() {
            String letter;

            for (int i = 0; i < ALL_CUBES; i++) {
                char ch = boggleBoard.getLetter(i / BOARD_COL, i % BOARD_COL);
                letter = ch + "";
                letter = letter.toLowerCase();
                if (ch == 'Q')  cube[i].setText("qu");
                else cube[i].setText(letter);
            }
        }

        private void highLightCube() {
            if (path == null) return;
            for (int i = 0; i < path.length; i++) {
                if (path[i] == -1) break;
                cube[path[i]].setBackground(cubeActive);
            }
        }

        private void unHighLightCube() {
            if (path == null) return;
            for (int i = 0; i < path.length; i++) {
                if (path[i] == -1) break;
                cube[path[i]].setBackground(cubeUnActive);
            }
        }

        private String getString() {
            StringBuilder tmp = new StringBuilder();
            for (int pos : path) {
                if (pos == -1) break;
                tmp.append(cube[pos].getText().toUpperCase());
            }
            return tmp.toString();
        }

        private void match(String text) {
            unHighLightCube();
            path = new int[ALL_CUBES];
            for (int i = 0; i < ALL_CUBES; i++) {
                path[i] = -1;
            }
            foundword = false;
            text = text.toLowerCase();
            for (int i = 0; i < ALL_CUBES; i++) {
                if (text.startsWith(cube[i].getText())) {
                    dfs(text, 0, 0, i / BOARD_COL, i % BOARD_COL);
                }
                if (foundword) break;
            }
            if (foundword) highLightCube();
        }

        // 妈的 第一次花了很长时间去看懂，真心以为自己懂了，但是自己再写一遍的时候，真的有很多细节，细节真的很容易出错。妈蛋。特别就是dfs里面相关的地方。
        private void dfs(String s, int cur, int p, int i, int j) {
            if (i < 0 || j < 0 || i >= BOARD_ROW || j >= BOARD_COL) return;
            if (cur >= s.length()) {
                foundword = true;
                return;
            }

            for (int k = 0; k < path.length; k++) {
                if (path[k] == i * BOARD_COL + j) return;
            }

            if (cur != 0 && s.charAt(cur - 1) == 'q') {
                if (s.charAt(cur) == 'u') cur ++;
                else return;
            }

            if (cur >= s.length()) {
                foundword = true;
                return;
            }
            if (s.charAt(cur) != cube[i * BOARD_COL + j].getText().charAt(0)) return;

            path[p] = i * BOARD_COL + j;

            for (int ii = i - 1; ii <= i + 1; ii++) {
                for (int jj = j - 1; jj <= j + 1; jj++) {
                    if (!foundword) dfs(s, cur + 1, p + 1, ii, jj);
                }
            }
            if (!foundword) path[cur] = -1;
        }
    }


    private int scoreWord(String s) {
        int pointValue;
        int length = s.length();
        if      (length < 5)  pointValue = 1;
        else if (length == 5) pointValue = 2;
        else if (length == 6) pointValue = 3;
        else if (length == 7) pointValue = 5;
        else                  pointValue = 11;
        return pointValue;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                int col = 4;
                int row = 4;
                if (args.length == 0) {
                    col = 4;
                    row = 4;
                }
                new BoggleGame(row,col, "Easy").setVisible(true);
            }
        });
        return ;
    }

}
