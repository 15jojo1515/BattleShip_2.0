import java.awt.*;

public class Carrier {
    final static public int length = 5;
    private Point point;
    private Direction direction;
    private Point[] points = new Point[length];
    public boolean sunk = false;
    
    public Carrier(Point point, Direction direction) {
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
    
    public boolean outOfRange() {
        for (Point x : points) {
            if ((x.x > 9) || (x.x < 0) || (x.y > 9) || (x.y < 0)) {
                return true;
            }
        }
        return false;
    }
}
