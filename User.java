/*
    _   ___  ___ ___ ___ __  __  ___  _  _ 
   /_\ / __|/ __|_ _|_ _|  \/  |/ _ \| \| |
  / _ \\__ \ (__ | | | || |\/| | (_) | .` |
 /_/ \_\___/\___|___|___|_|  |_|\___/|_|\_|
Created by Daniel Liu
 _  _  ____   __   ____  ____  ____     ___  __   _  _  _  _  ____  __ _  ____  ____ 
/ )( \(  __) / _\ (    \(  __)(  _ \   / __)/  \ ( \/ )( \/ )(  __)(  ( \(_  _)/ ___)
) __ ( ) _) /    \ ) D ( ) _)  )   /  ( (__(  O )/ \/ \/ \/ \ ) _) /    /  )(  \___ \
\_)(_/(____)\_/\_/(____/(____)(__\_)   \___)\__/ \_)(_/\_)(_/(____)\_)__) (__) (____/ go here

User Class
-Handles player info, such as # of wins and pokemons

*/
import java.util.*;
class User {

	public final String username;
	private int gold; //amount of money user has
	public int wins; //amount of victories the user has
	public ArrayList<Pokemon> pokemons; //list of the user's pokemons

	public User(String username, String[] pokemonList) {
		this.username = username;
		this.gold = 0;
		this.wins = 0;
		this.pokemons = new ArrayList<Pokemon>();

		for (int i = 0; i < pokemonList.length; i++) { //gets the first four pokemon from the string inputted
			if (i >= 4) break; 
			pokemons.add(new Pokemon(Pokemon.newPokemon(pokemonList[i]))); //create new pokemon object and add to user's bag
		}
	}


	public void modGold(int deltaGold) { //add or subtract money from user's bank account
		if (deltaGold > 0) { //if the user is gaining gold
			this.gold += deltaGold;
			//System.out.printf("You earned $%d!\n",deltaGold);
		} else if (deltaGold < 0) { //if the user is spending/losing gold
			this.gold += deltaGold;
			//System.out.printf("$%d was removed from your account.\n",Math.abs(deltaGold));
		}
		if (this.gold < 0) { //gold cannot be negative
			this.gold = 0;
		}
	}

}