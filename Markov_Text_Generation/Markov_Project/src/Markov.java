/** Abstract
 * @author Ethan Bonavida
 * @since   March 13 2023
 *  Description: This program will read a text file and generate a collection of words and words that follow that word.
 *  Those words will be used to generate new text. This is just a single Markov chain, but there are higher orders of Markov chains.
 * @version 1.04.F
 * check version 0.03.d for any notes from before, has all old comments
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * * VERSIONS
 *  - 0.01.a : setup the project, add required fields and members
 *  - 0.02.a: add names, double check keywords. Then map out project
 *  - 0.02.b: deal with file, read from file, and fill the map of words from the file.
 *  - 0.02.c: continue with adding words to the map and checking the punctuation. gotta figure this out
 *  - 0.03.a: NTS: the words with the punctuation will determine a new line, and then a word from __$; fix error portion and test to make sure it really is all working; added toString return; added function abstracts;
 *  - 0.03.b: define getSentence() and randomWord();
 *  - 0.03.c: added exception to print line
 *  - 0.03.d: tested with MarkovTest.java, fixed up small issues; noted through out. passed all tests this version :)
 *  - 0.04.a: double check all TODOs.. go through and clean up comments
 *  - 1.04.F: DONE
 *  TODO: ask about todo's in here. otherwise version passed everything with flying colors :)
 */
public class Markov {
    private static final int EXIT =0;
    private static final byte MAX_LOOP = 50;
    private static final char SPACE = ' ';
    // words that end in punctuation?
    private static final String ENDS_IN_PUNCTUATION = "__$"; // initializes, and then is used to check the prev word
    // ** prob can look in the last element of the string ( string.length-1)
    private static final String PUNCTUATION_MARKS=".!?$";
    // ** this word stores the previous word read from file. determines how to process current word.
    // if the prev word ends in punctuation, then the current words
    private String prevWord;

    // ** list of all words in the text file, the first word is "key", then second is added to the "value"
    /*
    It is raining when it is cloudy.
    Our structure will looks something like the following:
    __$=[It], raining=[when], is=[raining, cloudy.], It=[is], it=[is], when=[it]
     */
    // TODO words vs getWords()
    private HashMap<String, ArrayList<String>> words; // key: String , value : List of Strings


    // ** CLASS INITIALIZERS ***
    // initialize the hashmap with @START_OF_SENTENCE and new array list. @prevWord = @ENDS_IN_PUNCTUATION
    Markov () {
        this.words = new HashMap<String, ArrayList<String>>();
        this.words.put(ENDS_IN_PUNCTUATION, new ArrayList<String>()); // explicit typing?
        this.prevWord = ENDS_IN_PUNCTUATION;
    }
    // ** getter that returns @words **
    HashMap<String, ArrayList<String>> getWords(){
        return this.words;
    } // should be using this???? ( looks like its for testing purposes


    // *** METHODS ***
    // ** open file and then read each word ( space seperated)

    /**
     * @param filename the name of the text file to be opened and read through
     */
    public void addFromFile(String filename){
        // use filename to open and read file into the hashamp ( catch errors )
        // call addLine to parse each line
        File input_file = new File(filename);
        Scanner read_file=null;

        try {
            if ( input_file.createNewFile()) {
                //System.out.println("Hurray! created file: " + filename);
            }
            else {
                //System.out.println(filename + " was already created. Generating now...");
            }
            read_file= new Scanner(input_file); // open the file to be read
        } catch (FileNotFoundException e) {
            System.out.println("ERROR - file was not found..." +e);
            e.printStackTrace();
            System.exit(EXIT);
        } catch (IOException e) {
            System.out.println("ERROR - I/O broke..."+e);
            e.printStackTrace();
            System.exit(EXIT);
        }

        while (read_file!= null && read_file.hasNext()) {
            String line = read_file.nextLine().trim(); // read one line from the file to be passed, parsed, and added correctly, and trim leading and trailing spaces,
            // add check for empty string line?
            addLine(line); // pass and parse
        }
       // System.out.println("successfully read through all words!");
    }

    /**
     * @param line_from_file a line read from the text file; this will produce the set of words to be generated later from getSentence()
     */
    public void addLine(String line_from_file){
        // check if @line_from_file is not null ( 0 length)
        if (line_from_file.length() > 0) {
            // then split the line into words, then these words are passed into addWord
            String[] temp_str = line_from_file.trim().split("\\s+"); // whitespace regex https://stackoverflow.com/questions/7899525/how-to-split-a-string-by-space
            for (int word_index=0; word_index < temp_str.length; word_index++) {
                // first check the current word
                addWord(temp_str[word_index]); // should get the split strings, and can go through and add each word to @
                // after the current word is added, make it the previous word now.
                // so need to make @prevWord this current word? @prevWord is initialized to "__$", so needs to be new word
                // prevWord = temp_str[word_index]; // set to current word? TODO !-! i had this here and it works, but fails your test. does it make a difference?
            }
        }
        else {
            // System.out.println("Line was empty! no markov today!"); // when the line is empty, this block is run, so do nothing and continue to line with words
        }
    }

    /**
     * @param next_word_from_file the next word from the file, ahead by one word. Based on conditions will either be added to __$ key or added to already known key
     */
    void addWord(String next_word_from_file){
        // call endsWithPunctuation to check if prevword ended a sentence
        if( endsWithPunctuation(prevWord) ) {
            // if so then the current word is added to "__$" key
            getWords().get(ENDS_IN_PUNCTUATION).add(next_word_from_file);
            // after the current word is added, make it the previous word now.
            // so need to make @prevWord this current word? @prevWord is initialized to "__$", so needs to be new word
            prevWord=next_word_from_file; // TODO !-! should this be here?
        }
        else { // if not, then this stuff:
            // if the read char DOES NOT exist in key of hashmap, add this word to the key, and the next word as the value?
            if ( !getWords().containsKey(prevWord) ) { // check for prev word? this is "current" word, so would be new to add
                // create temp array for typing, add the next word to be the value
                ArrayList<String> temp_array = new ArrayList<String>();//explicit typing?
                temp_array.add(next_word_from_file);
                getWords().put(prevWord,temp_array); // add new key, and add new value to it.
                prevWord=next_word_from_file; // previous becomes current word for next iteration
            }
            // else read char DOES exist and so add the next word to the value of already existing key
            else {
                getWords().get(prevWord).add(next_word_from_file);
                prevWord=next_word_from_file; // previous becomes current word for next iteration
            }
        }
        // could add @prevword here
    }

    /**
     * @param current_word_from_file the current word to check for punctuation; punctuation means a sentence is ending, and need to account for word that starts new sentence
     * @return true when there is punctuation; false when there is no punctuation
     */
    public static boolean endsWithPunctuation(String current_word_from_file) {
        // check if the last char in the prevword == @PUNCTUATION_MARKS
        try {
            for (byte sub_index =0; sub_index < PUNCTUATION_MARKS.length(); sub_index++) {
                // is the last char equal to any of the chars in @PUNCTUATION_MARKS
                if ( current_word_from_file.charAt(current_word_from_file.length()-1) == PUNCTUATION_MARKS.charAt(sub_index) ) {
                    // if so, return true
                    return true;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e){
            // check for any errors ( i assume anything not ascii based, or in @PUNCTUATION_MARKS
            // catch then print the error word out
            // array out of bounds? null string. add try catch
            System.out.println("ERROR - Something went very wrong. Array out of bounds...");
            e.printStackTrace();
            System.exit(EXIT);
        }
        // else not in at all, no punctuation
        return false;
    }

    // ** creates the sentences to be displayed, calling randomWord() and first initializing from @ENDS_IN_PUNCTUATION
    public String getSentence(){
        // sentence to be constructed
        // first word is from @ENDS_IN_PUNCTUATION "__$"
        String current_word =randomWord(ENDS_IN_PUNCTUATION); // get the first word to start the sentence
        String sentence = "";  // sentence starts blank to avoid extra spacing; ( guess i could have deleted SPACE here but this works too)

        // if the current word does not end in punc, then concat to string ( look up string builder?)
        while ( !endsWithPunctuation(current_word)) {
            sentence += current_word+SPACE;
            current_word = randomWord(current_word);
        }
        // for the final and last word that DOES end in punctuation, just add the current word to the end!
        sentence += current_word;

        // if this current word ends in punc. then stop? end of sentence for now, return the sentence that has been created.
        return sentence;
    }

    /**
     * @param key_from_file the key given to look through the values of this key and choose a random word.
     * @return the random word found back to construct sentence to be generated
     */
    String randomWord(String key_from_file){
        // takes a string ( current word as key), and the looks through the arraylist and returns a random word from that
        // random between 0 and last element in @ENDS_IN_PUNCTUATION
        Random random_int = new Random(); // TODONE check the randomness values; seems to be random enough
        int first_word_index = 0;
        String temp_first_word = "";

        // if not then randomly choose one of the values to return back to add to the sentence.
        first_word_index=random_int.nextInt(getWords().get(key_from_file).size()); // hopefully returns a random integer from 0 - size of first word key: "__$"
        temp_first_word= getWords().get(key_from_file).get(first_word_index);

        return temp_first_word;
    }
    @Override // Overide the already existing toString method ** for Junit testing purposes **
    public String toString(){
        // returns the strings of the haspmap words.
        // ** TESTING PURPOSES ONLY **
        return words.toString(); // should return all the words/strings from @words
    }

    // *** MAIN DRIVER ***
    public static void main(String[] args) {
        Markov program = new Markov(); // should initialize the program .
        // TODO which file to leave for submission? or none?
        program.addFromFile("phrases.txt"); // takes the file name to apply morkov text generation to. fills up map @words
        //program.addFromFile("spam.txt");
        //program.addFromFile("hamlet.txt"); // TODONE bug with this guy... ( jk it just reads newlines as blank lines so prints out) so fix that check bookmark

        System.out.println(program);
        // then can loop and grab as many sentences possible from @words  using getSentence()
        for (byte i =0; i < MAX_LOOP; i++) {
            System.out.println(program.getSentence());
        }

    } // end main
}