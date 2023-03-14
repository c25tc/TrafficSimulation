import processing.core.PVector;

import static processing.core.PApplet.*;

public class Car {
    PVector pos;
    float rotation;
    float preferredSpeedMultiplier;
    float currentSpeed;
    float viewDistance;
    float[] distances;
    Road closestRoad;
    PVector frontPoint;
    PVector horizontalPoint;
    PVector rightFrontPoint;
    PVector leftFrontPoint;


    public Car(int x, int y, float rotation) {
        this.pos = new PVector(x, y);
        this.rotation = rotation;
        this.currentSpeed = 1; // arbitrary
        this.preferredSpeedMultiplier = 1 + ((float)Math.random()/5 - 0.1f); // to simulate different driving preferences
        this.closestRoad = findClosestRoad();
        this.viewDistance = 80; // can change
        this.frontPoint = new PVector();
        this.horizontalPoint = new PVector();
        this.rightFrontPoint = new PVector(); // DEBUGGING: so i can draw it
        this.leftFrontPoint = new PVector(); // DEBUGGING: so i can draw it
        // top, top right, right, bottom right, bottom, bottom left, left, top left
        this.distances = new float[8];
    }


    public void update() {
        this.closestRoad = findClosestRoad();
        this.currentSpeed = this.closestRoad.speedLimit * this.preferredSpeedMultiplier;

        // for the front of the car
        this.frontPoint = new PVector(cos(radians(this.rotation)) * 3 + this.pos.x, sin(radians(this.rotation)) * 3 + this.pos.y);
        this.horizontalPoint = new PVector(cos(radians(this.rotation + 90)) * viewDistance + this.pos.x, sin(radians(this.rotation + 90)) * viewDistance + this.pos.y);
        // a point in front of the car the view distance away
        getFrontPoint();

        // for the right side of the car
        getRightPont();

        // for the left side of the car
        getLeftPoint();

        updateSpeed();

        updateRotation(2);

        updatePosition();
    }
    public void getFrontPoint() {
        PVector rightIntersectionPoint = lineLineIntersection(this.pos, frontPoint, this.closestRoad.getOffsets(TrafficSimulation.roadWidth)[0], this.closestRoad.getOffsets(TrafficSimulation.roadWidth)[1]);
        PVector leftIntersectionPoint = lineLineIntersection(this.pos, frontPoint, this.closestRoad.getOffsets(-TrafficSimulation.roadWidth)[0], this.closestRoad.getOffsets(-TrafficSimulation.roadWidth)[1]);

        // if the car is angled towards the right wall, then set the front point to the right front point and vice versa
        if (angle(this.pos, frontPoint) - angle(this.closestRoad.a, this.closestRoad.b) > 0) {
            this.distances[0] = min(PVector.dist(this.pos, rightIntersectionPoint), this.viewDistance);
        } else {
            this.distances[0] = min(PVector.dist(this.pos, leftIntersectionPoint), this.viewDistance);
        }

        float closestCarDistance = 999999f;
        for (Car car : TrafficSimulation.cars) {
            if (car != this) {
                // get the distance between the car and this car
                PVector carIntersectionPoint = lineLineIntersection(this.pos, frontPoint, car.pos, car.horizontalPoint);
                if (PVector.dist(carIntersectionPoint, car.pos) < 20) {
                    if (PVector.dist(carIntersectionPoint, this.frontPoint) < PVector.dist(carIntersectionPoint, this.pos)) {
                        float distance = PVector.dist(this.pos, carIntersectionPoint);
                        if (distance < closestCarDistance) {
                            closestCarDistance = distance - 20; // so the car doesn't crash into the other car
                        }
                    }
                }


            }
        }

        float closestStopLightDistance = 999999f;
        for (StopLight stopLight : TrafficSimulation.stopLights) {
            if (stopLight.isOn) {
                if (PVector.dist(this.pos, stopLight.pos) < this.viewDistance) {
                    float distance = PVector.dist(this.pos, stopLight.pos);
                    if (distance < closestStopLightDistance) {
                        closestStopLightDistance = distance - 30; // so the car doesn't crash into the other car
                    }
                }
            }
        }


        this.distances[0] = min(closestCarDistance, closestStopLightDistance, this.distances[0]);


        // DEBUGGING: the intersection points of the front point and the road for both the right side and left side
        this.leftFrontPoint = leftIntersectionPoint;
        this.rightFrontPoint = rightIntersectionPoint;
    }

    public void getRightPont() {
        PVector rightSidePoint = new PVector(cos(radians(this.rotation + 90)) * this.viewDistance + this.pos.x, sin(radians(this.rotation + 90)) * viewDistance + this.pos.y);
        PVector intersectionPoint = lineLineIntersection(this.pos, rightSidePoint, this.closestRoad.getOffsets(TrafficSimulation.roadWidth)[0], this.closestRoad.getOffsets(TrafficSimulation.roadWidth)[1]);
        this.distances[2] = min(PVector.dist(this.pos, intersectionPoint), this.viewDistance);
    }

    public void getLeftPoint() {
        PVector leftSidePoint = new PVector(cos(radians(this.rotation - 90)) * this.viewDistance + this.pos.x, sin(radians(this.rotation - 90)) * viewDistance + this.pos.y);
        PVector intersectionPoint = lineLineIntersection(this.pos, leftSidePoint, this.closestRoad.getOffsets(-TrafficSimulation.roadWidth)[0], this.closestRoad.getOffsets(-TrafficSimulation.roadWidth)[1]);
        this.distances[6] = min(PVector.dist(this.pos, intersectionPoint), this.viewDistance);
    }

    public void updateSpeed() {
        this.currentSpeed -= map(this.distances[0], 0, this.viewDistance, 1, 0);
        if (PVector.dist(this.pos, this.closestRoad.b) < 10) {
            this.currentSpeed = this.currentSpeed /2;
        }
        this.currentSpeed = max(this.currentSpeed, 0);
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
        this.pos.x += Math.cos(radians(this.rotation)) * this.currentSpeed;
        this.pos.y += Math.sin(radians(this.rotation)) * this.currentSpeed;
    }

    // -------- Helper Functions --------
    public Road findClosestRoad() {
        Road closestRoad = null;
        float closestDistance = 1000000;
        for (Road road: TrafficSimulation.roads) {
            PVector closestPoint = getClosestPointOnSegment(road);
            float distance = PVector.dist(closestPoint, this.pos);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestRoad = road;
            }

        }
        return closestRoad;
    }

    // From online
    public PVector getClosestPointOnSegment(Road road) {
        double xDelta = road.c.x - road.a.x;
        double yDelta = road.c.y - road.a.y;

        double u = ((this.pos.x - road.a.x) * xDelta + (this.pos.y - road.a.y) * yDelta) / (xDelta * xDelta + yDelta * yDelta);

        final PVector closestPoint;
        if (u < 0) {
            closestPoint = road.a;
        }
        else if (u > 1) {
            closestPoint = road.c;
        }
        else {
            closestPoint = new PVector((int) Math.round(road.a.x + u * xDelta), (int) Math.round(road.a.y + u * yDelta));
        }

        return closestPoint;
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