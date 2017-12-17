import java.awt.*;
import java.awt.geom.AffineTransform;

// Structure of a ball
// Contains: velocity, position, mass, radius, angularVelocity, orientation
public class Ball implements Comparable<Ball>
{
    public Vector3d velocity;
    public Vector3d position;
    private float mass;
    private float radius;
    private float angularVelocity;
    private float orientation = 45f;
    
    public Ball(float x, float y, float z, float radius)
    {
        this(x, y, z, radius, 1);
    }

    public Ball(float x, float y, float z, float radius, float mass)
    {
        this.velocity = new Vector3d(0, 0, 0);
        this.position = new Vector3d(x, y, z);
        this.setMass(mass);
        this.setRadius(radius);
    }

    public void draw(Graphics2D g2)
    {            
        g2.setColor(Color.getHSBColor(0.5f, 0.5f, 0.5f)); // color of ball
        g2.fillOval( (int) (position.getX() - getRadius()), (int) (position.getY() - getRadius()), (int) (2 * getRadius()) , (int) (2 * getRadius()) );
    }

    public void setRadius(float radius) { this.radius = radius; }
    public float getRadius() { return radius; }

    public void resolveCollision(Ball ball)
    {
        Vector3d delta = (position.subtract(ball.position));
        float r = getRadius() + ball.getRadius();
        float dist2 = delta.dot(delta);

        if (dist2 > r*r) return; // no collision

        float d = delta.getLength();

        Vector3d mtd;
        mtd = delta.multiply(((getRadius() + ball.getRadius())-d)/d);

        // resolve intersection
        float inverseMass1 = 1 / getMass(); // inverse mass quantities
        float inverseMass2 = 1 / ball.getMass();

        // push-pull them apart
        position = position.add(mtd.multiply(inverseMass1 / (inverseMass1 + inverseMass2)));
        ball.position = ball.position.subtract(mtd.multiply(inverseMass2 / (inverseMass1 + inverseMass2)));

        // impact speed
        Vector3d v = (this.velocity.subtract(ball.velocity));
        float vn = v.dot(mtd.normalize());

        // sphere intersecting but moving away from each other already
        if (vn > 0.0f) return;

        // collision impulse
        float i = (-(1.0f + Constants.restitution) * vn) / (inverseMass1 + inverseMass2);
        Vector3d impulse = mtd.multiply(i);

        // change in momentum
        this.velocity = this.velocity.add(impulse.multiply(inverseMass1));
        ball.velocity = ball.velocity.subtract(impulse.multiply(inverseMass2));
    }

    private void setMass(float mass) { this.mass = mass; }
    private float getMass() { return mass; }

    public int compareTo(Ball ball)
    {
        if (this.position.getX() - this.getRadius() > ball.position.getX() - ball.getRadius())
        {
           return 1;
        }
        else if (this.position.getX() - this.getRadius() < ball.position.getX() - ball.getRadius())
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }

}




