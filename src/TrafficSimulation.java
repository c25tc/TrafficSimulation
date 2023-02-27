import processing.core.PApplet;
import processing.core.PVector;
import processing.event.MouseEvent;

import java.util.ArrayList;

import static processing.core.PApplet.radians;


public class TrafficSimulation extends PApplet {
    public static void main(String[] args) {
        PApplet.main("TrafficSimulation", args);
    }

    // GLOBAL VARIABLES
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

        roadWidth = 15;

        roads.add(new Road(100, 100, 300, 500, 1.0f));
//        roads.add(new Road(1000, 800, 301, 501, 1.0f));
        roads.add(new Road(301, 501, 1000, 500, 1.0f));
        cars.add(new Car( 99, 100));
    }

    public void draw() {
        background(0);
        pushMatrix(); // this is a push matrix for pan and zoom
        translate(offset.x, offset.y); // for the pan
        scale(scale); // for the zoom
        drawRoads();
        drawCars();
        popMatrix();
        drawSettings();
    }

    void drawSettings () {
        fill(100);
        rect(SCREEN_WIDTH, 0, SETTINGS_WIDTH, SCREEN_HEIGHT);
    }

    void drawRoads () {
        for (Road road: roads) {
            if (cars.get(0).closestRoad == road) { // DEBUGGING: if it is the nearest road, make the road red
                stroke(255, 0, 0);
            } else {
                stroke(255);
            }
            line(road.a.x, road.a.y, road.b.x, road.b.y);
            PVector[] edges = road.getOffsets(roadWidth); // get the road offsets (a function inside the road class)
            line(edges[0].x, edges[0].y, edges[1].x, edges[1].y);
            line(edges[2].x, edges[2].y, edges[3].x, edges[3].y);

        }
    }
    void drawCars () {
        noStroke();
        for (Car car: cars) {
            pushMatrix(); // a push matrix for the car location
            translate(car.pos.x, car.pos.y);
            rotate(radians(car.rotation));
            fill(0, 255, 0);
            rect(0, -5, 10, 10);
            fill(255);
            rect(-10, -5, 10, 10);
            popMatrix();

            // update car
            car.update();

            // DEBUGGING: for the sensors
            stroke(255);
            line(car.pos.x, car.pos.y, cos(radians(car.rotation - 90)) * car.distances[6] + car.pos.x, sin(radians(car.rotation - 90)) * car.distances[6] + car.pos.y);
            line(car.pos.x, car.pos.y, cos(radians(car.rotation + 90)) * car.distances[2] + car.pos.x, sin(radians(car.rotation + 90)) * car.distances[2] + car.pos.y);
            line(car.pos.x, car.pos.y, cos(radians(car.rotation)) * car.distances[0] + car.pos.x, sin(radians(car.rotation)) * car.distances[0] + car.pos.y);
            fill(0, 255, 0);
            circle(car.rightFrontPoint.x, car.rightFrontPoint.y, 5);
            circle(car.leftFrontPoint.x, car.leftFrontPoint.y, 5);
        }
    }

    // all the stuff to make the board pan and zoom and stuff
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