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

PokemonArena Class
-handles all the actual pokemon battle 'stuff'
-You don't really win the game, you try to get as high of a score as possible

>>>Features
-Starter packs
-Gold and xp system
-Levels of difficulty
-Randomized opponenets 
-Rewards for winning
-Custom messages for each attack type
-Custom messages for events (such as when a pokemon takes burning damage)

Method Overview
-choiceMenu : helps authenticate user input
-fight : method that handles the whole battle (users alternate attacking each other until someone loses)
-aftermath : handles post battle actions and rewards (based on performancce in battle)
-userSwap : authenticates user pokemon swapping (no swapping to a fainted pokemon etc)
-compSwap: helps the computer swap pokemon
-checkFaint :checks to see if pokemon is fainted (overloaded to check lists of pokemon)
*/

import java.util.*;
class PokemonArena {
	public static Scanner usin = new Scanner(System.in);
	public static Random rnd = new Random();

	public static int USER = 0;
	public static int COMP = 1;

	public static void main(String[]args) {
		Pokemon.init("pokemon_data.txt"); //load everything from file
	
		//WELCOME MESSAGE
		System.out.println("    _   ___  ___ ___ ___ __  __  ___  _  _ \n   /_\\ / __|/ __|_ _|_ _|  \\/  |/ _ \\| \\| |\n  / _ \\\\__ \\ (__ | | | || |\\/| | (_) | .` |\n /_/ \\_\\___/\\___|___|___|_|  |_|\\___/|_|\\_|");
		System.out.println(" Build Version Alpha 1	Created by Daniel Liu\n");

		System.out.print(" Trainer, welcome to ASCIIMON Arena! What should I call you? ");
		String username = usin.nextLine();

		//ADD ABILITY FOR THE USER TO CHOOSE A SAVE FILE
		//choose a starter pack
		System.out.println("\n"+username+", choose a starter pack:\n 1 Fire (Charmander,Vulpix,Growlithe)\n 2 Water (Squirtle,Psyduck,Poliwag)\n 3 Grass (Bulbasaur,Oddish,Bellsprout)\n 4 DEVELOPER MODE");
		int choice = choiceMenu(4);
		String[] starterPack = new String[]{"Charmander","Vulpix","Growlithe"}; //default choice is the firepack
		if (choice == 1) starterPack = new String[]{"Charmander","Vulpix","Growlithe"}; //ADD FULL STARTER PACK LATER
		if (choice == 2) starterPack = new String[]{"Squirtle","Psyduck","Poliwag"};
		if (choice == 3) starterPack = new String[]{"Bulbasaur","Oddish","Bellsprout"};
		if (choice == 4) { //use any pokemon (for testing purposes)
			System.out.println("\nDEVELOPER MODE: ENTER ANY POKEMON NAME (CASE-SENSITIVE)");
			usin.nextLine();
			starterPack = new String[]{usin.nextLine()};
		}
		User user = new User(username,starterPack); //create new user with starting pokemon

		System.out.println("Great choice! Let's get started, shall we?");
		
		//Trainer presets
		String[] bronzePrefixes = new String[]{"","Black Belt","Trainer","Pokemon Ranger","Youngster","Rich","Team Rocket","Hiker","Worker"};
		String[] bronzeNames = new String[]{"Sam","Andrew","Alex","Molly","Kenny","Mary","Louis","Anna","Samuel","Chance","Phillip","Andy","Ulfric","Johan","Rocky","Jerry","Maki","Jon"}; //these names start with a bronze prefix 
		String[] silverNames = new String[]{"Brock","Misty","Lt. Surge","Erika","Koga","Sabrina","Blaine","Geovanni"}; //these names start with 'gym leader'
		String[] goldNames = new String[]{"Lorelei","Bruno","Agatha","Lance","Dwight Schrute","Bot Seth","Ash Ketchum"}; //theese names start with 'elite'
		String championName = "Blue";

		while (true) { //THE GAME NEVER ENDS..... 								...unless you lose
			System.out.println("\n____________________________________________");
			//generate random opponent
			//User pick difficulty
			String compName = "";
			ArrayList<String> bag = new ArrayList<String>(); //comp's pokemon
			System.out.println("Choose a difficulty\n 1 Bronze (Pokemon Trainers)\n 2 Silver (Gym Leaders)\n 3 Gold (Elite)");
			int difficulty = choiceMenu(4);

			if (difficulty == 1) { //BRONZE
				//generate a name
				String prefix = bronzePrefixes[rnd.nextInt(bronzePrefixes.length)];
				String name = bronzeNames[rnd.nextInt(bronzeNames.length)];
				compName = prefix+" "+name;
				//generate pokemons
				while (bag.size() < 2) { //bronze enemies only have 2 pokemons
					Pokemon rndPoke = new Pokemon(Pokemon.rndPokemon()); //get a random pokemon
					if (rndPoke.baseLevel == 1 && bag.contains(rndPoke.name) == false) { //only weak pokemon are added (make sure there are no duplicates)
						bag.add(rndPoke.name);
					}
				}
				
			} else if (difficulty == 2) { //SILVER
				//generate a name
				String name = silverNames[rnd.nextInt(silverNames.length)];
				compName = "Gym Leader "+name;
				//generate pokemons
				while (bag.size() < 3) { //silver enemies have 3 pokemon
					Pokemon rndPoke = new Pokemon(Pokemon.rndPokemon()); //get a random pokemon
					if (rndPoke.baseLevel <= 15 && bag.contains(rndPoke.name) == false) { //only kinda weak pokemon are added
						bag.add(rndPoke.name);
					}
				}

			} else if (difficulty == 3) { //GOLD
				//generate a name
				String name = goldNames[rnd.nextInt(goldNames.length)];
				compName = "Elite "+name;
				//generate pokemons
				while (bag.size() < 4) { //gold enemies have 4 pokemon
					Pokemon rndPoke = new Pokemon(Pokemon.rndPokemon()); //get a random pokemon
					if (bag.contains(rndPoke.name) == false) { //all pokemons are added
						bag.add(rndPoke.name);
					}
				}
			}

			String[] compBag = new String[bag.size()]; //convert bag arraylist to array
			compBag = bag.toArray(compBag);
			
			User comp = new User(compName,compBag);
			System.out.println("\n____________________________________________"); //display some cool text
			System.out.printf("===== You are challenged by %s! =====\n\n",comp.username);
			int winner = fight(user,comp); //all of the fighting code
			//Determine outcome of battle
			if (winner == USER) { //if user wons
				System.out.println("\n____________________________________________\n===== You emerge VICTORIOUS =====");
				aftermath(user,difficulty); //give user rewards based on difficulty
			} else if (winner == COMP) { //if comp won
				System.out.println("\n____________________________________________\n===== You emerge DEFEATED =====");
				System.out.printf(" Trainer %s was finally defeated after taking down %d trainers!\n",user.username,user.wins);
				break;

			} else {
				System.out.println("hmm, something went wrong with the battle (no winner?)");
			}
		}
	
	}

	public static int choiceMenu(int numChoices) { //makes sure user input is valid
		while (true) {
			int choice = usin.nextInt();
			if (choice >= 1 && choice <= numChoices) { //if choice is valid
				return choice;
			}
			System.out.println("Invalid Input, please try again...");
		}
	}

	public static int fight(User user, User comp) {
		//CONSTANTS
		int USERTURN = USER;
		int COMPTURN = COMP;

		Pokemon userActive; //the user's current active pokemon
		Pokemon compActive; //the computers' current active pokemon

		//RESET POKEMONS
		for (Pokemon p : user.pokemons) { //for each pokemon in user's bag
			for (String c : p.conditions.keySet()) { //for each condition the pokemon has
				if (c != "Fainted") p.conditions.remove(c); //only fainted cannot be removed
			}
		}

		//SETUP
		int STARTER = rnd.nextInt(2); //decide who goes first (0 is user, 1 is comp)
		int turn = STARTER; 
		int winner = -1; //determines who won the game (0 is user, 1 is comp)
		
		//Select starting pokemon
		userActive = userSwap(user); //set the pokemon the user chose as their active pokemon
		System.out.println("____________________________________________");
		compActive = compSwap(comp); //set the pokemon the computer chose as their active pokemon
		System.out.println("____________________________________________");

		//GAME LOOP	
		while (true) {

			for (int t = 0; t < 2; t++) { //One round

				//Player's Turn
				if (turn == USERTURN) {
					System.out.printf("Your %s has %d hp and %d energy left.\n",userActive.name.toUpperCase(),userActive.pokedata()[0],userActive.pokedata()[1]); //show some info about current pokemon
					System.out.printf("What would you like %s to do?\n 1 Attack\n 2 Retreat\n 3 Pass\n",userActive.name); 
					int move = choiceMenu(3);
					if (move == 1) { //ATTACK
						System.out.printf("Which attack should %s use?\n",userActive.name);
						
						for (int i = 0; i < userActive.attacks.size(); i++) { //for each attack the pokemon has
							System.out.printf(" %d %s ",i+1,userActive.attacks.get(i).name); //display choice menu
							System.out.print(userActive.attacks.get(i)+"\n");
						}
						int attackChoice = choiceMenu(userActive.attacks.size());
						Pokemon.Attack curAttack = userActive.attacks.get(attackChoice-1); //the attack the user chose

						//Use the attack
						System.out.printf("Your %s used %s!\n",userActive.name.toUpperCase(),curAttack.name.toUpperCase());
						System.out.println(userActive.attack(curAttack.name,compActive));
						compActive.condition(); //apply active conditions to comp player

						//Check to see if pokemon was knocked out
						if (checkFaint(compActive)) {
							System.out.printf("%s's %s has fainted!\n",comp.username,compActive.name.toUpperCase()); //tell the user that they knocked out a pokemon
							if (checkFaint(comp.pokemons)) { //Check to see if all of comp's pokemons are knocked out
								//YOU WIN 
								winner = USERTURN;
								return winner;
							}
							//have comp pick another pokemon
							compActive = compSwap(comp);
						}		
						
					} else if (move == 2) { //SWITCH POKEMON
						if (userActive.conditions.keySet().contains("Flinching")) { //pokemon cannot retreat if it is flinching (stunned)
							System.out.printf("%s may not retreat, it is currently FLINCHING!\n",userActive.name);
						} else {
							userActive = userSwap(user); //user picks a new pokemon
						}

					} else if (move == 3) { //SKIP TURN
						System.out.println("You decided to skip your turn!");
					}
					System.out.println("____________________________________________");
					turn = COMPTURN; //switch turns
				} 

				//Computer's Turn
				else if (turn == COMPTURN) {
					System.out.printf("%s's %s has %d hp and %d energy left.\n",comp.username,compActive.name.toUpperCase(),compActive.pokedata()[0],compActive.pokedata()[1]);
					//ATTACK - computer picks a random, valid move to use
					ArrayList<Pokemon.Attack> possibleMoves = new ArrayList<Pokemon.Attack>(); //list of moves the computer can use
					
					for (int i = 0; i < compActive.attacks.size(); i++) {
						if (compActive.enoughEnergy(compActive.attacks.get(i))) { //if the comp can use the move
							possibleMoves.add(compActive.attacks.get(i));
						}
					}
					if (possibleMoves.size() > 0) { //If the pokemon can use an attack
						int attackChoice = rnd.nextInt(possibleMoves.size()); //choose a random attack
						Pokemon.Attack curAttack = possibleMoves.get(attackChoice);
						System.out.printf("%s's %s used %s!\n",comp.username,compActive.name.toUpperCase(),curAttack.name.toUpperCase());
						System.out.println(compActive.attack(curAttack.name,userActive)); //use attack
						userActive.condition(); //apply active conditions to user

						//Check to see if pokemon was knocked out
						if (checkFaint(userActive)) {
							System.out.printf("Your %s has fainted!\n",userActive.name.toUpperCase()); //tell the user that their pokemon was knocked out
							if (checkFaint(user.pokemons)) { //Check to see if all of user's pokemons are knocked out
								//YOU LOSE
								winner = COMPTURN;
								return winner;
							}			
							userActive = userSwap(user);//otherwise tell user to switch pokemon				
						}

					} else { //OTHERWISE, the computer will pass or retreat
						if (compActive.pokedata()[0] < Math.round(compActive.maxHp/2) && compActive.conditions.keySet().contains("Flinching") == false) { //if the comp's pokemon has less than half health, comp will retreat
							compActive = compSwap(comp); 
							//MAKE SURE COMP DOESNT RETREAT AND SEND OUT THE SAME POKEMON
						} else {
							System.out.printf("%s decided to skip their turn!\n",comp.username);
						}
					}

					System.out.println("____________________________________________"); //divide turns
					turn = USERTURN; //switch turns
				}
			}

			//END OF ROUND UPDATES

			//All pokemon recover 10 pp
			for (Pokemon userP : user.pokemons) {
				userP.modEnergy(10);
			}
			for (Pokemon compP : comp.pokemons) {
				compP.modEnergy(10);
			}			
		}
	}

	public static void aftermath(User user, int difficulty) { //resets pokemons and give rewards to player depending on outcome of battle
		//Rewards
		//CALCULATE BONUSES AND REWARDS
		int deltaGold = 200; //you automatically get 200 gold for winning
		int deltaXp = 500; //each pokemon automatically get 500 xp for winning
		System.out.println("-=-=- BONUSES -=-=-");
		System.out.println(" Victorious        +200 gold  +500 xp");
		if (checkFaint(user.pokemons) == false) { //if no pokemons are dead (BROKEN RIGHT NOW)
			deltaGold += 300;
			System.out.println(" Flawless Match    +300 gold  +500 xp");
		}
		//REWARDS BASED ON DIFFICULTY
		if (difficulty == 1) { //BRONZE
			deltaGold += 100; deltaXp += 100;
			System.out.println(" Bronze Match      +100 gold  +100 xp");
		} else if (difficulty == 2) { //SILVER 
			deltaGold += 300; deltaXp += 300;
			System.out.println(" Silver Match      +300 gold  +300 xp");
		} else if (difficulty == 3) { //GOLD
			deltaGold += 500; deltaXp += 500;
			System.out.println(" Gold Match        +500 gold  +500 xp");
		}
		System.out.println("\n-=-=- SUMMARY -=-=-");
		user.modGold(deltaGold); 
		user.wins ++;
		System.out.printf(" Gold: %d\n Xp: %d\n Wins: +1\n",deltaGold,deltaXp);

		//Each of the user's pokemons get healed 80Hp
		for (Pokemon userP : user.pokemons) { 
			userP.modHp(80);
		}
		System.out.println("\nAll of your Pokemon recovered 80 HP!");
	}

	public static Pokemon userSwap(User user) { //user switches pokemon
		for (int p = 0; p < user.pokemons.size(); p++) { //for each pokemon in user's bag
			System.out.println("Choose a Pokemon to use:");
			for (int i = 0; i < user.pokemons.size(); i++) {
				System.out.printf(" %d %s\n",i+1,user.pokemons.get(i).name); //prints a table showing all the pokemon in user's bag
			}
			int choice = choiceMenu(user.pokemons.size());
			Pokemon chosenOne = user.pokemons.get(choice-1); //the chosen pokemon
			if (checkFaint(chosenOne) == false) { //make sure pokemon isnt knocked out
				System.out.printf("%s, I choose you!\n",chosenOne.name);	
				return chosenOne; //retrun the chosen pokemon
			}
			System.out.println("This Pokemon has fainted... Please choose another.");
		}
		return new Pokemon(Pokemon.newPokemon("Bulbasaur")); //return null pokemon
	}

	public static Pokemon compSwap(User comp) { //comp switches pokemon
		ArrayList<Integer> choiceList = new ArrayList<Integer>(); //list of pokemons the comp will attempt to pick
		for (int i = 0; i < comp.pokemons.size(); i++) {  //for each pokemon in comp's bag
			choiceList.add(i);
		}
		Collections.shuffle(choiceList); //randomize the order the comp will choose its pokemon
		for (int p : choiceList) {
			Pokemon chosenOne = comp.pokemons.get(p);
			if (checkFaint(chosenOne) == false) { //make sure pokemon isnt knocked out
				System.out.printf("%s sent out %s!\n",comp.username,chosenOne.name);
				return chosenOne;
			}
		}
		return new Pokemon(Pokemon.newPokemon("Bulbasaur")); //return null pokemon
	}

	public static Boolean checkFaint(Pokemon pokemon) { //check to see if a pokemon has fainted
		if (pokemon.conditions.containsKey("Fainted")) { //if user's pokemon has fainted
			return true;
		} 
		return false;
	} public static Boolean checkFaint(ArrayList<Pokemon> pokemon) { //overloaded to check to see if all pokemon in array has fainted
		for (Pokemon p : pokemon) {
			if (checkFaint(p) == false) return false; //ALL POKEMON NEED TO BE FAINTED TO RETURN TRUE
		}
		return true;
	}
}