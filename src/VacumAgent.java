import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


class Map {
    private int x;
    private int y;
    private List<Integer> xList = new ArrayList<>();
    private List<Integer> yList = new ArrayList<>();
    private boolean _map[][];

    public Map() {
        x = 50;
        y = 50;
        _map = new boolean[100][100];
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void forward(String dir) {
        switch (dir) {
            case "North":
                y++;
                break;
            case "West":
                x--;
                break;
            case "South":
                y--;
                break;
            case "East":
                x++;
                break;
        }
    }

    public void back(String dir) {
        switch (dir) {
            case "North":
                y--;
                break;
            case "West":
                x++;
                break;
            case "South":
                y++;
                break;
            case "East":
                x--;
                break;
        }
    }

    public void RecordPosition() {
        _map[x][y] = true;
    }

    public boolean nextVisited (String dir) {
        int yTmp = this.y;
        int xTmp = this.x;

        switch (dir) {
            case "North":
                yTmp++;
                break;
            case "West":
                xTmp--;
                break;
            case "South":
                yTmp--;
                break;
            case "East":
                xTmp++;
                break;
        }

        return _map[xTmp][yTmp] == true;
    }

    public boolean isAtStart() {
        return this.x == 50 && this.y == 50;
    }

    public boolean correctXstart() {
        return this.x == 50;
    }

    public boolean correctYstart() {
        return this.y == 50;
    }

}


public class VacumAgent implements Agent{
    private Map map = new Map();
    private String direction = "North";
    private int initalWalls = 0;
    private int leftTurns = 0;

    /*  Sequence tells whether robot is looking for top or whether in spiral cleaning mode or finding initial
     *       0 = robot is turned off and needs to turn on
     *       1 = finding top left corner of room
     *       2 = in spiral cleaning
     *       3 = finding inital spot and turning off
     */
    private int sequence = 0;

    String[] actions = { "TURN_ON", "TURN_OFF", "TURN_RIGHT", "TURN_LEFT", "GO", "SUCK" };
    private String action = "";

    public String nextAction(Collection<String> percepts) {
        System.out.print("perceiving:");
        for(String percept:percepts) {
            System.out.print("'" + percept + "', ");
        }

        switch (sequence) {
            case 0:
                action = actions[0];
                sequence = 1;
                break;
            case 1:
                if (percepts.contains("BUMP")) {
                    action = turnLeft();
                    initalWalls++;
                    if (initalWalls == 2) {
                        sequence = 2;
                    }
                    map.back(direction);
                }
                else {
                    action = actions[4];
                    map.forward(direction);
                }
                break;
            case 2:
                if (percepts.contains("DIRT")) {
                    action = actions[5];
                } else if (map.nextVisited(direction)){
                    action = turnLeft();
                    leftTurns++;
                    if (leftTurns > 2) {
                        sequence = 3;
                    }
                } else if (percepts.contains("BUMP")) {
                    action = turnLeft();
                    leftTurns++;
                    if (leftTurns > 1) {
                        sequence = 3;
                    }
                    map.back(direction);
                } else {
                    action = actions[4];
                    leftTurns = 0;
                    map.RecordPosition();
                    map.forward(direction);
                }
                break;
            case 3:
                if (map.isAtStart()) {
                    // Turn off
                    action = actions[1];
                } else if (!map.correctXstart()) {
                    action = regulateXstart();
                } else if (!map.correctYstart()) {
                    action = regulateYstart();
                }
        }

        System.out.println("");
        return action;
    }

    private String turnLeft() {
        switch (direction) {
            case "North":
                direction = "West";
                break;
            case "West":
                direction = "South";
                break;
            case "South":
                direction = "East";
                break;
            case "East":
                direction = "North";
                break;
        }
        return actions[3];
    }

    private String turnRight() {
        switch (direction) {
            case "North":
                direction = "East";
                break;
            case "West":
                direction = "North";
                break;
            case "South":
                direction = "West";
                break;
            case "East":
                direction = "South";
                break;
        }
        return actions[2];
    }

    private String regulateXstart() {
        switch (direction) {
            case "East":
                if (map.getX() > 50) return turnRight();
                else {
                    map.forward(direction);
                    return actions[4];
                }
            case "South":
                if (map.getX() > 50) return turnRight();
                else return turnLeft();
            case "North":
                if (map.getX() > 50) return turnLeft();
                else return turnRight();
            default:
                if (map.getX() > 50) {
                    map.forward(direction);
                    return actions[4];
                }
                else return turnLeft();
        }
    }

    private String regulateYstart() {
        switch (direction) {
            case "East":
                if (map.getY() > 50) return turnRight();
                else return turnLeft();
            case "South":
                if (map.getY() > 50) {
                    map.forward(direction);
                    return actions[4];
                }
                else return turnLeft();
            case "North":
                if (map.getY() > 50) return turnLeft();
                else {
                    map.forward(direction);
                    return actions[4];
                }
            default:
                if (map.getY() > 50) return turnLeft();
                else return turnRight();
        }
    }
}