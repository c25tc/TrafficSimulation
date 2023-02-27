import processing.core.PVector;

import static processing.core.PApplet.*;

public class Car {
    PVector pos;
    float rotation;
    float drivingSpeed;
    float speed;
    float viewDistance;
    float[] distances;
    Road closestRoad;
    PVector frontPoint;
    PVector rightFrontPoint;
    PVector leftFrontPoint;


    public Car(int x, int y) {
        this.pos = new PVector(x, y);
        this.rotation = 60; // arbitrary
        this.speed = 1; // arbitrary
        this.drivingSpeed = 1 + ((float)Math.random()/10 - 0.05f); // to simulate different driving preferences
        this.closestRoad = findClosestRoad();
        this.viewDistance = 80; // can change
        this.frontPoint = new PVector();
        this.rightFrontPoint = new PVector(); // DEBUGGING: so i can draw it
        this.leftFrontPoint = new PVector(); // DEBUGGING: so i can draw it
        // top, top right, right, bottom right, bottom, bottom left, left, top left
        this.distances = new float[8];
    }

    // github copilot
    public Road findClosestRoad() { // might want to change so it doesn't just look at the two ends but at the whole
        Road closestRoad = null;
        float closestDistance = 1000000;
        for (Road road: TrafficSimulation.roads) {
            PVector[] tempEdges = new PVector[2];
            tempEdges[0] = road.a;
            tempEdges[1] = road.b;
            for (int i = 0; i < 2; i++) {
                float distance = PVector.dist(this.pos, tempEdges[i]);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestRoad = road;
                }
            }
        }
        return closestRoad;
    }

    public void update() {
        this.closestRoad = findClosestRoad();
        this.speed = this.closestRoad.speedLimit * this.drivingSpeed;

        // for the front of the car
        // a point in front of the car the view distance away
        this.frontPoint = new PVector(cos(radians(this.rotation)) * viewDistance + this.pos.x, sin(radians(this.rotation)) * viewDistance + this.pos.y);

        // the intersection points of the front point and the road for both the right side and left side
        PVector rightIntersectionPoint = lineLineIntersection(this.pos, frontPoint, this.closestRoad.getOffsets(TrafficSimulation.roadWidth)[0], this.closestRoad.getOffsets(TrafficSimulation.roadWidth)[1]);
        PVector leftIntersectionPoint = lineLineIntersection(this.pos, frontPoint, this.closestRoad.getOffsets(-TrafficSimulation.roadWidth)[0], this.closestRoad.getOffsets(-TrafficSimulation.roadWidth)[1]);

        // if the car is angled towards the right wall, then set the front point to the right front point and vice versa
        if (PVector.angleBetween(this.pos, frontPoint)*10 - PVector.angleBetween(this.closestRoad.a, this.closestRoad.b) > 0) {
            this.distances[0] = min(PVector.dist(this.pos, rightIntersectionPoint), this.viewDistance);
        } else {
            this.distances[0] = min(PVector.dist(this.pos, leftIntersectionPoint), this.viewDistance);
        }

        this.distances[0] = min(PVector.dist(this.pos, rightIntersectionPoint), PVector.dist(this.pos, leftIntersectionPoint), this.viewDistance);

        this.leftFrontPoint = leftIntersectionPoint;
        this.rightFrontPoint = rightIntersectionPoint;

        // for the right side of the car
        PVector rightSidePoint = new PVector(cos(radians(this.rotation + 90)) * this.viewDistance + this.pos.x, sin(radians(this.rotation + 90)) * viewDistance + this.pos.y);
        PVector intersectionPoint = lineLineIntersection(this.pos, rightSidePoint, this.closestRoad.getOffsets(TrafficSimulation.roadWidth)[0], this.closestRoad.getOffsets(TrafficSimulation.roadWidth)[1]);
        this.distances[2] = min(PVector.dist(this.pos, intersectionPoint), this.viewDistance);

        // for the left side of the car
        PVector leftSidePoint = new PVector(cos(radians(this.rotation - 90)) * this.viewDistance + this.pos.x, sin(radians(this.rotation - 90)) * viewDistance + this.pos.y);
        intersectionPoint = lineLineIntersection(this.pos, leftSidePoint, this.closestRoad.getOffsets(-TrafficSimulation.roadWidth)[0], this.closestRoad.getOffsets(-TrafficSimulation.roadWidth)[1]);
        this.distances[6] = min(PVector.dist(this.pos, intersectionPoint), this.viewDistance);

        updateSpeed();

        updateRotation(2);

        updatePosition();
    }

    public void updateSpeed() {
        this.speed -= map(this.distances[0], 0, this.viewDistance, 1, 0);
        if (PVector.dist(this.pos, this.closestRoad.b) < 10) {
            this.speed = this.speed/2;
        }
    }

    public void updateRotation(int type) {
        if (type == 0) { // just the sensors
            if (angle(this.pos, this.frontPoint) != angle(this.closestRoad.a, this.closestRoad.b)) {
                this.rotation += map(this.distances[2], 0, this.viewDistance, 0, 7);
                this.rotation -= map(this.distances[6], 0, this.viewDistance, 0, 7);
            } else {
                this.rotation += map(this.distances[2], 0, this.viewDistance, 0, 1.5f);
                this.rotation -= map(this.distances[6], 0, this.viewDistance, 0, 1.5f);
            }
        } else if (type == 1) { // just based off angle
            this.rotation -= (angle(this.pos, this.frontPoint) - angle(this.closestRoad.a, this.closestRoad.b))/10;
        } else if (type == 2) { // mix
            this.rotation += map(this.distances[2], 0, this.viewDistance, 0, 7);
            this.rotation -= map(this.distances[6], 0, this.viewDistance, 0, 7);

            this.rotation -= (angle(this.pos, this.frontPoint) - angle(this.closestRoad.a, this.closestRoad.b))/10;
        }
    }



    public void updatePosition() {
        this.pos.x += Math.cos(radians(this.rotation)) * this.speed;
        this.pos.y += Math.sin(radians(this.rotation)) * this.speed;
    }

    // From GitHub Copilot
    public PVector lineLineIntersection(PVector a1, PVector a2, PVector b1, PVector b2) {
        float x1 = a1.x;
        float y1 = a1.y;
        float x2 = a2.x;
        float y2 = a2.y;
        float x3 = b1.x;
        float y3 = b1.y;
        float x4 = b2.x;
        float y4 = b2.y;
        float x = ((x1*y2 - y1*x2)*(x3 - x4) - (x1 - x2)*(x3*y4 - y3*x4)) / ((x1 - x2)*(y3 - y4) - (y1 - y2)*(x3 - x4));
        float y = ((x1*y2 - y1*x2)*(y3 - y4) - (y1 - y2)*(x3*y4 - y3*x4)) / ((x1 - x2)*(y3 - y4) - (y1 - y2)*(x3 - x4));
        return new PVector(x, y);
    }

    // a function that calculates the slope between two PVectors
    public float angle(PVector a, PVector b) {
        float slope = (b.y - a.y) / (b.x - a.x);
        return degrees(atan(slope));
    }

}