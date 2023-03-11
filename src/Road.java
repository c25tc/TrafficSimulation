import processing.core.PVector;

public class Road {
    PVector a;
    PVector b;
    PVector c;
    float speedLimit;

    public Road(int ax, int ay, int bx, int by, float speed) {
        this.a = new PVector(ax, ay);
        this.b = new PVector(bx, by);
        this.c = LengthenLine(a, b, -2);
        this.speedLimit = speed;
    }

    public PVector[] getOffsets(float offset) { // gets the offsets for the both of the edges and returns them
        PVector[] edges = new PVector[4];
        PVector[] tempEdges = offset(offset);
        edges[0] = tempEdges[0];
        edges[1] = tempEdges[1];
        tempEdges = offset(-offset);
        edges[2] = tempEdges[0];
        edges[3] = tempEdges[1];
        return edges;
    }

    public PVector[] offset(float offset) { // gets the offset in one direction
        PVector[] edges = new PVector[2];
        PVector direction = PVector.sub(b, a);
        direction.normalize();
        PVector normal = new PVector(-direction.y, direction.x);
        normal.mult(offset);
        edges[0] = PVector.add(a, normal);
        edges[1] = PVector.add(b, normal);
        return edges;
    }

    public PVector LengthenLine(PVector startPoint, PVector endPoint, float pixelCount) {
        if (startPoint.dist(endPoint) == 0)
            return endPoint; // not a line

        double dx = endPoint.x - startPoint.x;
        double dy = endPoint.y - startPoint.y;
        if (dx == 0)
        {
            // vertical line:
            if (endPoint.y < startPoint.y)
                return new PVector(endPoint.x, endPoint.y -= pixelCount);
            else
                return new PVector(endPoint.x, endPoint.y += pixelCount);
        }
        else if (dy == 0)
        {
            // horizontal line:
            if (endPoint.x < startPoint.x)
                return new PVector(endPoint.x -= pixelCount, endPoint.y);
            else
                return new PVector(endPoint.x += pixelCount, endPoint.y);
        }
        else
        {
            // non-horizontal, non-vertical line:
            double length =  Math.sqrt(dx * dx + dy * dy);
            double scale = (length + pixelCount) / length;
            dx *= scale;
            dy *= scale;
            return new PVector(startPoint.x + (float)dx, startPoint.y + (float)dy);
        }
    }
}