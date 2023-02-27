import processing.core.PApplet;
import processing.core.PVector;
import processing.event.MouseEvent;

import java.util.ArrayList;


public class TrafficSimulation extends PApplet {
    public static void main(String[] args) {
        PApplet.main("TrafficSimulation", args);
    }

    int SCREEN_WIDTH = 1125, SCREEN_HEIGHT = 750;
    int SETTINGS_WIDTH = 250;
    float scale;
    PVector offset;
    boolean mouseDown;
    int[] mouseDownPos;
    static int roadWidth;

    static ArrayList<Road> roads = new ArrayList<>();
    static ArrayList<Car> cars = new ArrayList<>();

    public void settings() {
        size(SCREEN_WIDTH + SETTINGS_WIDTH, SCREEN_HEIGHT);
    }

    public void setup() {
        scale = 1f;
        offset = new PVector(0, 0);
        mouseDown = false;
        mouseDownPos = new int[4];

        roadWidth = 10;

        roads.add(new Road(100, 100, 300, 500, 1.0f));
        roads.add(new Road(301, 501, 1000, 800, 1.0f));
        cars.add(new Car( 99, 100));
    }

    public void draw() {
        background(0);
        pushMatrix();
        translate(offset.x, offset.y);
        scale(scale);
        drawRoads();
        drawCars();
        popMatrix();
        drawSettings();
    }

    void drawSettings () {
        fill(255);
        rect(SCREEN_WIDTH, 0, SETTINGS_WIDTH, SCREEN_HEIGHT);
    }

    void drawRoads () {
        for (Road road: roads) {
            if (cars.get(0).closestRoad == road) {
                stroke(255, 0, 0);
            } else {
                stroke(255);
            }
//            stroke(100);
            line(road.a.x, road.a.y, road.b.x, road.b.y);
            PVector[] edges = road.getOffsets(roadWidth);
//            stroke(255, 0, 0);
            line(edges[0].x, edges[0].y, edges[1].x, edges[1].y);
//            stroke(0, 255, 0);
            line(edges[2].x, edges[2].y, edges[3].x, edges[3].y);
        }
    }
    void drawCars () {
        fill(255);
        noStroke();
        for (Car car: cars) {
            pushMatrix();
            translate(car.pos.x, car.pos.y);
            rotate(radians(car.rotation));
            fill(0, 255, 0);
            rect(0, -5, 10, 10);
            fill(255);
            rect(-10, -5, 10, 10);
            popMatrix();
            car.update();
            stroke(255);
            line(car.pos.x, car.pos.y, cos(radians(car.rotation - 90)) * car.distances[6] + car.pos.x, sin(radians(car.rotation - 90)) * car.distances[6] + car.pos.y);
            line(car.pos.x, car.pos.y, cos(radians(car.rotation + 90)) * car.distances[2] + car.pos.x, sin(radians(car.rotation + 90)) * car.distances[2] + car.pos.y);
            line(car.pos.x, car.pos.y, cos(radians(car.rotation)) * car.distances[0] + car.pos.x, sin(radians(car.rotation)) * car.distances[0] + car.pos.y);
            fill(0, 255, 0);
            circle(car.rightFrontPoint.x, car.rightFrontPoint.y, 5);
            circle(car.leftFrontPoint.x, car.leftFrontPoint.y, 5);
        }
    }

    public void mouseWheel(MouseEvent event) {
        float e = event.getCount();
        if (mouseX < SCREEN_WIDTH) {
            scale += e * 0.01f;
            offset.x -= e * ((mouseX) / 100f);
            offset.y -= e * ((mouseY) / 100f);
        }
    }

    public void mousePressed() {
        if (mouseX < SCREEN_WIDTH) { // in the board area
            mouseDownPos[0] = mouseX;
            mouseDownPos[1] = mouseY;
            mouseDownPos[2] = (int) offset.x;
            mouseDownPos[3] = (int) offset.y;
            mouseDown = true;
//            for (Organism org :
//                    organismsList) {
//                if (Math.abs((mouseX - offset.x) / (boardWidth / (float) SCREEN_WIDTH) - (org.pos.x + (org.cells.length * cellSize) / 2f)) < 10 && Math.abs((mouseY - offset.y) / (boardHeight / (float) SCREEN_HEIGHT) - (org.pos.y + (org.cells[0].length * cellSize) / 2f)) < 10) {
////                    organismLookAtNum = org.key;
//                    lookAtOrg = org;
//                }
//            }

        }
    }
    public void mouseDragged() {
        if (mouseX < SCREEN_WIDTH) { // in the board area
            offset.x = mouseDownPos[2] - (mouseDownPos[0] - mouseX);
            offset.y = mouseDownPos[3] - (mouseDownPos[1] - mouseY);
        }

    }

    public void mouseReleased() {
        mouseDown = false;
    }

}