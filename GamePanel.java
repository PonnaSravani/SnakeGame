import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener {
    static final int UNIT_SIZE = 25;
    static final int WIDTH = 800;
    static final int HEIGHT = 600;
    static final int GAME_UNITS = (WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    static final int DELAY = 100;  

    int[] x = new int[GAME_UNITS];
    int[] y = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten = 0;
    int appleX;
    int appleY;
    boolean running = false;
    boolean paused = false;
    Timer timer;
    char direction = 'R';

    int level = 1;  
    int applesToLevelUp = 5;  

    
    Color[] snakeSkins = { Color.green, Color.blue, Color.red, Color.orange };
    int currentSkinIndex = 0; 
    public GamePanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
    }

    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            if (applesEaten % applesToLevelUp == 0) {
                levelUp();
            }
            newApple();
        }
    }

    private void levelUp() {
        level++;  
        if (DELAY > 30) {  
            timer.setDelay(DELAY - 10);  
        }
    }

    public void newApple() {
        appleX = (int) (Math.random() * (WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = (int) (Math.random() * (HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }
        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
            running = false;
        }
        if (!running) {
            gameOver();  
        }
    }

    public void gameOver() {
        running = false;
        repaint();  
    }

    public void draw(Graphics g) {
        if (running) {
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

           
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(snakeSkins[currentSkinIndex]);  
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0)); 
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            g.setColor(Color.white);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
            
            
            g.setFont(new Font("Ink Free", Font.BOLD, 30));
            g.drawString("Level: " + level, 10, 30); 
        } else if (paused) {
            g.setColor(Color.white);
            g.setFont(new Font("Ink Free", Font.BOLD, 75));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Game Paused", (WIDTH - metrics.stringWidth("Game Paused")) / 2, HEIGHT / 2);
        } else {
            gameOverScreen(g);
        }
    }

    public void gameOverScreen(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (WIDTH - metrics.stringWidth("Game Over")) / 2, HEIGHT / 2);

        g.setColor(Color.white);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();  
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_LEFT && direction != 'R') {
                direction = 'L';
            } else if (key == KeyEvent.VK_RIGHT && direction != 'L') {
                direction = 'R';
            } else if (key == KeyEvent.VK_UP && direction != 'D') {
                direction = 'U';
            } else if (key == KeyEvent.VK_DOWN && direction != 'U') {
                direction = 'D';
            } else if (key == KeyEvent.VK_P) {
                paused = !paused;
            } else if (key == KeyEvent.VK_S) {
                switchSkin();  
            }
        }
    }

    private void switchSkin() {
        currentSkinIndex = (currentSkinIndex + 1) % snakeSkins.length;  
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 
        draw(g);  
    }
}