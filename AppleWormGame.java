import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class AppleWormGame extends JPanel implements KeyListener, ActionListener {

    private final int TILE_SIZE = 40;
    private final int ROWS = 10;
    private final int COLS = 15;

    private Timer timer;
    private int score = 0;
    private int level = 1;

    private ArrayList<Point> worm;
    private Point apple;
    private Point exit;

    private String direction = "UP";

    private boolean[][] walls;

    private boolean gameOver = false;
    private boolean gameStarted = false;

    private Random rand = new Random();

    public AppleWormGame() {
        setPreferredSize(new Dimension(COLS * TILE_SIZE, ROWS * TILE_SIZE + 40));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);

        initGame();
        timer = new Timer(200, this);
    }

    private void initGame() {
        worm = new ArrayList<>();
        worm.add(new Point(7, 5));
        worm.add(new Point(7, 6));

        direction = "UP";

        generateWalls();
        apple = generateRandomPoint();
        exit = generateRandomPoint();

        while (exit.equals(apple)) {
            exit = generateRandomPoint();
        }

        score = 0;
        gameOver = false;
        gameStarted = false;
    }

    private void generateWalls() {
        walls = new boolean[ROWS][COLS];

        // Bordas
        for (int i = 0; i < ROWS; i++) {
            walls[i][0] = true;
            walls[i][COLS - 1] = true;
        }
        for (int j = 0; j < COLS; j++) {
            walls[0][j] = true;
            walls[ROWS - 1][j] = true;
        }

        // Obstáculos internos (varia com o nível)
        if (level >= 2) {
            for (int i = 3; i < 7; i++) {
                walls[5][i] = true;
            }
        }
        if (level >= 3) {
            for (int i = 2; i < 8; i++) {
                walls[i][8] = true;
            }
        }
    }

    private Point generateRandomPoint() {
        Point p;
        do {
            int x = rand.nextInt(COLS);
            int y = rand.nextInt(ROWS);
            p = new Point(x, y);
        } while (walls[p.y][p.x] || worm.contains(p));
        return p;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Grade
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= ROWS; i++) {
            g.drawLine(0, i * TILE_SIZE, COLS * TILE_SIZE, i * TILE_SIZE);
        }
        for (int i = 0; i <= COLS; i++) {
            g.drawLine(i * TILE_SIZE, 0, i * TILE_SIZE, ROWS * TILE_SIZE);
        }

        // Paredes
        g.setColor(Color.DARK_GRAY);
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (walls[r][c]) {
                    g.fillRect(c * TILE_SIZE, r * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        // Maçã
        g.setColor(Color.RED);
        g.fillOval(apple.x * TILE_SIZE + 10, apple.y * TILE_SIZE + 10, 20, 20);

        // Saída
        g.setColor(Color.BLUE);
        g.fillRect(exit.x * TILE_SIZE + 10, exit.y * TILE_SIZE + 10, 20, 20);

        // Minhoca
        g.setColor(new Color(0, 180, 0));
        for (Point p : worm) {
            g.fillRoundRect(p.x * TILE_SIZE + 5, p.y * TILE_SIZE + 5, TILE_SIZE - 10, TILE_SIZE - 10, 10, 10);
        }

        // Cabeça com outra cor
        g.setColor(Color.GREEN.darker());
        Point head = worm.get(0);
        g.fillRoundRect(head.x * TILE_SIZE + 5, head.y * TILE_SIZE + 5, TILE_SIZE - 10, TILE_SIZE - 10, 10, 10);

        // HUD
        g.setColor(Color.BLACK);
        g.fillRect(0, ROWS * TILE_SIZE, COLS * TILE_SIZE, 40);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Pontos: " + score + " | Nível: " + level, 10, ROWS * TILE_SIZE + 25);

        if (!gameStarted) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, 0, COLS * TILE_SIZE, ROWS * TILE_SIZE + 40);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 26));
            g.drawString("Pressione ESPAÇO para começar", 50, ROWS * TILE_SIZE / 2);
        }

        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, 0, COLS * TILE_SIZE, ROWS * TILE_SIZE + 40);
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("GAME OVER", 150, ROWS * TILE_SIZE / 2);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Pressione ESPAÇO para reiniciar", 90, ROWS * TILE_SIZE / 2 + 40);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameStarted && !gameOver) {
            moveWorm();
            repaint();
        }
    }

    private void moveWorm() {
        Point head = new Point(worm.get(0));

        switch (direction) {
            case "UP": head.y -= 1; break;
            case "DOWN": head.y += 1; break;
            case "LEFT": head.x -= 1; break;
            case "RIGHT": head.x += 1; break;
        }

        // Colisão com parede
        if (head.x < 0 || head.x >= COLS || head.y < 0 || head.y >= ROWS || walls[head.y][head.x]) {
            endGame();
            return;
        }

        // Colisão consigo mesmo
        if (worm.contains(head)) {
            endGame();
            return;
        }

        worm.add(0, head);

        // Comeu maçã
        if (head.equals(apple)) {
            score += 10;
            apple = generateRandomPoint();
        } else {
            worm.remove(worm.size() - 1);
        }

        // Chegou na saída
        if (head.equals(exit) && score >= 10) {
            level++;
            timer.setDelay(Math.max(50, 200 - level * 20));
            initGame();
            gameStarted = true;
            timer.start();
        }
    }

    private void endGame() {
        gameOver = true;
        timer.stop();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        switch (k) {
            case KeyEvent.VK_UP: if (!direction.equals("DOWN")) direction = "UP"; break;
            case KeyEvent.VK_DOWN: if (!direction.equals("UP")) direction = "DOWN"; break;
            case KeyEvent.VK_LEFT: if (!direction.equals("RIGHT")) direction = "LEFT"; break;
            case KeyEvent.VK_RIGHT: if (!direction.equals("LEFT")) direction = "RIGHT"; break;
            case KeyEvent.VK_SPACE:
                if (!gameStarted || gameOver) {
                    initGame();
                    gameStarted = true;
                    timer.start();
                }
                break;
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Apple Worm Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(new AppleWormGame());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
  
    

