/* File: Move.java
 * Author: Kevin Khieu
 * Defines the behavior of a Pokemon move. */

import java.util.*;

public class Move {
	String name;	// Move Name
	String type1;	// Move Primary Type
	String type2;	// Move Secondary Type
	int power;	// Strength of Move
	int accuracy;	// Accuracy of Move
	
	// We pass in a line read from Pokemon-Moves.txt to parse the details corresponding to this move.
	public Move (String line){
		int nameEnd = line.indexOf(" ");
		name = line.substring(0,nameEnd);
		String data = line.substring(nameEnd+1);
		StringTokenizer tokenizer = new StringTokenizer(data);
		type1=tokenizer.nextToken();
		type2=tokenizer.nextToken();
		power=Integer.parseInt(tokenizer.nextToken());
		accuracy=Integer.parseInt(tokenizer.nextToken());
	}
	
	/* Getter Functions! */
	
	public String getName(){
		return name;
	}
	
	public String getType(){
		return type1;
	}
	
	public String getCategory(){
		return type2;
	}
	
	public int getPower(){
		return power;
	}
	
	public int getAccuracy(){
		return accuracy;
	}
}
