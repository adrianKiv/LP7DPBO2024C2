import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.function.Supplier;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    int frameWidth = 360;
    int frameHeight = 640;
    Image backroundImage;
    Image birdImage;
    Image lowerPipeImage;
    Image UpperPipeImage;

    int playerStartPostX = frameWidth / 8;
    int playerStartPostY = frameHeight / 2;
    int playerWidth= 34;
    int playerHeight = 24;
    Player player;
    int pipeStartposX = frameWidth;
    int pipeStartposY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;
    ArrayList<Pipe> pipes;
    Timer gameLoop;
    Timer pipesCooldown;
    int gravity = 1;
    JLabel scoreLabel;
    int score = 0;


    public FlappyBird(){
        setPreferredSize(new Dimension(frameWidth, frameHeight));
//        setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);

        try {
            backroundImage = new ImageIcon(getClass().getResource("assets/background.png")).getImage();
            birdImage = new ImageIcon(getClass().getResource("assets/bird.png")).getImage();
            lowerPipeImage = new ImageIcon(getClass().getResource("assets/lowerPipe.png")).getImage();
            UpperPipeImage = new ImageIcon(getClass().getResource("assets/upperPipe.png")).getImage();
        } catch (NullPointerException e) {
            System.out.println("One or more images could not be found. Please check the file paths and try again.");
        }

        scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setBounds(20, 20, 100, 20); // Ubah posisi dan ukuran sesuai kebutuhan
        add(scoreLabel);

        player = new Player(playerStartPostX, playerStartPostY, playerWidth, playerHeight, birdImage);
        pipes = new ArrayList<Pipe>();

        pipesCooldown = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("pipa");
                placePipes();
            }
        });
        pipesCooldown.start();

        gameLoop = new Timer(1000/60, this);
        gameLoop.start();
    }

    public void placePipes(){
        int randomPosY = (int) (pipeStartposY - pipeHeight/4 - Math.random() * (pipeHeight/2));
        int openingSpace = frameHeight/4;

        int pairId = pipes.size() / 2; // Dapatkan id pasangan untuk setiap pasangan pipa
        Pipe upperPipe = new Pipe(pipeStartposX, randomPosY, pipeWidth, pipeHeight, UpperPipeImage, pairId, true);
        pipes.add(upperPipe);

        Pipe lowerPipe = new Pipe(pipeStartposX, (randomPosY + openingSpace + pipeHeight), pipeWidth, pipeHeight, lowerPipeImage, pairId, false);
        pipes.add(lowerPipe);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        g.drawImage(backroundImage, 0, 0, frameWidth, frameHeight, null);

        g.drawImage(player.getImage(), player.getPosX(), player.getPosY(), player.getWidth(), player.getHeight(), null);

        for(int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.getImage(), pipe.getPosX(), pipe.getPosY(), pipe.getWidth(), pipe.getHeight(), null);
        }
    }

    public void move(){
        player.setVelocityY(player.getVelocityY() + gravity);
        player.setPosY(player.getPosY() + player.getVelocityY());
        player.setPosY(Math.max(player.getPosY(), 0));

        // Cek jika burung telah mencapai batas bawah JFrame
        if (player.getPosY() > frameHeight) {
            gameLoop.stop();
            pipesCooldown.stop();
            JOptionPane.showMessageDialog(this, "Game Over! Tekan 'R' untuk restart.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
        }

        for(int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.setPosX(pipe.getPosX() + pipe.getVelocityX());

            // Jika player melewati pipa atas dalam setiap pasangan, tambahkan skor
            if (pipe.getPosX() + pipe.getWidth() < player.getPosX() && !pipe.isPassed() && pipe.isUpperPipe()) {
                score++;
                scoreLabel.setText("Score: " + score);
                // Tandai semua pipa dalam pasangan ini sudah dilewati
                for (Pipe p : pipes) {
                    if (p.getPairId() == pipe.getPairId()) {
                        p.setPassed(true);
                    }
                }
            }
        }
    }

    public void checkCollision() {
        for (Pipe pipe : pipes) {
            if (player.getBounds().intersects(pipe.getBounds())) {
                gameLoop.stop();
                pipesCooldown.stop();
                JOptionPane.showMessageDialog(this, "Game Over! Tekan 'R' atau untuk restart.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        checkCollision();
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE){
           player.setVelocityY(-10);
        }

        // Jika game berhenti dan pemain menekan tombol 'R', restart game
        if (!gameLoop.isRunning() && e.getKeyCode() == KeyEvent.VK_R) {
            restartGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void restartGame() {
        // Reset posisi dan kecepatan player
        player.setPosX(playerStartPostX);
        player.setPosY(playerStartPostY);
        player.setVelocityY(0);

        // Hapus semua pipa
        pipes.clear();

        // Reset skor
        score = 0;
        scoreLabel.setText("Score: " + score);

        // Mulai ulang game loop dan cooldown pipa
        gameLoop.start();
        pipesCooldown.start();
    }
}
