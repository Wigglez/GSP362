package mygame;

import com.jme3.app.SimpleApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import com.jme3.font.BitmapText;
import javax.swing.border.BevelBorder;


public class Menu extends JFrame implements ActionListener
{
    private JMenuItem Start= new JMenuItem("Start");
    private JMenuItem HighScore= new JMenuItem("High Scores");
    private JMenuItem Close= new JMenuItem("Close");
    private JMenuItem Graphics = new JMenuItem("Graphics");
    private JMenuItem Controls = new JMenuItem("Controls");
    
    
    JTextArea textArea = new JTextArea();
    Text text = new Text();
    Table table = new Table();
   
    public static void main(String agrs[])
    {
     
        new Menu();
        
        
        
    }
  
    public Menu()
    {
        
        super("Project");
        JMenuBar menubar = new JMenuBar();
        JMenu menu1 = new JMenu("Game");
        JMenu menu2 = new JMenu("Settings");
        JMenu menu3 = new JMenu("Info");
        
        Start.addActionListener(this);
        HighScore.addActionListener(this);
        Close.addActionListener(this);
        Graphics.addActionListener(this);
        Controls.addActionListener(this);
        
        menubar.setBorder(new BevelBorder(BevelBorder.RAISED));
        
        menu1.add(Start);
        menu1.addSeparator();
        
        menu1.add(HighScore);
        menu1.addSeparator();
        
        menu1.add(Close);
        
        menu2.add(Graphics);
        
        menu3.add(Controls);
        
        menubar.add(menu1);
        menubar.add(menu2);
        menubar.add(menu3);
        
       
        
      
        
        setJMenuBar(menubar);
        setSize(800,600);
        getFont();
        setVisible(true);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }
    public void actionPerformed(ActionEvent event)
    {
        if(event.getSource() == Start)
        {
            Thread thread = new Thread(new Runnable() {
             
            @Override
            public void run() {
                Text text = new Text();
                text.start();
            }
        });
             
              thread.start();
        }
        if(event.getSource() == HighScore)
        {
//           JFrame jf = new JFrame();
//           
//       
//           jf.setTitle("Title");
//           jf.setSize(500,500);
           //jf.setVisible(true);
          // jf.add(table);
           getContentPane().add(table);
           setVisible(true);
           
        }
        else{
            getContentPane().remove(table);
            setVisible(true);
            
        }
        if(event.getSource() == Graphics)
        {
            
        }
        if(event.getSource() == Controls)
        {        
            
            
                textArea.setText("");
                getContentPane().setLayout(new FlowLayout());
                textArea.setText("Controls"
                                         + "\n\nPress W To Jump"
                                         + "\nPress A To Move Left"
                                         + "\nPress S To Crouch"
                                         + "\nPress D To Move Right"
                                         + "\nPress SPACE to shoot");
                textArea.setEditable(false);
                textArea.setLineWrap(false);
                textArea.setOpaque(false);
                textArea.setFont(new Font("Serif", Font.PLAIN, 36));

                textArea.setBorder(BorderFactory.createEmptyBorder());
                getContentPane().add(textArea, BorderLayout.CENTER);
                


                setVisible(true);
            
            
        }
        else{
            textArea.setText("");
            getContentPane().add(textArea, BorderLayout.CENTER);
        }
        if(event.getSource() == Close)
        {
            System.exit(0);
        }
    }
 
}

