// Import the swing library for GUI components
import javax.swing.*;

public class GameBoard extends JFrame
{
    public GameBoard()
    {
        // Setting title for the game
        setTitle("Snake");

        // Set the size of the game board
        setSize(1000, 1025);

        // Close the operation when the game window is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Make the window centered on the screen when opened
        setLocationRelativeTo(null);

        // Add the game panel class
        add(new GamePanel());

        // Prevent the user from warping the game window
        setResizable(false);

        // Make the frame visible
        setVisible(true);
    }
}
