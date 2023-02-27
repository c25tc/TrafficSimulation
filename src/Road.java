import processing.core.PVector;

public class Road {
    PVector a;
    PVector b;
    float speedLimit;

    public Road(int ax, int ay, int bx, int by, float speed) {
        this.a = new PVector(ax, ay);
        this.b = new PVector(bx, by);
        this.speedLimit = speed;
    }

    // a function that offsets the road by a certain amount and returns the two edges
    public PVector[] getOffsets(float offset) {
        PVector[] edges = new PVector[4];
        PVector[] tempEdges = offset(offset);
        edges[0] = tempEdges[0];
        edges[1] = tempEdges[1];
        tempEdges = offset(-offset);
        edges[2] = tempEdges[0];
        edges[3] = tempEdges[1];
        return edges;
    }

    public PVector[] offset(float offset) {
        PVector[] edges = new PVector[2];
        PVector direction = PVector.sub(b, a);
        direction.normalize();
        PVector normal = new PVector(-direction.y, direction.x);
        normal.mult(offset);
        edges[0] = PVector.add(a, normal);
        edges[1] = PVector.add(b, normal);
        return edges;
    }
}