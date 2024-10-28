import javax.swing.*;    // Import swing library for JPanel class
import java.awt.*;       // Import for drawing the game elements
import java.awt.event.*; // Import for handling keyboard input and action events
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements ActionListener, KeyListener
{
    // Game constants for the grid and timing
    // Determine how large each snake segment and food item will be
    private final int UNIT_SIZE = 50; // Size of each grid unit
    private final int TOTAL_UNITS = (950 * 950) / (UNIT_SIZE * UNIT_SIZE); // Total units on the board

    // Arrays to hold the x and y coordinates of the snake's body
    private final int[] x = new int[TOTAL_UNITS];
    private final int[] y = new int[TOTAL_UNITS];

    // Current length of the snake
    private int bodyParts = 3; // Starting length of the snake
    private int foodEaten; // Track the number of food items eaten
    private int foodX, foodY; // x and y position of the food

    // Direction control - snake starts moving right
    private char direction = 'R'; // 'R' = Right, 'U' = Up, 'D' = Down, 'L' = Left
    private boolean running = false; // Game running state - false now just to declare the boolean
    private Timer timer; // Timer for controlling the game loop

    // Game state control: "welcome", "active", "gameOver"
    private String gameState = "welcome"; // Start the game in the welcome screen

    // Initialize the variable to track and store the 'highScore'
    private int highScore = 0; //

    // Add list to store multiple obstacles
    private List<Point> obstacles = new ArrayList<>();

    // Constructor to set up the panel start the game
    public GamePanel()
    {
        this.setPreferredSize(new Dimension(950, 950)); // Set the size of the panel
        this.setBackground(new Color(255, 182, 193)); // Set the background color of the panel to light pink
        this.setFocusable(true); // Ensure the panel can receive key input
        this.addKeyListener(this); // Add a KeyListener for controlling the snake

        // Create and set up the Start button
        // Buttons to start and replay the game
        JButton startButton = new JButton("Start");
        startButton.setFocusable(false); // Avoid focus stealing from KeyListener
        startButton.addActionListener(e -> startGame());

        // Create and set up the Replay button
        JButton replayButton = new JButton("Replay");
        replayButton.setFocusable(false); // Avoid focus stealing from KeyListener
        replayButton.addActionListener(e -> replayGame());

        // Setup for the layout of the buttons
        this.setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(replayButton);
        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    // Method to initialize the game
    public void startGame()
    {
        // Reset game state
        gameState = "active"; // Switch to game state
        running = true; // Set the game to running state

        // Initialize the snake and food, then start the timer
        spawnFood(); // Generate the first piece of food
        bodyParts = 3;
        foodEaten = 0;
        direction = 'R';

        // Reset obstacle
        obstacles.clear();

        // Reset the snake's initial position (starting in the top left corner)
        for (int i = 0; i < bodyParts; i++)
        {
            x[i] = 100 - (i * UNIT_SIZE); // Position segments horizontally
            y[i] = 100; // Keep them in the same row
        }

        // Start a new timer
        if (timer != null)
        {
            timer.stop(); // Stop any existing timer
        }

        // Delay in milliseconds for the game loop(control speed)
        int DELAY = 100;
        timer = new Timer(DELAY, this); // Initialize the timer with the game speed
        timer.start(); // Start the game loop
        repaint();
    }

    // Method to Restart the game (relay)
    public void replayGame()
    {
        // Stop the current game
        if (timer != null)
        {
            // Stop the timer from the previous game
            timer.stop();
        }
        // Reset and start the game again
        startGame(); // Reset the snake, food, and game state
    }

    // Method to handle the game's graphics (painting the snake, food, and grid)
    // Also any other additional features
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        // Handle different game states
        switch (gameState) {
            case "welcome":
                drawWelcomeScreen(g);
                break;
            case "active":
                if (running) {
                    drawGame(g);
                } else {
                    gameState = "gameOver";
                    drawGameOver(g);
                }
                break;
            case "gameOver":
                drawGameOver(g);
                break;
        }
    }

    // Draw the welcome screen
    public void drawWelcomeScreen(Graphics g)
    {
        g.setColor(new Color(128, 0, 0));
        g.setFont(new Font("Ink Free", Font.BOLD, 100));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Welcome to Snake", (950 - metrics.stringWidth("Welcome to Snake")) / 2, 950 / 2 - 50);

        g.setFont(new Font("Ink Free", Font.BOLD, 50));
        g.drawString("Press Start to Play", (950 - metrics.stringWidth("Press Start to Play")) / 2 + 130, 2 + 125);

        // Draw the warning for obstacles
        g.setColor(Color.BLACK); // Set the color to black
        g.setFont(new Font("Ink Free", Font.BOLD, 60));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Avoid the black squares!!", (950 - metrics.stringWidth("Avoid the black squares!!")) / 4 + 120, 950 / 2 + 100);
    }

    // Draw the game state
    public void drawGame(Graphics g)
    {
        // Draw the food
        g.setColor(Color.GREEN); // Set color to green
        g.fillRect(foodX, foodY, UNIT_SIZE, UNIT_SIZE); // Set what you're coloring

        // Draw the snake
        for (int i = 0; i < bodyParts; i++) {
            // Head of the snake
            if (i == 0) {
                g.setColor(new Color(62, 10, 62)); // Color dark purple for the head of the snake
            }
            // Body of the snake
            else {
                g.setColor(new Color(128, 0, 128)); // Color purple for the rest of the body
            }
            g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
        }

        // Draw the obstacle
       for (Point obstacle : obstacles)
       {
           g.setColor(Color.BLACK); // Set color to black
           g.fillRect(obstacle.x, obstacle.y, UNIT_SIZE, UNIT_SIZE); // Draw the obstacle
       }

        // Draw the score
        g.setColor(Color.BLACK);
        g.setFont(new Font("Ink Free", Font.BOLD, 35));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + foodEaten, (950 - metrics.stringWidth("Score: " + foodEaten)) / 2, g.getFont().getSize());

        // Draw the high score
        g.drawString("High Score: " + highScore, (950 - metrics.stringWidth("High Score: " + highScore)) / 2, g.getFont().getSize() + 50);
    }

    // Method to spawn food at random positions
    public void spawnFood()
    {
        boolean foodOnSnake;
        do {
            foodOnSnake = false;
            int gridHeight = (950 / UNIT_SIZE);
            int gridWidth = (950 / UNIT_SIZE);
            foodX = (int) (Math.random() * gridWidth) * UNIT_SIZE;
            foodY = (int) (Math.random() * gridHeight) * UNIT_SIZE;

            // Check if the food spawns on the snake's body
            for (int i = 0; i < bodyParts; i++) {
                if (x[i] == foodX && y[i] == foodY) {
                    foodOnSnake = true;
                    break;
                }
            }
            //spawnFood();
            repaint();
        }
        // Repeat until the food is not on the snake's body
        while (foodOnSnake); //
    }

    // Method to spawn obstacle's
    public void spawnObstacle()
    {
        boolean obstacleOnSnakeOrFood;
        Point newObstacle;
        do {
            obstacleOnSnakeOrFood = false;
            int gridHeight = (950 / UNIT_SIZE);
            int gridWidth = (950 / UNIT_SIZE);

            // Generate new obstacle position
            int obstacleX = (int) (Math.random() * gridWidth) * UNIT_SIZE;
            int obstacleY = (int) (Math.random() * gridHeight) * UNIT_SIZE;

            newObstacle = new Point(obstacleX, obstacleY);

            // Check if the obstacle spawns on the snake's body or food
            for (int i = 0; i < bodyParts; i++)
            {
                if ((x[i] == obstacleX && y[i] == obstacleY) || (obstacleX == foodX && obstacleY == foodY))
                {
                    obstacleOnSnakeOrFood = true;
                    break;
                }
            }
        }
        while (obstacleOnSnakeOrFood); // Repeat until the obstacle is not on the snake or food
        obstacles.add(newObstacle); // Add the new obstacle to the list
    }

    // Method to move the snake based on its current direction
    public void move()
    {
        // For every part that is not the head
        for (int i = bodyParts; i > 0; i--)
        {
            x[i] = x[i -1]; // Shift the body parts to follow the head
            y[i] = y[i -1];
        }

        // Update the position of the head based on direction
        switch (direction)
        {
            case 'U':
                y[0] -= UNIT_SIZE; // Move Up
                break;
            case 'D':
                y[0] += UNIT_SIZE; // Move Down
                break;
            case 'R':
                x[0] += UNIT_SIZE; // Move Right
                break;
            case 'L':
                x[0] -= UNIT_SIZE; // Move Left
                break;
        }
    }

    // Check if the snake has collided with the wall or itself
    public void checkCollisions()
    {
        // Check if the head collides with the body
        for (int i = bodyParts; i > 0; i--)
        {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }

        // Check if the head collides with the walls
        if (x[0] < 0 || x[0] >= 950 || y[0] < 0 || y[0] >= 950)
        {
            running = false;
        }

        // Check if the head collides with the obstacle
        for (Point obstacle : obstacles)
        {
            if (x[0] == obstacle.x && y[0] == obstacle.y)
            {
                running = false; // End the game if the head collides with the obstacle
                break;
            }
        }

        // Update the high score if needed when the game ends
        if (!running)
        {
            if (foodEaten > highScore)
            {
                highScore = foodEaten; // Update the high score variable
            }
            timer.stop(); // Stop the timer if the game is over
        }
    }

    // Method to check if the snake has eaten food
    public void checkFood()
    {
        if ((x[0] == foodX) && (y[0] == foodY))
        {
            bodyParts++; // Increase the size of the snake
            foodEaten++; // Increase the score
            spawnFood(); // Spawn new food

            // Check if the player has scored 7 points to spawn the obstacle
            if (foodEaten % 2 == 0)
                spawnObstacle(); // Spawn the obstacle
        }
    }

    // Method to display the game over screen
    public void drawGameOver(Graphics g)
    {
        // Display 'Game Over' text
        g.setColor(new Color(128, 0, 0)); // Set current drawing color to red
        g.setFont(new Font("Ink Free", Font.BOLD, 90)); // Set current font (name, style, size)
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (950 - metrics.stringWidth("Game Over")) / 2, 950 / 2);

        // Display the current score
        g.setFont(new Font("Ink Free", Font.BOLD, 50));
        g.drawString("Score: " + foodEaten, (950 - metrics.stringWidth("Score: " + foodEaten)) / 2, 950 / 2 + 100);

        // Display high score
        g.drawString("High Score: " + highScore, (950 - metrics.stringWidth("High Score: " + highScore)) / 2, 950 / 2 + 200);
    }

    // ActionPerformed method for the Timer (game loop)
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (running)
        {
            move(); // Move the snake
            checkFood(); //  Check if the snake eats food
            checkCollisions(); // check for collisions
        }
        repaint(); // Repaint the screen with the updated state
    }

    // Method for handling key input (controls)
    @Override
    public void keyPressed(KeyEvent e)
    {
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_LEFT: // Left arrow key is equal to 'L'
                if (direction != 'R') direction = 'L'; // Move left if not moving right
                break;
            case KeyEvent.VK_RIGHT: // Right arrow key is equal to 'R'
                if (direction != 'L') direction = 'R'; // Move right if not moving left
                break;
            case KeyEvent.VK_UP: // Up arrow key is equal to 'U'
                if (direction != 'D') direction = 'U'; // Move up if not moving down
                break;
            case KeyEvent.VK_DOWN: // Down arrow is equal to 'D'
                if (direction != 'U') direction = 'D'; // Move down if not moving up
                break;
        }
    }

    // Not used but required for the KeyListener interface
    @Override
    public void keyReleased(KeyEvent e)
    {
        // EMPTINESS
    }

    // Not used but also required for the KeyListener interface
    @Override
    public void keyTyped(KeyEvent e)
    {
        // UR MOM
    }
}
