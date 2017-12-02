/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment5;

//Imports are listed in full to show what's being used
//could just import javax.swing.* and java.awt.* etc..
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JFrame;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.border.EtchedBorder;

//This program simulates a fish tank

class GlobalVariables {
    public ArrayList<Fish> mFish;
    public Assignment5 mFishTank;

    private GlobalVariables() {
        mFish = new ArrayList<Fish>();
        mFishTank = new Assignment5();
    }

    private static GlobalVariables instance;

    public static GlobalVariables getInstance() {
        if (instance == null){
            instance = new GlobalVariables();
        }
        return instance;
    }
}

class Fish implements Comparable<Fish>{
    
    int mX;
    int mY;
    int mId;
    Color mColor;
    
    public Fish(int id, int x, int y, Color color){
        
        mId = id;
        mX = x;
        mY = y;
        mColor = color;
    }
    
    //paint() allows the user to create a fish everytime the mouse is clicked.
    //it uses a closed polygon which is defined with arrays and multiple coordinates
    public void paint(Graphics g){
        
        // Implement this function
        int x = this.mX;
        int y = this.mY;
        
        int[] mX = {x + 2, x + 7, x + 20, x + 16};
        int[] mxX = {x + 20, x + 25, x + 25, x + 23};
        
        int[] mY = {y + 7, y + 18, y + 17, y + 12};
        int[] myY = {y + 23, y + 12, y + 24, y + 23};
       
        
        g.fillPolygon(mxX, myY, 4);
        g.fillPolygon(mX, mY, 4);
        g.setColor(this.mColor);
    }
    
    //move() randomly computes the direction the fish will move when 
    //the user clicks the simulation tab
    public void move(){
         
        // Implement this function
        
        int x = this.mX + (int) (Math.floor(Math.random() * 6) - 3) * 30;
        int y = this.mY + (int) (Math.floor(Math.random() * 6) - 3) * 30;
        
        for (Fish fish : GlobalVariables.getInstance().mFish){
            if (fish.mX == x ){
                return;
            }
            if (fish.mY == y){
                return;
            }
        }
        
        if ( x > 400 || y > 400){
            return;
        }
        if ( x < 0 || y < 0){
            return;
        }
        
        this.mX = x;
        this.mY = y;
        
    }

    
    //compareTo() compares x & y values
    @Override
    public int compareTo(Fish o){
       
        // Implement this function
        if (o.mY == this.mY  && o.mX == this.mX && this.mId != o.mId ){
           return 1;
       }
        return 0;
     }
}

class FishTick extends TimerTask{

    @Override
    public void run() {
     
        if (Assignment5.mSimulateStatus){
            
            for (int x = 0;x<GlobalVariables.getInstance().mFish.size();x++){
                
                Fish f = GlobalVariables.getInstance().mFish.get(x);
                f.move();
                GlobalVariables.getInstance().mFish.set(x, f);
            }
              
            GlobalVariables.getInstance().mFishTank.mDrawPanel.paint();
        }
    }
}

public class Assignment5 extends javax.swing.JFrame implements java.awt.event.MouseListener, java.awt.event.MouseMotionListener{
    
    private final int mNumRows = 20;
    private final int mNumCols = 20;
    private final int mGridSz = 30;
    
    private int mSelectedFishIndex = -1;
    private boolean mDragged = false;
    
    private int mTopHeight;
           
    JToolBar mToolbar;
    JToggleButton mSimulationButton;
    DrawPanel mDrawPanel;
    
    private int mFishIndex = 0;
    
    static public boolean mSimulateStatus = false;
    
    public static void main(String[] args) {
 
        GlobalVariables global = GlobalVariables.getInstance();
        
        if (global == null){
            System.out.println("Cannot initialize, exiting ....");
            return;
        }
        
    }

    private JToggleButton addButton(String title){
        
        JToggleButton button = new JToggleButton(title);
        button.addItemListener(new ItemListener() {
           public void itemStateChanged(ItemEvent ev) {
               mSimulateStatus = !mSimulateStatus;
           }
        }); 
        
        this.mToolbar.add(button);
        
        return (button);
    }
    
    public Assignment5()
    {  
        JFrame guiFrame = new JFrame();
 
        guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        guiFrame.setTitle("MY FISH TANK");
        
        // Create a toolbar and give it an etched border.
        this.mToolbar = new JToolBar();
        this.mToolbar.setBorder(new EtchedBorder());
        
        mSimulationButton = addButton("Simulate");
        this.mToolbar.add(mSimulationButton);
 
        //This will center the JFrame in the middle of the screen
        guiFrame.setLocationRelativeTo(null);
    
        this.mDrawPanel = new DrawPanel(mNumRows, mNumCols, mGridSz);
        
        this.mDrawPanel.setBackground(Color.cyan); 
        this.mDrawPanel.paint();
        
        guiFrame.add(mDrawPanel);
        guiFrame.add(this.mToolbar, BorderLayout.NORTH);
        
        // Add the Exit Action
        JButton button = new JButton("Quit");
        button.setToolTipText("Quit the program");
        button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
        });
        mToolbar.add(button);
      
        guiFrame.addMouseListener(this);
        guiFrame.addMouseMotionListener(this);
        
        //make sure the JFrame is visible
        guiFrame.setVisible(true);
        
        mTopHeight = guiFrame.getInsets().top + mToolbar.getHeight();
        guiFrame.setSize(mNumRows * mGridSz, mNumCols * mGridSz + mTopHeight);
        
        Timer timer = new Timer("tick", true);
        timer.scheduleAtFixedRate(new FishTick(), Calendar.getInstance().get(Calendar.MILLISECOND), 500);
    }

    //mouseClicked() runs when the mouse has been clicked (pressed
    //and released, i.e simulation) on a component. It verifies if the application
    //is in manipulation mode
    @Override
    public void mouseClicked(MouseEvent e) {
        
        // Implement this function
        
        if ( !this.mSimulateStatus ){
            
            int red = (int) (0xff* Math.random());
            int green = (int) (0xff* Math.random());
            int blue = (int) (0xff* Math.random());
            int count = 0;
            int mX = e.getX() - 10;
            int mY = e.getY() - 8;
            int x = (mX - (mX%this.mGridSz));
            int y = (mY - (mY%this.mGridSz)) - 55;
            GlobalVariables.getInstance().mFishTank.mDrawPanel.paint();
            
            Fish f = new Fish(this.mFishIndex++, x, y, new Color(red,green, blue));
            if (count == 0 && x < 400 && y < 400){
               GlobalVariables.getInstance().mFish.add(f);
            }
            for (Fish o: GlobalVariables.getInstance().mFish) {
                 if (f.compareTo(o) == 1) {
                    count = 1;
                    break;
                }
            }
        }
    }

    
    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    //mouseReleased() runs when the mouse is released. It verifies the mouseDragged
    //is true & verifies empty slots
    @Override
    public void mouseReleased(MouseEvent e) {
        // Implement this function
        if (this.mDragged && this.mSelectedFishIndex != 0) {
            this.mDragged = true;
            Fish fish = GlobalVariables.getInstance().mFish.get(this.mSelectedFishIndex);
            GlobalVariables.getInstance().mFishTank.mDrawPanel.paint();
            
            fish.mX = e.getX() - (e.getX() % 30);
            fish.mY = e.getY() - (e.getY() % 30) - 80;
         }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    //mouseDragged() verifies that the application is in manipulation mode
    //& finds out which fish is selected from the tank
    @Override
    public void mouseDragged(MouseEvent e) {
       // Implement this function
        if (!this.mSimulateStatus) {
            int mX = (int)Math.floor((e.getX() - 10) / 30) * 30;
            int mY = (int)Math.floor((e.getY() - 10) / 30) * 30 - 80;

            for (Fish fish : GlobalVariables.getInstance().mFish){
                if (fish.mX == mX && fish.mY == mY){
                    Object o = null;
                    this.mSelectedFishIndex = GlobalVariables.getInstance().mFish.indexOf(o);
                    break;
                }
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}

class DrawPanel extends JPanel{

    int mRows;
    int mCols;
    int mGridSz;
    int maxGridSz;
    
    ArrayList<Fish> mFish;
    
    public DrawPanel(int numberOfRows, int numberOfCols, int gridSz){
        
        mGridSz = gridSz;
        mRows = numberOfRows;
        mCols = numberOfCols;
        maxGridSz = mGridSz * mRows;
    }
    
    private void paintBackground(Graphics g){
        
        for (int i = 1; i < mRows; i++) { 
            g.drawLine(i * mGridSz, 0, i * mGridSz, maxGridSz); 
        }
        
        for (int mAnimateStatus = 1; mAnimateStatus < mCols; mAnimateStatus++) { 
            g.drawLine(0, mAnimateStatus * mGridSz, maxGridSz, mAnimateStatus * mGridSz); 
        }
    }
    
    @Override
    public void paintComponent(Graphics g){
        
        super.paintComponent(g);
        
        paintBackground(g);
        
        for (Fish f:GlobalVariables.getInstance().mFish){  
            f.paint(g);
        }
        
    }

    public void paint(){ 
        repaint();
    }
}
