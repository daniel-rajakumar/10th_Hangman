//Programmer: Daniel
//Date: June 15th, 2020
//Topic: Game
//Title: Hangman

import java.util.*;

enum Level {
    BEGINNER, STANDARD, ADVANCED
}

public class Main {
    
    final boolean DEVELOPER_MODE = true;//for testing purposing only(shows the words)
    Scanner enter = new Scanner(System.in); 

    //words
    final String[][] words = {
        //BEGINNER
        {"less", "well", "trip", "mac", "time", "pie", "stuck", "down", "with", "bowl", "code"},
        //STANDARD
        {"buffalo", "length", "physics", "chemistry", "bottle", "java", "windows", "apple", "keyboard", "password", "android", "zero"},
        //ADVANCED
        {"kiwifruit", "witchcraft", "ivy","buzzwords", "textpad", "jazzy", "wyvern", "bookworm", "boxcar", "linux", "ubuntu", "vikings"}
    };

    //emojis
    final String up =  "👆 ";
    final String right ="👉 ";
    final String left ="👈 ";
    final String down  ="👇 ";
    final String heart  ="❤️ ";
    final String warning  ="🛑 ";
    final String list  ="📜 ";
    final String star  ="🌟 ";
    final String time  ="⌚️ ";
    final String data  ="💾 ";
    //                {during 6-lives, during 5-lives, ... during 1-lives, during 0-lives}
    final String[] face   = {"  ",    "😀",  "🤨  ", "🤔",  "🤒 ",  "🤕", "😵 "};
    final String[] shirt  = {"  ",    "  ",   "🎽 ",  "🎽 ",  "🎽 ",  "🎽 ", "🎽 "};
    final String[] jeans  = {"  ",    "  ",   "👖 ",  "👖 ",  "👖 ",  "👖 ",  "👖 "};
    final String[] l_hand = {"  ",    "  ",   "  ",   "🤛 ",  "🤛 ", "🤛 ", "🤛 "};
    final String[] r_hand = {"  ",    "  ",   "  ",   "  ",   "🤜 ",  "🤜",  "🤜 "};
    final String[] l_leg  = {"  ",    "  ",   "  ",   "  ",   "  ",   "⛸ ",  "⛸ "};
    final String[] r_leg  = {"  ",    "  ",   "  ",   "  ",   "  ",   "  ",   "🥾 "};

    //the word
    String secretWord;
    static int numOfLivesSaved = 0;

    //runs the whole game ("root")
    void run() {
        //clear screen
        cls();
        //game info and welcome
        gameInfo();
        //select level (ask user)
        Level lvl = selectLevel();
        //generate a random word based on the user's picked level
        secretWord = randomWordSelector(lvl);
        //refresh the variables before playing a new game...
        refreshVariables();
        //starts the game
        play(secretWord, 6);
        //play again option...
        if (askPlayAgain()) 
            new Main().run();//run the game again
        else 
            byeByeArt();//print bye ascii art
    }

    //level selector 
    Level selectLevel(){
        //available levels 
        write("Choose a valid level: \n");
        write("   - 0 for " + Level.BEGINNER + "\n");
        write("   - 1 for " + Level.STANDARD + "\n");
        write("   - 2 for " + Level.ADVANCED + "\n\n");
        write("Game level: ");
        //"try" asking user for the level in integer form...
        try {
            //ask user for the level(int)... and return the user's level
            switch (enter.nextInt()) {
                case 0: return Level.BEGINNER;
                case 1: return Level.STANDARD;
                case 2: return Level.ADVANCED;
                
                //if the input is out of range...
                default: 
                    cls();
                    write("Oops... Level unavailable | Try again\n\n");
                    return selectLevel();
            }

        //"catch" if the user's input is not integer then... re-ask the question
        } catch (Exception e) {
            cls();
            write("Oops... You must choose an integer | Try again\n\n");
            return new Main().selectLevel();
        }
    }

    //get a random word based on the given level
    String randomWordSelector(Level lvl){
        switch (lvl) {
            case BEGINNER: return words[0][(int)(Math.random() * words[0].length)];
            case STANDARD: return words[1][(int)(Math.random() * words[1].length)];
            case ADVANCED: return words[2][(int)(Math.random() * words[2].length)];
            default: return null;
        }
    }

    //refresh variables 
    void refreshVariables(){
        numCorrectGuess = 0;
        numWrongGuess = 0;
        numDupGuess = 0;
        enter.nextLine();
    }

    //variables for playing the actual game
    ArrayList<Character> guessed = new ArrayList<>();
    char previousGuess;
    String previousGuessStats;
    boolean win = false;
    int numCorrectGuess, numWrongGuess, numDupGuess;


    void play(String secretWord, int lives){
        //clear screen
        cls();
        //draw the hangMan in emojis😀
        draw_hangMan(6-lives);
        //stop here, if the user win/lose (and printing an ascii art)
        if(lives == 0 || win) {printAsciiArt(); return;}
        //ask question(guess)
        char userGuess = askUserGuess();
        //deciding the result (correct/wrong/duplicate)
        if(isCharAvailable(userGuess)) duplicateGuess(lives, userGuess);//duplicate
        else {
            guessed.add(userGuess);//add the guess to the database

            if(!guessCheck(userGuess)) wrongGuess(lives, userGuess);//wrong
            else correctGuess(lives, userGuess);//correct
        }
    }

    //ask for re-match
    boolean askPlayAgain(){
        write("Hey! want to play again?: ");
        String playAgain = enter.nextLine();
        
        //rematch?
        if(playAgain.toLowerCase().equals("y")) return true;//rematch!!
        else if(playAgain.toLowerCase().equals("n")) return false;//no rematch
        else {
            cls();
            write("Oops... invalid input\n");
            write("type: ['Y' for yes]['N' for No]\n");
            return askPlayAgain();
        }
    }


    //if the guess is correct...
    void correctGuess(int lives, char userGuess) {
        numCorrectGuess++;
        previousGuessStats = "Correct";
        //checking for win
        win = checkForWin();
        //do nothing and play again...
        play(secretWord, lives);
    }
    
    //if the guess is in-correct...
    void wrongGuess(int lives, char userGuess) {
        numWrongGuess++;
        previousGuessStats = "Wrong";
        //play again, with a reduced life...
        play(secretWord, --lives);
    }
    
    //if the guess is duplicate...
    void duplicateGuess(int lives, char userGuess) {
        numDupGuess++;
        previousGuessStats = "Duplicate";
        //do nothing and play again...
        play(secretWord, lives);
    }

    //ask for the guess...
    char askUserGuess(){
        write("(All lower case) Guess a letter: ");
        char userGuess;
        try {
            userGuess = enter.nextLine().charAt(0);
        } catch (Exception e) {
            userGuess = ' ';//if incase the user pressed enter without a char(or invalid char), then count it as space
        }
        previousGuess = userGuess;
        return userGuess;
    }

    //print ending ascii art, depending on the result
    void printAsciiArt(){
        if(win) 
            write(winningArt());
        else 
            write(losingArt());
    }

    //check for win
    boolean checkForWin(){
        for (int i = 0; i < secretWord.length(); i++) 
            if (!isCharAvailable(secretWord.charAt(i)))
                return false;//not yet :(
        
        //if the above loop passes... that means there is no char left to guess... (the user win!)
        return true;//win :)
    }

    //does the char available in the guessed database? 
    boolean isCharAvailable(char guess){
        //check for the char in the user's guessed letters
        for (char i : guessed) 
            if(guess == i) return true;//available

        return false;//not available(the char is new letter)
    }

    //decide whether the guess is correct/wrong. 
    boolean guessCheck(char guess){
        //check for the user guess result (correct/wrong)
        for (char i : secretWord.toCharArray()) 
            if(i == guess) return true;//correct

        return false;//wrong
    }

    //write the given sentence in fancy font
    void write(String sentence){
        //write it in fancy letter
        for (char i : sentence.toCharArray())
            System.out.print(getFancyLetter(i));
    }

    //draw the emoji hangman
    void draw_hangMan(int i){
        String Letters = "";
        String theWord = "";

        //if developer mode is ON, its assigned the value from none to the answer 
        if(DEVELOPER_MODE)
            theWord += warning + " The word: " + secretWord + " " + warning;
        
        //shows the user's progress on the word...
        for (int j = 0; j < secretWord.length(); j++)
            if (isCharAvailable(secretWord.charAt(j)))
                Letters += secretWord.charAt(j) + "  ";//if they have found the letter, add the letter
            else 
                Letters += "_  ";//if they have yet to find the letter, add "_" instead of the letter.
        
        //warning that you are in developer mode
        if(DEVELOPER_MODE) System.out.println("DEVELOPER MODE IS ON!");
        //String previousGuess = 

        write("Number of lives you have saved, today: " + numOfLivesSaved + "\n");
        System.out.println("\t" + right + right + right + right + right + right + right + right + right + right + right + right + right + right);
        System.out.println("\t" + right + right + right + right + right + right + right + right + right + right + right + right + right + right);
        System.out.println("\t" + up + up + up          + right + right + right + right + right + right + right + right + down + down + down);
        System.out.println("\t" + up + up + up  + "                "                                                      + down + down + down);
        System.out.println("\t" + up + up + up  + "                "                                                      + down + down + down);
        System.out.println("\t" + up + up + up  + "                  "                                                           + down);
        System.out.println("\t" + up + up + up  + "                  "                                                          + face[i]);
        System.out.println("\t" + up + up + up  + "                "                                               + l_hand[i]  + shirt[i] + r_hand[i]);
        System.out.println("\t" + up + up + up  + "                  "                                                          + jeans[i]);
        System.out.println("\t" + up + up + up  + "                 "                                                     + l_leg[i] + r_leg[i]);
        System.out.println("\t" + up + up + up);
        System.out.println("\t" + up + up + up);
        System.out.print  ("\t" + up + up + up  + "           " + star);      write(" " + Letters + " ");     System.out.println(star);
        System.out.println("\t" + up + up + up);
        System.out.print  ("\t" + up + up + up  + "           ");             write(theWord);                 System.out.println();
        System.out.println("\t" + up + up + up);
        System.out.print  ("\t" + up + up + up  + "           " + data);      write(" Your Guesses Stats [Correct = "  + numCorrectGuess + ", Wrong = "  + numWrongGuess + ", Duplicate = "  + numDupGuess + "] ");   System.out.println(data);
        System.out.println("\t" + up + up + up);
        System.out.print  ("\t" + up + up + up  + "           " + heart);     write(" Health Left: " + (6-i) + " ");  System.out.println(heart);
        System.out.println("\t" + up + up + up);
        System.out.print  ("\t" + up + up + up  + "           " + list);      write(" Letters Guessed So Far: " + Arrays.toString(guessed.toArray()) + " ");  System.out.println(list);
        System.out.println("\t" + up + up + up); 
        System.out.print  ("\t" + up + up + up  + "           " + time);      write(" Previous Guess: " + previousGuess + " ("+ previousGuessStats+") ");     System.out.println(time);
        System.out.println("\t" + up + up + up);
        System.out.println("  " + up + up + up + up + up);
        System.out.println(up + up + up + up + up + up + up);
        System.out.println(right + left + right + left + right + left + right + left + right + left + right + left + right + left + right + left + right + left + right + left + right + left + right + left + right + left + right + left + right + left + right + left + right + left + right + left + "\n");
    }

    //clear screen
    void cls() {  
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    } 


    //customized font. 
    String getFancyLetter(char letter){
        //other fancy letters looks bad in console such as (𝕒𝕓𝕔𝕕𝕖𝕗𝕘)
        //serif font
        //small
        String[] s_letter = {"𝚊", "𝚋", "𝚌", "𝚍", "𝚎", "𝚏", "𝚐", "𝚑", "𝚒", "𝚓", "𝚔", "𝚕", "𝚖", "𝚗", "𝚘", "𝚙", "𝚚", "𝚛", "𝚜", "𝚝", "𝚞", "𝚟", "𝚠", "𝚡", "𝚢", "𝚣"};
        //Capital
        String[] C_letter = {"𝙰", "𝙱", "𝙲", "𝙳", "𝙴", "𝙵", "𝙶", "𝙷", "𝙸", "𝙹", "𝙺", "𝙻", "𝙼", "𝙽", "𝙾", "𝙿", "𝚀", "𝚁", "𝚂", "𝚃", "𝚄", "𝚅", "𝚆", "𝚇", "𝚈", "𝚉"};        

        switch (letter) {
            //letters
            case 'a': return s_letter[0];    case 'A': return C_letter[0]; 
            case 'b': return s_letter[1];    case 'B': return C_letter[1]; 
            case 'c': return s_letter[2];    case 'C': return C_letter[2]; 
            case 'd': return s_letter[3];    case 'D': return C_letter[3]; 
            case 'e': return s_letter[4];    case 'E': return C_letter[4]; 
            case 'f': return s_letter[5];    case 'F': return C_letter[5]; 
            case 'g': return s_letter[6];    case 'G': return C_letter[6]; 
            case 'h': return s_letter[7];    case 'H': return C_letter[7]; 
            case 'i': return s_letter[8];    case 'I': return C_letter[8]; 
            case 'j': return s_letter[9];    case 'J': return C_letter[9]; 
            case 'k': return s_letter[10];   case 'K': return C_letter[10];
            case 'l': return s_letter[11];   case 'L': return C_letter[11];
            case 'm': return s_letter[12];   case 'M': return C_letter[12];
            case 'n': return s_letter[13];   case 'N': return C_letter[13];
            case 'o': return s_letter[14];   case 'O': return C_letter[14];
            case 'p': return s_letter[15];   case 'P': return C_letter[15];
            case 'q': return s_letter[16];   case 'Q': return C_letter[16];
            case 'r': return s_letter[17];   case 'R': return C_letter[17];
            case 's': return s_letter[18];   case 'S': return C_letter[18];
            case 't': return s_letter[19];   case 'T': return C_letter[19];
            case 'u': return s_letter[20];   case 'U': return C_letter[20];
            case 'v': return s_letter[21];   case 'V': return C_letter[21];
            case 'w': return s_letter[22];   case 'W': return C_letter[22];
            case 'x': return s_letter[23];   case 'X': return C_letter[23];
            case 'y': return s_letter[24];   case 'Y': return C_letter[24];
            case 'z': return s_letter[25];   case 'Z': return C_letter[25];
            //symbols
            case '!' : return "!"; case '0' : return "0";
            case '@' : return "@"; case '1' : return "1";
            case '#' : return "#"; case '2' : return "2";
            case '$' : return "$"; case '3' : return "3";
            case '%' : return "%"; case '4' : return "4";
            case '^' : return "^"; case '5' : return "5";
            case '&' : return "&"; case '6' : return "6";
            case '*' : return "*"; case '7' : return "7";
            case '(' : return "("; case '8' : return "8";
            case ')' : return ")"; case '9' : return "9";
            case '_' : return "_"; 
            case '-' : return "-";
            case '+' : return "+";
            case '=' : return "=";
            case '{' : return "{";
            case '}' : return "}";
            case '[' : return "[";
            case ']' : return "]";
            case '|' : return "|";
            case '\\' : return "\\";
            case ':' : return ":";
            case ';' : return ";";
            case '\"' : return "\"";
            case '\'' : return "\'";
            case '<' : return "<";
            case '>' : return ">";
            case '?' : return "?";
            case ',' : return ",";
            case '.' : return ".";
            case '/' : return "/";
            case '~' : return "~";
            case '`' : return "`";
            //other strange chars...
            case '—' : return "—";
            case '”' : return "”";
            case '“' : return "“";
            //special chars
            case '\b': return "\b";
            case '\t': return "\t";
            case '\n': return "\n";
            case '\f': return "\f";
            case '\r': return "\r";
            //default...
            default: return " " ;
        }
    }

    //art for winning(chocolate bar as Hangman promised!)
    //and add the winning score...
    String winningArt(){
        //add the num of lives saved... 
        numOfLivesSaved++;

        //draw...
        return  "\n" + 
                "                                 _       " + "\n" + 
                "                                | |       " + "\n" + 
                " ___  ___  _ __   __ _ _ __ __ _| |_ ___  " + "\n" + 
                "/  _ / _ \\| '_ \\ / _` | '__/ _` | __/ __| " + "\n" + 
                "| (_| (_) | | | | (_| | | | (_| | |_\\__ \\ " + "\n" + 
                "\\___ \\___/|_| |_|\\__, |_|  \\__,_|\\__|___/ " + "\n" + 
                "                  __/ |                   " + "\n" + 
                "                  |___/  " + "\n" + 
                "  ___  ___  ___  ___  ___.---------------. " + "\n" + 
                ".'\\__\\'\\__\\'\\__\\'\\__\\'\\__,`   .  ____ ___ \\ " + "\n" + 
                "|\\/ __\\/ __\\/ __\\/ __\\/ _:\\   |`.  \\  \\___ \\ " + "\n" + 
                " \\\\'\\__\\'\\__\\'\\__\\'\\__\\'\\_`.__|\"\"`. \\  \\___ \\ " + "\n" + 
                "  \\\\/ __\\/ __\\/ __\\/ __\\/ __:                \\ " + "\n" + 
                "   \\\\'\\__\\'\\__\\'\\__\\ \\__\\'\\_;-----------------` " + "\n" + 
                "    \\\\/   \\/   \\/   \\/   \\/ : Congratulations!| " + "\n" + 
                "     \\|______________________;________________| " + "\n" + 
                "\n\n\n";
    }

    //losing art (RIP)
    String losingArt(){
        return "\n" + 
        " ,------.     ,--.    ,------.  " + " \n" +
        " |  .--. '    |  |    |  .--. ' " + " \n" +
        " |  '--'.'    |  |    |  '--' | " + " \n" +
        " |  |\\  \\ .--.|  |.--.|  | --'  " + " \n" +
        " `--' '--''--'`--''--'`--'      " + " \n\n" +  
        "                       The word was: " + secretWord +
        "\n\n\n";                             
    }

    //welcome art
    void welcomeArt(){
        System.out.println();
        System.out.println("  _                                             " );
        System.out.println(" | |                                            " );
        System.out.println(" | |__   __ _ _ __   __ _ _ __ ___   __ _ _ __  " );
        System.out.println(" | '_ \\ / _` | '_ \\ / _` | '_ ` _ \\ / _` | '_ \\ " );
        System.out.println(" | | | | (_| | | | | (_| | | | | | | (_| | | | |" );
        System.out.println(" |_| |_|\\__,_|_| |_|\\__, |_| |_| |_|\\__,_|_| |_|" );
        System.out.println("                     __/ |                      " );
        System.out.println("                    |___/          -Daniel " );
        System.out.println();
    } 
    
    //exit art
    void byeByeArt(){
        cls();
        System.out.println("Congratulations for saving " + numOfLivesSaved + " lives today! \n");
        System.out.println("  _                 ");
        System.out.println(" | |                ");
        System.out.println(" | |__  _   _  ___  ");
        System.out.println(" | '_ \\| | | |/ _ \\ ");
        System.out.println(" | |_) | |_| |  __/ ");
        System.out.println(" |_.__/ \\__, |\\___| ");
        System.out.println("         __/ |      ");
        System.out.println("        |___/ ");
        
        write("\n\nThanks for saving lives! \n\n\n");
        write("Version \t\t\t\t1.0.0\n");
        write("Updated on \t\t\t\tJune 15, 2020\n");
        write("Downloads \t\t\t\t0+ Downloads\n");
        write("Downloads size \t\t\t21 KB\n");
        write("Released on \t\t\tJune 15th, 2020\n");
        write("Programmed by \t\t\tDaniel \n");
    }

    //into
    void gameInfo(){
        welcomeArt();
        write("Welcome to the game of HangMan!!!\n\n");
        write("How this game works: \n");
        write("   - We generate a random hidden word\n");
        write("   - you will find the hidden word by guessing it(letter by letter)\n");
        write("   - you will be given 6 health points\n");
        write("   - if your guess, does NOT match one of the hidden word's letter, then your health will reduce by one\n");
        write("   - if your guess, DOES match one of the hidden word's letter, then we will reveal all the occurences of the letter\n");
        write("   - once you find all the letters of the hidden word, then you win.\n");
        write("   - if you could not able to find all the letters and your health points hits 0, then you lose\n\n");
        write("How does the hangman works: \n");
        write("   - each time you guess wrong, a new body part of the hangman will be drawn (6 max)\n");
        write("   - if the hangman gets all his body parts before you find the hidden word, then Hangman will get hanged(RIP)\n\n");
        write("Your goal: \n");
        write("   - save the hangman from getting hanged by finding the secret word before its get too late.\n\n");
        write("A message from the hangman: \n");
        write("   - \"Please save me, I will give you a Chocolate Bar\"\n");
        write("                                             - Hangman\n\n");
        write("_______________________________________________________________________________________________________________\n\n");

    }

    public static void main(String[] args) throws Exception {
        new Main().run();
    }
}
