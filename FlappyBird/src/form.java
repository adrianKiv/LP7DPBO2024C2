import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class form {
    private JButton mulai;
    private JPanel panel1;

    public static void main(String[] args) {
        // Buat form awal
        JFrame startFrame = new JFrame("Flappy Bird");
        startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startFrame.setSize(360, 360);

        // Buat tombol untuk memulai game
        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Tutup form awal
                startFrame.dispose();

                // Buka form game
                JFrame gameFrame = new JFrame("Flappy Bird");
                gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                gameFrame.setSize(360, 640);
                gameFrame.add(new FlappyBird());
                gameFrame.setVisible(true);
            }
        });

        // Tambahkan tombol ke form awal
        startFrame.getContentPane().add(startButton);
        startFrame.setVisible(true);
    }
}
