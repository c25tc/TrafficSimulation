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
    PVector rightFrontPoint;
    PVector leftFrontPoint;

    public Car(int x, int y) {
        this.pos = new PVector(x, y);
        this.rotation = 80;
        this.speed = 1;
        this.drivingSpeed = 1 + ((float)Math.random()/10 - 0.05f);
        this.closestRoad = findClosestRoad();
        this.viewDistance = 80;
        this.rightFrontPoint = new PVector();
        this.leftFrontPoint = new PVector();
        // top, top right, right, bottom right, bottom, bottom left, left, top left
        this.distances = new float[8];
    }

    public Road findClosestRoad() {
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
        // todo: add more sensors
        // todo: make this a for loop that loops through all of the roads
        PVector frontPoint = new PVector(cos(radians(this.rotation)) * viewDistance + this.pos.x, sin(radians(this.rotation)) * viewDistance + this.pos.y);
        float shortestDistance = 1000000;
        for (Road road: TrafficSimulation.roads) {
            PVector rightIntersectionPoint = lineLineIntersection(this.pos, frontPoint, road.getOffsets(TrafficSimulation.roadWidth)[0], road.getOffsets(TrafficSimulation.roadWidth)[1]);
            PVector leftIntersectionPoint = lineLineIntersection(this.pos, frontPoint, road.getOffsets(-TrafficSimulation.roadWidth)[0], road.getOffsets(-TrafficSimulation.roadWidth)[1]);
            if (PVector.angleBetween(this.pos, frontPoint)*10 - PVector.angleBetween(road.a, road.b) > 0) {
                if (PVector.dist(this.pos, rightIntersectionPoint) < shortestDistance) {
                    shortestDistance = min(PVector.dist(this.pos, rightIntersectionPoint), this.viewDistance);
                    this.rightFrontPoint = rightIntersectionPoint;
                }
            } else {
                if (PVector.dist(this.pos, leftIntersectionPoint) < shortestDistance) {
                    shortestDistance = min(PVector.dist(this.pos, leftIntersectionPoint), this.viewDistance);
                    this.leftFrontPoint = leftIntersectionPoint;
                }
            }
        }
        this.distances[0] = shortestDistance;
//        PVector rightIntersectionPoint = lineLineIntersection(this.pos, frontPoint, this.closestRoad.getOffsets(TrafficSimulation.roadWidth)[0], this.closestRoad.getOffsets(TrafficSimulation.roadWidth)[1]);
//        PVector leftIntersectionPoint = lineLineIntersection(this.pos, frontPoint, this.closestRoad.getOffsets(-TrafficSimulation.roadWidth)[0], this.closestRoad.getOffsets(-TrafficSimulation.roadWidth)[1]);
//        if (PVector.angleBetween(this.pos, frontPoint)*10 - PVector.angleBetween(this.closestRoad.a, this.closestRoad.b) > 0) {
//            this.distances[0] = min(PVector.dist(this.pos, rightIntersectionPoint), this.viewDistance);
//        } else {
//            this.distances[0] = min(PVector.dist(this.pos, leftIntersectionPoint), this.viewDistance);
//        }

//        this.distances[0] = min(PVector.dist(this.pos, rightIntersectionPoint), PVector.dist(this.pos, leftIntersectionPoint), this.viewDistance);

//        this.leftFrontPoint = leftIntersectionPoint;
//        this.rightFrontPoint = rightIntersectionPoint;
        // for the right side of the car
        PVector rightSidePoint = new PVector(cos(radians(this.rotation + 90)) * viewDistance + this.pos.x, sin(radians(this.rotation + 90)) * viewDistance + this.pos.y);
        PVector intersectionPoint = lineLineIntersection(this.pos, rightSidePoint, this.closestRoad.getOffsets(TrafficSimulation.roadWidth)[0], this.closestRoad.getOffsets(TrafficSimulation.roadWidth)[1]);
        this.distances[2] = min(PVector.dist(this.pos, intersectionPoint), this.viewDistance);
        // for the left side of the car
        PVector leftSidePoint = new PVector(cos(radians(this.rotation - 90)) * viewDistance + this.pos.x, sin(radians(this.rotation - 90)) * viewDistance + this.pos.y);
        intersectionPoint = lineLineIntersection(this.pos, leftSidePoint, this.closestRoad.getOffsets(-TrafficSimulation.roadWidth)[0], this.closestRoad.getOffsets(-TrafficSimulation.roadWidth)[1]);
        this.distances[6] = min(PVector.dist(this.pos, intersectionPoint), this.viewDistance);

        this.speed -= map(this.distances[0], 0, this.viewDistance, (this.closestRoad.speedLimit*this.drivingSpeed-0.1f)/2, 0);

        if (round(PVector.angleBetween(this.pos, frontPoint)*100)/10f != round(PVector.angleBetween(this.closestRoad.a, this.closestRoad.b)*10)/10f) {
            this.rotation += map(this.distances[2], 0, this.viewDistance, 0, 7);
            this.rotation -= map(this.distances[6], 0, this.viewDistance, 0, 7);
        } else {
            this.rotation += map(this.distances[2], 0, this.viewDistance, 0, 1.5f);
            this.rotation -= map(this.distances[6], 0, this.viewDistance, 0, 1.5f);
        }

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

}