/* Elite Class: Defines the opponents the player faces in the game. */
import java.util.*;

public class Elite {
	
	private String name;	// Name of Opponent
	private int level;	// Level of Pokemon (How strong they are)
	private String pkmn1;	// First Pokemon
	private String pkmn2;	// Second Pokemon
	private String pkmn3;	// Third Pokemon
	private String pkmn4;	// Fourth Pokemon
	private String pkmn5;	// Fifth Pokemon
	
	/* We run a string through the constructor to define our Elite. Each line comes from the Elite-Four-Pokemon.txt file */
	public Elite (String line){
		int nameEnd = line.indexOf(" ");
		name = line.substring(0,nameEnd);
		String data = line.substring(nameEnd+1);
		StringTokenizer tokenizer = new StringTokenizer(data);
		level = Integer.parseInt(tokenizer.nextToken());
		pkmn1=tokenizer.nextToken();
		pkmn2=tokenizer.nextToken();
		pkmn3=tokenizer.nextToken();
		pkmn4=tokenizer.nextToken();
		pkmn5=tokenizer.nextToken();
	}
	
	/* Getter Functions! */
	
	public String getName(){
		return name;
	}
	
	public int getLevel(){
		return level;
	}
	
	public String getFirstPokemon(){
		return pkmn1;
	}
	
	public String getSecondPokemon(){
		return pkmn2;
	}
	
	public String getThirdPokemon(){
		return pkmn3;
	}
	
	public String getFourthPokemon(){
		return pkmn4;
	}
	
	public String getFifthPokemon(){
		return pkmn5;
	}
}
