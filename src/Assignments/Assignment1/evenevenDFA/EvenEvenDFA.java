package Assignments.Assignment1.evenevenDFA;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EvenEvenDFA extends JFrame {

    // UI Elements
    private JTextField inputTextField;
    private JButton startBtn;
    private JButton resetBtn;
    private JTextArea stepLogArea;
    private DFACanvas canvas; // Our drawing area
    private Timer animationTimer;

    // Simulation Variables
    private String tapeString = "";
    private int tapeIndex = 0;
    private int currentState = 0; // Starts at q0
    private boolean isSimulating = false;
    private boolean isFinished = false;

    public EvenEvenDFA() {
        setTitle("Simple EVEN-EVEN DFA Simulator");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. TOP PANEL: Controls
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Enter String (a, b): "));

        inputTextField = new JTextField(15);
        topPanel.add(inputTextField);

        startBtn = new JButton("Simulate");
        resetBtn = new JButton("Reset");
        resetBtn.setEnabled(false); // Disabled until simulation ends

        topPanel.add(startBtn);
        topPanel.add(resetBtn);
        add(topPanel, BorderLayout.NORTH);

        // 2. RIGHT PANEL: The Step-by-Step Log
        stepLogArea = new JTextArea(10, 25);
        stepLogArea.setEditable(false);
        JScrollPane scrollBox = new JScrollPane(stepLogArea);
        scrollBox.setBorder(BorderFactory.createTitledBorder("Simulation Log"));
        add(scrollBox, BorderLayout.EAST);

        // 3. CENTER PANEL: The Drawing Canvas
        canvas = new DFACanvas();
        canvas.setBackground(Color.WHITE);
        add(canvas, BorderLayout.CENTER);

        // 4. BUTTON CLICKS
        startBtn.addActionListener(e -> startTheSimulation());
        resetBtn.addActionListener(e -> resetTheSimulation());
    }

    private void startTheSimulation() {
        tapeString = inputTextField.getText().toLowerCase().trim();

        // Check if user entered wrong letters
        if (!tapeString.matches("[ab]*")) {
            JOptionPane.showMessageDialog(this, "Only 'a' and 'b' are allowed!");
            return;
        }

        // Setup starting values
        tapeIndex = 0;
        currentState = 0;
        isSimulating = true;
        isFinished = false;

        // Lock the buttons
        startBtn.setEnabled(false);
        inputTextField.setEnabled(false);

        stepLogArea.setText("Started!\nStarting at q0 (Even A, Even B)\n\n");
        canvas.repaint(); // Tell canvas to redraw

        // Create a timer that runs every 1 second (1000 milliseconds)
        animationTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tapeIndex < tapeString.length()) {
                    char letter = tapeString.charAt(tapeIndex);
                    int oldState = currentState;

                    // DFA RULES
                    if (currentState == 0 && letter == 'a') currentState = 1;
                    else if (currentState == 0 && letter == 'b') currentState = 2;
                    else if (currentState == 1 && letter == 'a') currentState = 0;
                    else if (currentState == 1 && letter == 'b') currentState = 3;
                    else if (currentState == 2 && letter == 'a') currentState = 3;
                    else if (currentState == 2 && letter == 'b') currentState = 0;
                    else if (currentState == 3 && letter == 'a') currentState = 2;
                    else if (currentState == 3 && letter == 'b') currentState = 1;

                    // Add to log
                    stepLogArea.append("Read '" + letter + "': q" + oldState + " -> q" + currentState + "\n");

                    tapeIndex++;
                    canvas.repaint(); // Redraw with new state
                } else {
                    // String is finished
                    animationTimer.stop();
                    isSimulating = false;
                    isFinished = true;
                    resetBtn.setEnabled(true); // Unlock reset button

                    if (currentState == 0) {
                        stepLogArea.append("\nResult: ACCEPTED!");
                    } else {
                        stepLogArea.append("\nResult: REJECTED!");
                    }
                    canvas.repaint();
                }
            }
        });
        animationTimer.start();
    }

    private void resetTheSimulation() {
        inputTextField.setEnabled(true);
        startBtn.setEnabled(true);
        resetBtn.setEnabled(false);
        inputTextField.setText("");
        stepLogArea.setText("");
        tapeString = "";
        isSimulating = false;
        isFinished = false;
        currentState = 0;
        canvas.repaint();
    }

    // --- DRAWING CLASS ---
    class DFACanvas extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Draw the tape at the top
            drawInputTape(g);

            // X and Y locations for our 4 states
            int q0x = 150, q0y = 150;
            int q1x = 400, q1y = 150;
            int q2x = 150, q2y = 400;
            int q3x = 400, q3y = 400;

            // 1. Draw Lines between states
            g.setColor(Color.BLACK);
            // q0 to q1 (Horizontal)
            g.drawLine(q0x, q0y, q1x, q1y);
            g.drawString("a", 270, 140);

            // q0 to q2 (Vertical)
            g.drawLine(q0x, q0y, q2x, q2y);
            g.drawString("b", 130, 280);

            // q1 to q3 (Vertical)
            g.drawLine(q1x, q1y, q3x, q3y);
            g.drawString("b", 415, 280);

            // q2 to q3 (Horizontal)
            g.drawLine(q2x, q2y, q3x, q3y);
            g.drawString("a", 270, 390);

            // Draw simple arrow pointing to START (q0)
            g.drawLine(50, 150, 120, 150);
            g.drawString("START", 50, 140);
            g.fillPolygon(new int[]{120, 110, 110}, new int[]{150, 145, 155}, 3); // Simple triangle

            // 2. Draw the States (Circles) over the lines
            drawCircleState(g, q0x, q0y, "q0", currentState == 0, true);
            drawCircleState(g, q1x, q1y, "q1", currentState == 1, false);
            drawCircleState(g, q2x, q2y, "q2", currentState == 2, false);
            drawCircleState(g, q3x, q3y, "q3", currentState == 3, false);

            // 3. Draw final result text
            if (isFinished) {
                g.setFont(new Font("Arial", Font.BOLD, 24));
                if (currentState == 0) {
                    g.setColor(Color.GREEN);
                    g.drawString("ACCEPTED!", 220, 500);
                } else {
                    g.setColor(Color.RED);
                    g.drawString("REJECTED!", 220, 500);
                }
            }
        }

        private void drawInputTape(Graphics g) {
            if (tapeString.isEmpty()) return;

            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("Tape:", 50, 40);

            for (int i = 0; i < tapeString.length(); i++) {
                int boxX = 120 + (i * 40); // Move right by 40px for each box
                int boxY = 15;

                // Highlight the box we are currently reading
                if (i == tapeIndex && isSimulating) {
                    g.setColor(Color.YELLOW);
                    g.fillRect(boxX, boxY, 40, 40);
                }

                // Draw box outline
                g.setColor(Color.BLACK);
                g.drawRect(boxX, boxY, 40, 40);

                // Draw the letter inside the box
                g.drawString(String.valueOf(tapeString.charAt(i)), boxX + 15, boxY + 25);
            }
        }

        private void drawCircleState(Graphics g, int x, int y, String name, boolean isCurrent, boolean isFinal) {
            int radius = 30; // 30px radius means 60px wide/tall

            // If this is the current state, fill it yellow. Otherwise, fill it white.
            if (isCurrent) {
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(Color.WHITE);
            }

            // Draw the filled circle (subtracting radius to center it exactly on X,Y)
            g.fillOval(x - radius, y - radius, radius * 2, radius * 2);

            // Draw the black outline
            g.setColor(Color.BLACK);
            g.drawOval(x - radius, y - radius, radius * 2, radius * 2);

            // If it's a final state, draw a smaller circle inside
            if (isFinal) {
                g.drawOval(x - radius + 5, y - radius + 5, (radius * 2) - 10, (radius * 2) - 10);
            }

            // Write the state name (q0, q1, etc) in the middle
            g.drawString(name, x - 10, y + 5);
        }
    }

    public static void main(String[] args) {
        // Start the application
        SwingUtilities.invokeLater(() -> new EvenEvenDFA().setVisible(true));
    }
}