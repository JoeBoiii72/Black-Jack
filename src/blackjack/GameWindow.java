
package blackjack;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JFrame;

public class GameWindow extends JFrame{
    
    private final Game gp;
    private final Set<Integer> keysPressed = new HashSet<>();

      
    public GameWindow(int width,int height) throws IOException{
        
        super("Black Jack");
      
        gp = new Game(width,height);
      //////////////// config/////////////
        super.getContentPane().add(gp, BorderLayout.CENTER);      
        super.pack();     
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setResizable(false);
        super.setVisible(true);
        super.setFocusable(true);
        ///////////////////////////////
    }

    public static void main(String[] args) throws IOException{
        GameWindow w = new GameWindow(600,600);
    }  
}
