import acm.util.*;
import acm.program.*;
import acm.graphics.*;
import acm.io.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

// Class that defines a Pokemon.
// Class Constants: Stats, Moves, Graphics, Sound.
public class Pokemon {
	
	// Private Instance Variables
	private String name;
	private String type1;
	private String type2;
	
	// Four moves for each Pokemon
	private String move1;
	private String move2;
	private String move3;
	private String move4;
	
	// Pokemon Stats stored in array: [hp]  [Attack]  [Defense]  [SpAtt]  [SpDef]  [Speed]
	private int[] statistics = new int[6];
	
	// Random generator used to determine accuracy. 
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	/* Constructor */
	public Pokemon (String line){
		
		// Find name of Pokemon from data file
		int nameEnd = line.indexOf(" ");
		name = line.substring(0,nameEnd);
		
		// Parse the rest of the data to determine Pokemon's stats
		String data = line.substring(nameEnd+1);
		StringTokenizer tokenizer = new StringTokenizer(data);
		int count = 0;
		type1=tokenizer.nextToken();
		type2=tokenizer.nextToken();
		move1=tokenizer.nextToken();
		move2=tokenizer.nextToken();
		move3=tokenizer.nextToken();
		move4=tokenizer.nextToken();
		
		while (tokenizer.hasMoreTokens()){	
			statistics[count] = Integer.parseInt(tokenizer.nextToken());
			count++;
			if (count == 6) break;
		}
	}
	
	public String getName(){
		return name;
	}
	
	public int getHP(){
		return statistics[0];
	}
	
	public int editHP(int x, int y){
		x+=y;
		return x;
	}
	
	public int getAttack(){
		return statistics[1];
	}
	
	public int getDefense(){
		return statistics[2];
	}
	
	public int getSpAtt(){
		return statistics[3];
	}
	
	public int getSpDef(){
		return statistics[4];
	}
	
	public int getSpeed(){
		return statistics[5];
	}
	
	public String getType1(){
		return type1;
	}
	
	public String getType2(){
		return type2;
	}
	
	public String getMove1(){
		return move1;
	}
	
	public String getMove2(){
		return move2;
	}
	
	public String getMove3(){
		return move3;
	}
	
	public String getMove4(){
		return move4;
	}
	
	public String chooseMove(){
		int x = rgen.nextInt(0,3);
		if (x == 0){
			return move1;
		} else if (x == 1){
			return move2;
		} else if (x == 2){
			return move3;
		} else {
			return move4;
		}
	}
	
	public boolean isDead(){
		if(statistics[0] <= 0){
			return true;
		} else{
			return false;
		}
	}
}

