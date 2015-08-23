/*
 * File: GraphicsContest.java
 * --------------------------
 * Title: Pokemon Ruby Elite Four Demo
 * Description: Replication of Pokemon Ruby version's Elite Four with modified battle mechanics.
 * Entry for Stanford's CS106A Introductory Java Course's Graphics Contest.
 * Author: Kevin Khieu
 * 
 * Note/Disclaimer: I do not own any of the images used in this game - I pulled most of them 
 * from Bulbapedia and edited them through Photoshop.
 */


import acm.util.*;
import acm.program.*;
import acm.graphics.*;
import java.awt.event.*;
import java.awt.*;


public class GraphicsContest extends GraphicsProgram implements PokemonConstants {
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	// Initializes our list of Pokemon, Moves, and Characters in the game.
	private Pokedex pokedex;
	private MoveList moves;
	private Opponent challengers;
	private Elite elitefour;
	private Elite us;
	
	// We use two GImages as Backgrounds
	private GImage background;
	private GImage background2;
	
	// Pokemon and Trainer Sprites to use in the game.
	private GImage opponentPokemon;
	private GImage ourPokemon;
	private GImage trainer;
	private GImage trainerSprite;
	private GImage opponent;
	private GImage opponentSprite;
	
	// The generic Pokemon Textbox.
	private GImage textfield;
	
	// Yes/No Option for the Tutorial Screen.
	private GRect YesBox;
	private GRect NoBox;
	private GLabel Yes;
	private GLabel No;
	private boolean yes=true;
	private GImage YNOption;
	
	// Labels and Strings used throughout the code to display messages.
	private GLabel Line1;
	private GLabel Line2;
	private String LineA;
	private String LineB;
	
	// initializes an integer to keep track of what move the user chooses in battle.
	private int chosenMove = 1;
	
	// HP Indicators and Trackers to use in game. 
	private GLabel ourHPIndicator;
	private GLabel theirHPIndicator;
	private GLabel ourMaxHPIndicator;
	private GLabel theirMaxHPIndicator;
	
	// Displays Pokemon Names on the Battle Screen.
	GLabel theirName;
	GLabel ourName;
	
	// Gameboy Interface Buttons (just for funsies)
	private GOval AButton;
	private GOval BButton;
	private GRect upButton;
	private GRect downButton;
	private GRect leftButton;
	private GRect rightButton;
	private GRect centerButton;
	private GRect startButton;
	private GRect selectButton;
	private GRect selector;
	
	// Integer used to keep track of mouseListener Scenarios
	// (when certain buttons should do certain things if clicked at certain times)
	private int scene=0;
	
	// Used as coordinates to keep track of player position when moving around map.
	private int x = 0;
	private int y = 1;
	
	// Used to check when the User clicks "A", thus prompting us to continue on with program.
	private boolean advance = false;
	
	/* We use an array to keep track of the player's position on the map.
	 * Think of it as a mathematical map behind the actual graphical map.
	 */
	private int[][] basicMap = new int[MAP_STEP_WIDTH][MAP_STEP_LENGTH];
	private int[] playerPosition = new int[2];
	private GRect[] string = new GRect[3];
	
	// Determines if we should start dialog between the user and his/her opponent.
	private boolean facingTrainer = false;
	
	// Keeps track of our own and our opponents' Pokemon who are participating in the current battle.
	private Pokemon theirPKMN1;
	private Pokemon theirPKMN2;
	private Pokemon theirPKMN3;
	private Pokemon theirPKMN4;
	private Pokemon theirPKMN5;
	private Pokemon ourPKMN1;
	private Pokemon ourPKMN2;
	private Pokemon ourPKMN3;
	private Pokemon ourPKMN4;
	private Pokemon ourPKMN5;
	private Pokemon theirCurrent;
	private Pokemon ourCurrent;
	
	// Pokemon stats in battle - important to determine basic battle/move mechanics.
	private int accuracy;
	private int ourAttack;
	private int ourDefense;
	private int ourSpecialAttack;
	private int ourSpecialDefense;
	private int ourSpeed;
	private int theirAttack;
	private int theirDefense;
	private int theirSpecialAttack;
	private int theirSpecialDefense;
	private int theirSpeed;
	private int ourLevel = 54;
	private int theirLevel;
	
	// Tracks Hit Points. 
	// NOTE: We do not use the hit point bar in this game, we display the HP Numbers on the interface.
	private int ourHP;
	private int ourHPLoss = 0;
	private int ourMaxHP;
	private int theirHP;
	private int theirHPLoss = 0;
	private int theirMaxHP;
	
	
	// Tracks how many Pokemon are left on each team in battle.
	private int theirPKMNLeft;
	private int ourPKMNLeft;
	
	// Tracks end result of each Pokemon Battle to determine necessary action.
	private boolean opponentDefeated=false;
	private boolean usDefeated=false;
	private boolean theirResetHP;
	private boolean usResetHP = false;
	
	// GLabels for the user interface (so he knows what move to click on)
	private GLabel Move1;
	private GLabel Move2;
	private GLabel Move3;
	private GLabel Move4;

	/* Initializes our list of Moves, our list of Pokemon, and our list of Opponents (and their
	 * Pokemon that they will use in Battle).
	 * 
	 * We also create our array that will keep track of our player's position on the map. The
	 * position at basicMap[4][5] is where the opponent will be, so we label that as -1 to 
	 * indicate we cannot walk on that square.
	 * 
	 * We then break down our program into four parts: setUp, StartScreen, Tutorial, and Game.
	 */
	public void run() {
		moves = new MoveList("Pokemon-Moves.txt");
		pokedex = new Pokedex("Pokemon-Stats.txt");
		challengers = new Opponent("Elite-Four-Pokemon.txt");
		
		for (int i = 0; i < MAP_STEP_WIDTH;i++){
			for (int k = 0; k < MAP_STEP_LENGTH; k++){
				basicMap[i][k]=0;
			}
		}
		basicMap[4][5]=-1;
		addKeyListeners();
		addMouseListeners();
		setUp();
		displayStartScreen();
		runTutorial();
		playGame();	
	}
	
	// Creates the GameBoy Interface to play on
	private void setUp(){
		createGameboy();
		createDirectionalPad();
		createABButtons();
		createStartSelect();
	}
	
	// Creates the "bulk" of the gameboy minus the buttons.
	private void createGameboy(){
		GRect rect1 = new GRect(0,0,APPLICATION_WIDTH, BORDER/2);
		GRect rect2 = new GRect(0,0,BORDER/2, APPLICATION_HEIGHT);
		GRect rect3 = new GRect(0, GAME_HEIGHT + BORDER, APPLICATION_WIDTH, APPLICATION_HEIGHT-BORDER-GAME_HEIGHT);
		GRect rect4 = new GRect(APPLICATION_WIDTH-BORDER/2,0,BORDER/2, APPLICATION_HEIGHT);
		
		rect1.setFilled(true);
		rect2.setFilled(true);
		rect3.setFilled(true);
		rect4.setFilled(true);
		rect1.setColor(Color.CYAN);
		rect2.setColor(Color.CYAN);
		rect3.setColor(Color.CYAN);
		rect4.setColor(Color.CYAN);
		add(rect1);
		add(rect2);
		add(rect3);
		add(rect4);
		
		GRect border1 = new GRect(BORDER/2, BORDER/2, (GAME_WIDTH + BORDER), BORDER/2);
		GRect border2 = new GRect(BORDER/2, BORDER/2, BORDER/2, GAME_HEIGHT+BORDER);
		GRect border3 = new GRect(BORDER+GAME_WIDTH, BORDER/2, BORDER/2, GAME_HEIGHT+BORDER);
		GRect border4 = new GRect(BORDER/2, BORDER+GAME_HEIGHT, (GAME_WIDTH + BORDER), BORDER/2);
		
		border1.setFilled(true);
		border2.setFilled(true);
		border3.setFilled(true);
		border4.setFilled(true);
		border1.setColor(Color.BLACK);
		border2.setColor(Color.BLACK);
		border3.setColor(Color.BLACK);
		border4.setColor(Color.BLACK);
		add(border1);
		add(border2);
		add(border3);
		add(border4);
	}
	
	// Creates the arrow keys used to navigate the screen.
	private void createDirectionalPad(){
		GRect upButtonBack = new GRect(28* getWidth()/160, 175*getHeight()/292,BUTTON_WIDTH, BUTTON_LENGTH);
		upButtonBack.setFilled(true);
		upButtonBack.setFillColor(Color.GRAY);
		add(upButtonBack);
		
		GImage upArrow = new GImage("Arrow.jpg");
		upArrow.setSize(BUTTON_WIDTH, BUTTON_LENGTH);
		add(upArrow, 28* getWidth()/160, 175*getHeight()/292);
		upButton = new GRect(28* getWidth()/160, 175*getHeight()/292,BUTTON_WIDTH, BUTTON_LENGTH);
		add(upButton);
		
		GRect downButtonBack = new GRect(28*getWidth()/160, (175*getHeight()/292)+BUTTON_LENGTH+BUTTON_WIDTH, BUTTON_WIDTH, BUTTON_LENGTH);
		downButtonBack.setFilled(true);
		downButtonBack.setFillColor(Color.GRAY);
		add(downButtonBack);
		
		GImage downArrow = new GImage("DownArrow.jpg");
		downArrow.setSize(BUTTON_WIDTH, BUTTON_LENGTH);
		add(downArrow, 28*getWidth()/160, (175*getHeight()/292)+BUTTON_LENGTH+BUTTON_WIDTH+2);
		downButton = new GRect(28*getWidth()/160, (175*getHeight()/292)+BUTTON_LENGTH+BUTTON_WIDTH, BUTTON_WIDTH, BUTTON_LENGTH);
		add(downButton);
		
		GRect rightButtonBack = new GRect((28*getWidth()/160)+BUTTON_WIDTH, (175*getHeight()/292)+BUTTON_LENGTH, BUTTON_LENGTH, BUTTON_WIDTH);
		rightButtonBack.setFilled(true);
		rightButtonBack.setColor(Color.GRAY);
		add(rightButtonBack);
		
		GImage rightArrow = new GImage("RightArrow.jpg");
		rightArrow.setSize(BUTTON_LENGTH, BUTTON_WIDTH);
		add(rightArrow,(28*getWidth()/160)+BUTTON_LENGTH-5, (175*getHeight()/292)+BUTTON_LENGTH);
		rightButton = new GRect((28*getWidth()/160)+BUTTON_WIDTH, (175*getHeight()/292)+BUTTON_LENGTH, BUTTON_LENGTH, BUTTON_WIDTH);
		add(rightButton);
		
		GRect leftButtonBack = new GRect((28*getWidth()/160)-BUTTON_LENGTH, (175*getHeight()/292)+BUTTON_LENGTH, BUTTON_LENGTH, BUTTON_WIDTH);
		leftButtonBack.setFilled(true);
		leftButtonBack.setColor(Color.GRAY);
		add(leftButtonBack);
		
		GImage leftArrow = new GImage("LeftArrow.jpg");
		leftArrow.setSize(BUTTON_LENGTH, BUTTON_WIDTH);
		add(leftArrow,(28*getWidth()/160)-BUTTON_WIDTH-5, (175*getHeight()/292)+BUTTON_LENGTH);
		
		leftButton = new GRect((28*getWidth()/160)-BUTTON_LENGTH, (175*getHeight()/292)+BUTTON_LENGTH, BUTTON_LENGTH, BUTTON_WIDTH);
		add(leftButton);
		
		centerButton = new GRect(28*getWidth()/160, (175*getHeight()/292)+BUTTON_LENGTH, BUTTON_WIDTH, BUTTON_WIDTH);
		centerButton.setFilled(true);
		centerButton.setFillColor(Color.GRAY);
		add(centerButton);
	}
	
	// Creates the A and B Buttons.
	private void createABButtons(){
		GOval AButtonBack = new GOval((132*getWidth()/160),(175*getHeight()/292)+BUTTON_LENGTH/2,BUTTON_WIDTH*1.5, BUTTON_WIDTH*1.5);
		AButtonBack.setFilled(true);
		AButtonBack.setFillColor(Color.GRAY);
		add(AButtonBack);
		
		GLabel a = new GLabel("A",(132*getWidth()/160),(175*getHeight()/292)+BUTTON_LENGTH/2);
		a.setFont("MONOSPACED-35");
		a.setColor(Color.BLACK);
		add(a);
		a.move(AButtonBack.getWidth()/2-a.getWidth()/2+1,(AButtonBack.getHeight()/2)+(a.getHeight()/2-6));
		
		AButton = new GOval((132*getWidth()/160),(175*getHeight()/292)+BUTTON_LENGTH/2,BUTTON_WIDTH*1.5, BUTTON_WIDTH*1.5);
		add(AButton);
		
		GOval BButtonBack = new GOval((105*getWidth()/160), (200*getHeight()/292), BUTTON_WIDTH*1.5, BUTTON_WIDTH*1.5);
		BButtonBack.setFilled(true);
		BButtonBack.setFillColor(Color.GRAY);
		add(BButtonBack);
		
		GLabel b = new GLabel("B",(105*getWidth()/160),(200*getHeight()/292));
		b.setFont("MONOSPACED-35");
		b.setColor(Color.BLACK);
		add(b);
		b.move(BButtonBack.getWidth()/2-b.getWidth()/2+1,BButtonBack.getHeight()/2+b.getHeight()/2-6);

		BButton = new GOval((105*getWidth()/160), (200*getHeight()/292), BUTTON_WIDTH*1.5, BUTTON_WIDTH*1.5);
		add(BButton);
	}
	
	// These buttons are never used - just for show.
	private void createStartSelect(){
		GRect startButtonBack = new GRect(getWidth()/2-START_OFFSET-BUTTON_WIDTH, 250*getHeight()/292, BUTTON_WIDTH, 10);
		startButtonBack.setFilled(true);
		startButtonBack.setFillColor(Color.GRAY);
		add(startButtonBack);
		
		GLabel start = new GLabel("start",getWidth()/2-START_OFFSET-BUTTON_WIDTH/2, 250*getHeight()/292 + startButtonBack.getHeight());
		start.setFont("MONOSPACED-9");
		start.setColor(Color.BLACK);
		start.move(-start.getWidth()/2,-2);
		add(start);
		
		startButton = new GRect(getWidth()/2-START_OFFSET-BUTTON_WIDTH, 250*getHeight()/292, BUTTON_WIDTH, 10);
		add(startButton);
		
		GRect selectButtonBack = new GRect(getWidth()/2+START_OFFSET, 250*getHeight()/292, BUTTON_WIDTH, 10);
		selectButtonBack.setFilled(true);
		selectButtonBack.setFillColor(Color.GRAY);
		add(selectButtonBack);
		
		GLabel select = new GLabel("select",getWidth()/2+START_OFFSET+BUTTON_WIDTH/2, 250*getHeight()/292 + selectButtonBack.getHeight());
		select.setFont("MONOSPACED-9");
		select.setColor(Color.BLACK);
		select.move(-select.getWidth()/2,-2);
		add(select);
		
		selectButton = new GRect(getWidth()/2+START_OFFSET, 250*getHeight()/292, BUTTON_WIDTH, 10);
		add(selectButton);
	}
	
	// Displays the start screen to indicate we are ready to play.
	private void displayStartScreen(){
		setBackground(Color.BLACK);
		background = new GImage("START_SCREEN.png");
		background.setSize(GAME_WIDTH, GAME_HEIGHT);
		add(background,BORDER,BORDER);
		background.sendToBack();
		
		GLabel text2 = new GLabel("PRESS A TO PLAY");
		text2.setFont("MONOSPACED-25");
		text2.setColor(Color.WHITE);
		add(text2,getWidth()/2-text2.getWidth()/2,background.getHeight()+text2.getHeight()+10);
		
		// Waits for user to click A to continue. (refer to Scene options below)
		while(advance==false){
			pause(1);
		}
		remove(background);
		remove(text2);
		pause(500);
	}
	
	// Professor Oak runs the user through the tutorial.
	// We also give the user the option to give up now while he/she still has pride.
	private void runTutorial(){
		background=new GImage("ProfessorOak.jpg");
		background.setSize(GAME_WIDTH, GAME_HEIGHT);
		add(background,BORDER,BORDER);
		background.sendToBack();
		pause(500);
		
		textfield = new GImage("TextField.png");
		textfield.scale(GAME_WIDTH/textfield.getWidth());
		add(textfield, BORDER,bottomGameWindow-textfield.getHeight());
		
		Line1 = new GLabel("Welcome to the world", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("of Pokemon!", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("My name is Prof. Oak. I will", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("guide you through this game.", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("In this demo, you will be", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("challenging the Elite Four.", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("The group is comprised", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("of four of the best", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("Pokemon trainers in the", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("world!", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("Beat them all, and you will", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("be crowned the Pokemon", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("Champion in the Hoenn ", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("Region!", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("Are you ready to begin?", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		addYesNo();
		scene=1;
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		
		Line1 = new GLabel("You will be given five", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("pre-chosen Pokemon to fight", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("with: Blaziken, Swampert", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("Sceptile, Raichu, -", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("and Rayquaza.", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("Each has its own strengths -", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("and weaknesses. Use your", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("Pokemon to their best -", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("potential, and you may", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("become a CHAMPION!", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("Note that for this demo,", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("you cannot use items or", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("switch Pokemon, and battle", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("mechanics have been changed.", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("In the game, use the arrow", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("keys, A, and B buttons", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("to navigate the screen.", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("If you feel like quitting at", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("any time, close the screen", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("to end the game.", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("Good Luck! You'll need it.", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("Press A to continue...", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		remove(textfield);
		pause(500);
		remove(background);
		pause(500);
		
		// sets scene to -1 to that the user can't screw stuff up.
		scene = -1;
	}
	
	public void mouseClicked(MouseEvent e){
		
		// Scene 0: Used for dialogs when we need the user to hit the A Button
		// to continue on with the program.
		if (scene == 0){
				if (getElementAt(e.getX(), e.getY()) == AButton){
					advance=true;
				}
				// Scene 1: Used for selecting Yes/No in tutorial.
		} else if (scene == 1){
				if (getElementAt(e.getX(), e.getY()) == downButton){
					remove(YesBox);
					add(NoBox);
					Yes.sendToFront();
					No.sendToFront();
					yes = false;
				} else if (getElementAt(e.getX(), e.getY()) == upButton){
					remove(NoBox);
					add(YesBox);
					Yes.sendToFront();
					No.sendToFront();
					yes = true;
				} else if (getElementAt(e.getX(), e.getY()) == AButton){
					if (yes){
						remove(YesBox);
						remove(YNOption);
						remove(Yes);
						remove(No);
						advance=true;
						scene = 0;
					} else {
						remove(NoBox);
						remove(YNOption);
						remove(Yes);
						remove(No);
						remove(Line1);
						Line1 = new GLabel("Please Come Back And", Line1X, Line1Y);
						Line1.setFont("COURIER-15");
						Line1.setColor(Color.BLACK);
						add(Line1);
						
						Line2 = new GLabel("play another time!", Line2X, Line2Y);
						Line2.setFont("COURIER-15");
						Line2.setColor(Color.BLACK);
						add(Line2);
						while (!advance){
							pause(100000);
						}
					}
			}
				// Scene 2: Used for moving character around the map.
		} else if (scene == 2){
			if (getElementAt(e.getX(), e.getY()) == upButton){
				remove(trainer);
				trainer = new GImage("Boy_Backward.png");
				trainer.setSize(Xstep+5,Ystep+4);
				add(trainer, BORDER+(2+playerPosition[x])*Xstep,BORDER+GAME_HEIGHT-72-playerPosition[y]*Ystep);
				facingTrainer=false;
				
				if (playerPosition[y]!=6 && basicMap[playerPosition[x]][playerPosition[y]+1]!=-1){
					playerPosition[y]++;
				}
				if (basicMap[playerPosition[x]][playerPosition[y]+1]==-1){
					facingTrainer=true;
				}
			}
			if (getElementAt(e.getX(), e.getY()) == downButton){
				remove(trainer);
				trainer = new GImage("Boy_Forward.png");
				trainer.setSize(Xstep+5,Ystep+4);
				add(trainer, BORDER+(2+playerPosition[x])*Xstep,BORDER+GAME_HEIGHT-72-playerPosition[y]*Ystep);
				facingTrainer=false;
				
				if (basicMap[playerPosition[x]][playerPosition[y]-1]!=-1 && playerPosition[y]!=0){
					playerPosition[y]--;
				}
				if(basicMap[playerPosition[x]][playerPosition[y]-1]==-1){
					facingTrainer=true;
				}
			}
			if (getElementAt(e.getX(), e.getY()) == rightButton){
				remove(trainer);
				trainer = new GImage("Boy_Right.png");
				trainer.setSize(Xstep+5,Ystep+4);
				add(trainer, BORDER+(2+playerPosition[x])*Xstep,BORDER+GAME_HEIGHT-72-playerPosition[y]*Ystep);
				facingTrainer=false;
				
				if (basicMap[playerPosition[x]+1][playerPosition[y]]!=-1 && playerPosition[0]!=8){
					playerPosition[x]++;
				} 
				if (basicMap[playerPosition[x]+1][playerPosition[y]]==-1){
					facingTrainer=true;
				}
			}
			if (getElementAt(e.getX(), e.getY()) == leftButton){
				remove(trainer);
				trainer = new GImage("Boy_Left.png");
				trainer.setSize(Xstep+5,Ystep+4);
				add(trainer, BORDER+(2+playerPosition[x])*Xstep,BORDER+GAME_HEIGHT-72-playerPosition[y]*Ystep);
				facingTrainer=false;
				
				if (basicMap[playerPosition[x]-1][playerPosition[y]]!=-1 && playerPosition[0]!=0){
					playerPosition[x]--;
				} 
				if(basicMap[playerPosition[x]-1][playerPosition[y]]==-1){
					facingTrainer=true;
				}
			}
			if (getElementAt(e.getX(), e.getY()) == AButton){
				if (facingTrainer){
					scene=0;
				}
			}
			
			// Scene 3: Battle Screen. 
		} else if (scene == 3){

			background2=new GImage("Battle_Screen.jpg");
			background2.setSize(GAME_WIDTH, GAME_HEIGHT);
			add(background2,BORDER,BORDER);
			
			// Scene 4: Used for selecting a Move in the Battle Screen.
		} else if (scene ==4){
			if (getElementAt(e.getX(), e.getY()) == AButton){
				scene=-1;
				advance=true;
			}
			
			if (chosenMove==1){
				if (getElementAt(e.getX(), e.getY()) == downButton){
					selector.setLocation(selector2X, selector2Y);
					chosenMove = 2;
				}
				if (getElementAt(e.getX(), e.getY()) == rightButton){
					selector.setLocation(selector3X, selector3Y);
					chosenMove = 3;
				}
			} else if (chosenMove == 2){
				if (getElementAt(e.getX(), e.getY()) == upButton){
					selector.setLocation(selector1X, selector1Y);
					chosenMove = 1;
				}
				if (getElementAt(e.getX(), e.getY()) == rightButton){
					selector.setLocation(selector4X, selector4Y);
					chosenMove = 4;
				}
			} else if (chosenMove == 3){
				if (getElementAt(e.getX(), e.getY()) == downButton){
					selector.setLocation(selector4X, selector4Y);
					chosenMove = 4;
				}
				if (getElementAt(e.getX(), e.getY()) == leftButton){
					selector.setLocation(selector1X, selector1Y);
					chosenMove = 1;
				}
			} else if (chosenMove == 4){
				if (getElementAt(e.getX(), e.getY()) == upButton){
					selector.setLocation(selector3X, selector3Y);
					chosenMove = 3;
				}
				if (getElementAt(e.getX(), e.getY()) == leftButton){
					selector.setLocation(selector2X, selector2Y);
					chosenMove = 2;
				}
			}
		}
	}
	
	// Adds a Yes/No textbox in the screen. used in the tutorial.
	private void addYesNo(){
		YNOption=new GImage("TextField.png");
		YNOption.setSize(textfield.getHeight(),textfield.getHeight());
		add(YNOption,getWidth()-BORDER-YNOption.getWidth(),bottomGameWindow-2*textfield.getHeight());
		
		Yes = new GLabel("Yes",getWidth()-BORDER-YNOption.getWidth(),bottomGameWindow-2*textfield.getHeight());
		Yes.setFont("COURIER-22");
		add(Yes);
		Yes.move(YNOption.getWidth()/2-Yes.getWidth()/2+1, YNOption.getHeight()/2-7);
		
		No = new GLabel("No",getWidth()-BORDER-YNOption.getWidth(),bottomGameWindow-2*textfield.getHeight());
		No.setFont("COURIER-22");
		add(No);
		No.move(YNOption.getWidth()/2-No.getWidth()/2+1, YNOption.getHeight()-15);
		
		YesBox = new GRect(getWidth()-BORDER-YNOption.getWidth(),bottomGameWindow-2*textfield.getHeight()-14, textfield.getHeight()-6,textfield.getHeight()/2-9);
		YesBox.setFilled(true);
		YesBox.setColor(Color.YELLOW);
		NoBox = new GRect(getWidth()-BORDER-YNOption.getWidth(),(textfield.getHeight()/2)+bottomGameWindow-2*textfield.getHeight(), textfield.getHeight()-6,textfield.getHeight()/2-9);
		NoBox.setFilled(true);
		NoBox.setColor(Color.YELLOW);
		YesBox.move(YNOption.getWidth()/2-YesBox.getWidth()/2-1,20);
		NoBox.move(YNOption.getWidth()/2-NoBox.getWidth()/2-1,0);
		add(YesBox);
		Yes.sendToFront();
		No.sendToFront();
	}
	
	// Post-tutorial sequence. 
	private void playGame(){
		
		/* Sydney: First Challenge
		 * The first room the user is placed in is Sydney's room. The user can walk 
		 * around the room, and when he clicks A while facing Sydney, he will enter a 
		 * battle with him. If user wins, he/she moves on to next room.
		 */
		
		// Sets background to be the correct image.
		background=new GImage("Sydney.png");
		background.setSize(GAME_WIDTH, GAME_HEIGHT);
		add(background,BORDER,BORDER);
		background.sendToBack();
		
		// Creates Sydney's character in the map.
		opponentSprite = new GImage("Sydney_Sprite.png");
		opponentSprite.setSize(Xstep+5, Ystep+4);
		add(opponentSprite, BORDER+(2+4)*Xstep,BORDER+GAME_HEIGHT-72-5*Ystep);
		
		// Scene 2 allows us to move around.
		scene = 2;
		
		// Create and add the Player's Sprite in the correct position.
		playerPosition[x]=4;
		playerPosition[y]=0;
		trainer = new GImage("Boy_Backward.png");
		trainer.setSize(Xstep+5,Ystep+4);
		add(trainer, BORDER+(2+playerPosition[x])*Xstep,BORDER+GAME_HEIGHT-72-playerPosition[y]*Ystep);
		
		// Movement.
		while(scene==2){
			trainer.setLocation(BORDER+(2+playerPosition[x])*Xstep,BORDER+GAME_HEIGHT-72-playerPosition[y]*Ystep);
		}
		
		// Dialog if the player clicks on Sydney.
		textfield = new GImage("TextField.png");
		textfield.scale(GAME_WIDTH/textfield.getWidth());
		add(textfield, BORDER,bottomGameWindow-textfield.getHeight());
		
		Line1 = new GLabel("Welcome to the Elite Four!", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("I shall be your first", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("challenge. If you defeat", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("me, you will move on!", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("Ready? Here I come!", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		
		// Battle Sequence
		remove(Line1);
		remove(Line2);
		remove(textfield);
		
		// Blinks the screen once to indicate a battle will begin. 
		battleHype();
		scene = -1;
		while (scene==-1){
			pause(1000);
			
			// We layer the battle image/graphics over our current map.
			background2=new GImage("Battle_Screen.jpg");
			background2.setSize(GAME_WIDTH, GAME_HEIGHT);
			add(background2,BORDER,BORDER);
			
			// Each leader has its own fighting sequence/private method due to the
			// specific files required for each fight.
			fightSydney();
		}
		
		// After the battle we remove everything from the battle field.
		remove(background2);
		remove(trainerSprite);
		remove(opponent);
		remove(textfield);
		remove(ourMaxHPIndicator);
		remove(theirMaxHPIndicator);
		remove(ourHPIndicator);
		remove(theirHPIndicator);
		facingTrainer = false;
		
		// Sydney then brings the user to the next room. Game continues.
		textfield = new GImage("TextField.png");
		textfield.scale(GAME_WIDTH/textfield.getWidth());
		add(textfield, BORDER,bottomGameWindow-textfield.getHeight());
		
		Line1 = new GLabel("I will now bring you", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("to the next room.", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		pause(1000);
		remove(textfield);
		remove(Line1);
		remove(Line2);
		
		/* Phoebe: Second Challenge
		 * The second room the user is placed in is Phoebe's room. The user can walk 
		 * around the room, and when he clicks A while facing Phoebe, he will enter a 
		 * battle with her. She is slightly more challenging than Sydney.
		 */
		remove(background);
		remove(opponentSprite);
		remove(trainer);
		
		// Resets the room, player position, and the opponent sprite to match Phoebe's room.
		facingTrainer = false;
		pause(200);
		playerPosition[x]=4;
		playerPosition[y]=0;
		trainer = new GImage("Boy_Backward.png");
		trainer.setSize(Xstep+5,Ystep+4);
		add(trainer, BORDER+(2+playerPosition[x])*Xstep,BORDER+GAME_HEIGHT-72-playerPosition[y]*Ystep);
		background=new GImage("Phoebe.png");
		background.setSize(GAME_WIDTH, GAME_HEIGHT);
		add(background,BORDER,BORDER);
		background.sendToBack();
		
		opponentSprite = new GImage("Phoebe_Sprite.png");
		opponentSprite.setSize(Xstep+5, Ystep+4);
		add(opponentSprite, BORDER+(2+4)*Xstep,BORDER+GAME_HEIGHT-72-5*Ystep);
		scene = 2;
		trainer.sendToFront();
		
		while(scene==2){
			trainer.setLocation(BORDER+(2+playerPosition[x])*Xstep,BORDER+GAME_HEIGHT-72-playerPosition[y]*Ystep);
		}
		
		textfield = new GImage("TextField.png");
		textfield.scale(GAME_WIDTH/textfield.getWidth());
		add(textfield, BORDER,bottomGameWindow-textfield.getHeight());
		
		Line1 = new GLabel("Hello! I'm Phoebe.", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("I will be your next", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("challenge. If you defeat", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("me, you will move on!", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("I'm stronger than Sydney", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("so be careful!", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("Let's Battle!", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		
		remove(Line1);
		remove(Line2);
		remove(textfield);
		battleHype();
		scene = -1;
		while (scene==-1){
			pause(1000);
			background2=new GImage("Battle_Screen.jpg");
			background2.setSize(GAME_WIDTH, GAME_HEIGHT);
			add(background2,BORDER,BORDER);
			fightPhoebe();
		}
		remove(background2);
		remove(trainerSprite);
		remove(opponent);
		remove(textfield);
		remove(ourMaxHPIndicator);
		remove(theirMaxHPIndicator);
		remove(ourHPIndicator);
		remove(theirHPIndicator);
		facingTrainer = false;
		
		textfield = new GImage("TextField.png");
		textfield.scale(GAME_WIDTH/textfield.getWidth());
		add(textfield, BORDER,bottomGameWindow-textfield.getHeight());
		
		Line1 = new GLabel("I will now bring you", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("to the next room.", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		pause(1000);
		remove(textfield);
		remove(Line1);
		remove(Line2);
		
		/* Glacia: Third Challenge
		 * Same room mechanics as the previous two rooms.
		 */		
		remove(background);
		remove(opponentSprite);
		remove(trainer);
		pause(200);
		
		// Resets the room, player position, and the opponent sprite to match Glacia's room.
		playerPosition[x]=4;
		playerPosition[y]=0;
		trainer = new GImage("Boy_Backward.png");
		trainer.setSize(Xstep+5,Ystep+4);
		add(trainer, BORDER+(2+playerPosition[x])*Xstep,BORDER+GAME_HEIGHT-72-playerPosition[y]*Ystep);
		background=new GImage("Glacia.png");
		background.setSize(GAME_WIDTH, GAME_HEIGHT);
		add(background,BORDER,BORDER);
		background.sendToBack();
		
		opponentSprite = new GImage("Glacia_Sprite.png");
		opponentSprite.setSize(Xstep+5, Ystep+4);
		add(opponentSprite, BORDER+(2+4)*Xstep,BORDER+GAME_HEIGHT-72-5*Ystep);
		trainer.sendToFront();
		while(scene==2){
			trainer.setLocation(BORDER+(2+playerPosition[x])*Xstep,BORDER+GAME_HEIGHT-72-playerPosition[y]*Ystep);
		}
		
		textfield = new GImage("TextField.png");
		textfield.scale(GAME_WIDTH/textfield.getWidth());
		add(textfield, BORDER,bottomGameWindow-textfield.getHeight());
		
		Line1 = new GLabel("Welcome. My name is", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("Glacia, and I am the", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("third challenge in the ", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("Elite Four. Beat me, and", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("you will advance to the", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("Champion Battle.", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("Let's Battle!", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		
		remove(Line1);
		remove(Line2);
		remove(textfield);
		battleHype();
		scene = -1;
		while (scene==-1){
			pause(1000);
			background2=new GImage("Battle_Screen.jpg");
			background2.setSize(GAME_WIDTH, GAME_HEIGHT);
			add(background2,BORDER,BORDER);
			fightGlacia();
		}
		remove(background2);
		remove(trainerSprite);
		remove(opponent);
		remove(textfield);
		remove(ourMaxHPIndicator);
		remove(theirMaxHPIndicator);
		remove(ourHPIndicator);
		remove(theirHPIndicator);
		facingTrainer = false;
		
		textfield = new GImage("TextField.png");
		textfield.scale(GAME_WIDTH/textfield.getWidth());
		add(textfield, BORDER,bottomGameWindow-textfield.getHeight());
		
		Line1 = new GLabel("I will now bring you", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("to the final room.", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		pause(1000);
		remove(textfield);
		remove(Line1);
		remove(Line2);
		remove(background);
		
		/* Drake: Last Challenge
		 * Same room mechanics as the previous three rooms. If the user wins here, he/she
		 * becomes Champion. (I removed the final champion from the game due to time constraints
		 * in programming this, so if you know about Pokemon and are wondering why I stopped at
		 * Drake, that's why haha).
		 */		
		remove(opponentSprite);
		remove(trainer);
		facingTrainer = false;
		
		pause(200);
		
		// Resets the room, player position, and the opponent sprite to match Drake's room.
		playerPosition[x]=4;
		playerPosition[y]=0;
		trainer = new GImage("Boy_Backward.png");
		trainer.setSize(Xstep+5,Ystep+4);
		add(trainer, BORDER+(2+playerPosition[x])*Xstep,BORDER+GAME_HEIGHT-72-playerPosition[y]*Ystep);
		background=new GImage("Drake.png");
		background.setSize(GAME_WIDTH, GAME_HEIGHT);
		add(background,BORDER,BORDER);
		background.sendToBack();
		
		opponentSprite = new GImage("Drake_Sprite.png");
		opponentSprite.setSize(Xstep+5, Ystep+4);
		add(opponentSprite, BORDER+(2+4)*Xstep,BORDER+GAME_HEIGHT-72-5*Ystep);
		trainer.sendToFront();
		facingTrainer = false;
		
		while(scene==2){
			trainer.setLocation(BORDER+(2+playerPosition[x])*Xstep,BORDER+GAME_HEIGHT-72-playerPosition[y]*Ystep);
		}
		textfield = new GImage("TextField.png");
		textfield.scale(GAME_WIDTH/textfield.getWidth());
		add(textfield, BORDER,bottomGameWindow-textfield.getHeight());
		
		Line1 = new GLabel("You're really something, kid.", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("Few people have ever made", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("it this far in the Elite", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("Four. If you win this, ", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("you will become the next", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("Pokemon Champion!", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		Line1 = new GLabel("I won't hold back...", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("Let's do this!", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		remove(textfield);
		battleHype();
		scene = -1;
		while (scene==-1){
			pause(1000);
			background2=new GImage("Battle_Screen.jpg");
			background2.setSize(GAME_WIDTH, GAME_HEIGHT);
			add(background2,BORDER,BORDER);
			fightDrake();
		}
		remove(background2);
		remove(trainerSprite);
		remove(opponent);
		remove(textfield);
		remove(ourMaxHPIndicator);
		remove(theirMaxHPIndicator);
		remove(ourHPIndicator);
		remove(theirHPIndicator);
		
		scene = 0;
		
		textfield = new GImage("TextField.png");
		textfield.scale(GAME_WIDTH/textfield.getWidth());
		add(textfield, BORDER,bottomGameWindow-textfield.getHeight());
		
		Line1 = new GLabel("You definitely have the", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("skills of a true champ!", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("I'm afraid I must admit ", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("defeat - you are a very", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		Line1 = new GLabel("skilled trainer and worthy", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("of being Pokemon Champion!", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		// WINNER
		Line1 = new GLabel("Congratulations! You win!", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		scene = -1;	
	}
	
	// Blinks screen once before a battle to indicate a battle will start.
	private void battleHype(){
		GRect blackSquare = new GRect(BORDER,BORDER,GAME_WIDTH, GAME_HEIGHT);
		blackSquare.setFilled(true);
		blackSquare.setColor(Color.BLACK);
		for (int i = 0; i < 6; i++){
			add(blackSquare);
			pause(100);
			remove(blackSquare);
		}		
	}
	
	// Fighting Sydney Battle Sequence!
	private void fightSydney(){
		// Sets Sydney as the current elitefour we are facing.
		elitefour = challengers.findEntry("Sydney");
		
		// Pulls the Trainer characteristics for this battle.
		us = challengers.findEntry("Trainer");
		
		// Sets our Pokemon for the battle. Note that lineups cannot be
		// adjusted/swapped in this demo.
		ourPKMN1 = pokedex.findEntry(us.getFirstPokemon());
		ourPKMN2 = pokedex.findEntry(us.getSecondPokemon());
		ourPKMN3 = pokedex.findEntry(us.getThirdPokemon());
		ourPKMN4 = pokedex.findEntry(us.getFourthPokemon());
		ourPKMN5 = pokedex.findEntry(us.getFifthPokemon());
		theirPKMN1 = pokedex.findEntry(elitefour.getFirstPokemon());
		theirPKMN2 = pokedex.findEntry(elitefour.getSecondPokemon());
		theirPKMN3 = pokedex.findEntry(elitefour.getThirdPokemon());
		theirPKMN4 = pokedex.findEntry(elitefour.getFourthPokemon());
		theirPKMN5 = pokedex.findEntry(elitefour.getFifthPokemon());
		
		// Set the opponent's name into a string for ease.
		String opponentName = elitefour.getName();
		
		// Opponent's face image in battle.
		opponent = new GImage("SydneyTrainer.png");
		
		// Our face image in battle.
		trainerSprite = new GImage("Trainer_Battle.png");
		opponent.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
		trainerSprite.setSize(OPPONENT_WIDTH,OPPONENT_HEIGHT);
		add(trainerSprite, getWidth()-BORDER-trainerSprite.getWidth(),BORDER+GAME_HEIGHT-trainerSprite.getHeight()-textfield.getHeight());
		add(opponent, BORDER+0,275-224+20);
		background2.sendToBack();
		opponentSprite.sendToBack();
		trainer.sendToBack();
		background.sendToBack();
		
		// Animations for dramatic entrances into the game.
		while(opponent.getX()<239){
			opponent.move(3,0);
			trainerSprite.move(-3, 0);
			pause(9);
		}
		scene = 0;
		pause(500);
		Line1 = new GLabel("You have been challenged", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("by Elite Four "+ opponentName + "!", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		scene = -1;
		
		// Faces go away to make way for the battle screen.
		while(opponent.getX()<GAME_WIDTH-BORDER-opponent.getWidth()){
			opponent.move(4,0);
			pause(4);
		}
		while(trainerSprite.getX()>BORDER){
			trainerSprite.move(-4,0);
			pause(4);
		}
		remove(opponent);
		remove(trainerSprite);
		
		// Set our marker variables to their correct pre-game values.
		theirPKMNLeft=5;
		ourPKMNLeft=5;
		theirResetHP = false;
		usResetHP = false;
		
		// While both of us have Pokemon, we must battle.
		while (ourPKMNLeft>0 && theirPKMNLeft >0){
			// Starting condition means we both send out new pokemon.
			if (theirPKMNLeft == 5 && ourPKMNLeft == 5){
				theirCurrent = theirPKMN1;
				opponentPokemon = new GImage("Mightyena.png");
				ourCurrent = ourPKMN1;
				ourPokemon = new GImage("Blaziken.png");
			}
			
			// Otherwise we determine which pokemon each player sends out one at a time.
			if (theirPKMNLeft ==4){
				theirCurrent = theirPKMN2; 
				opponentPokemon = new GImage("Shiftry.png");
			} else if (theirPKMNLeft ==3){
				theirCurrent = theirPKMN3; 
				opponentPokemon = new GImage("Cacturne.png");
			} else if (theirPKMNLeft ==2){
				theirCurrent = theirPKMN4; 
				opponentPokemon = new GImage("Sharpedo.png");
			} else if (theirPKMNLeft ==1){
				theirCurrent = theirPKMN5; 
				opponentPokemon = new GImage("Absol.png");
			}
			
			if (ourPKMNLeft ==4){
				ourCurrent = ourPKMN2; 
				ourPokemon = new GImage("Swampert.png");
			} else if (ourPKMNLeft ==3){
				ourCurrent = ourPKMN3;
				ourPokemon = new GImage("Sceptile.png");
			} else if (ourPKMNLeft ==2){
				ourCurrent = ourPKMN4;
				ourPokemon = new GImage("Rayquaza.png");
			} else if (ourPKMNLeft ==1){
				ourCurrent = ourPKMN5; 
				ourPokemon = new GImage("Raichu.png");
			}
			
			// At the start, neither of us have lost a battle yet, so we run this script.
			if (!usDefeated && !opponentDefeated){
				
				// The generic "Go _______ " script.
				String line1 = elitefour.getName() +" sent out "+ theirCurrent.getName() + "!";
				String line2;
				Line1 = new GLabel(line1, Line1X, Line1Y);
				Line1.setFont("COURIER-15");
				Line1.setColor(Color.BLACK);
				add(Line1);
				
				opponent = opponentPokemon;
				opponent.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
				add(opponent, getWidth()-BORDER-opponent.getWidth(),275-224+20);
				background2.sendToBack();
				opponentSprite.sendToBack();
				trainer.sendToBack();
				background.sendToBack();
				while(opponent.getX()>239){
					opponent.move(-3,0);
					pause(9);
				}
				pause(1000);
				remove(Line1);
				
				line1 = "Go "+ ourCurrent.getName() + "!";
				Line1 = new GLabel(line1, Line1X, Line1Y);
				Line1.setFont("COURIER-15");
				Line1.setColor(Color.BLACK);
				add(Line1);
				
				trainerSprite = ourPokemon;
				trainerSprite.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
				add(trainerSprite, BORDER,BORDER+GAME_HEIGHT-trainerSprite.getHeight()-textfield.getHeight());
				
				// NOTE: We use trainerSprite and opponent for both the images of the trainers
				// and the Pokemon. I'm not sure why I named them differently - I just noticed that now.
				
				while(trainerSprite.getX()<75){
					trainerSprite.move(3, 0);
					pause(9);
				}
				pause(1000);
				remove(Line1);

				remove(background2);
				
				// Adds our Battle Screen for battling.
				background2=new GImage("BattleScreen1.jpg");
				background2.setSize(GAME_WIDTH, GAME_HEIGHT);
				add(background2,BORDER,BORDER);
				trainerSprite.sendToFront();
				opponent.sendToFront();
				ourName = new GLabel(ourCurrent.getName(), ourNameX, ourNameY);
				ourName.setFont("COURIER-15");
				ourName.setColor(Color.BLACK);
				add(ourName);
				
				theirName = new GLabel(theirCurrent.getName(), theirNameX, theirNameY);
				theirName.setFont("COURIER-15");
				theirName.setColor(Color.BLACK);
				add(theirName);
				usResetHP = true;
				theirResetHP = true;
			}
			if (usDefeated){
				
				// If we lose a battle, we must send out our next Pokemon.
				String line1;
				line1 = "Go "+ ourCurrent.getName() + "!";
				Line1 = new GLabel(line1, Line1X, Line1Y);
				Line1.setFont("COURIER-15");
				Line1.setColor(Color.BLACK);
				add(Line1);
				
				trainerSprite = ourPokemon;
				trainerSprite.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
				add(trainerSprite, BORDER,BORDER+GAME_HEIGHT-trainerSprite.getHeight()-textfield.getHeight());
				
				background2.sendToBack();
				opponentSprite.sendToBack();
				trainer.sendToBack();
				background.sendToBack();
				
				
				while(trainerSprite.getX()<75){
					trainerSprite.move(3, 0);
					pause(9);
				}
				pause(1000);
				remove(Line1);

				remove(background2);
				background2=new GImage("BattleScreen1.jpg");
				background2.setSize(GAME_WIDTH, GAME_HEIGHT);
				add(background2,BORDER,BORDER);
				trainerSprite.sendToFront();
				opponent.sendToFront();
				theirHPIndicator.sendToFront();
				theirMaxHPIndicator.sendToFront();
				theirName.sendToFront();
				ourName = new GLabel(ourCurrent.getName(), ourNameX, ourNameY);
				ourName.setFont("COURIER-15");
				ourName.setColor(Color.BLACK);
				add(ourName);
				usResetHP = true;
				usDefeated = false;
			}
			if(opponentDefeated){
				// If the opponent loses a battle, he/she must send out a new Pokemon.
				String line1 = elitefour.getName() +" sent out "+ theirCurrent.getName() + "!";
				Line1 = new GLabel(line1, Line1X, Line1Y);
				Line1.setFont("COURIER-15");
				Line1.setColor(Color.BLACK);
				add(Line1);
				
				opponent = opponentPokemon;
				opponent.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
				add(opponent, getWidth()-BORDER-opponent.getWidth(),275-224+20);
				
				background2.sendToBack();
				opponentSprite.sendToBack();
				trainer.sendToBack();
				background.sendToBack();
				
				while(opponent.getX()>239){
					opponent.move(-3,0);
					pause(9);
				}
				pause(1000);
				remove(Line1);
				ourHPIndicator.sendToFront();
				ourMaxHPIndicator.sendToFront();
				ourName.sendToFront();
				
				theirName = new GLabel(theirCurrent.getName(), theirNameX, theirNameY);
				theirName.setFont("COURIER-15");
				theirName.setColor(Color.BLACK);
				add(theirName);
				theirResetHP = true;
				opponentDefeated = false;
			}
			// Runs the battle with the current Pokemon in battle.
			runBattle("Sydney",ourCurrent, theirCurrent, ourPokemon, opponentPokemon);
			
			// Analyzes post battle conditions to determine further action.
			analyzeBattle();
		}
		
		// If we lose.
		if (ourPKMNLeft == 0){
			remove(opponent);
			remove(trainerSprite);
			remove(theirName);
			remove(ourName);
			remove(ourMaxHPIndicator);
			remove(ourHPIndicator);
			remove(theirMaxHPIndicator);
			remove(theirHPIndicator);
			opponentName = elitefour.getName();
			opponent = new GImage("SydneyTrainer.png");
			trainerSprite = new GImage("Trainer_Battle.png");
			opponent.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
			trainerSprite.setSize(OPPONENT_WIDTH,OPPONENT_HEIGHT);
			add(trainerSprite, getWidth()-BORDER-trainerSprite.getWidth(),BORDER+GAME_HEIGHT-trainerSprite.getHeight()-textfield.getHeight());
			add(opponent, BORDER+0,275-224+20);
			background2.sendToBack();
			opponentSprite.sendToBack();
			trainer.sendToBack();
			background.sendToBack();
			while(opponent.getX()<239){
				opponent.move(3,0);
				trainerSprite.move(-3, 0);
				pause(9);
			}
			
			Line1 = new GLabel("Already lost? That's disappointing.", Line1X, Line1Y);
			Line1.setFont("COURIER-15");
			Line1.setColor(Color.BLACK);
			add(Line1);
			
			Line2 = new GLabel("GAME OVER", Line2X, Line2Y);
			Line2.setFont("COURIER-15");
			Line2.setColor(Color.BLACK);
			add(Line2);
			advance = false;
			scene = -1;
			while(!advance){
				pause(1);
			}
		}
		
		// If we win.
		if (theirPKMNLeft == 0){
			remove(opponent);
			remove(trainerSprite);
			remove(theirName);
			remove(ourName);
			remove(ourMaxHPIndicator);
			remove(ourHPIndicator);
			remove(theirMaxHPIndicator);
			remove(theirHPIndicator);
			opponentName = elitefour.getName();
			opponent = new GImage("SydneyTrainer.png");
			trainerSprite = new GImage("Trainer_Battle.png");
			opponent.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
			trainerSprite.setSize(OPPONENT_WIDTH,OPPONENT_HEIGHT);
			add(trainerSprite, getWidth()-BORDER-trainerSprite.getWidth(),BORDER+GAME_HEIGHT-trainerSprite.getHeight()-textfield.getHeight());
			add(opponent, BORDER+0,275-224+20);
			background2.sendToBack();
			opponentSprite.sendToBack();
			trainer.sendToBack();
			background.sendToBack();
			while(opponent.getX()<239){
				opponent.move(3,0);
				trainerSprite.move(-3, 0);
				pause(9);
			}
			
			Line1 = new GLabel("You won the battle!", Line1X, Line1Y);
			Line1.setFont("COURIER-15");
			Line1.setColor(Color.BLACK);
			add(Line1);
			
			Line2 = new GLabel("Press A to continue on!", Line2X, Line2Y);
			Line2.setFont("COURIER-15");
			Line2.setColor(Color.BLACK);
			add(Line2);
			advance = false;
			scene = 0;
			while(!advance){
				pause(1);
			}
			remove(Line1);
			remove(Line2);
			scene = 2;
		}
	}
	
	// Runs our Battle between two pokemon.
	private void runBattle(String face, Pokemon ourCurrent, Pokemon theirCurrent, GImage ourPokemon, GImage opponentPokemon){
		// Gets the level of the EliteFour member we are facing.
		theirLevel = elitefour.getLevel();
		
		// Assigns statistics based on Pokemon's official formula.
		ourAttack = (2*ourCurrent.getAttack())*ourLevel/100+5;
		ourDefense = (2*ourCurrent.getDefense())*ourLevel/100+5;
		ourSpecialAttack = (2*ourCurrent.getSpAtt())*ourLevel/100+5;
		ourSpecialDefense = (2*ourCurrent.getSpDef())*ourLevel/100+5;
		ourSpeed = (2*ourCurrent.getSpeed())*ourLevel/100+5;
		
		// Resets our HP numbers if we just died
		if (usResetHP){
			// Again this is Pokemon's "formula" - I didn't make this up.
			ourHP= (2*ourCurrent.getHP())*ourLevel/100+10+ourLevel;
			ourMaxHP = (2*ourCurrent.getHP())*ourLevel/100+10+ourLevel;
			ourHPIndicator = new GLabel(""+ourHP, ourHPX, ourHPY);
			ourHPIndicator.setFont("MONOSPACED-12");
			ourHPIndicator.setColor(Color.BLACK);
			add(ourHPIndicator);
	
			ourMaxHPIndicator = new GLabel(""+ourMaxHP, ourMaxHPX, ourMaxHPY);
			ourMaxHPIndicator.setFont("MONOSPACED-12");
			ourMaxHPIndicator.setColor(Color.BLACK);
			add(ourMaxHPIndicator);
			usResetHP = false;
			ourHPLoss = 0;
			
			// We add the HP Indicators to keep track of HP in this game instead of
			// using the HP Bars. 
		}
		
		// Assigns the battle statistics for our enemy's Pokemon.
		theirAttack = (2*theirCurrent.getAttack())*theirLevel/100+5;
		theirDefense = (2*theirCurrent.getDefense())*theirLevel/100+5;
		theirSpecialAttack= (2*theirCurrent.getSpAtt())*theirLevel/100+5;
		theirSpecialDefense= (2*theirCurrent.getSpDef())*theirLevel/100+5;
		theirSpeed= (2*theirCurrent.getSpeed())*theirLevel/100+5;
		
		// resets our opponent's pokemon if they just died.
		if (theirResetHP){
			theirHP= (2*theirCurrent.getHP())*theirLevel/100+10+theirLevel;	
			theirMaxHP = (2*theirCurrent.getHP())*theirLevel/100+10+theirLevel;
			theirHPIndicator = new GLabel(""+theirHP, theirHPX, theirHPY);
			theirHPIndicator.setFont("MONOSPACED-12");
			theirHPIndicator.setColor(Color.BLACK);
			add(theirHPIndicator);
			
			theirMaxHPIndicator = new GLabel(""+theirMaxHP, theirMaxHPX, theirMaxHPY);
			theirMaxHPIndicator.setFont("MONOSPACED-12");
			theirMaxHPIndicator.setColor(Color.BLACK);
			add(theirMaxHPIndicator);
			theirResetHP = false;
			theirHPLoss = 0;
		}
		
		// Initializes our Moves so that we can select a move to use on the enemy.
		// theirMove is randomly chosen out of the opponent's move pool in BattleInterface();
		Move theirMove;
		Move ourMove;
		Move move1 = moves.findEntry(ourCurrent.getMove1());
		Move move2 = moves.findEntry(ourCurrent.getMove2());
		Move move3 = moves.findEntry(ourCurrent.getMove3());
		Move move4 = moves.findEntry(ourCurrent.getMove4());
		while (ourHP>0 && theirHP >0){
			scene = 4;
			theirMove=moves.findEntry(theirCurrent.chooseMove());
			BattleInterface(move1,move2,move3,move4,theirMove);	
		}
		
	}
	
	// Same as fightSydney() except Phoebe-specific files are put in place of Sydney's files.
	private void fightPhoebe(){
		elitefour = challengers.findEntry("Phoebe");
		us = challengers.findEntry("Trainer");
		ourPKMN1 = pokedex.findEntry(us.getFirstPokemon());
		ourPKMN2 = pokedex.findEntry(us.getSecondPokemon());
		ourPKMN3 = pokedex.findEntry(us.getThirdPokemon());
		ourPKMN4 = pokedex.findEntry(us.getFourthPokemon());
		ourPKMN5 = pokedex.findEntry(us.getFifthPokemon());
		theirPKMN1 = pokedex.findEntry(elitefour.getFirstPokemon());
		theirPKMN2 = pokedex.findEntry(elitefour.getSecondPokemon());
		theirPKMN3 = pokedex.findEntry(elitefour.getThirdPokemon());
		theirPKMN4 = pokedex.findEntry(elitefour.getFourthPokemon());
		theirPKMN5 = pokedex.findEntry(elitefour.getFifthPokemon());
		
		usDefeated = false;
		opponentDefeated = false;
		String opponentName = elitefour.getName();
		opponent = new GImage("PhoebeTrainer.png");
		trainerSprite = new GImage("Trainer_Battle.png");
		opponent.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
		trainerSprite.setSize(OPPONENT_WIDTH,OPPONENT_HEIGHT);
		add(trainerSprite, getWidth()-BORDER-trainerSprite.getWidth(),BORDER+GAME_HEIGHT-trainerSprite.getHeight()-textfield.getHeight());
		add(opponent, BORDER+0,275-224+20);
		background2.sendToBack();
		opponentSprite.sendToBack();
		trainer.sendToBack();
		background.sendToBack();
		while(opponent.getX()<239){
			opponent.move(3,0);
			trainerSprite.move(-3, 0);
			pause(9);
		}
		scene = 0;
		pause(500);
		Line1 = new GLabel("You have been challenged", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("by Elite Four "+ opponentName + "!", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		scene = -1;
		while(opponent.getX()<GAME_WIDTH-BORDER-opponent.getWidth()){
			opponent.move(4,0);
			pause(4);
		}
		while(trainerSprite.getX()>BORDER){
			trainerSprite.move(-4,0);
			pause(4);
		}
		remove(opponent);
		remove(trainerSprite);
		theirPKMNLeft=5;
		ourPKMNLeft=5;
		theirResetHP = false;
		usResetHP = false;
		while (ourPKMNLeft>0 && theirPKMNLeft >0){
			if (theirPKMNLeft == 5 && ourPKMNLeft == 5){
				theirCurrent = theirPKMN1;
				opponentPokemon = new GImage("Dusclops.png");
				ourCurrent = ourPKMN1;
				ourPokemon = new GImage("Blaziken.png");
			}

			if (theirPKMNLeft ==4){
				theirCurrent = theirPKMN2; 
				opponentPokemon = new GImage("Banette.png");
			} else if (theirPKMNLeft ==3){
				theirCurrent = theirPKMN3; 
				opponentPokemon = new GImage("Sableye.png");
			} else if (theirPKMNLeft ==2){
				theirCurrent = theirPKMN4; 
				opponentPokemon = new GImage("Banette.png");
			} else if (theirPKMNLeft ==1){
				theirCurrent = theirPKMN5; 
				opponentPokemon = new GImage("Dusclops.png");
			}
			
			if (ourPKMNLeft ==4){
				ourCurrent = ourPKMN2; 
				ourPokemon = new GImage("Swampert.png");
			} else if (ourPKMNLeft ==3){
				ourCurrent = ourPKMN3;
				ourPokemon = new GImage("Sceptile.png");
			} else if (ourPKMNLeft ==2){
				ourCurrent = ourPKMN4;
				ourPokemon = new GImage("Rayquaza.png");
			} else if (ourPKMNLeft ==1){
				ourCurrent = ourPKMN5; 
				ourPokemon = new GImage("Raichu.png");
			}
			if (!usDefeated && !opponentDefeated){
				String line1 = elitefour.getName() +" sent out "+ theirCurrent.getName() + "!";
				String line2;
				Line1 = new GLabel(line1, Line1X, Line1Y);
				Line1.setFont("COURIER-15");
				Line1.setColor(Color.BLACK);
				add(Line1);
				
				opponent = opponentPokemon;
				opponent.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
				add(opponent, getWidth()-BORDER-opponent.getWidth(),275-224+20);
				background2.sendToBack();
				opponentSprite.sendToBack();
				trainer.sendToBack();
				background.sendToBack();
				while(opponent.getX()>239){
					opponent.move(-3,0);
					pause(9);
				}
				pause(1000);
				remove(Line1);
				
				line1 = "Go "+ ourCurrent.getName() + "!";
				Line1 = new GLabel(line1, Line1X, Line1Y);
				Line1.setFont("COURIER-15");
				Line1.setColor(Color.BLACK);
				add(Line1);
				
				trainerSprite = ourPokemon;
				trainerSprite.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
				add(trainerSprite, BORDER,BORDER+GAME_HEIGHT-trainerSprite.getHeight()-textfield.getHeight());
				
				while(trainerSprite.getX()<75){
					trainerSprite.move(3, 0);
					pause(9);
				}
				pause(1000);
				remove(Line1);

				remove(background2);
				background2=new GImage("BattleScreen1.jpg");
				background2.setSize(GAME_WIDTH, GAME_HEIGHT);
				add(background2,BORDER,BORDER);
				trainerSprite.sendToFront();
				opponent.sendToFront();
				ourName = new GLabel(ourCurrent.getName(), ourNameX, ourNameY);
				ourName.setFont("COURIER-15");
				ourName.setColor(Color.BLACK);
				add(ourName);
				
				theirName = new GLabel(theirCurrent.getName(), theirNameX, theirNameY);
				theirName.setFont("COURIER-15");
				theirName.setColor(Color.BLACK);
				add(theirName);
				usResetHP = true;
				theirResetHP = true;
			}
			if (usDefeated){
				String line1;
				line1 = "Go "+ ourCurrent.getName() + "!";
				Line1 = new GLabel(line1, Line1X, Line1Y);
				Line1.setFont("COURIER-15");
				Line1.setColor(Color.BLACK);
				add(Line1);
				
				trainerSprite = ourPokemon;
				trainerSprite.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
				add(trainerSprite, BORDER,BORDER+GAME_HEIGHT-trainerSprite.getHeight()-textfield.getHeight());
				
				background2.sendToBack();
				opponentSprite.sendToBack();
				trainer.sendToBack();
				background.sendToBack();
				
				
				while(trainerSprite.getX()<75){
					trainerSprite.move(3, 0);
					pause(9);
				}
				pause(1000);
				remove(Line1);

				remove(background2);
				background2=new GImage("BattleScreen1.jpg");
				background2.setSize(GAME_WIDTH, GAME_HEIGHT);
				add(background2,BORDER,BORDER);
				trainerSprite.sendToFront();
				opponent.sendToFront();
				theirHPIndicator.sendToFront();
				theirMaxHPIndicator.sendToFront();
				theirName.sendToFront();
				ourName = new GLabel(ourCurrent.getName(), ourNameX, ourNameY);
				ourName.setFont("COURIER-15");
				ourName.setColor(Color.BLACK);
				add(ourName);
				usResetHP = true;
				usDefeated = false;
				theirResetHP = false;
				opponentDefeated=false;
			}
			if(opponentDefeated){
				String line1 = elitefour.getName() +" sent out "+ theirCurrent.getName() + "!";
				Line1 = new GLabel(line1, Line1X, Line1Y);
				Line1.setFont("COURIER-15");
				Line1.setColor(Color.BLACK);
				add(Line1);
				
				opponent = opponentPokemon;
				opponent.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
				add(opponent, getWidth()-BORDER-opponent.getWidth(),275-224+20);
				
				background2.sendToBack();
				opponentSprite.sendToBack();
				trainer.sendToBack();
				background.sendToBack();
				
				while(opponent.getX()>239){
					opponent.move(-3,0);
					pause(9);
				}
				pause(1000);
				remove(Line1);
				ourHPIndicator.sendToFront();
				ourMaxHPIndicator.sendToFront();
				ourName.sendToFront();
				
				theirName = new GLabel(theirCurrent.getName(), theirNameX, theirNameY);
				theirName.setFont("COURIER-15");
				theirName.setColor(Color.BLACK);
				add(theirName);
				theirResetHP = true;
				opponentDefeated = false;
				usDefeated = false;
				usResetHP = false;
			}
			runBattle("Phoebe",ourCurrent, theirCurrent, ourPokemon, opponentPokemon);
			analyzeBattle();
		}
		
		if (ourPKMNLeft == 0){
			remove(opponent);
			remove(trainerSprite);
			remove(theirName);
			remove(ourName);
			remove(ourMaxHPIndicator);
			remove(ourHPIndicator);
			remove(theirMaxHPIndicator);
			remove(theirHPIndicator);
			opponentName = elitefour.getName();
			opponent = new GImage("PhoebeTrainer.png");
			trainerSprite = new GImage("Trainer_Battle.png");
			opponent.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
			trainerSprite.setSize(OPPONENT_WIDTH,OPPONENT_HEIGHT);
			add(trainerSprite, getWidth()-BORDER-trainerSprite.getWidth(),BORDER+GAME_HEIGHT-trainerSprite.getHeight()-textfield.getHeight());
			add(opponent, BORDER+0,275-224+20);
			background2.sendToBack();
			opponentSprite.sendToBack();
			trainer.sendToBack();
			background.sendToBack();
			while(opponent.getX()<239){
				opponent.move(3,0);
				trainerSprite.move(-3, 0);
				pause(9);
			}
			
			Line1 = new GLabel("Darn... You weren't tough.", Line1X, Line1Y);
			Line1.setFont("COURIER-15");
			Line1.setColor(Color.BLACK);
			add(Line1);
			
			Line2 = new GLabel("GAME OVER", Line2X, Line2Y);
			Line2.setFont("COURIER-15");
			Line2.setColor(Color.BLACK);
			add(Line2);
			advance = false;
			scene = -1;
			while(!advance){
				pause(1);
			}
		}
		if (theirPKMNLeft == 0){
			remove(opponent);
			remove(trainerSprite);
			remove(theirName);
			remove(ourName);
			remove(ourMaxHPIndicator);
			remove(ourHPIndicator);
			remove(theirMaxHPIndicator);
			remove(theirHPIndicator);
			opponentName = elitefour.getName();
			opponent = new GImage("PhoebeTrainer.png");
			trainerSprite = new GImage("Trainer_Battle.png");
			opponent.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
			trainerSprite.setSize(OPPONENT_WIDTH,OPPONENT_HEIGHT);
			add(trainerSprite, getWidth()-BORDER-trainerSprite.getWidth(),BORDER+GAME_HEIGHT-trainerSprite.getHeight()-textfield.getHeight());
			add(opponent, BORDER+0,275-224+20);
			background2.sendToBack();
			opponentSprite.sendToBack();
			trainer.sendToBack();
			background.sendToBack();
			while(opponent.getX()<239){
				opponent.move(3,0);
				trainerSprite.move(-3, 0);
				pause(9);
			}
			
			Line1 = new GLabel("You won the battle!", Line1X, Line1Y);
			Line1.setFont("COURIER-15");
			Line1.setColor(Color.BLACK);
			add(Line1);
			
			Line2 = new GLabel("Press A to continue on!", Line2X, Line2Y);
			Line2.setFont("COURIER-15");
			Line2.setColor(Color.BLACK);
			add(Line2);
			advance = false;
			scene = 0;
			while(!advance){
				pause(1);
			}
			remove(Line1);
			remove(Line2);
			scene = 2;
		}
	}
	
	// Same as fightSydney() except Glacia-specific files are put in place of Sydney's files.
	private void fightGlacia(){
		elitefour = challengers.findEntry("Glacia");
		us = challengers.findEntry("Trainer");
		usDefeated = false;
		opponentDefeated = false;
		ourPKMN1 = pokedex.findEntry(us.getFirstPokemon());
		ourPKMN2 = pokedex.findEntry(us.getSecondPokemon());
		ourPKMN3 = pokedex.findEntry(us.getThirdPokemon());
		ourPKMN4 = pokedex.findEntry(us.getFourthPokemon());
		ourPKMN5 = pokedex.findEntry(us.getFifthPokemon());
		theirPKMN1 = pokedex.findEntry(elitefour.getFirstPokemon());
		theirPKMN2 = pokedex.findEntry(elitefour.getSecondPokemon());
		theirPKMN3 = pokedex.findEntry(elitefour.getThirdPokemon());
		theirPKMN4 = pokedex.findEntry(elitefour.getFourthPokemon());
		theirPKMN5 = pokedex.findEntry(elitefour.getFifthPokemon());
		
		String opponentName = elitefour.getName();
		opponent = new GImage("GlaciaTrainer.png");
		trainerSprite = new GImage("Trainer_Battle.png");
		opponent.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
		trainerSprite.setSize(OPPONENT_WIDTH,OPPONENT_HEIGHT);
		add(trainerSprite, getWidth()-BORDER-trainerSprite.getWidth(),BORDER+GAME_HEIGHT-trainerSprite.getHeight()-textfield.getHeight());
		add(opponent, BORDER+0,275-224+20);
		background2.sendToBack();
		opponentSprite.sendToBack();
		trainer.sendToBack();
		background.sendToBack();
		while(opponent.getX()<239){
			opponent.move(3,0);
			trainerSprite.move(-3, 0);
			pause(9);
		}
		scene = 0;
		pause(500);
		Line1 = new GLabel("You have been challenged", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("by Elite Four "+ opponentName + "!", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		scene = -1;
		while(opponent.getX()<GAME_WIDTH-BORDER-opponent.getWidth()){
			opponent.move(4,0);
			pause(4);
		}
		while(trainerSprite.getX()>BORDER){
			trainerSprite.move(-4,0);
			pause(4);
		}
		remove(opponent);
		remove(trainerSprite);
		theirPKMNLeft=5;
		ourPKMNLeft=5;
		theirResetHP = false;
		usResetHP = false;
		while (ourPKMNLeft>0 && theirPKMNLeft >0){
			if (theirPKMNLeft == 5 && ourPKMNLeft == 5){
				theirCurrent = theirPKMN1;
				opponentPokemon = new GImage("Glalie.png");
				ourCurrent = ourPKMN1;
				ourPokemon = new GImage("Blaziken.png");
			}

			if (theirPKMNLeft ==4){
				theirCurrent = theirPKMN2; 
				opponentPokemon = new GImage("Sealeo.png");
			} else if (theirPKMNLeft ==3){
				theirCurrent = theirPKMN3; 
				opponentPokemon = new GImage("Sealeo.png");
			} else if (theirPKMNLeft ==2){
				theirCurrent = theirPKMN4; 
				opponentPokemon = new GImage("Glalie.png");
			} else if (theirPKMNLeft ==1){
				theirCurrent = theirPKMN5; 
				opponentPokemon = new GImage("Walrein.png");
			}
			
			if (ourPKMNLeft ==4){
				ourCurrent = ourPKMN2; 
				ourPokemon = new GImage("Swampert.png");
			} else if (ourPKMNLeft ==3){
				ourCurrent = ourPKMN3;
				ourPokemon = new GImage("Sceptile.png");
			} else if (ourPKMNLeft ==2){
				ourCurrent = ourPKMN4;
				ourPokemon = new GImage("Rayquaza.png");
			} else if (ourPKMNLeft ==1){
				ourCurrent = ourPKMN5; 
				ourPokemon = new GImage("Raichu.png");
			}
			if (!usDefeated && !opponentDefeated){
				String line1 = elitefour.getName() +" sent out "+ theirCurrent.getName() + "!";
				String line2;
				Line1 = new GLabel(line1, Line1X, Line1Y);
				Line1.setFont("COURIER-15");
				Line1.setColor(Color.BLACK);
				add(Line1);
				
				opponent = opponentPokemon;
				opponent.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
				add(opponent, getWidth()-BORDER-opponent.getWidth(),275-224+20);
				background2.sendToBack();
				opponentSprite.sendToBack();
				trainer.sendToBack();
				background.sendToBack();
				while(opponent.getX()>239){
					opponent.move(-3,0);
					pause(9);
				}
				pause(1000);
				remove(Line1);
				
				line1 = "Go "+ ourCurrent.getName() + "!";
				Line1 = new GLabel(line1, Line1X, Line1Y);
				Line1.setFont("COURIER-15");
				Line1.setColor(Color.BLACK);
				add(Line1);
				
				trainerSprite = ourPokemon;
				trainerSprite.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
				add(trainerSprite, BORDER,BORDER+GAME_HEIGHT-trainerSprite.getHeight()-textfield.getHeight());
				
				while(trainerSprite.getX()<75){
					trainerSprite.move(3, 0);
					pause(9);
				}
				pause(1000);
				remove(Line1);

				remove(background2);
				background2=new GImage("BattleScreen1.jpg");
				background2.setSize(GAME_WIDTH, GAME_HEIGHT);
				add(background2,BORDER,BORDER);
				trainerSprite.sendToFront();
				opponent.sendToFront();
				ourName = new GLabel(ourCurrent.getName(), ourNameX, ourNameY);
				ourName.setFont("COURIER-15");
				ourName.setColor(Color.BLACK);
				add(ourName);
				
				theirName = new GLabel(theirCurrent.getName(), theirNameX, theirNameY);
				theirName.setFont("COURIER-15");
				theirName.setColor(Color.BLACK);
				add(theirName);
				usResetHP = true;
				theirResetHP = true;
			}
			if (usDefeated){
				String line1;
				line1 = "Go "+ ourCurrent.getName() + "!";
				Line1 = new GLabel(line1, Line1X, Line1Y);
				Line1.setFont("COURIER-15");
				Line1.setColor(Color.BLACK);
				add(Line1);
				
				trainerSprite = ourPokemon;
				trainerSprite.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
				add(trainerSprite, BORDER,BORDER+GAME_HEIGHT-trainerSprite.getHeight()-textfield.getHeight());
				
				background2.sendToBack();
				opponentSprite.sendToBack();
				trainer.sendToBack();
				background.sendToBack();
				
				
				while(trainerSprite.getX()<75){
					trainerSprite.move(3, 0);
					pause(9);
				}
				pause(1000);
				remove(Line1);

				remove(background2);
				background2=new GImage("BattleScreen1.jpg");
				background2.setSize(GAME_WIDTH, GAME_HEIGHT);
				add(background2,BORDER,BORDER);
				trainerSprite.sendToFront();
				opponent.sendToFront();
				theirHPIndicator.sendToFront();
				theirMaxHPIndicator.sendToFront();
				theirName.sendToFront();
				ourName = new GLabel(ourCurrent.getName(), ourNameX, ourNameY);
				ourName.setFont("COURIER-15");
				ourName.setColor(Color.BLACK);
				add(ourName);
				usResetHP = true;
				usDefeated = false;
			}
			if(opponentDefeated){
				String line1 = elitefour.getName() +" sent out "+ theirCurrent.getName() + "!";
				Line1 = new GLabel(line1, Line1X, Line1Y);
				Line1.setFont("COURIER-15");
				Line1.setColor(Color.BLACK);
				add(Line1);
				
				opponent = opponentPokemon;
				opponent.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
				add(opponent, getWidth()-BORDER-opponent.getWidth(),275-224+20);
				
				background2.sendToBack();
				opponentSprite.sendToBack();
				trainer.sendToBack();
				background.sendToBack();
				
				while(opponent.getX()>239){
					opponent.move(-3,0);
					pause(9);
				}
				pause(1000);
				remove(Line1);
				ourHPIndicator.sendToFront();
				ourMaxHPIndicator.sendToFront();
				ourName.sendToFront();
				
				theirName = new GLabel(theirCurrent.getName(), theirNameX, theirNameY);
				theirName.setFont("COURIER-15");
				theirName.setColor(Color.BLACK);
				add(theirName);
				theirResetHP = true;
				opponentDefeated = false;
			}
			runBattle("Glacia",ourCurrent, theirCurrent, ourPokemon, opponentPokemon);
			analyzeBattle();
		}
		if (ourPKMNLeft == 0){
			remove(opponent);
			remove(trainerSprite);
			remove(theirName);
			remove(ourName);
			remove(ourMaxHPIndicator);
			remove(ourHPIndicator);
			remove(theirMaxHPIndicator);
			remove(theirHPIndicator);
			opponentName = elitefour.getName();
			opponent = new GImage("GlaciaTrainer.png");
			trainerSprite = new GImage("Trainer_Battle.png");
			opponent.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
			trainerSprite.setSize(OPPONENT_WIDTH,OPPONENT_HEIGHT);
			add(trainerSprite, getWidth()-BORDER-trainerSprite.getWidth(),BORDER+GAME_HEIGHT-trainerSprite.getHeight()-textfield.getHeight());
			add(opponent, BORDER+0,275-224+20);
			background2.sendToBack();
			opponentSprite.sendToBack();
			trainer.sendToBack();
			background.sendToBack();
			while(opponent.getX()<239){
				opponent.move(3,0);
				trainerSprite.move(-3, 0);
				pause(9);
			}
			
			Line1 = new GLabel("Come back next time, kid.", Line1X, Line1Y);
			Line1.setFont("COURIER-15");
			Line1.setColor(Color.BLACK);
			add(Line1);
			
			Line2 = new GLabel("GAME OVER", Line2X, Line2Y);
			Line2.setFont("COURIER-15");
			Line2.setColor(Color.BLACK);
			add(Line2);
			advance = false;
			scene = -1;
			while(!advance){
				pause(1);
			}
		}
		if (theirPKMNLeft == 0){
			remove(opponent);
			remove(trainerSprite);
			remove(theirName);
			remove(ourName);
			remove(ourMaxHPIndicator);
			remove(ourHPIndicator);
			remove(theirMaxHPIndicator);
			remove(theirHPIndicator);
			opponentName = elitefour.getName();
			opponent = new GImage("GlaciaTrainer.png");
			trainerSprite = new GImage("Trainer_Battle.png");
			opponent.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
			trainerSprite.setSize(OPPONENT_WIDTH,OPPONENT_HEIGHT);
			add(trainerSprite, getWidth()-BORDER-trainerSprite.getWidth(),BORDER+GAME_HEIGHT-trainerSprite.getHeight()-textfield.getHeight());
			add(opponent, BORDER+0,275-224+20);
			background2.sendToBack();
			opponentSprite.sendToBack();
			trainer.sendToBack();
			background.sendToBack();
			while(opponent.getX()<239){
				opponent.move(3,0);
				trainerSprite.move(-3, 0);
				pause(9);
			}
			
			Line1 = new GLabel("You won the battle!", Line1X, Line1Y);
			Line1.setFont("COURIER-15");
			Line1.setColor(Color.BLACK);
			add(Line1);
			
			Line2 = new GLabel("Press A to continue on!", Line2X, Line2Y);
			Line2.setFont("COURIER-15");
			Line2.setColor(Color.BLACK);
			add(Line2);
			advance = false;
			scene = 0;
			while(!advance){
				pause(1);
			}
			remove(Line1);
			remove(Line2);
			scene = 2;
		}
	}
	
	// Same as fightSydney() except Drake-specific files are put in place of Sydney's files.
	private void fightDrake(){
		elitefour = challengers.findEntry("Drake");
		us = challengers.findEntry("Trainer");
		usDefeated = false;
		opponentDefeated = false;
		ourPKMN1 = pokedex.findEntry(us.getFirstPokemon());
		ourPKMN2 = pokedex.findEntry(us.getSecondPokemon());
		ourPKMN3 = pokedex.findEntry(us.getThirdPokemon());
		ourPKMN4 = pokedex.findEntry(us.getFourthPokemon());
		ourPKMN5 = pokedex.findEntry(us.getFifthPokemon());
		theirPKMN1 = pokedex.findEntry(elitefour.getFirstPokemon());
		theirPKMN2 = pokedex.findEntry(elitefour.getSecondPokemon());
		theirPKMN3 = pokedex.findEntry(elitefour.getThirdPokemon());
		theirPKMN4 = pokedex.findEntry(elitefour.getFourthPokemon());
		theirPKMN5 = pokedex.findEntry(elitefour.getFifthPokemon());
		
		String opponentName = elitefour.getName();
		opponent = new GImage("DrakeTrainer.png");
		trainerSprite = new GImage("Trainer_Battle.png");
		opponent.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
		trainerSprite.setSize(OPPONENT_WIDTH,OPPONENT_HEIGHT);
		add(trainerSprite, getWidth()-BORDER-trainerSprite.getWidth(),BORDER+GAME_HEIGHT-trainerSprite.getHeight()-textfield.getHeight());
		add(opponent, BORDER+0,275-224+20);
		background2.sendToBack();
		opponentSprite.sendToBack();
		trainer.sendToBack();
		background.sendToBack();
		while(opponent.getX()<239){
			opponent.move(3,0);
			trainerSprite.move(-3, 0);
			pause(9);
		}
		scene = 0;
		pause(500);
		Line1 = new GLabel("You have been challenged", Line1X, Line1Y);
		Line1.setFont("COURIER-15");
		Line1.setColor(Color.BLACK);
		add(Line1);
		
		Line2 = new GLabel("by Elite Four "+ opponentName + "!", Line2X, Line2Y);
		Line2.setFont("COURIER-15");
		Line2.setColor(Color.BLACK);
		add(Line2);
		advance = false;
		while(!advance){
			pause(1);
		}
		remove(Line1);
		remove(Line2);
		
		scene = -1;
		while(opponent.getX()<GAME_WIDTH-BORDER-opponent.getWidth()){
			opponent.move(4,0);
			pause(4);
		}
		while(trainerSprite.getX()>BORDER){
			trainerSprite.move(-4,0);
			pause(4);
		}
		remove(opponent);
		remove(trainerSprite);
		theirPKMNLeft=5;
		ourPKMNLeft=5;
		theirResetHP = false;
		usResetHP = false;
		while (ourPKMNLeft>0 && theirPKMNLeft >0){
			if (theirPKMNLeft == 5 && ourPKMNLeft == 5){
				theirCurrent = theirPKMN1;
				opponentPokemon = new GImage("Shelgon.png");
				ourCurrent = ourPKMN1;
				ourPokemon = new GImage("Blaziken.png");
			}

			if (theirPKMNLeft ==4){
				theirCurrent = theirPKMN2; 
				opponentPokemon = new GImage("Altaria.png");
			} else if (theirPKMNLeft ==3){
				theirCurrent = theirPKMN3; 
				opponentPokemon = new GImage("Flygon.png");
			} else if (theirPKMNLeft ==2){
				theirCurrent = theirPKMN4; 
				opponentPokemon = new GImage("Flygon.png");
			} else if (theirPKMNLeft ==1){
				theirCurrent = theirPKMN5; 
				opponentPokemon = new GImage("Salamence.png");
			}
			
			if (ourPKMNLeft ==4){
				ourCurrent = ourPKMN2; 
				ourPokemon = new GImage("Swampert.png");
			} else if (ourPKMNLeft ==3){
				ourCurrent = ourPKMN3;
				ourPokemon = new GImage("Sceptile.png");
			} else if (ourPKMNLeft ==2){
				ourCurrent = ourPKMN4;
				ourPokemon = new GImage("Rayquaza.png");
			} else if (ourPKMNLeft ==1){
				ourCurrent = ourPKMN5; 
				ourPokemon = new GImage("Raichu.png");
			}
			if (!usDefeated && !opponentDefeated){
				String line1 = elitefour.getName() +" sent out "+ theirCurrent.getName() + "!";
				String line2;
				Line1 = new GLabel(line1, Line1X, Line1Y);
				Line1.setFont("COURIER-15");
				Line1.setColor(Color.BLACK);
				add(Line1);
				
				opponent = opponentPokemon;
				opponent.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
				add(opponent, getWidth()-BORDER-opponent.getWidth(),275-224+20);
				background2.sendToBack();
				opponentSprite.sendToBack();
				trainer.sendToBack();
				background.sendToBack();
				while(opponent.getX()>239){
					opponent.move(-3,0);
					pause(9);
				}
				pause(1000);
				remove(Line1);
				
				line1 = "Go "+ ourCurrent.getName() + "!";
				Line1 = new GLabel(line1, Line1X, Line1Y);
				Line1.setFont("COURIER-15");
				Line1.setColor(Color.BLACK);
				add(Line1);
				
				trainerSprite = ourPokemon;
				trainerSprite.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
				add(trainerSprite, BORDER,BORDER+GAME_HEIGHT-trainerSprite.getHeight()-textfield.getHeight());
				
				while(trainerSprite.getX()<75){
					trainerSprite.move(3, 0);
					pause(9);
				}
				pause(1000);
				remove(Line1);

				remove(background2);
				background2=new GImage("BattleScreen1.jpg");
				background2.setSize(GAME_WIDTH, GAME_HEIGHT);
				add(background2,BORDER,BORDER);
				trainerSprite.sendToFront();
				opponent.sendToFront();
				ourName = new GLabel(ourCurrent.getName(), ourNameX, ourNameY);
				ourName.setFont("COURIER-15");
				ourName.setColor(Color.BLACK);
				add(ourName);
				
				theirName = new GLabel(theirCurrent.getName(), theirNameX, theirNameY);
				theirName.setFont("COURIER-15");
				theirName.setColor(Color.BLACK);
				add(theirName);
				usResetHP = true;
				theirResetHP = true;
			}
			if (usDefeated){
				String line1;
				line1 = "Go "+ ourCurrent.getName() + "!";
				Line1 = new GLabel(line1, Line1X, Line1Y);
				Line1.setFont("COURIER-15");
				Line1.setColor(Color.BLACK);
				add(Line1);
				
				trainerSprite = ourPokemon;
				trainerSprite.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
				add(trainerSprite, BORDER,BORDER+GAME_HEIGHT-trainerSprite.getHeight()-textfield.getHeight());
				
				background2.sendToBack();
				opponentSprite.sendToBack();
				trainer.sendToBack();
				background.sendToBack();
				
				
				while(trainerSprite.getX()<75){
					trainerSprite.move(3, 0);
					pause(9);
				}
				pause(1000);
				remove(Line1);

				remove(background2);
				background2=new GImage("BattleScreen1.jpg");
				background2.setSize(GAME_WIDTH, GAME_HEIGHT);
				add(background2,BORDER,BORDER);
				trainerSprite.sendToFront();
				opponent.sendToFront();
				theirHPIndicator.sendToFront();
				theirMaxHPIndicator.sendToFront();
				theirName.sendToFront();
				ourName = new GLabel(ourCurrent.getName(), ourNameX, ourNameY);
				ourName.setFont("COURIER-15");
				ourName.setColor(Color.BLACK);
				add(ourName);
				usResetHP = true;
				usDefeated = false;
			}
			if(opponentDefeated){
				String line1 = elitefour.getName() +" sent out "+ theirCurrent.getName() + "!";
				Line1 = new GLabel(line1, Line1X, Line1Y);
				Line1.setFont("COURIER-15");
				Line1.setColor(Color.BLACK);
				add(Line1);
				
				opponent = opponentPokemon;
				opponent.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
				add(opponent, getWidth()-BORDER-opponent.getWidth(),275-224+20);
				
				background2.sendToBack();
				opponentSprite.sendToBack();
				trainer.sendToBack();
				background.sendToBack();
				
				while(opponent.getX()>239){
					opponent.move(-3,0);
					pause(9);
				}
				pause(1000);
				remove(Line1);
				ourHPIndicator.sendToFront();
				ourMaxHPIndicator.sendToFront();
				ourName.sendToFront();
				
				theirName = new GLabel(theirCurrent.getName(), theirNameX, theirNameY);
				theirName.setFont("COURIER-15");
				theirName.setColor(Color.BLACK);
				add(theirName);
				theirResetHP = true;
				opponentDefeated = false;
			}
			runBattle("Drake",ourCurrent, theirCurrent, ourPokemon, opponentPokemon);
			analyzeBattle();
		}
		if (ourPKMNLeft == 0){
			remove(opponent);
			remove(trainerSprite);
			remove(theirName);
			remove(ourName);
			remove(ourMaxHPIndicator);
			remove(ourHPIndicator);
			remove(theirMaxHPIndicator);
			remove(theirHPIndicator);
			opponentName = elitefour.getName();
			opponent = new GImage("DrakeTrainer.png");
			trainerSprite = new GImage("Trainer_Battle.png");
			opponent.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
			trainerSprite.setSize(OPPONENT_WIDTH,OPPONENT_HEIGHT);
			add(trainerSprite, getWidth()-BORDER-trainerSprite.getWidth(),BORDER+GAME_HEIGHT-trainerSprite.getHeight()-textfield.getHeight());
			add(opponent, BORDER+0,275-224+20);
			background2.sendToBack();
			opponentSprite.sendToBack();
			trainer.sendToBack();
			background.sendToBack();
			while(opponent.getX()<239){
				opponent.move(3,0);
				trainerSprite.move(-3, 0);
				pause(9);
			}
			
			Line1 = new GLabel("So close... Yet so far.", Line1X, Line1Y);
			Line1.setFont("COURIER-15");
			Line1.setColor(Color.BLACK);
			add(Line1);
			
			Line2 = new GLabel("GAME OVER", Line2X, Line2Y);
			Line2.setFont("COURIER-15");
			Line2.setColor(Color.BLACK);
			add(Line2);
			advance = false;
			scene = -1;
			while(!advance){
				pause(1);
			}
		}
		if (theirPKMNLeft == 0){
			remove(opponent);
			remove(trainerSprite);
			remove(theirName);
			remove(ourName);
			remove(ourMaxHPIndicator);
			remove(ourHPIndicator);
			remove(theirMaxHPIndicator);
			remove(theirHPIndicator);
			opponentName = elitefour.getName();
			opponent = new GImage("DrakeTrainer.png");
			trainerSprite = new GImage("Trainer_Battle.png");
			opponent.setSize(OPPONENT_WIDTH, OPPONENT_HEIGHT);
			trainerSprite.setSize(OPPONENT_WIDTH,OPPONENT_HEIGHT);
			add(trainerSprite, getWidth()-BORDER-trainerSprite.getWidth(),BORDER+GAME_HEIGHT-trainerSprite.getHeight()-textfield.getHeight());
			add(opponent, BORDER+0,275-224+20);
			background2.sendToBack();
			opponentSprite.sendToBack();
			trainer.sendToBack();
			background.sendToBack();
			while(opponent.getX()<239){
				opponent.move(3,0);
				trainerSprite.move(-3, 0);
				pause(9);
			}
			
			Line1 = new GLabel("You won the battle!", Line1X, Line1Y);
			Line1.setFont("COURIER-15");
			Line1.setColor(Color.BLACK);
			add(Line1);
			
			Line2 = new GLabel("Press A to continue on!", Line2X, Line2Y);
			Line2.setFont("COURIER-15");
			Line2.setColor(Color.BLACK);
			add(Line2);
			advance = false;
			scene = 0;
			while(!advance){
				pause(1);
			}
			remove(Line1);
			remove(Line2);
			scene = 2;
		}
	}
	
	/* BattleInterface()
	 * Most important "method" of this game.
	 * Determines the move order, the effects of moves, and end results of each Turn in battle.
	 */
	private void BattleInterface(Move move1, Move move2, Move move3, Move move4, Move theirMove){
		// Allow the user to select a move to use this turn.
		selector = new GRect(selector1X, selector1Y, 128,24);
		selector.setFilled(true);
		selector.setFillColor(Color.YELLOW);
		add(selector);
		Move1 = new GLabel(move1.getName(), Line1X, Line1Y);
		Move1.setFont("COURIER-15");
		Move1.setColor(Color.BLACK);
		add(Move1);
		
		Move2 = new GLabel(move2.getName(), Line2X, Line2Y);
		Move2.setFont("COURIER-15");
		Move2.setColor(Color.BLACK);
		add(Move2);
		
		Move3 = new GLabel(move3.getName(), Line3X, Line3Y);
		Move3.setFont("COURIER-15");
		Move3.setColor(Color.BLACK);
		add(Move3);
		
		Move4 = new GLabel(move4.getName(), Line4X, Line4Y);
		Move4.setFont("COURIER-15");
		Move4.setColor(Color.BLACK);
		add(Move4);
		chosenMove=1;
		advance=false;
		while(!advance){
			pause(1);
		}
		
		// Once the move is chosen, we remove the move selections to make way for Battle Text.
		remove(Move1);
		remove(Move2);
		remove(Move3);
		remove(Move4);
		remove(selector);
		Move ourMove;
		
		// Determines which move was selected by the user.
		if (chosenMove==1){
			ourMove=move1;
		} else if (chosenMove==2){
			ourMove=move2;
		} else if (chosenMove==3){
			ourMove=move3;
		} else {
			ourMove=move4;
		}
		
		// If our Pokemon is faster than their Pokemon, we move first.
		if (ourCurrent.getSpeed() >= theirCurrent.getSpeed()){
			// Battle Text saying our Pokemon just did a move.
			LineA = ourCurrent.getName() + " used "+ ourMove.getName() + "!";
			Line1 = new GLabel(LineA, Line1X, Line1Y);
			Line1.setFont("COURIER-15");
			Line1.setColor(Color.BLACK);
			add(Line1);
			pause(1000);
			
			// Determines if the Pokemon's move just hit its target or not.
			accuracy = ourMove.getAccuracy();
			if(rgen.nextInt(0,100)<=accuracy){
				// If the move hits, we must figure out if it is a Physical or Special move.
				if (ourMove.getCategory().equals("PHYSICAL")){
					
					// if it is physical, we use our Attack Stat, and the opponent's Defense Stat, to determine the power.
					theirHPLoss += ((((2.0*ourLevel/5.0+2.0)*ourAttack*ourMove.getPower())/(double) theirDefense)/(255-ourLevel/10.0));
					remove(theirHPIndicator);
					
					// revisedHP is used to indicate how much HP the opponent has left after the move.
					int revisedHP = theirMaxHP - theirHPLoss;
					if (revisedHP <= 0){
						revisedHP = 0;
						opponentDefeated=true;
					}
					
					// update the opponent's HP Indicator with revisedHP's new value.
					theirHPIndicator = new GLabel(""+(revisedHP), theirHPX, theirHPY);
					theirHPIndicator.setFont("MONOSPACED-12");
					theirHPIndicator.setColor(Color.BLACK);
					add(theirHPIndicator);
	
				} else {
					// If the opponent used a Special move, we use this formula, which uses Special Attack, Special Defense, and our attack's power.
					theirHPLoss += ((((2.0*ourLevel/5.0+2.0)*ourSpecialAttack*ourMove.getPower())/(double) theirSpecialDefense)/(255-ourLevel/10.0));
					remove(theirHPIndicator);
					int revisedHP = theirMaxHP - theirHPLoss;
					if (revisedHP <= 0){
						revisedHP = 0;
						opponentDefeated=true;
					}
					theirHPIndicator = new GLabel(""+(revisedHP), theirHPX, theirHPY);
					theirHPIndicator.setFont("MONOSPACED-12");
					theirHPIndicator.setColor(Color.BLACK);
					add(theirHPIndicator);	
				}
				// Adjust theirHP to account for the end change in HP in the opponent's pokemon.
				theirHP= theirMaxHP - theirHPLoss;
				
				// Flicker Animation indicating damage was dealt.
				remove(opponent);
				pause(250);
				add(opponent);
				
			} else {
				
				// If the user misses, we just say he missed and move on.
				remove(Line1);
				LineA = "It missed!";
				Line1 = new GLabel(LineA, Line1X, Line1Y);
				Line1.setFont("COURIER-15");
				Line1.setColor(Color.BLACK);
				add(Line1);
				theirHPLoss+=0;
			}
			
			// Waits for user to click to continue.
			// You will see the following 5 lines of code often, but everytime they appear,
			// they are just a way of waiting for the user to click.
			scene=0;
			advance = false;
			while(!advance){
				pause(1);
			}
			remove(Line1);
			
			// If the opponent hasn't died from the last move, it gets a shot at making a move on our Pokemon.
			if (!opponentDefeated){
				
				// Same mechanics as when we moved, except the statistics are flipped so that we use our opponent's statistics, not our own, to calculate damage.
				LineA = theirCurrent.getName() + " used "+ theirMove.getName() + "!";
				Line1 = new GLabel(LineA, Line1X, Line1Y);
				Line1.setFont("COURIER-15");
				Line1.setColor(Color.BLACK);
				add(Line1);
				pause(1000);
				accuracy = theirMove.getAccuracy();
				if(rgen.nextInt(0,100)<=accuracy){
					if (theirMove.getCategory().equals("PHYSICAL")){
						ourHPLoss += ((((2.0*theirLevel/5.0+2.0)*theirAttack*theirMove.getPower())/(double) ourDefense)/(255-theirLevel/10.0));
						remove(ourHPIndicator);
						int revisedHP = ourMaxHP - ourHPLoss;
						
						if (revisedHP <= 0){
							revisedHP = 0;
							usDefeated=true;
						}
						ourHPIndicator = new GLabel(""+(revisedHP), ourHPX, ourHPY);
						ourHPIndicator.setFont("MONOSPACED-12");
						ourHPIndicator.setColor(Color.BLACK);
						add(ourHPIndicator);
		
					} else {
						ourHPLoss += ((((2.0*theirLevel/5.0+2.0)*theirSpecialAttack*theirMove.getPower())/(double) ourSpecialDefense)/(255-theirLevel/10.0));
						remove(ourHPIndicator);
						int revisedHP = ourMaxHP - ourHPLoss;

						if (revisedHP <= 0){
							revisedHP = 0;
							usDefeated=true;
						}
						
						ourHPIndicator = new GLabel(""+(revisedHP), ourHPX, ourHPY);
						ourHPIndicator.setFont("MONOSPACED-12");
						ourHPIndicator.setColor(Color.BLACK);
						add(ourHPIndicator);	
					}
					ourHP= ourMaxHP - ourHPLoss;
					remove(trainerSprite);
					pause(250);
					add(trainerSprite);
				} else {
					remove(Line1);
					LineA = "It missed!";
					Line1 = new GLabel(LineA, Line1X, Line1Y);
					Line1.setFont("COURIER-15");
					Line1.setColor(Color.BLACK);
					add(Line1);
					ourHPLoss+=0;
				}
				scene=0;
				advance = false;
				while(!advance){
					pause(1);
				}
				remove(Line1);
			
				// If the opponent isn't alive, it must be "fainted".
			} else {
				// Use Battle text to tell the user the opponent has fainted.
				LineA = theirCurrent.getName() + " has fainted!";
				Line1 = new GLabel(LineA, Line1X, Line1Y);
				Line1.setFont("COURIER-15");
				Line1.setColor(Color.BLACK);
				add(Line1);
				
				// Animation to move the opponent's sprite off the screen.
				while(opponent.getX() < getWidth()-BORDER-opponent.getWidth()){
					opponent.move(4,0);
					pause(2);
				}
				remove(opponent);
				remove(theirHPIndicator);
				remove(theirMaxHPIndicator);
				remove(theirName);
				
				// removes all of the informatino related to the previous pokemon off the screen.
				advance = false;
				while(!advance){
					pause(1);
				}
				remove(Line1);
			}
			
			// If the opponent's move ends up destroying our Pokemon, we do the same script but for our Pokemon.
			if(usDefeated){
				LineA = ourCurrent.getName() + " has fainted!";
				Line1 = new GLabel(LineA, Line1X, Line1Y);
				Line1.setFont("COURIER-15");
				Line1.setColor(Color.BLACK);
				add(Line1);
				while(trainerSprite.getX() > BORDER){
					trainerSprite.move(-4,0);
					pause(2);
				}
				remove(trainerSprite);	
				remove(ourHPIndicator);
				remove(ourMaxHPIndicator);
				remove(ourName);
				
				advance = false;
				while(!advance){
					pause(1);
				}
				remove(Line1);
			}
		
			
			// This code is run if the Opponent's Pokemon is faster than our Pokemon. In
			// this case, we have the opponent move first, and we move second. (We ignore all
			// of the above code) up until when we checked for which Pokemon is faster.
		} else {
			LineA = theirCurrent.getName() + " used "+ theirMove.getName() + "!";
			Line1 = new GLabel(LineA, Line1X, Line1Y);
			Line1.setFont("COURIER-15");
			Line1.setColor(Color.BLACK);
			add(Line1);
			pause(1000);
			accuracy = theirMove.getAccuracy();
			if(rgen.nextInt(0,100)<=accuracy){
				if (theirMove.getCategory().equals("PHYSICAL")){
					ourHPLoss += ((((2.0*theirLevel/5.0+2.0)*theirAttack*theirMove.getPower())/(double) ourDefense)/(255-theirLevel/10.0));
					remove(ourHPIndicator);
					int revisedHP = ourMaxHP - ourHPLoss;
					if (revisedHP <= 0){
						revisedHP = 0;
						usDefeated=true;
					}
					ourHPIndicator = new GLabel(""+(revisedHP), ourHPX, ourHPY);
					ourHPIndicator.setFont("MONOSPACED-12");
					ourHPIndicator.setColor(Color.BLACK);
					add(ourHPIndicator);
	
				} else {
					ourHPLoss += ((((2.0*theirLevel/5.0+2.0)*theirSpecialAttack*theirMove.getPower())/(double) ourSpecialDefense)/(255-theirLevel/10.0));
					remove(ourHPIndicator);
					int revisedHP = ourMaxHP - ourHPLoss;
					if (revisedHP <= 0){
						revisedHP = 0;
						usDefeated=true;
					}
					ourHPIndicator = new GLabel(""+(revisedHP), ourHPX, ourHPY);
					ourHPIndicator.setFont("MONOSPACED-12");
					ourHPIndicator.setColor(Color.BLACK);
					add(ourHPIndicator);
				}
				ourHP= ourMaxHP - ourHPLoss;
				remove(trainerSprite);
				pause(250);
				add(trainerSprite);
			} else {
				remove(Line1);
				LineA = "It missed!";
				Line1 = new GLabel(LineA, Line1X, Line1Y);
				Line1.setFont("COURIER-15");
				Line1.setColor(Color.BLACK);
				add(Line1);
				ourHPLoss+=0;
			}
			
			scene=0;
			advance = false;
			while(!advance){
				pause(1);
			}
			remove(Line1);
			
			if (!usDefeated){
				LineA = ourCurrent.getName() + " used "+ ourMove.getName() + "!";
				Line1 = new GLabel(LineA, Line1X, Line1Y);
				Line1.setFont("COURIER-15");
				Line1.setColor(Color.BLACK);
				add(Line1);
				pause(1000);
				accuracy = ourMove.getAccuracy();
				if(rgen.nextInt(0,100)<=accuracy){
					if (ourMove.getCategory().equals("PHYSICAL")){
						theirHPLoss += ((((2.0*ourLevel/5.0+2.0)*ourAttack*ourMove.getPower())/(double) theirDefense)/(255-ourLevel/10.0));
						remove(theirHPIndicator);
						int revisedHP = theirMaxHP - theirHPLoss;
						
						if (revisedHP <= 0){
							revisedHP = 0;
							opponentDefeated=true;
						}
						theirHPIndicator = new GLabel(""+(revisedHP), theirHPX, theirHPY);
						theirHPIndicator.setFont("MONOSPACED-12");
						theirHPIndicator.setColor(Color.BLACK);
						add(theirHPIndicator);
		
					} else {
						theirHPLoss += ((((2.0*ourLevel/5.0+2.0)*ourSpecialAttack*ourMove.getPower())/(double) theirSpecialDefense)/(255-ourLevel/10.0));
						remove(theirHPIndicator);
						int revisedHP = theirMaxHP - theirHPLoss;
						
						if (revisedHP <= 0){
							revisedHP = 0;
							opponentDefeated=true;
						}
						theirHPIndicator = new GLabel(""+(revisedHP), theirHPX, theirHPY);
						theirHPIndicator.setFont("MONOSPACED-12");
						theirHPIndicator.setColor(Color.BLACK);
						add(theirHPIndicator);	
					}
					theirHP= theirMaxHP - theirHPLoss;
					remove(opponent);
					pause(250);
					add(opponent);
				} else {
					remove(Line1);
					LineA = "It missed!";
					Line1 = new GLabel(LineA, Line1X, Line1Y);
					Line1.setFont("COURIER-15");
					Line1.setColor(Color.BLACK);
					add(Line1);
					theirHPLoss+=0;
				}
				scene=0;
				advance = false;
				while(!advance){
					pause(1);
				}
				remove(Line1);
			} else {
				LineA = ourCurrent.getName() + " has fainted!";
				Line1 = new GLabel(LineA, Line1X, Line1Y);
				Line1.setFont("COURIER-15");
				Line1.setColor(Color.BLACK);
				add(Line1);
				while(trainerSprite.getX() > BORDER){
					trainerSprite.move(-4,0);
					pause(2);
				}
				remove(trainerSprite);	
				remove(ourHPIndicator);
				remove(ourMaxHPIndicator);
				remove(ourName);
				
				advance = false;
				while(!advance){
					pause(1);
				}
				remove(Line1);
			}
			
			if(opponentDefeated){
				LineA = theirCurrent.getName() + " has fainted!";
				Line1 = new GLabel(LineA, Line1X, Line1Y);
				Line1.setFont("COURIER-15");
				Line1.setColor(Color.BLACK);
				add(Line1);
				while(opponent.getX() < getWidth()-BORDER-opponent.getWidth()){
					opponent.move(4,0);
					pause(2);
				}
				remove(opponent);
				remove(theirHPIndicator);
				remove(theirMaxHPIndicator);
				remove(theirName);
				advance = false;
				while(!advance){
					pause(1);
				}
				remove(Line1);
			}
		}
	}
	
	// Analyze Battles by reducing the number of Pokemon left for the person who just
	// lost a pokemon.
	private void analyzeBattle(){
		if(usDefeated){
			ourPKMNLeft--;
		} else {
			theirPKMNLeft--;
		}
	}
}
