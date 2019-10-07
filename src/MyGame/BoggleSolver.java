package MyGame;

import edu.princeton.cs.algs4.Bag;

import java.util.HashSet;
import java.util.Stack;

public class BoggleSolver
{
    private Node root;
    private BoggleBoard boggleBoard;
    private int col, row;
    private Bag<Integer>[] adj;
    private boolean[] vis;
    private Stack<Integer> dice;
    private HashSet<String> allword;

    private class Node {
        boolean val;
        private Node[] next = new Node[26];
    }

    private void put (String word) {
        root = put(root, word, 0);
    }

    private Node put(Node x, String word, int cnt) {
        if (x == null) x = new Node();
        if (cnt == word.length()) {
            x.val = true;
            return x;
        }
        int c = word.charAt(cnt) - 'A';
        x.next[c] = put(x.next[c], word, cnt + 1);
        return x;
    }

    private boolean get (String word) {
        Node x = get(root, word, 0);
        if (x == null) return false;
        return x.val;
    }

    private Node get(Node x, String word, int cnt) {
        if (x == null) return null;
        if (cnt == word.length()) return x;
        int c = word.charAt(cnt) - 'A';
        return get(x.next[c], word, cnt + 1);
    }


    public BoggleSolver(String[] dictionary) {
        if (dictionary == null) throw new IllegalArgumentException("Wrong dictionary");
        root = new Node();
        for (String s: dictionary)
            put(s);
    }

    private boolean check (int i,int j) {
        return i >= 0 && j >= 0 && i < row && j < col;
    }

    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board == null) throw new IllegalArgumentException("Wrong board");
        this.boggleBoard = board;
        row = board.rows();
        col = board.cols();
        int cubes = row * col;
        adj = (Bag<Integer>[]) new Bag[cubes];
        allword = new HashSet<>();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                int v = i * col + j;
                adj[v] = new Bag<Integer>();
                if (check(i - 1, j)) adj[v].add((i - 1) * col + j);
                if (check(i + 1, j)) adj[v].add((i + 1) * col + j);
                if (check(i, j - 1)) adj[v].add(i * col + j - 1);
                if (check(i, j + 1)) adj[v].add(i * col + j + 1);
                if (check(i + 1, j - 1)) adj[v].add((i + 1) * col + j - 1);
                if (check(i + 1, j + 1)) adj[v].add((i + 1) * col + j + 1);
                if (check(i - 1, j - 1)) adj[v].add((i - 1) * col + j - 1);
                if (check(i - 1, j + 1)) adj[v].add((i - 1) * col + j + 1);
            }
        }

        for (int i = 0; i < cubes; i++) {
            vis = new boolean[cubes];
            dice = new Stack<>();
            vis[i] = true;
            dice.add(i);
            char ch = getChar(i);
            if (ch == 'Q') dfs(i, root.next['Q' - 'A'].next['U' - 'A'], "QU", dice);
            else dfs(i, root.next[ch - 'A'], ch + "", dice);
        }
        return allword;
    }

    private char getChar (int v) {
        return boggleBoard.getLetter(v / col, v % col);
    }
    private void dfs(int pos, Node x, String res, Stack<Integer> dice) {
        if (res.length() > 2 && x != null && x.val == true) {
            allword.add(res);
        }

        for (int w : adj[pos]) {
            char ch = getChar(w);
            if (!vis[w] && x != null && x.next[ch - 'A'] != null) {
                vis[w] = true;
                dice.push(w);
                if (ch == 'Q') dfs(w, x.next['Q' - 'A'].next['U' - 'A'], res + "QU", dice);
                else dfs(w, x.next[ch - 'A'], res + ch, dice);
                // 后面这里漏掉了关于dfs里面重要的内容，这个就相当于八皇后里面，选这个位置，或者不选择这个位置，如果上面的递归
                // 一直递归到了最底层，那么就会发生一件事情当返回之后 会不会影响后面的举动呢，这里要讲它设置成不会影响到其他地方。
                // 个人觉得这里的dice可要可不要，本来是以为是保证最上面的一个元素准确被弄掉
                int d = dice.pop();
                vis[d] = false;
            }
        }
    }

    public int scoreOf(String word) {
        if (get(word) == false)
            return 0;
        else {
            int len = word.length();
            if (len <= 2) return 0;
            else if(len == 3 || len == 4) return 1;
            else if (len == 5) return 2;
            else if (len == 6) return 3;
            else if (len == 7) return 5;
            else return 11;
        }
    }
}