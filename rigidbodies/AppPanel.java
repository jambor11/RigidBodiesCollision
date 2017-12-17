import java.awt.*;
import java.util.Random;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.image.*;
import java.lang.Thread;

public class AppPanel extends JPanel 
{
    private BallControl mainPanel;
    private JButton resetButton;
    private JSlider gravitySlider;
    private JSlider corSlider;
	
    public AppPanel(BallControl mainPanel)
    {
        this.setPreferredSize(new Dimension(200, 60));
        this.setMaximumSize(new Dimension(5000, 100));
        this.mainPanel = mainPanel;
        
        resetButton = new JButton("Reset");

        gravitySlider = new JSlider(JSlider.HORIZONTAL, 0, 3000, 2000); // gravity, rework for values not px/s
        gravitySlider.setBorder(BorderFactory.createTitledBorder("Gravity - " + gravitySlider.getValue() + "px/s"));
        gravitySlider.setMajorTickSpacing(200);
        gravitySlider.setMinorTickSpacing(100);
        gravitySlider.setPaintTicks(true);

        corSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 85);
        corSlider.setBorder(BorderFactory.createTitledBorder("Restitution - " + corSlider.getValue() + "%"));
        corSlider.setMajorTickSpacing(10);
        corSlider.setMinorTickSpacing(5);
        corSlider.setPaintTicks(true);

        // controls
        this.add(gravitySlider);
        this.add(corSlider);
        this.add(resetButton);
                
        ButtonHandler buttonHandler = new ButtonHandler();
        resetButton.addActionListener(buttonHandler);

        SliderHandler sliderHandler = new SliderHandler();
        gravitySlider.addChangeListener(sliderHandler);
    }
	
    private class ButtonHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e) 
        {
            JButton source = (JButton)e.getSource();

            if (source == resetButton) { mainPanel.clearBalls(); }
        }
    }
	
    private class SliderHandler implements ChangeListener
    {
        public void stateChanged(ChangeEvent e) 
        {
            JSlider source = (JSlider)e.getSource();
            if (source == gravitySlider)
            {
                source.setBorder(BorderFactory.createTitledBorder("Gravity - " + source.getValue() + "px/s"));
                mainPanel.setGravity(source.getValue());
            }
        }
    }	
}
