import java.awt.*;

public class Submarine {
    final static public int length = 3;
    private Point point;
    private Direction direction;
    private Point[] points = new Point[length];
    public boolean sunk = false;
    
    public Submarine(Point point, Direction direction) {
        this.point = point;
        this.direction = direction;
        
        switch (direction) {
            case NORTH -> {
                for (int i = 0; i < length; i++) {
                    points[i] = new Point(point.x, point.y - i);
                }
            }
            case SOUTH -> {
                for (int i = 0; i < length; i++) {
                    points[i] = new Point(point.x, point.y + i);
                }
            }
            case WEST -> {
                for (int i = 0; i < length; i++) {
                    points[i] = new Point(point.x - i, point.y);
                }
            }
            case EAST -> {
                for (int i = 0; i < length; i++) {
                    points[i] = new Point(point.x + i, point.y);
                }
            }
        }
    }
    
    public Point[] getPoints() {
        return points;
    }
    
    public Point getPoint() {
        return point;
    }
    
    public void setPoint(Point point) {
        this.point = point;
    }
    
    public Direction getDirection() {
        return direction;
    }
    
    public void setDirection(Direction direction) {
        this.direction = direction;
    }
    
    public void DrawShip(Dimension dimension, Graphics graphics, boolean opponent) {
        int squareSize = dimension.height / 10;
        int x;
        int y;
        if (opponent) {
            x = Run.opponentBoardX + point.x * squareSize;
            y = Run.opponentBoardY + point.y * squareSize;
        } else {
            x = Run.playerBoardX + point.x * squareSize;
            y = Run.playerBoardY + point.y * squareSize;
        }
        switch (direction) {
            case NORTH -> graphics.fillOval(x, y - squareSize * (length - 1), squareSize, squareSize * length);
            case SOUTH -> graphics.fillOval(x, y, squareSize, squareSize * length);
            case WEST -> graphics.fillOval(x - squareSize * (length - 1), y, squareSize * length, squareSize);
            case EAST -> graphics.fillOval(x, y, squareSize * length, squareSize);
        }
    }
    
    public boolean outOfRange(Carrier carrier, BattleShip battleShip, Cruiser cruiser) {
        for (Point point1 : points) {
            if ((point1.x > 9) || (point1.x < 0) || (point1.y > 9) || (point1.y < 0)) {
                return true;
            }
            for (Point point2 : carrier.getPoints()) {
                if ((point2.equals(point1))) {
                    return true;
                }
            }
            for (Point point2 : battleShip.getPoints()) {
                if ((point2.equals(point1))) {
                    return true;
                }
            }
            for (Point point2 : cruiser.getPoints()) {
                if ((point2.equals(point1))) {
                    return true;
                }
            }
            
        }
        return false;
    }
}
