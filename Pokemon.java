//****ASCIIMON**** COPYWRITED IM THE FIRST ONE TO USE THIS NAME 21/11/2018
/*

    _   ___  ___ ___ ___ __  __  ___  _  _ 
   /_\ / __|/ __|_ _|_ _|  \/  |/ _ \| \| |
  / _ \\__ \ (__ | | | || |\/| | (_) | .` |
 /_/ \_\___/\___|___|___|_|  |_|\___/|_|\_|

_____/\\\\\\\\\________/\\\\\\\\\\\__________/\\\\\\\\\__/\\\\\\\\\\\__/\\\\\\\\\\\__/\\\\____________/\\\\_______/\\\\\_______/\\\\\_____/\\\_        
 ___/\\\\\\\\\\\\\____/\\\/////////\\\_____/\\\////////__\/////\\\///__\/////\\\///__\/\\\\\\________/\\\\\\_____/\\\///\\\____\/\\\\\\___\/\\\_       
  __/\\\/////////\\\__\//\\\______\///____/\\\/_______________\/\\\_________\/\\\_____\/\\\//\\\____/\\\//\\\___/\\\/__\///\\\__\/\\\/\\\__\/\\\_      
   _\/\\\_______\/\\\___\////\\\__________/\\\_________________\/\\\_________\/\\\_____\/\\\\///\\\/\\\/_\/\\\__/\\\______\//\\\_\/\\\//\\\_\/\\\_     
    _\/\\\\\\\\\\\\\\\______\////\\\______\/\\\_________________\/\\\_________\/\\\_____\/\\\__\///\\\/___\/\\\_\/\\\_______\/\\\_\/\\\\//\\\\/\\\_    
     _\/\\\/////////\\\_________\////\\\___\//\\\________________\/\\\_________\/\\\_____\/\\\____\///_____\/\\\_\//\\\______/\\\__\/\\\_\//\\\/\\\_   
      _\/\\\_______\/\\\__/\\\______\//\\\___\///\\\______________\/\\\_________\/\\\_____\/\\\_____________\/\\\__\///\\\__/\\\____\/\\\__\//\\\\\\_  
       _\/\\\_______\/\\\_\///\\\\\\\\\\\/______\////\\\\\\\\\__/\\\\\\\\\\\__/\\\\\\\\\\\_\/\\\_____________\/\\\____\///\\\\\/_____\/\\\___\//\\\\\_ 
        _\///________\///____\///////////___________\/////////__\///////////__\///////////__\///______________\///_______\/////_______\///_____\/////__                                         
 
 _  _  ____   __   ____  ____  ____     ___  __   _  _  _  _  ____  __ _  ____  ____ 
/ )( \(  __) / _\ (    \(  __)(  _ \   / __)/  \ ( \/ )( \/ )(  __)(  ( \(_  _)/ ___)
) __ ( ) _) /    \ ) D ( ) _)  )   /  ( (__(  O )/ \/ \/ \/ \ ) _) /    /  )(  \___ \
\_)(_/(____)\_/\_/(____/(____)(__\_)   \___)\__/ \_)(_/\_)(_/(____)\_)__) (__) (____/ go here

Pokemon / Pokemon.Attack class
-Handles pokemon and attack data
-Loads all info from text file

>>>FEATURES
-Status affect inflicting attacks, including burn, freeze, poison, paralyze and a couple more
-Attacks have a chance of missing 
-There are A LOT of pokemon and attacks
-Pokemons can gain xp and level up (not implemented), leveling up boosts hp

*/

import java.util.*;
import java.io.*;
import java.lang.*;

public class Pokemon {
  public static Random rnd = new Random();

  //Class variables
  public static Set<String> pokemon_data = new HashSet<String>(); //List of all pokemon creation data (master list of all pokemon stats)
  private static final int MAXENERGY = 50; //All pokemon have a max energy of 50
  private static final int LVLUPXP = 1000; //amount of xp required to level up

  //Object Variables
  public final String name; 
  public final String number; //The pokemon's pokedex ID
  public final int baseLevel; //The default level of pokemon
  public int maxHp; //max hp of pokemon (can be increased via leveling up)
  public final String type; //elemetal type of pokemon; determines weakness and resistance
  public final String resist;
  public final String weak;
  public ArrayList<Attack> attacks = new ArrayList<Attack>(); //list of array objects
  private Set<String> attack_data = new HashSet<String>(); //List of default attack creation data
  //dictionary for buffs/debuffs (duration [in rounds], effect name)
  public Map<String,Integer> conditions = new HashMap<String,Integer>(); //Contains all conditions that pokemon has and how many turns it lasts for
  
  //Non-final variables
  private int hp; //current hp of pokemon
  private int energy; //All pokemon have a max energy of 50
  private int xp;
  private int level;

  public Pokemon(String data) { //Making the pokemon for the first time (this object is stored and is never modified)
    //<name>,<number>,<hp>,<type>,<resistance>,<weakness>,<num attacks>,[<attack name>, <energy cost>,<damage>,<special>]
    //data = data.replaceAll("");
    String[] datalist = data.split(","); //Turn data into a list
    
    //Species exclusive stats
    this.name = datalist[0];
    this.number = datalist[1];
    this.baseLevel = Integer.parseInt(datalist[2]); //may not be safe
    this.maxHp = Integer.parseInt(datalist[3]); //the pokemon gains 3 hp per level
    this.type = datalist[4];
    this.resist = datalist[5];
    this.weak = datalist[6];

    //Default attacks
    int numAttacks = Integer.parseInt(datalist[7]);
    for (int i = 8; i < 8+numAttacks; i++) {
      this.attack_data.add(datalist[i]); //add attatck creation data to list
      this.attacks.add(new Attack(Attack.newAttack(datalist[i]))); //add attack object to list
    }

    //Pokemon exclusive stats
    this.hp = this.maxHp; //Pokemon starts at max hp
    this.energy = MAXENERGY; //All pokemons start at max energy (50 pp)
    this.xp = 0; //Pokemon starts at 0 xp
    this.level = this.baseLevel; //every level grants 3 extra hp, pokemon evolves every 15 levels
    
  }
  
  
  public void modHp(int deltaHp) { //Change the amount of hp the pokemon has 
    if (this.hp+deltaHp >= this.maxHp) { //Pokemon cannot heal over max health
      this.hp = this.maxHp;
    } else if (this.hp+deltaHp <= 0) { //Pokemon dies
      this.hp = 0;
      this.faint(); //pokemon faints
    } else { //otherwise change the hp like usual 
      this.hp += deltaHp;
    }
  }

  
  public void modEnergy(int deltaEnergy) { //change the amount of energy the pokemon has
    if (this.energy+deltaEnergy >= MAXENERGY) { //Pokemon can never go over 50 energy
      this.energy = MAXENERGY;
    } else if (this.energy+deltaEnergy < 0) { //Pokemon can never go under 0 energy
      this.energy = 0;
    } else { //Othermise change energy like usual
      this.energy += deltaEnergy;
    } 
  }

  public void modXp(int deltaXp) { //change the amount of xp (and levels) the pokemon has
    deltaXp = Math.abs(deltaXp); //xp cannot be negative
    if (this.xp+deltaXp >= 1000) { //if the pokemon levels up
      int extraXp = 1000-(this.xp+deltaXp); //amount of xp that carrys over to the next level
      this.level++; //incerease level by 1 
      this.xp = extraXp; //reset xp
      this.maxHp += 3; //everytime pokemon levels up, it gains 3 hp

    } else { //increase xp normally
      this.xp += deltaXp;
    }

  }
  
  public String attack(String attackName, Pokemon defpokemon) {  //defpokemon is the pokemon that is being attacked (defending pokemon)
    //get ready for if-statement hell
    Attack attack = new Attack(Attack.newAttack(attackName)); //i just used the word 'attack' 6 times in one line

    if (this.containsAttack(attackName) && this.enoughEnergy(attack)) { //check to see if pokemon has access to attack, and that it has enough energy to use attack
      this.modEnergy(-1*attack.energyCost); //use energy

      //EFFECTIVENESS MESSAGES
      String effectiveness = "";
      String status = "";
      String message = "";

      //Accuracy (does the attack land or not)
      int hit = Attack.attackLand(attack.accuracy); //determine if the attack hits or not 
      if (hit == 0) {
        return String.format("The move %s missed...",attackName.toUpperCase());
      }

      double dmgMult = 1; //the damage multiplier based on type matchups
      //NOTE: THE CURRENT ATTACK SYSTEM CALCULATES WEAKNESS AND RESISTANCE BASED ON THE TYPES OF THE TWO POKEMON, NOT THE TYPE OF ATTACK 
      if (this.type.contains(defpokemon.weak)) { //if the defending pokemon is weak to the attacking pokemon
        dmgMult = 2; //the attack does 2x damage
        effectiveness = " and was SUPER EFFECTIVE";
      } else if (this.type.contains(defpokemon.resist)) { //if the defending pokemon has a resistance to the attacking pokemon
        dmgMult = 0.5; //the attack does half damage
        effectiveness = " and was NOT VERY EFFECTIVE";
      }

      //CHECK FOR DEBUFFS AFFECTING DAMAGE
      int dmg_mod = 0;
      if (this.conditions.keySet().contains("Disabled")) { //pokemon who are disabled do 10 less damage
        dmg_mod = -10;
      }

      if (this.conditions.keySet().contains("Flinching")) { //pokemon who are fllincihg cannot attack
        hit = 0;
        return String.format("%s is FLINCHING, so it's attack misses!",this.name);
      }

      int atk_dmg = -1*((int) Math.round(attack.dmg*dmgMult)+dmg_mod)*hit; //the damage the attack does after all modifiers

      if (atk_dmg > 0) atk_dmg = 0; //attack damage cannot heal the other pokemon

      //AGGRESSIVE ATTACKS (pure damage)
      if (attack.atk_type.equals("aggressive")) { 
        defpokemon.modHp(atk_dmg); //deal damage normally
        return String.format("The move %s dealt %d damage%s!",attackName.toUpperCase(),-1*atk_dmg,effectiveness);

      //STATUS ATTACKS (inflicts a status effect)
      } else if (attack.atk_type.equals("status")) { 
        String effect = "";
        //special effects
        if (attack.special.equals("Burn")) {
          status = defpokemon.applyCondition("Burned");
        } else if (attack.special.equals("Poison")) { //not to be confused with the type 'poison'
          status = defpokemon.applyCondition("Poisoned");
        } else if (attack.special.equals("Freeze")) {
          status = defpokemon.applyCondition("Frozen");        
        } else if (attack.special.equals("Paralyze")) {
          status = defpokemon.applyCondition("Paralyzed");
        } else if (attack.special.equals("Trap")) {
          status = defpokemon.applyCondition("Trapped");
        } else if (attack.special.equals("Flinch")) {
          status = defpokemon.applyCondition("Flinching");
        } else if (attack.special.equals("Sleep")) {
          status = defpokemon.applyCondition("Sleeping");
        } else if (attack.special.equals("Confuse")) {
          status = defpokemon.applyCondition("Confused");
        } else if (attack.special.equals("Disable")) {
          status = defpokemon.applyCondition("Disabled");
        }

        defpokemon.modHp(atk_dmg); //deal damage
        return String.format("The move %s dealt %d damage%s%s!",attackName.toUpperCase(),-1*atk_dmg,effectiveness,status);

      //PASSIVE ATTACK (only affects attacking pokemon)
      } else if (attack.atk_type.equals("passive")) {
        if (attack.special.equals("Recharge")) {
          this.modEnergy(20); //add 20 energy to pokemon
          return String.format("%s restored 20 hp",this.name);
        }
        return ("Oops, something went WRONG");

      //OTHER ATTACK (unique attacks)
      } else if (attack.atk_type.equals("other")) { 


        if (attack.special.equals("Wild Card")) { //50% chance of dealing damage
          if (rnd.nextInt(2) == 1) { //generate 0 or 1, (0 means failed, 1 means success)
            defpokemon.modHp(atk_dmg); //deal damage    
            return String.format("The move %s dealt %d damage%s!",attackName.toUpperCase(),-1*atk_dmg,effectiveness);
          }  
          return String.format("The move %s missed...",attackName.toUpperCase());
        } else if (attack.special.equals("Wild Storm")) { //50% chance of dealing damage, if successful, try attacking again
          int hits = 0;
          int total_dmg = 0;
          while (true) {
            if (rnd.nextInt(2) == 0) { //generate 0 or 1, if the attack fails, stop attacking
              return String.format("The move %s did a total of %d damage with %d hits!",attackName.toUpperCase(),-1*total_dmg,hits);
            }
            hits++;
            defpokemon.modHp(atk_dmg); //otherwise deal damage  
            total_dmg += atk_dmg;   
          }
        } else if (attack.special.equals("Penetrate")) { //Attack is not affected by damage modifiers (it can miss tho)
          defpokemon.modHp(-1*attack.dmg*hit);
          return String.format("The move %s did a solid %d damage!",attackName.toUpperCase(),attack.dmg);
        } else if (attack.special.equals("Self Destruct")) { //User of attack faints
          defpokemon.modHp(atk_dmg);
          this.modHp(-99999); 
          return String.format("%s self-destructed dealing %d!",this.name,-1*atk_dmg);
        } else if (attack.special.equals("Gamble")) { //User loses half hp is attack misses
          if (hit == 0) { //if attack misses
            this.modHp(-1*Math.round(this.maxHp/2));
            return String.format("%s lost the gamble, dealing %d damage to itself!",this.name,Math.round(this.maxHp/2));
          }
          defpokemon.modHp(atk_dmg);  
          return String.format("The move %s dealt %d damage%s!",attackName.toUpperCase(),-1*atk_dmg,effectiveness);

        } else if (attack.special.equals("Leech")) { //User restores half of the damage dealt
          defpokemon.modHp(atk_dmg); //do damage normally
          this.modHp(-1*Math.round(atk_dmg/2)); //heal half of the damage done
          return String.format("The move %s dealt %d damage%s. %s leeched %d hp!",attackName.toUpperCase(),-1*atk_dmg,effectiveness,this.name,(-1*Math.round(atk_dmg/2)));
        } else if (attack.special.equals("Damage Scale")) { //damage equal to attacking pokemon's level
          defpokemon.modHp(-1*this.level);
        }
        return ("Oops, something went WRONG");
      }
    }

    return String.format("It looks like %s doesn't have enough energy to perform the move!",this.name);
    //return message about effectiveness of attack
  }
  
  public String applyCondition(String cndName) { //add a condition to a pokemon
    int turns = 0;
    if (cndName.equals("Burned") && this.type.contains("Fire") == false) { //Fire types cannot be burned
      if (Attack.attackLand(70) == 1) { //there is a 70% chance for a move to inflict burning
        this.conditions.put("Burned",4); //Burn lasts for 4 turns
        return (" and applied BURNING for 4 turns");
      }
      //Pokemon's attacks will also do half the damage
    } else if (cndName.equals("Poisoned") && this.type.contains("Poison") == false) { //Poison type pokemon cannot be poisoned
      if (Attack.attackLand(70) == 1) { //there is a 70% chance for a move to poison
        this.conditions.put("Poisoned",1000000); //Poisoned lasts until pokemon faints or the effect is treated
        return (" and applied POISON");
      }
    } else if (cndName.equals("Frozen") && this.type.contains("Ice") == false) { //Ice type pokemon cannot be frozen
      if (Attack.attackLand(70) == 1) { //there is a 70% chance for a move to freeze
        this.conditions.put("Frozen",4); //Pokemon is frozen for 3 turns, during which it cannot move (cannot retreat, but can still attack)
        return (" and applied FREEZING for 4 turns");
      }
    } else if (cndName.equals("Paralyzed")) {
      //25% chance of not being able to attack, lasts until faint or cured
      return String.format(" and applied PARALYSIS");
    } else if (cndName.equals("Trapped")) {
      if (Attack.attackLand(75) == 1) { //there is a 75% for pokemon to be trapped
        turns = rnd.nextInt(4)+2;
        this.conditions.put("Trapped",turns); //pokemon is trapped for 2-5 turns
        return String.format(" and TRAPPED the enemy pokemon for %d turns",turns);
      }
    } else if (cndName.equals("Flinching")) { //*****FLinching is the same as STUNNED, i just called it something different*****
      if (Attack.attackLand(50) == 1) { //there is a 50% chance that the pokemon will be stunned for ONE turn
        this.conditions.put("Flinching",1);
        return (" and applied FLINCHING for 1 turn");
      }
    } else if (cndName.equals("Sleeping")) {
      if (Attack.attackLand(80) == 1) { //there is an 80% for pokemon to fall asleep
        turns = rnd.nextInt(8)+1;
        this.conditions.put("Sleeping",turns); //pokemon will sleep for 1-7 turns
        return String.format(" and put the enemy to SLEEP for %d turns",turns);
      }
    } else if (cndName.equals("Confused")) {
      if (Attack.attackLand(60) == 1) { //there is an 60% for pokemon to fall asleep
        turns = rnd.nextInt(3)+1;
        this.conditions.put("Confused",turns); //pokemon will sleep for 1-3 turns, during which it cannot attack (it can retreat)
        return String.format(" and put applied CONFUSION got %d turns",turns);
      } 
    } else if (cndName.equals("Disabled")) {
      if (Attack.attackLand(60) == 1) { //there is an 60% for pokemon to be disabled
        this.conditions.put("Disabled",1000000); //pokemon will sleep for 1-3 turns, during which it cannot attack (it can retreat)
        return (" and DISABLED the target");  
      }
    }
    return "";
  }


  public void condition() { //apply all conditions the pokemon currently has (should be called every single turn)
    for (String cndName : this.conditions.keySet()) { //for each condition the pokemon has
      if (cndName.equals("Burned")) {
        int dmg = Math.round(this.maxHp/8);
        this.modHp(-1*dmg); //burn makes the pokemon lose 1/8 of its health per turn
        System.out.printf("%s took %d damage from BURNING\n",this.name,dmg);
      } else if (cndName.equals("Poisoned")) {
        int dmg = Math.round(this.maxHp/16);
        this.modHp(-1*Math.round(this.maxHp/16)); //Poisoned pokemon lose 1/16 of its health per turn
        System.out.printf("%s took %d damage from POISONING\n",this.name,dmg);
      } else if (cndName.equals("Trapped")) {
        this.modHp(-10); //deals 10 damage
        System.out.printf("%s took 10 damage from being TRAPPED\n",this.name);
      }

      this.conditions.put(cndName, this.conditions.get(cndName)-1); //update remaining turns condition is active for
      if (this.conditions.get(cndName) <= 0) { //if the condition wears off, remove from list
        this.conditions.remove(cndName); 
      }
      
    }
  }

  public void faint() { //pokemon gets knocked out
    this.conditions.put("Fainted",1000000); //faint lasts forever (until revived)
  }

  //Helping methods
  public static void init(String filename) { //Load all of the Pokemon data from file
    
    try {
      Scanner fin = new Scanner(new BufferedReader(new FileReader(filename)));
      int n = fin.nextInt(); //the first line of the file contains the amount of lines of data the file contains
      fin.nextLine();
      
      String[] data = new String[n]; //array to hold all pokemon data
      for (int i = 0; i < n; i++) { //For each line of input
        data[i] = fin.nextLine(); //get one line from the file
      }  

      //If all data was imported successfully, start adding data to master lists
      for (String d : data) { 
        int index1 = Pokemon.charIndex(d,',',7); //index of the ',' before <numAttacks>
        int index2 = Pokemon.charIndex(d,',',8); //Index of the ',' after <numAttacks>
        int numAttacks = Integer.parseInt(d.substring(index1+1,index2)); //Number of attacks to be imported
        String pokemon_construct = d.substring(0,index1)+(","+numAttacks); //get everything before attack data
        String attack_construct = d.substring(index2+1); //get attack data

        //Make new attack objects
        int index3; //The last ',' of each attack
        String curAtk;
        for (int i = 0; i < numAttacks; i++) { //separate the attack data
          index3 = Pokemon.charIndex(attack_construct,',',7);
          curAtk = attack_construct.substring(0,index3);
          Attack.attack_data.add(curAtk); //add attack data to master list
          pokemon_construct += (","+curAtk.substring(0,charIndex(curAtk,',',1))); //add attack name to pokemon constructor
          attack_construct = attack_construct.substring(index3+1); //delete the used attack data
        }

        pokemon_data.add(pokemon_construct); //add pokemon data to master list
      } 
      //System.out.println(Attack.attack_data);
      fin.close();
    } catch(IOException ex) { //In case the file is unabled to be opened
      System.out.println("IO Exception: Unable to find "+filename);
    }

  }

  private static Integer charIndex(String s, char c, int occurrence) { //finds the index of the nth occurrence of substring in string
    int curOcc = 0;
    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) == c) curOcc++; //if char is found, add one to occurence count
      if (curOcc == occurrence) return i; //if nth occurrence of char is found, return index
    }
    return -1;
  }

  public Boolean enoughEnergy(Attack attack) { //determines if the pokemon has enough energy to use a move
    if ((this.energy-attack.energyCost) >= 0) return true;
    return false;
  }

  public int[] pokedata() { //returns array of modifable pokemon data (such as hp and energy)
    return new int[]{this.hp,this.energy,this.xp,this.level};
  }

  public static String newPokemon(String name) { //help make a new Pokemon object given the name of the 'mon
    String checkname; 
    for (String d : Pokemon.pokemon_data) { //For each pokemon
      checkname = d.substring(0,d.indexOf(','));
      if (checkname.equals(name)) { //If the pokemon was found
        return d; //return its creation data
      }
    }
    return ""; //otherwise return an empty string
  }

  
  public static String rndPokemon() { //returns the creation data of a random pokemon
    int rndIndex = rnd.nextInt(Pokemon.pokemon_data.size()+1);
    int i = 0; //counter
    for (String d : Pokemon.pokemon_data) {
      if (i == rndIndex) return d;
      i++;
    }
    return "";
  }


  public Boolean containsAttack(String attackName) { //finds out if pokemon has an attack
    for (int i = 0; i < this.attacks.size(); i++) {
      if (this.attacks.get(i).name.equals(attackName)) { //if the attack belongs to the pokemon
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() { //Show modifiable details about pokemon
    //PRINT OUT THE CONDITIONS THE POKEMON HAS AS WELL
    return String.format("hp: %d energy: %d xp: %d level: %d",this.hp,this.energy,this.xp,this.level); 
  }


  //Classception
  static class Attack {
    public static Set<String> attack_data = new HashSet<String>(); //List of all attack objects

    public final String name;
    public final String type; //elemental type of attack (usually pokemon with the attacks of the same type)
    public final String atk_type; //classification of attack (passive - effects only self, aggressive - deals damage, status - inflicts effect, other)
    public final int dmg; 
    public final int accuracy; //%chance of attack landing
    public final int energyCost;
    public final String special; //special effect attack applies
  
    public Attack(String data) {
      String[] datalist = data.split(","); //Turn data into a list 

      this.name = datalist[0];
      this.type = datalist[1];
      this.atk_type = datalist[2];
      this.dmg = Integer.parseInt(datalist[3]);
      this.accuracy = Integer.parseInt(datalist[4]);
      this.energyCost = Integer.parseInt(datalist[5]);
      this.special = datalist[6];
      
    }

    @Override
    public String toString() {
      return String.format("[damage=%s,accuracy=%d,energy=%d,special=%s]",this.dmg,this.accuracy,this.energyCost,this.special);
    }

    public static String newAttack(String name) { //help make a new attack object given the name
      String checkname; 
      for (String d : Attack.attack_data) { //For each pokemon
        checkname = d.substring(0,d.indexOf(','));
        if (checkname.equals(name)) { //If the pokemon was found
          return d; //return its creation data
        }
      }
      return ""; //otherwise return an empty string
    }

    public static int attackLand(int percent) { //take in a percentage chance of successful attack and determine if attack is successful
      int chance = rnd.nextInt(100)+1; //draw number from 1 to 100
      if (chance <= percent) { //attack landed!
        return 1; 
      } else { //attack missed...
        return 0;
      }
    }
  }

  
}