import processing.core.PApplet;
import processing.core.PVector;
import processing.event.MouseEvent;

import java.util.ArrayList;


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
    ArrayList<Button> buttons = new ArrayList<>();

    // COLORS
    int backgroundColor = color(225, 239, 246);
    int lightBlueColor = color(151, 210, 251);
    int pinkColor = color(236, 203, 217);
    int darkBlueColor = color(131, 188, 255);
    int greenColor = color(128, 255, 232);

    // SETTINGS VARIABLES
    int speed = 1;
    PVector newRoadStart = new PVector(0, 0);
    PVector newRoadEnd = new PVector(0 , 0);
    boolean isEditing = true;
    boolean isAdding = false;
    boolean isAddingCars = false;
    boolean isDeleting = true;
    float addCarRotation = 0;


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

        // create buttons
        buttons.add(new Button(SCREEN_WIDTH + 30, SCREEN_HEIGHT - 60, 90, 30, lightBlueColor, "Edit", false));
        buttons.add(new Button(SCREEN_WIDTH + 130, SCREEN_HEIGHT - 60, 90, 30, darkBlueColor, "Simulate", false));
        buttons.add(new Button(SCREEN_WIDTH + 30, 60, 90, 30, lightBlueColor, "Add Roads", true));
        buttons.add(new Button(SCREEN_WIDTH + 130, 60, 90, 30, darkBlueColor, "Add Cars", true));


        roads.add(new Road(100, 100, 300, 500, 1.0f));
        cars.add(new Car( 99, 100, 60));
        cars.add(new Car( 140, 178, 60));
    }

    public void draw() {
        background(backgroundColor);
        strokeWeight(1);
        stroke(255);
        pushMatrix(); // this is a push matrix for pan and zoom
        translate(offset.x, offset.y); // for the pan
        scale(scale); // for the zoom
        drawRoads();
        drawAddingPreview();
        drawCars();
        if (!isEditing) {
            simulate();
        }
        popMatrix();
        drawSettings();

    }

    void simulate() {
        for (int i = 0; i < speed; i++) {
            updateCars();
        }
    }

    void drawSettings () {
        fill(pinkColor);
        rect(SCREEN_WIDTH, 10, SETTINGS_WIDTH - 10, SCREEN_HEIGHT -20, 20);
        drawButtons();
        checkButtons();
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

    void updateCars () {
        for (Car car: cars) {
            car.update();
        }
    }

    void drawButtons () {
        for (Button button: buttons) {
            if ((isEditing && button.isEdit) || (!isEditing && !button.isEdit) || button.text.equals("Simulate") || button.text.equals("Edit")) {

                if (button.isActive) {
                    stroke(0);
                    strokeWeight(2);
                } else {
                    noStroke();
                }
                fill(button.color);
                rect(button.x, button.y, button.w, button.h, 10);
                fill(0);
                text(button.text, button.x + 10, button.y + button.h / 2f + 3);
            }

        }
    }

    void checkButtons () {
        if (mousePressed) {
            for (Button button: buttons) {
                if (button.isClicked(mouseX, mouseY)) {
                    System.out.println(button.text);
                    switch (button.text) {
                        case "Edit" -> {
                            button.isActive = true;
                            buttons.get(1).isActive = false;
                            isEditing = true;
                            isAdding = false;
                            isDeleting = false;
                        }
                        case "Simulate" -> {
                            button.isActive = true;
                            buttons.get(0).isActive = false;
                            isEditing = false;
                            isAdding = false;
                            isDeleting = false;
                        }
                        case "Add Roads" -> {
                            button.isActive = true;
                            buttons.get(3).isActive = false;
                            isAddingCars = false;
                            isAdding = false;
                            isDeleting = false;
                        }
                        case "Add Cars" -> {
                            button.isActive = true;
                            isAddingCars = true;
                            buttons.get(2).isActive = false;
                            isAdding = false;
                            isDeleting = false;
                        }
                        case "Delete" -> {
                            button.isActive = true;
                            buttons.get(4).isActive = false;
                            isAdding = false;
                            isDeleting = true;
                        }
                    }
                }
            }
        }

    }

    void drawAddingPreview() {
        if (mouseX > 0 && mouseX < SCREEN_WIDTH && mouseY > 0 && mouseY < SCREEN_HEIGHT && isEditing) {
            if (isAddingCars) {
                fill(255);
                noStroke();
                pushMatrix(); // a push matrix for the car location
                translate(newRoadStart.x, newRoadStart.y);
                rotate(radians(addCarRotation));
                fill(255, 100);
                rect(0, -5, 10, 10);
                fill(255);
                rect(-10, -5, 10, 10);
                popMatrix();
            } else {
                if (isAdding) {
                    stroke(255);
                    strokeWeight(3);
                    line(newRoadStart.x, newRoadStart.y, newRoadEnd.x, newRoadEnd.y);
                    strokeWeight(1);
                } else {
                    fill(255);
                    noStroke();
                    circle(newRoadStart.x, newRoadStart.y, 10);
                }
            }
        }

    }

    public PVector getClosestPointOnSegment(Road road, PVector pos) {
        double xDelta = road.b.x - road.a.x;
        double yDelta = road.b.y - road.a.y;

        double u = ((pos.x - road.a.x) * xDelta + (pos.y - road.a.y) * yDelta) / (xDelta * xDelta + yDelta * yDelta);

        final PVector closestPoint;
        if (u < 0) {
            closestPoint = road.a;
        }
        else if (u > 1) {
            closestPoint = road.b;
        }
        else {
            closestPoint = new PVector((int) Math.round(road.a.x + u * xDelta), (int) Math.round(road.a.y + u * yDelta));
        }

        return closestPoint;
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

    public void mouseClicked() {
        if (mouseX < SCREEN_WIDTH) {
            if (isEditing) {
                if (isDeleting) {
                    if (isAddingCars) {
                            for (Car car: cars) {
                                if (PVector.dist(car.pos, new PVector((mouseX - offset.x) / scale,(mouseY - offset.y) / scale) ) < 10 && cars.size() > 1) {
                                    cars.remove(car);
                                    break;
                                }
                            }
                    } else {
                        System.out.println("deleting");
                    }
                } else {
                    if (isAddingCars) {
                        cars.add(new Car((int)((mouseX - offset.x) / scale), (int)((mouseY - offset.y) / scale), addCarRotation));
                    } else {
                        if (!isAdding) {
                            newRoadEnd = new PVector(((mouseX - offset.x) / scale), ((mouseY - offset.y) / scale));
                            isAdding = true;
                        } else {
                            roads.add(new Road((int)newRoadStart.x, (int)newRoadStart.y, (int)newRoadEnd.x, (int)newRoadEnd.y, 1.0f));
                            isAdding = false;
                        }
                    }
                }


            }
        }

    }
    public void mouseMoved() {
        if (mouseX < SCREEN_WIDTH) {
            if (isAdding) {
                newRoadEnd = new PVector(((mouseX - offset.x) / scale), ((mouseY - offset.y) / scale));
                for (Road road: roads) {
                    if (PVector.dist(new PVector(((mouseX - offset.x) / scale), ((mouseY - offset.y) / scale)), road.a) < 20) {
                        newRoadEnd = road.a;
                    } else if (PVector.dist(new PVector(((mouseX - offset.x) / scale), ((mouseY - offset.y) / scale)), road.b) < 20) {
                        newRoadEnd = road.b;
                    }
                }
            } else {
                newRoadStart = new PVector(((mouseX - offset.x) / scale), ((mouseY - offset.y) / scale));
                for (Road road: roads) {
                    if (PVector.dist(new PVector(((mouseX - offset.x) / scale), ((mouseY - offset.y) / scale)), road.a) < 20) {
                        newRoadStart = road.a;
                    } else if (PVector.dist(new PVector(((mouseX - offset.x) / scale), ((mouseY - offset.y) / scale)), road.b) < 20) {
                        newRoadStart = road.b;
                    }
                }
            }
        }

    }
    public void mousePressed() {
        if (mouseX < SCREEN_WIDTH) { // in the board area
            mouseDownPos[0] = mouseX;
            mouseDownPos[1] = mouseY;
            mouseDownPos[2] = (int) offset.x;
            mouseDownPos[3] = (int) offset.y;
            mouseDown = true;

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

    public void keyPressed() {
        if (key == CODED) {
            if (keyCode == UP && speed < 20) speed++;
            if (keyCode == DOWN && speed > 0) speed--;
            if (keyCode == LEFT && addCarRotation > 0 && isEditing && isAddingCars) addCarRotation -= 3;
            if (keyCode == RIGHT && addCarRotation < 359 && isEditing && isAddingCars) addCarRotation += 3;
        }
    }
}