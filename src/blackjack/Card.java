package blackjack;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Card {

    static void setY(int y) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private final BufferedImage deck;
    private boolean isDealers = false;
    private int cardNum;
    private int suitNum;
    private int value;
    private boolean clicked = false;
    
    private int startx = 35;
    private int starty = 25;
    
    private int Xpos = 40;
    private static int Ypos = 450;
    private static final int d_Ypos = 75;
    
    private static final int WIDTH = 370;
    private static final int HEIGHT = 550;
    
    private int SPACING_X = 20;
    private int SPACING_Y = 20;
    
    public Card(int cardNum, String suit, boolean isDealers) throws IOException{
        
        switch(suit){
            case"spades":this.suitNum=1;break;
            case"hearts":this.suitNum=2;break;
            case"diamonds":this.suitNum=3;break;
            case"clubs":this.suitNum=4;break;
            case"":this.suitNum=5;break;
        }
        
        this.isDealers = isDealers;
        this.cardNum = cardNum;
        this.deck = ImageIO.read(new File("images/deck.png"));
        this.startx = this.startx+(Card.WIDTH+this.SPACING_X)*(this.cardNum-1);
        this.starty = this.starty+(Card.HEIGHT+this.SPACING_Y)*(this.suitNum-1);
        this.value = calculateValue();
    }
    
    public void paint(Graphics window, int index){
        
        Image card = deck.getSubimage(startx, starty, WIDTH, HEIGHT);
        card = card.getScaledInstance((WIDTH/4), (HEIGHT/4), Image.SCALE_SMOOTH);
        
        if(this.clicked && this.cardNum == 1){
            if(this.isDealers){window.fillRect(Xpos-5, d_Ypos-5, (WIDTH/4)+10, (HEIGHT/4)+10); this.value = 11;}
            else{window.fillRect(Xpos-5, Ypos-5, (WIDTH/4)+10, (HEIGHT/4)+10); this.value = 11;}
        }
        else if(!this.clicked && this.cardNum == 1){this.value = 1;}
        
        if(index != 0){Xpos = (120*index)+40;}
        if(isDealers){window.drawImage(card, Xpos, d_Ypos, null);}
        else{window.drawImage(card, Xpos, Ypos, null);}  
    }
    
    private int calculateValue() {
        if(this.cardNum < 11){return this.cardNum;}
        else{return 10;}  
    }
    
    public int getValue(){return this.value;}
    boolean getisDealers() {return this.isDealers;}
    
    boolean getClicked(){return this.clicked;}
    public void setClicked(boolean state){this.clicked = state;}
    
    int getCardValue(){return this.value;}
    public void setCardValue(int new_value){this.value = new_value;}
    
    int getYpos() {return Card.Ypos;}
    public void setYpos(int y){Card.Ypos = y;}
    
    int getXpos() {return this.Xpos;}
    public void setXpos(int x){this.Xpos = x;}

    
    
}
