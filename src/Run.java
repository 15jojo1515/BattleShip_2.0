import org.w3c.dom.css.RGBColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Run {
    public static int playerBoardX = 450;
    public static int playerBoardY = 250;
    public static int opponentBoardX = 895;
    public static int opponentBoardY = 250;
    private static final int WIDTH = 1600;
    private static final int HEIGHT = 1067;
    private static Canvas canvas = new Canvas();
    private static Graphics graphics;
    // fps
    private static double time;
    private static int count = 0;
    private static double fpsVal = 0;
    private static double displayFPS = 0;
    private static boolean[] clicked = {false};
    private static Direction[] direction = {Direction.EAST};
    private static JFrame frame;

    public static void main(String[] args) {

        boolean[] clicked = {false};
        Direction[] direction = {Direction.EAST};

        frame = new JFrame("Battle Ship");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.setVisible(true);
        frame.setResizable(false);

        canvas.setSize(WIDTH, HEIGHT);
        canvas.setBackground(new Color(0,0,153));
        canvas.setVisible(true);
        frame.add(canvas);
        canvas.createBufferStrategy(3);

        MouseListener mouseListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    clicked[0] = true;
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };
        canvas.addMouseListener(mouseListener);

        MouseWheelListener mouseWheelListener = new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getWheelRotation() == 1) {
                    if (direction[0].equals(Direction.EAST)) {
                        direction[0] = Direction.SOUTH;
                    } else if (direction[0].equals(Direction.SOUTH)) {
                        direction[0] = Direction.WEST;
                    } else if (direction[0].equals(Direction.WEST)) {
                        direction[0] = Direction.NORTH;
                    } else if (direction[0].equals(Direction.NORTH)) {
                        direction[0] = Direction.EAST;
                    }
                } else if (e.getWheelRotation() == -1) {
                    if (direction[0].equals(Direction.EAST)) {
                        direction[0] = Direction.NORTH;
                    } else if (direction[0].equals(Direction.NORTH)) {
                        direction[0] = Direction.WEST;
                    } else if (direction[0].equals(Direction.WEST)) {
                        direction[0] = Direction.SOUTH;
                    } else if (direction[0].equals(Direction.SOUTH)) {
                        direction[0] = Direction.EAST;
                    }
                }
            }
        };
        canvas.addMouseWheelListener(mouseWheelListener);

        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == 'r') {
                    if (direction[0].equals(Direction.EAST)) {
                        direction[0] = Direction.SOUTH;
                    } else if (direction[0].equals(Direction.SOUTH)) {
                        direction[0] = Direction.WEST;
                    } else if (direction[0].equals(Direction.WEST)) {
                        direction[0] = Direction.NORTH;
                    } else if (direction[0].equals(Direction.NORTH)) {
                        direction[0] = Direction.EAST;
                    }
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        };
        canvas.addKeyListener(keyListener);

        Cursor cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
        frame.setCursor(cursor);
        Player player2;

        // main run
        while (true) {
            Dimension boardDim = new Dimension(700, 700);
            graphics = canvas.getBufferStrategy().getDrawGraphics();
            Player.drawLoading(graphics);
            Player player = new Player(false, true, true, boardDim);
            playerBoardX = 450;


            //main menu
            while (player.inMenu) {
                startFPS();
                startGraphics();

                Player.drawExitButton(graphics);
                player.drawReadyButton(graphics, 650, 450);
                if (clicked[0]) {
                    double x = MouseInfo.getPointerInfo().getLocation().getX();
                    double y = MouseInfo.getPointerInfo().getLocation().getY();
                    if ((x > 650) && (x < 900) && (y > 450) && (y < 550)) {
                        player.inMenu = false;
                    }
                    if ((x > 1575) && (x <= 1600) && (y >= 0) && (y < 10)) {
                        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                    }
                    clicked[0] = false;
                }

                endFPS();
                endGraphics();
            }
            player2 = new Player(true, false, false, boardDim);

            // start and playing game
            while (!(player.isWin() || player2.isWin())) {
                //start frame
                startFPS();
                startGraphics();
                //

                Player.drawExitButton(graphics);
                if (!(player.isReady())) {
                    player.drawBoard(graphics, playerBoardX, playerBoardY, boardDim);
                    if (clicked[0]) {
                        // ship placement
                        player.placeShip(MouseInfo.getPointerInfo().getLocation(), direction[0], boardDim);
                        // exit button
                        double x = MouseInfo.getPointerInfo().getLocation().getX();
                        double y = MouseInfo.getPointerInfo().getLocation().getY();
                        if ((x > 1575) && (x <= 1600) && (y >= 0) && (y < 10)) {
                            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                        }

                        clicked[0] = false;
                    }
                    player.drawRemainingShips(boardDim, graphics);
                    player.drawShipMouse(MouseInfo.getPointerInfo().getLocation(), direction[0], graphics, boardDim);
                    player.drawShips(boardDim, graphics, false);
                    player.placedAll();

                } else if (player.isReady() && !((player.isWin()) || (player2.isWin()))) {
                    player.drawBoard(graphics, playerBoardX, playerBoardY, boardDim);
                    player.drawShips(boardDim, graphics, false);
                    player2.drawBoard(graphics, opponentBoardX, opponentBoardY, boardDim);
                    player2.drawShips(boardDim,graphics,true);  //to make opponents ships visible for testing

                    player.drawHoveringAttack(MouseInfo.getPointerInfo().getLocation(), graphics, boardDim);
                    if (clicked[0]) {
                        // attack
                        if (player.isTurn()) {
                            player.attack(MouseInfo.getPointerInfo().getLocation(), boardDim, player2, true);
                        }
                        // exit button
                        double x = MouseInfo.getPointerInfo().getLocation().getX();
                        double y = MouseInfo.getPointerInfo().getLocation().getY();
                        if ((x > 1575) && (x <= 1600) && (y >= 0) && (y < 10)) {
                            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                        }

                        clicked[0] = false;
                        player.calcWin(player2);
                    }
                    if (player2.isTurn()) {
                        player2.attackBot(player);
                        player.calcWin(player2);
                    }
                    player2.drawSunkShips(graphics, boardDim, true);
                    player.drawattacks(graphics, boardDim, true);
                    player2.drawattacks(graphics, boardDim, false);
                }

                //end frame
                endFPS();
                endGraphics();
            }

            // win loss menu
            while ((player.isWin() || player2.isWin())) {
                startFPS();
                startGraphics();

                Player.drawExitButton(graphics);
                if (player.isWin()){
                    Player.drawWin(graphics);
                    if (clicked[0]) {
                        double x = MouseInfo.getPointerInfo().getLocation().getX();
                        double y = MouseInfo.getPointerInfo().getLocation().getY();
                        if ((x > 600) && (x < 1000) && (y > 600) && (y < 700)) {
                            player.setWin(false);
                        }
                        if ((x > 1575) && (x <= 1600) && (y >= 0) && (y < 10)) {
                            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                        }
                        clicked[0] = false;
                    }
                }
                else {
                    Player.drawLoss(graphics);
                    if (clicked[0]) {
                        double x = MouseInfo.getPointerInfo().getLocation().getX();
                        double y = MouseInfo.getPointerInfo().getLocation().getY();
                        if ((x > 600) && (x < 1000) && (y > 600) && (y < 700)) {
                            player2.setWin(false);
                        }
                        if ((x > 1575) && (x <= 1600) && (y >= 0) && (y < 10)) {
                            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                        }
                        clicked[0] = false;
                    }
                }

                endFPS();
                endGraphics();
            }
        }
    }

    private static void startGraphics(){
        graphics = canvas.getBufferStrategy().getDrawGraphics();
        graphics.clearRect(0, 0, WIDTH, HEIGHT);
    }
    private static void endGraphics(){
        canvas.getBufferStrategy().show();
        graphics.dispose();
    }
    private static void startFPS(){
        time = System.nanoTime();
    }
    private static void endFPS(){
        count++;
        try {
            time = 1 / (System.nanoTime() - time) * 1000000000;
        } catch (Exception e) {
            time = -1;
        }
        fpsVal += time;
        if (count == 10) {
            displayFPS = fpsVal / 10;
            fpsVal = 0;
            count = 0;
        }
        graphics.setColor(Color.black);
        graphics.setFont(new Font(Font.SERIF, Font.BOLD, 25));
        graphics.drawString(String.format("%.1f FPS", displayFPS), 10, 20);
    }
}
