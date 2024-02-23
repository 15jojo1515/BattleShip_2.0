import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Player{
    private Carrier carrier;
    private BattleShip battleShip;
    private Cruiser cruiser;
    private Submarine submarine;
    private PatrolBoat patrolBoat;
    private ArrayList<Point> misses = new ArrayList<>(0);
    private ArrayList<Point> hits = new ArrayList<>(0);
    private boolean ready = false;
    private boolean win = false;
    private boolean bot;
    private boolean turn;
    private Ship currentShip;
    public boolean inMenu;
    private Image grayX;
    private Image redX;
    public boolean loading = true;
    private Clip missSound;
    private Clip hitSound;
    private Clip sunkSound;
    private Clip winSound;
    private Clip lossSound;

    public boolean isTurn() {
        return turn;
    }
    public Player(boolean bot, boolean turn, boolean inMenu, Dimension dimension){
        this.bot = bot;
        this.turn = turn;
        this.inMenu = inMenu;
        currentShip = Ship.CARRIER;
        if (bot){
            placeBoatsBot(dimension);
        }

        // load images
        String dir = Paths.get("").toAbsolutePath().normalize().toString();
        try{
            grayX = ImageIO.read(new File(dir+"\\assets\\grayX.png"));
            redX = ImageIO.read((new File(dir+"\\assets\\redX.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        grayX = grayX.getScaledInstance(dimension.width/10-5,dimension.height/10-7,Image.SCALE_DEFAULT);
        redX = redX.getScaledInstance(dimension.width/10-5,dimension.height/10-5,Image.SCALE_DEFAULT);

        // load sounds
        try {
            // miss Sound
            missSound = AudioSystem.getClip();
            missSound.open(AudioSystem.getAudioInputStream(new File(dir+"\\assets\\missSound.wav")));
            // hit Sound
            hitSound = AudioSystem.getClip();
            hitSound.open(AudioSystem.getAudioInputStream(new File(dir+"\\assets\\hitSound.wav")));
            // sink Sound
            sunkSound = AudioSystem.getClip();
            // win Sound
            winSound = AudioSystem.getClip();
            // loss Sound
            lossSound = AudioSystem.getClip();
        } catch (Exception ignored){}
        loading = false;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    public BattleShip getBattleShip() {
        return battleShip;
    }

    public void setBattleShip(BattleShip battleShip) {
        this.battleShip = battleShip;
    }

    public Cruiser getCruiser() {
        return cruiser;
    }

    public void setCruiser(Cruiser cruiser) {
        this.cruiser = cruiser;
    }

    public Submarine getSubmarine() {
        return submarine;
    }

    public void setSubmarine(Submarine submarine) {
        this.submarine = submarine;
    }

    public PatrolBoat getPatrolBoat() {
        return patrolBoat;
    }

    public void setPatrolBoat(PatrolBoat patrolBoat) {
        this.patrolBoat = patrolBoat;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean isWin() {
        return win;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public void drawBoard(Graphics graphics,int xPosition,int yPosition,Dimension boardDimension){
        graphics.setColor(Color.BLACK);
        graphics.fillRect(xPosition-5,yPosition-5,boardDimension.width+5,boardDimension.height+5);
        graphics.setColor(Color.BLUE);
        int xBox = boardDimension.width/10-5;
        int yBox = boardDimension.height/10-5;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                graphics.fillRect(xPosition+i*boardDimension.width/10, yPosition+j*boardDimension.height/10,xBox , yBox);
            }
        }
    }//done

    public void drawShipMouse(Point point, Direction direction, Graphics graphics,Dimension dimension){
        graphics.setColor(Color.DARK_GRAY);
        switch (direction) {
            case EAST ->
                    graphics.fillOval(point.x - dimension.width / 20, point.y - dimension.width / 20, getCurrentShipSize() * dimension.width / 10, dimension.height / 10);
            case WEST ->
                    graphics.fillOval(point.x - dimension.width / 20 * (getCurrentShipSize() * 2 - 1), point.y - dimension.width / 20, getCurrentShipSize() * dimension.width / 10, dimension.height / 10);
            case NORTH ->
                    graphics.fillOval(point.x - dimension.width / 20, point.y - dimension.width / 20 * (getCurrentShipSize() * 2 - 1), dimension.height / 10, getCurrentShipSize() * dimension.width / 10);
            case SOUTH ->
                    graphics.fillOval(point.x - dimension.width / 20, point.y - dimension.width / 20, dimension.height / 10, getCurrentShipSize() * dimension.width / 10);
        }
    }//done
    public void drawShips(Dimension dimension, Graphics graphics,boolean opponent){
        graphics.setColor(Color.DARK_GRAY);
        if (carrier != null){
            carrier.DrawShip(dimension,graphics,opponent);
        }
        if (battleShip != null){
            battleShip.DrawShip(dimension,graphics,opponent);
        }
        if (cruiser != null){
            cruiser.DrawShip(dimension,graphics,opponent);
        }
        if (submarine != null){
            submarine.DrawShip(dimension,graphics,opponent);
        }
        if (patrolBoat != null){
            patrolBoat.DrawShip(dimension,graphics,opponent);
        }
    }//done
    public Ship getCurrentShip(){
        return currentShip;
    }
    public int getCurrentShipSize(){
        switch (currentShip){
            case CARRIER -> {
                return 5;
            }
            case BATTLESHIP -> {
                return 4;
            }
            case CRUISER, SUBMARINE -> {
                return 3;
            }
            case PATROL_BOAT -> {
                return 2;
            }
        }
        return 0;
    }//done
    private boolean canPlace(Point point, Direction direction, Dimension dimension){
        if (currentShip == Ship.CARRIER){
            Carrier tempCarrier = new Carrier(point,direction);
            return !(tempCarrier.outOfRange());
        }
        if (currentShip == Ship.BATTLESHIP){
            BattleShip tempBattleship = new BattleShip(point,direction);
            return !(tempBattleship.outOfRange(carrier));
        }
        if (currentShip.equals(Ship.CRUISER)){
            Cruiser tempCruiser = new Cruiser(point,direction);
            return !(tempCruiser.outOfRange(carrier,battleShip));
        }
        if (currentShip.equals(Ship.SUBMARINE)){
            Submarine tempSubmarine = new Submarine(point,direction);
            return !(tempSubmarine.outOfRange(carrier,battleShip,cruiser));
        }
        if (currentShip == Ship.PATROL_BOAT){
            PatrolBoat tempPatrolBoat = new PatrolBoat(point,direction);
            return !(tempPatrolBoat.outOfRange(carrier,battleShip,cruiser,submarine));
        }
        return false;
    }//done
    public void placeShip(Point point,Direction direction, Dimension dimension){
        if (bot){
            if (canPlace(point,direction,dimension)){
                createShip(point,direction);
            }
        }else{
            Point convertedPoint = convert2Point(point, dimension,false);
            if ((convertedPoint.x<=9)&&(convertedPoint.x>=0)&&(convertedPoint.y<=9)&&(convertedPoint.y>=0)&&(canPlace(convertedPoint,direction,dimension))) {
            createShip(convertedPoint, direction);
            }
        }
    }//done
    private Point convert2Point(Point point,Dimension dimension,boolean opponent){
        if (opponent){
            point.setLocation(point.x-Run.opponentBoardX,point.y-Run.opponentBoardY);
        }else{
            point.setLocation(point.x-Run.playerBoardX,point.y-Run.playerBoardY);
        }
        point.setLocation(Math.floorDiv(point.x*10,dimension.width),Math.floorDiv(point.y*10,dimension.height));
        return point;
    }//done
    public void cycleShip(){
        switch (currentShip){
            case CARRIER -> currentShip = Ship.BATTLESHIP;
            case BATTLESHIP -> currentShip = Ship.CRUISER;
            case CRUISER -> currentShip = Ship.SUBMARINE;
            case SUBMARINE ->  currentShip = Ship.PATROL_BOAT;
        }
    }//done
    private void createShip(Point point,Direction direction){
        switch (currentShip){
            case CARRIER -> carrier = new Carrier(point, direction);
            case BATTLESHIP -> battleShip = new BattleShip(point, direction);
            case CRUISER -> cruiser = new Cruiser(point, direction);
            case SUBMARINE -> submarine = new Submarine(point, direction);
            case PATROL_BOAT -> patrolBoat = new PatrolBoat(point, direction);
        }
        cycleShip();
    }//done
    public void drawRemainingShips(Dimension dimension, Graphics graphics){
        graphics.setColor(Color.DARK_GRAY);
        int squareSize = dimension.height / 10;
        if (carrier == null){
            graphics.fillOval(75,Run.playerBoardY +squareSize,BattleShip.length*squareSize,squareSize);
        }
        if (battleShip == null){
            graphics.fillOval(75,Run.playerBoardY +squareSize*2,Cruiser.length*squareSize,squareSize);
        }
        if (cruiser == null){
            graphics.fillOval(75,Run.playerBoardY +squareSize*3,Submarine.length*squareSize,squareSize);
        }
        if (submarine == null){
            graphics.fillOval(75,Run.playerBoardY +squareSize*4,PatrolBoat.length*squareSize,squareSize);
        }
    }//done
    public void placedAll(){
        if ((carrier != null)&&(battleShip != null)&&(cruiser != null)&&(submarine != null)&&(patrolBoat != null)){
            ready = true;
            Run.playerBoardX = 5;
        }
    }//done
    public void drawReadyButton(Graphics graphics,int xLocation,int yLocation){
        graphics.setColor(new Color(147, 0, 0));
        graphics.fillRoundRect(xLocation,yLocation,250,100,10,10);
        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font(Font.SANS_SERIF,Font.BOLD,100));
        graphics.drawString("Start",xLocation+10,yLocation+90);
    }//done
    private void placeBoatsBot(Dimension dimension){
        Random rnd = new Random();
        while (patrolBoat == null){
            Point point = new Point(rnd.nextInt(10), rnd.nextInt(10));
            Direction direction = Direction.values()[rnd.nextInt(4)];
            placeShip(point,direction,dimension);
        }
    }//done
    public void drawHoveringAttack(Point point, Graphics graphics, Dimension dimension){
        drawX(convert2Point(point,dimension,true),graphics,dimension, false,true);
    }//done
    private void drawX(Point convertedPoint, Graphics graphics, Dimension dimension, boolean hit, boolean opponent) {
        int squareSize = dimension.width/10;
        if ((convertedPoint.x<=9)&&(convertedPoint.x>=0)&&(convertedPoint.y<=9)&&(convertedPoint.y>=0)){// checks if in range
            if (opponent){
                if (hit) {
                    graphics.drawImage(redX, convertedPoint.x*squareSize+Run.opponentBoardX, convertedPoint.y*squareSize+Run.opponentBoardY, null);
                } else {
                    graphics.drawImage(grayX, convertedPoint.x*squareSize+Run.opponentBoardX, convertedPoint.y*squareSize+Run.opponentBoardY, null);
                }
            }else {
                if (hit) {
                    graphics.drawImage(redX, convertedPoint.x*squareSize+Run.playerBoardX, convertedPoint.y*squareSize+Run.playerBoardY, null);
                } else {
                    graphics.drawImage(grayX, convertedPoint.x*squareSize+Run.playerBoardX, convertedPoint.y*squareSize+Run.playerBoardY, null);
                }
            }
        }
    }
    public static void drawLoading(Graphics graphics){
        graphics.setColor(new Color(147, 0, 0));
        graphics.setFont(new Font(Font.SANS_SERIF,Font.BOLD,200));
        graphics.drawString("LOADING",500,500);
    }

    /**
     * tries attack
     * @param point mouse point
     * @param dimension board dimension
     * @param player the player that's being attacked
     * @param opponent true if attacking the opponents board
     */
    public void attack(Point point,Dimension dimension, Player player, boolean opponent){
        point = convert2Point(point,dimension,opponent);
        if ((point.x<=9)&&(point.x>=0)&&(point.y<=9)&&(point.y>=0)){
            if (checkHit(point,player)&&!(hits.contains(point))){
                hits.add(point);
                turn = false;
                player.turn = true;
                // play sound
                hitSound.flush();
                hitSound.setFramePosition(0);
                hitSound.start();
            }else if (!(misses.contains(point))&&!(hits.contains(point))){
                misses.add(point);
                turn = false;
                player.turn = true;
                // play sound
                missSound.flush();
                missSound.setFramePosition(0);
                missSound.start();
            }

            if (containsAllHit(player.carrier.getPoints())){
                player.carrier.sunk = true;
            }if (containsAllHit(player.battleShip.getPoints())){
                player.battleShip.sunk = true;
            }if (containsAllHit(player.cruiser.getPoints())){
                player.cruiser.sunk = true;
            }if (containsAllHit(player.submarine.getPoints())){
                player.submarine.sunk = true;
            }if (containsAllHit(player.patrolBoat.getPoints())){
                player.patrolBoat.sunk = true;
            }
        }

    }
    private boolean containsAllHit(Point[] points){
        for (Point point : points) {
            if (!hits.contains(point)){
                return false;
            }
        }
        return true;
    }
    private boolean checkHit(Point point,Player player){
        for (Point x :player.getCarrier().getPoints()) {
            if (point.equals(x)){
                return true;
            }
        }
        for (Point x :player.getBattleShip().getPoints()) {
            if (point.equals(x)){
                return true;
            }
        }
        for (Point x :player.getCruiser().getPoints()) {
            if (point.equals(x)){
                return true;
            }
        }
        for (Point x :player.getSubmarine().getPoints()) {
            if (point.equals(x)){
                return true;
            }
        }
        for (Point x :player.getPatrolBoat().getPoints()) {
            if (point.equals(x)){
                return true;
            }
        }
        return false;
    }
    public void drawattacks(Graphics graphics, Dimension dimension, boolean opponent){
        if (misses!=null) {
            for (Point x : misses) {
                drawX(x, graphics, dimension, false, opponent);
            }
        }
        if (hits != null) {
            for (Point x : hits) {
                drawX(x, graphics, dimension, true, opponent);
            }
        }

    }// TODO: 2/12/2024 optimize

    /**
     *
     * @param player the player that's being attacked
     */
    public void attackBot(Player player){
        Random rnd = new Random();
        Point point;
        while (true){
            point = new Point(rnd.nextInt(10), rnd.nextInt(10));
            if (!((hits.contains(point))||(misses.contains(point)))){
                break;
            }
        }
        if (checkHit(point,player)){
            hits.add(point);
        }else {
            misses.add(point);
        }
        if (containsAllHit(player.carrier.getPoints())){
            player.carrier.sunk = true;
        }if (containsAllHit(player.battleShip.getPoints())){
            player.battleShip.sunk = true;
        }if (containsAllHit(player.cruiser.getPoints())){
            player.cruiser.sunk = true;
        }if (containsAllHit(player.submarine.getPoints())){
            player.submarine.sunk = true;
        }if (containsAllHit(player.patrolBoat.getPoints())){
            player.patrolBoat.sunk = true;
        }
        turn = false;
        player.turn = true;
    }//
    public void drawSunkShips(Graphics graphics,Dimension dimension, boolean opponent){
        graphics.setColor(Color.darkGray);
        if (carrier.sunk){
            carrier.DrawShip(dimension,graphics,opponent);
        }
        if (battleShip.sunk){
            battleShip.DrawShip(dimension,graphics,opponent);
        }
        if (cruiser.sunk){
            cruiser.DrawShip(dimension,graphics,opponent);
        }
        if (submarine.sunk){
            submarine.DrawShip(dimension,graphics,opponent);
        }
        if (patrolBoat.sunk){
            patrolBoat.DrawShip(dimension,graphics,opponent);
        }
    }//done
    public void calcWin(Player player){
        if ((carrier.sunk)&&(battleShip.sunk)&&(cruiser.sunk)&&(submarine.sunk)&&(patrolBoat.sunk)){
            player.setWin(true);
            ready = false;
        } else if ((player.carrier.sunk)&&(player.battleShip.sunk)&&(player.cruiser.sunk)&&(player.submarine.sunk)&&(player.patrolBoat.sunk)) {
            setWin(true);
            ready = false;
        }
    }//done
    public static void drawWin(Graphics graphics){
        graphics.setColor(new Color(147, 0, 0));
        graphics.setFont(new Font(Font.SANS_SERIF,Font.BOLD,200));
        graphics.drawString("You Win",400,500);
        drawBackToMenuButton(graphics);
    }
    public static void drawLoss(Graphics graphics){
        graphics.setColor(new Color(147, 0, 0));
        graphics.setFont(new Font(Font.SANS_SERIF,Font.BOLD,200));
        graphics.drawString("You Lose",400,500);
        drawBackToMenuButton(graphics);
    }
    private static void drawBackToMenuButton(Graphics graphics){
        graphics.fillRoundRect(600,600,400,100,10,10);
        graphics.setFont(new Font(Font.SANS_SERIF,Font.BOLD,40));
        graphics.setColor(Color.black);
        graphics.drawString("Back to Main Menu",620,670);
    }
    public static void drawExitButton(Graphics graphics) {
        graphics.setColor(new Color(147, 0, 0));
        graphics.setFont(new Font(Font.SANS_SERIF,Font.BOLD,10));
        graphics.fillRoundRect(1575,0,25,10,10,10);
        graphics.setColor(Color.black);
        graphics.drawString("X",1584,7);
    }


}
