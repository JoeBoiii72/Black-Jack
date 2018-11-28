package blackjack;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JPanel;

////////////////////// L O G I C ////////////////////////
//1- player gets two cards DONE
//2- dealer gets one card and one turned card DONE
//3- wait for user to choose stand or hit DONE
    //3.1- if hit a new card is added to player cards if the value is now higher than 21 bust = true DONE
    //3.2- if stand add no cards and set the current score to the plpayer final score. DONE
//4- after this, turn over the dealers card by replacing the first card with a new random card DONE
//5- if not standing wait for players input again goto 3 DONE
//6- after this dealer must check if his total is under 16 (with aces included but only if that brings it below 21 and over 16)DONE
    //6.1- if it is below 16 the dealer will hit DONE
    //6.2- else the dealer will stand DONE
//

class Game extends JPanel implements MouseListener, ActionListener{

    
    private final int PANEL_WIDTH;
    private final int PANEL_HEIGHT;
    private int totalcash = 1000;
    private int betamount = 500;
    private ArrayList<Card> player_cards = new ArrayList<>();
    private ArrayList<Card> dealer_cards = new ArrayList<>();
    private boolean mouse_down = false;
    private int player_total,player_final;
    private int dealer_total,dealer_final;
    private boolean bust = false;
    private boolean turned = false;
    private boolean standing = false;
    private boolean end;
    private boolean dealersGo = false;
    private boolean dealer_bust;
    private boolean moneyadded;
    private boolean gameover = false;
    
    public Game(int width, int height) throws IOException{
        
        Music music = new Music("sounds/out_of_touch.mid");
        //Music music = new Music("sounds/carelesswhisper.mid");
        //Music music = new Music("sounds/bach_jesu_joylonger_pno.mid");
        music.start();
         
        this.PANEL_WIDTH = width;
        this.PANEL_HEIGHT = height;
        
        
        gameSetup();
        
        super.setBackground(new Color(85, 170, 85));
        super.setOpaque(true);
        super.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        super.addMouseListener(this);
           
        
        JButton standBtn = new JButton ("STAND");
        JButton hitBtn = new JButton ("HIT");
        JButton splitBtn = new JButton ("SPLIT");
        JButton doubleBtn = new JButton ("DOUBLE");
        JButton restartBtn = new JButton ("NEW HAND");
        
        standBtn.addActionListener(this);
        hitBtn.addActionListener(this);
        splitBtn.addActionListener(this);
        doubleBtn.addActionListener(this);
        restartBtn.addActionListener(this);
        
        super.add(hitBtn);
        super.add(standBtn);
        //super.add(splitBtn);
        super.add(doubleBtn);
        super.add(restartBtn);
        
    }
    
    
    /////////////////////are main game loop/////////////////////////////
    
    
    
    @Override
    public void paintComponent(Graphics window){
        
        super.paintComponent(window);//paint on the jframe created in gamewindow.java
        
        if(!gameover){ //if we havent lost all our cash
            

            //display all dealer and player cards
            for(int i = 0; i < player_cards.size(); i++){player_cards.get(i).paint(window, i);}
            for(int i = 0; i < dealer_cards.size(); i++){dealer_cards.get(i).paint(window, i);}

            //add up all cards value
            player_total = addUpCards(player_cards);
            dealer_total = addUpCards(dealer_cards);
            
            
            //if the game hasnt ended yet and its the dealers go, let the dealer go.
            if(standing || bust){ dealersGo = true;}//if we are standing or bust its the dealers go
            if(!end){
                if(dealersGo){
                   try {dealergo();} 
                   catch (IOException ex) {System.out.println(ex);}
                }
            }
            
            //display total cash
            window.drawString("Total cash: £"+totalcash, 10, 30);
            
            //if dealer or player bust tell user.
            if(dealer_bust){window.drawString("DEALER HAS BUST.", 100, 260);SFX.busted.play();}
            if(bust){window.drawString("YOU HAVE BUST.", 100, 250);if(!standing){SFX.busted.play();}}
            
            
            //display score or dealer and player
            window.drawString("YOU: "+player_total+" || DEALER: "+dealer_total, 100, 300);


            //check who wins
            if((player_final > 0 && dealer_final > 0) || end){
                if(bust && dealer_bust){//no one wins
                    window.drawString("all bust no wins", 100, 330);
                }
                else if(!dealer_bust && (bust || dealer_final > player_final)){//dealer wins
                    window.drawString("DEALER WON!", 100, 330);
                    if(!moneyadded){totalcash -= betamount;moneyadded = true;}
                    window.drawString("You just lost £"+betamount, 200, 360);
                }
                else if(!bust){//player wins
                    window.drawString("YOU WON!", 100, 330);
                    if(!moneyadded){totalcash += betamount;moneyadded = true;}
                    window.drawString("You just won £"+betamount, 200, 360);
                }
            }
        }//display gameover screen if no money left.
        else{window.drawString("GAMEOVER", 100, 330);}
        if(totalcash <= 0 && !gameover){ gameover=true;repaint();}
        
        
    }
    
   
    ///////////////////////////////////////////////////////
    
    @Override
    public void mousePressed(MouseEvent e){
        int x = e.getX();
        int y = e.getY();
        AcePressed(x,y); //check if aces are pressed.
    }
    public void mouseReleased(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseClicked(MouseEvent e){}
    
    

    private void AcePressed(int x, int y){
        
        //if current game hasn't ended
        //loop through everycard and check if it was the one that was clicked.
        //if it was and it was an ace, change its value to 11 or back to 1 accordingly 
        // then add up the new total.
        if(!end){
            for(int i = 0; i < player_cards.size(); i++){
                //if it was inside the cards boundaries.
                if((x >= player_cards.get(i).getXpos() && x <= player_cards.get(i).getXpos()+100) && (y >= player_cards.get(i).getYpos() && y <= player_cards.get(i).getYpos() + 200)){
                    if(!standing && !end){ //make sure user can't change value if we are already standing or bust.
                        player_cards.get(i).setClicked(!player_cards.get(i).getClicked()); //set clicked to true
                        player_total = addUpCards(player_cards);
                    }
                    repaint();//repaint and display to rectangles over clicked cards
                }
            }
        } 
    }

    //this is our button handler
    @Override
    public void actionPerformed(ActionEvent e){
        //call different methods based on what button was clicked.
        switch(e.getActionCommand()){
            case "HIT":g_hit();break;
            case "STAND":g_stand();break;
            case "SPLIT":g_split();break;
            case "DOUBLE":g_double();break;
            case "NEW HAND":try{g_restart();}catch(IOException err){System.out.println(err);};
        }      
    }
    
    private void g_stand(){
        if(!standing && !bust){
            standing = true;
            player_final = addUpCards(player_cards);
            dealersGo = true;
            repaint();
        }
    }
    
    private void g_split() {}
    
    
    //double bet amount and just hit then stand, thats essentially what doubling is.
    //do this only if not already standing.
    private void g_double() {
        if(!standing && !bust){
            betamount *= 2;
            g_hit();
            g_stand();
        }
    }
    
    private void g_hit(){
        try{
            //if not already bust or standing
            if(!standing && !bust){
                //add a random card
                Card cardToAdd = randomCard(false);
                player_cards.add(cardToAdd);
                //check if it makes player bust if it does set bust to true
                if(player_total + cardToAdd.getValue() > 21){
                    player_final = addUpCards(player_cards);
                    bust=true;
                    end=true;
                }
                repaint();
            }
        }catch(IOException ex){}
        
    }
    
    //return new random card
    //set dealer to true if you want it to be displayed with the dealer cards
    //aka have the Ypos of the card = 75 instead of 450
    private Card randomCard(boolean isdealers) throws IOException {
        
        Random rand = new Random();
        
        String[] suits = {"spades","hearts","clubs","diamonds"};
        int n1 = rand.nextInt(13) + 1;
        int n2 = rand.nextInt(suits.length) + 0;
        
        return new Card(n1,suits[n2],isdealers);
    }
    
    
    //this just adds up the card values in a given card array.
    private int addUpCards(ArrayList<Card> cards) {
        
        //if we are adding dealer
        //make sure we dont add up a blank card
        int score = 0;
        
        for(int i = 0; i < cards.size(); i++){
            if(turned || !(i == 0 && cards.get(i).getisDealers())){
                int value = cards.get(i).getValue();
                score += value;
            }
            
        }
        return score; 
    }
    
    //sets up game i.e. gives the dealer and player two cards like IRL
    private void gameSetup() throws IOException {
        
        try {
            dealer_cards.add(new Card(5,"",true));
            dealer_cards.add(randomCard(true));
            
            player_cards.add(randomCard(false));
            player_cards.add(randomCard(false));
            repaint();
        }
        catch (IOException a){}
    }
    
    //checks if adding this ace gets us lower than 21 and higher than 16 then click it i.e. make it worth 11
    private int checkDealerAces(){
        
        for(int i = 0; i < dealer_cards.size(); i++){
            if(dealer_cards.get(i).getCardValue() == 1){//if its an ace
                if(dealer_total + 10 >= 16 && dealer_total + 10 < 21){
                    dealer_cards.get(i).setClicked(true);
                    dealer_cards.get(i).setCardValue(11);
                    addUpCards(dealer_cards);
                }
            }
        }
        return dealer_total;
        
        
    
    }
    
    //check if he hasnt turned his first card
    //then check if hes already winning

    private void dealergo() throws IOException {
        dealer_total = addUpCards(dealer_cards);
        System.out.println(dealer_total);
        
        if(turned){
        
            //if we are not already winning
            if(dealer_total < player_total){
                dealer_total = checkDealerAces();//add aces if appropriate
                if(dealer_total <= 17){ //hit if less than 17
                    Card cardToAdd = randomCard(true);
                    dealer_cards.add(cardToAdd);
                    if(dealer_total + cardToAdd.getValue() > 21){ //make bust if over 21
                        dealer_final = addUpCards(dealer_cards);
                        dealer_bust = true;
                        end = true;
                    }
                }
                else if(dealer_total > 16){//stand if already over 16
                    dealer_final = addUpCards(dealer_cards);
                    end = true;
                }
            }
            else{ //if we are winning already just stand and set end to true.
                dealer_final = addUpCards(dealer_cards);
                end = true;
            }
        }
        else{//if dealer has not turned his first card
            dealer_cards.set(0,randomCard(true));
            turned = true;
        }
        
        
        if(dealer_final > 21 || dealer_total > 21){dealer_bust=true;}
        dealer_total = addUpCards(dealer_cards);
        repaint();
    }
    
    //sets variables back to default state to restart game;
    private void g_restart() throws IOException {
        if(!gameover){
            player_cards = new ArrayList<>();
            dealer_cards = new ArrayList<>();
            mouse_down = false;
            player_total = 0;
            player_final = -1;
            dealer_total = 0;
            dealer_final = -1;
            bust = false;
            turned = false;
            standing = false;
            end = false;
            dealersGo = false;
            dealer_bust = false;
            moneyadded = false;
            gameSetup();
            repaint();
        }
    }

    
    
}

/*
if(!end){
        
            

            if(!bust && !standing){//normal paint
                window.setFont(new Font ("Courier New", 1, 14));
                scoreSTR = "Your Score: " + player_total;
                window.drawString(scoreSTR, 10, Card.Ypos - 10);
            }
            else if(standing || bust){ //if standing or bust just display player final score.
                window.setFont(new Font ("Courier New", 1, 20));
                scoreSTR = "YOUR FINAL SCORE: " + player_total;
                window.drawString(scoreSTR, 100, 300);
            }
            
            //display dealer score
            window.setFont(new Font ("Courier New", 1, 14));
            dealer_scoreSTR = "Dealer Score: " + dealer_total;
            window.drawString(dealer_scoreSTR, 10, Card.d_Ypos - 10);
        }
        if(end){
            if(player_total < dealer_total && dealer_total < 21 || bust){
                window.setFont(new Font ("Courier New", 1, 14));
                window.drawString("YOU: "+player_total+" || DEALER: "+dealer_total, 100, 300);
                window.drawString("YOU LOST, DEALER WINS!", 100, 310);
            }
            else if(player_total > dealer_total && player_total < 21){
                window.setFont(new Font ("Courier New", 1, 14));
                window.drawString("YOU: "+player_total+" || DEALER: "+dealer_total, 100, 300);
                window.drawString("DEALER LOST, YOU WIN!", 100, 310);
            }
}





if(!turned){dealer_cards.set(0,randomCard(true));}//if dealer has not turned his first card
            else{dealer_cards.add(randomCard(true));}//else just add a card to the dealers
            if(!end){if(!standing){player_cards.add(randomCard(false));}}//if not standing add another card
            if (addUpCards(player_cards) > 21){SFX.busted.play();bust = true;repaint();} //if player is bust 
            else{turned = true;repaint();}

*/

