import java.awt.*;
import java.util.Random;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.FontMetrics;
import java.lang.Thread;

// Main logic, of collision and ball handling
public class BallControl extends Canvas 
{
    private BufferStrategy strategy;
    private Graphics2D g2;

    private Ball[] balls = new Ball[5000];
    private Ball currentBall;
    private int ballCount;	

    private int maxFrameRate = 66;
    private int currentFrameRate;


    public BallControl()
    {
        setPreferredSize(new Dimension(800, 600));
        setIgnoreRepaint(true);

        MouseHandler mouseHandler = new MouseHandler();
        addMouseMotionListener(mouseHandler);
        addMouseListener(mouseHandler);
    }

    public void start() { mainLoop(); }

    public void mainLoop()
    {
        long previousTime = System.currentTimeMillis();
        long currentTime = previousTime;
        long elapsedTime;
        long totalElapsedTime = 0;
        int frameCount = 0;

        while(true)
        {
            currentTime = System.currentTimeMillis();
            elapsedTime = (currentTime - previousTime); // elapsed time in seconds
            totalElapsedTime += elapsedTime;

            if (totalElapsedTime > 1000)
            {
                currentFrameRate = frameCount;
                frameCount = 0;
                totalElapsedTime = 0;
            }

            updateGame(elapsedTime / 1000f);
            render();

            try { Thread.sleep(5); } 
            catch (Exception e) { e.printStackTrace(); }

            previousTime = currentTime;
            frameCount++;
        }
    }

    private int getFpsDelay(int desiredFps)
    {
        return 1000 / desiredFps;
    }


    public void clearBalls()
    {
        ballCount = 0;
    }

    public void setGravity(float pixelsPerSecond)
    {
        Constants.gravity = pixelsPerSecond;
    }    

    public void render()
    {
        if (strategy == null || strategy.contentsLost())
        {
            createBufferStrategy(2);
            strategy = getBufferStrategy();
            Graphics g = strategy.getDrawGraphics();
            this.g2 = (Graphics2D) g;
        }         

        this.g2.setColor(Color.WHITE);
        this.g2.fillRect(0, 0, getWidth(), getHeight());

        for(int i = 0; i < ballCount; i++)
        {
            balls[i].draw(this.g2);
        }

        Ball tempBall = currentBall;
        if (tempBall != null) tempBall.draw(this.g2);

        if (!strategy.contentsLost()) strategy.show();
    }

    public void updateGame(float elapsedSeconds)
    {
        // step the position of movable objects based off their velocity/gravity and elapsedTime
        for (int i = 0; i < ballCount; i++)
        {   
            //apply gravity
            balls[i].velocity.setY(balls[i].velocity.getY() + (Constants.gravity * (elapsedSeconds))); 

            // move the ball in a direction
            balls[i].position.setX(balls[i].position.getX() + (balls[i].velocity.getX() * (elapsedSeconds)));
            balls[i].position.setY(balls[i].position.getY() + (balls[i].velocity.getY() * (elapsedSeconds)));
            
            // stop the ball
            if (Math.abs(balls[i].velocity.getX()) < Constants.epsilon) balls[i].velocity.setX(0);
            if (Math.abs(balls[i].velocity.getY()) < Constants.epsilon) balls[i].velocity.setY(0);
        }
        checkCollisions();
    }

    // Insertion sort for Sweep and Prune
    public void insertionSort(Comparable[] a)
    {
        for( int p = 1; p < ballCount; p++ )
        {
            Comparable tmp = a[ p ];
            int j = p;
            for( ; j > 0 && tmp.compareTo( a[ j - 1 ] ) < 0; j-- )
                a[ j ] = a[ j - 1 ];
            a[ j ] = tmp;
        }
    }


    public void checkCollisions()
    {
        insertionSort(balls);

        // Check for collision with walls
        for (int i = 0; i < ballCount; i++)
        {      
            if (balls[i].position.getX() - balls[i].getRadius() < 0) // Left wall
            {
                balls[i].position.setX(balls[i].getRadius()); // Place ball against edge
                balls[i].velocity.setX(-(balls[i].velocity.getX() * Constants.restitution)); // Reverse direction and account for friction
                balls[i].velocity.setY(balls[i].velocity.getY() * Constants.restitution);
            }
            else if (balls[i].position.getX() + balls[i].getRadius() > getWidth()) // Right Wall
            {
                balls[i].position.setX(getWidth() - balls[i].getRadius());
                balls[i].velocity.setX(-(balls[i].velocity.getX() * Constants.restitution)); 
                balls[i].velocity.setY((balls[i].velocity.getY() * Constants.restitution));
            }

            if (balls[i].position.getY() - balls[i].getRadius() < 0) // Top Wall
            {
                balls[i].position.setY(balls[i].getRadius());
                balls[i].velocity.setY(-(balls[i].velocity.getY() * Constants.restitution)); 
                balls[i].velocity.setX((balls[i].velocity.getX() * Constants.restitution));
            }
            else if (balls[i].position.getY() + balls[i].getRadius() > getHeight()) // Bottom Wall
            {
                balls[i].position.setY(getHeight() - balls[i].getRadius());
                balls[i].velocity.setY(-(balls[i].velocity.getY() * Constants.restitution));
                balls[i].velocity.setX((balls[i].velocity.getX() * Constants.restitution));
            }

            // Ball to Ball collision
            for(int j = i + 1; j < ballCount; j++)
            {
                if ((balls[i].position.getX() + balls[i].getRadius()) < (balls[j].position.getX() - balls[j].getRadius()))
                    break;

                if((balls[i].position.getY() + balls[i].getRadius()) < (balls[j].position.getY() - balls[j].getRadius()) ||
                   (balls[j].position.getY() + balls[j].getRadius()) < (balls[i].position.getY() - balls[i].getRadius()))
                    continue;

                balls[i].resolveCollision(balls[j]);

            }
        }
    }

    private class MouseHandler extends MouseAdapter implements MouseMotionListener
    {
        public void mousePressed(MouseEvent e)
        {
            currentBall = new Ball(e.getX(), e.getY(), 15, 15);
        }

       public void mouseReleased(MouseEvent e)
       {
               balls[ballCount] = currentBall;
               ballCount++;
               currentBall = null;
       }
    }
}
