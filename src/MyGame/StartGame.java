package MyGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartGame extends JFrame {

    public StartGame() {

        ImageIcon im = new ImageIcon("bk.png");
        JLabel bkLabel = new JLabel(im);
        this.getLayeredPane().add(bkLabel, new Integer(Integer.MIN_VALUE));
        bkLabel.setBounds(0, 0, im.getIconWidth(), im.getIconHeight());
        setBounds(0,0,1097, 713);
        Container container = this.getContentPane();
        ((JPanel)container).setOpaque(false);


        JLabel gameName = new JLabel("BoggleBoard");
        gameName.setForeground(Color.WHITE);
        gameName.setFont(new Font("SansSerif", Font.BOLD, 75));
        gameName.setOpaque(false);
        gameName.setBounds(550,80,550,150);


        JPanel buttonPanel = new JPanel();
        JButton gameStart = new JButton("Start Game");
        gameStart.setPreferredSize(new Dimension(150,50));
        gameStart.setMaximumSize(gameStart.getPreferredSize());
        gameStart.setMinimumSize(gameStart.getPreferredSize());
        gameStart.setBounds(700,300,150,50);
        gameStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] allDifficult = {"Easy", "So-So", "Common", "Impossible", "Death"};
                Object diff = JOptionPane.showInputDialog(null,"Please choose the level of your Opponent","Level Choose",
                        JOptionPane.INFORMATION_MESSAGE, null, allDifficult, allDifficult[0]);
                if (diff == null) System.exit(0);
                dispose();
                new BoggleGame(4,4, (String)diff).setVisible(true);
                
            }
        });

        JButton gameOver = new JButton("Exit Game");
        gameOver.setPreferredSize(new Dimension(150,50));
        gameOver.setMaximumSize(gameOver.getPreferredSize());
        gameOver.setMinimumSize(gameOver.getPreferredSize());
        gameOver.setBounds(700,540,150,50);
        gameOver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });



        JButton introduce = new JButton("Introduce Game");
        introduce.setPreferredSize(new Dimension(150,50));
        introduce.setMaximumSize(introduce.getPreferredSize());
        introduce.setMinimumSize(introduce.getPreferredSize());
        introduce.setBounds(700,360,150,50);
        introduce.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,"Boggle is a word game designed by Allan Turoff and distributed by Hasbro. \n" +
                        "It involves a board made up of 16 cubic dice, where each die has a letter printed on each of its 6 sides. \n" +
                        "At the beginning of the game, the 16 dice are shaken and randomly distributed into a 4-by-4 tray, with only the top sides of the dice visible. \n" +
                        "The players compete to accumulate points by building valid words from the dice, according to these rules:\n\n" +
                        "1. A valid word must be composed by following a sequence of adjacent dice—two dice are adjacent if they are horizontal, vertical, or diagonal neighbors.\n" +
                        "2. A valid word can use each die at most once.\n" +
                        "3. A valid word must contain at least 3 letters.\n" +
                        "4. A valid word must be in the dictionary (which typically does not contain proper nouns).\n", "Introduction",JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton contact = new JButton("Contact Me");
        contact.setPreferredSize(new Dimension(150,50));
        contact.setMaximumSize(introduce.getPreferredSize());
        contact.setMinimumSize(introduce.getPreferredSize());
        contact.setBounds(700,480,150,50);
        contact.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,"If you want to contact me \n\n" +
                        "you can email at me at wsx1128@outlook.com\n\n" +
                        "or visit my own blog at wsx1128.xyz\n\n\n" +
                        "Thank you for playing my game!!!");
            }
        });

        JButton ranking = new JButton("Ranking List");
        ranking.setPreferredSize(new Dimension(150,50));
        ranking.setMaximumSize(introduce.getPreferredSize());
        ranking.setMinimumSize(introduce.getPreferredSize());
        ranking.setBounds(700,420,150,50);


        container.setLayout(null);
        container.add(gameName);
        container.add(gameStart);
        container.add(introduce);
        container.add(ranking);
        container.add(contact);
        container.add(gameOver);

        this.setResizable(false);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Boggle_MyGame");
        this.setLocationRelativeTo(null);
        this.setSize(1097, 713);

    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new StartGame().setVisible(true);
            }
        });
        return ;
    }
}
