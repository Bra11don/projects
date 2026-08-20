import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Scanner;

/*
stores all the dictionary data and provides methods for solving the puzzle
 */
public class Letterman {

    //var to keep track of the words we'll process = 0, and var to keep count of the words we visited =1
    int count= 0;
    int vistdwords = 1;


    private Config c;

    // variable to store our dictionary
    private ArrayList<WordInfo> dictionary;
    private int beginIndex = -1;
    private int endIndex = -1;

    public Letterman (Config c){
        this.c = c;
    }

    /*
    read the dictionary from standard input
     */
    public void readDictionary() {
        // Scanner object
        Scanner in = new Scanner(System.in);

        // get number of words in dictionary
        int count = in.nextInt();

        // read to the end of the line
        in.nextLine();

        // construct our AL
        dictionary = new ArrayList<>(count);


        //read all the words
        while (in.hasNextLine()) {
            String line = in.nextLine();

            // check for blank line
            if (line.length() == 0) {
                break;
            }

            // FIXME there is a bug
            if (line.charAt(0) == '/' && line.charAt(1) == '/') {
                // comment
                continue;
            }

            //check if this is the beginWord
            //debug and make sure this works
            if (line.equals(c.getBeginWord())){
                beginIndex = dictionary.size();
            }
            else if(line.equals(c.getEndWord())){
                endIndex = dictionary.size();
            }


            // Adding the word to dictionary
            //if we have begin and end words of the same length, we know that lengthmode is not required
            //so we are only adding words with the same length (not more or less)
            //this approach is more time efficient
            if (!c.isLengthMode()){
                if ((line.length() == c.getBeginWord().length()) ){
                    dictionary.add(new WordInfo(line));
                }
            }
            //if not, we can then add all other words into the dictionary
            else{
                dictionary.add(new WordInfo(line));
            }

        }

        //checking if begin and end words are valid words in the dictionary
        if (beginIndex == -1){
            System.err.println("begin word does not exist in the dictionary");
            System.exit(1);
        }
        if (endIndex == -1){
            System.err.println("end word does not exist in the dictionary");
            System.exit(1);
        }

        // print the size of the dictionary
        System.out.println("Words in dictionary: " + count);

    }

    /**
     *  search from a beginning word to an end word
     */
    public void search(){

        //deque to keep track of our reachable collection
        //store the index of the word we are processing from our dictionary Array List
         ArrayDeque<Integer> processing = new ArrayDeque<>();

        //initially populate this with the starting word
        //mark as visited and add to the deque
        dictionary.get(beginIndex).visited = true;

        //adding to Stack or queue respectively
        if (c.isStackMode()){
            processing.addFirst(beginIndex);
            if (c.isCheckpoint2()) {
                System.out.println("  adding " + dictionary.get(beginIndex).text);
            }
        }else{
            processing.addLast(beginIndex);
            if (c.isCheckpoint2()) {
                System.out.println("  adding " + dictionary.get(beginIndex).text);
            }
        }

        //if a word hasnt been added before and it's not the end word - process then add
        while (!processing.isEmpty() && !dictionary.get(endIndex).visited){
            //remove the next element
            int currIdx = processing.removeFirst();
            WordInfo curr = dictionary.get(currIdx);

            //var counting words to be processed++
            count++;

            if (curr.text.equals(c.getBeginWord())){
                curr.previous = -1;
            }
            if (c.isCheckpoint2()) {
                System.out.println(count + ": processing " + curr.text);
            }
            //loop through dictionary and check for sufficiently similar items
            for (int i = 0; i<dictionary.size();i++){
                //skip ourselves
                if (currIdx == i){
                    continue;
                }

                WordInfo other = dictionary.get(i);
                //if the word is not visited and it sufficiently similar to the current word - process then add to the stack/queue
                if (!other.visited && sufficientlySimilar(curr.text, other.text)){
                    //visit and add
                    other.visited = true;
                    other.previous = currIdx;

                    //FIXME add to the deque
                    if (c.isStackMode()){
                        processing.addFirst(i); //addFirst for stack
                        if (c.isCheckpoint2()) {
                            System.out.println("  adding " + other.text);
                        }
                    }else{
                        processing.addLast(i); //addLast for Queue
                        if (c.isCheckpoint2()) {
                            System.out.println("  adding " + other.text);
                        }
                    }
                    vistdwords++;
                    if (processing.contains(endIndex)){ //if we have found the end word then the program should stop there
                        break;
                    }
                }
            }
        }
        printing(); // call the printing method here
    }


    private boolean sufficientlySimilar(String a, String b) {
        //change: 1 character difference
        // swap : 2 character different with the 2 characters adjacent and swapped
        //length: 1 character different adn 1 character length difference

        int charDifference = 0; //keep track of the difference in characters
        int lengthDifference = 0; //keep track of the difference in length of words..
        int swapDiff = 0; //keep track of character swap instance

        //TODO: performance tuning: are there ways to make this code faster?
        //are there ways to return as soon as we know we've failed to be sufficiently similar
        if (a.length() == b.length()){ // if the words are the same length
            //only swap and change will apply
            //go character by character to check for equivalence
            for (int i = 0; i<a.length();i++){

                //if the characters are different then increment the charDiff value
                if (a.charAt(i) != b.charAt(i)){
                    charDifference++;
                }
            }

            if (c.isChangeMode()) { //in change mode only one character difference should be counted
                if (charDifference <= 1) {
                    return true; //return when chaDiff is 2
                }
            }

            // in swap mode, have swapDiff increment when charDiff is 2
            // a charDiff of 2 means that the adjacent characters have switched positions
            if (c.isSwapMode()){
                    //TODO: hint: it might be helpful to keep track of the index of the change
                    for (int i = 0; i < a.length() -1; i++) {
                        int j = i + 1;
                        if ((a.charAt(i) != b.charAt(i) && (a.charAt(j) != b.charAt(j)))) { //checking if character at positions i adn j in both words are not equal
                            if ((a.charAt(i) == b.charAt(j) && (a.charAt(j) == b.charAt(i)))) { //if they are not equal then check if a position swap has happened
                                if (charDifference == 2) {
                                    swapDiff++;
                                }
                            }
                        }
                    }
                    return swapDiff == 1; //only count if 1 swap has happened
            }

            }
        else { // if the words are different in length then lengthmode is applied
            // find which word among the 2 is shorter, to check for either insertion or deletion
            //iff there is a one character difference then increment lengthdifference
            if (b.length() - a.length() == 1){ //b is the longer word, a is the shorter word
                int j = 0;
                for (int i = 0; i< a.length(); i++){
                    if (a.charAt(i) != b.charAt(j)){
                            lengthDifference++;
                            i--;
                    }
                    j++;
                    if (lengthDifference>1){
                        return false;
                    }
                }
                //checking if lengthdifference is 0, and the characters at the end of the strings are either similar or different.
                //if that's the case then return true
                //otherwise just return when there is a onecharacter length difference
                if (lengthDifference == 0 && (a.charAt(a.length()-1) != b.charAt(b.length()-1))){
                    return true;
                }
                if (lengthDifference == 0 && (a.charAt(a.length()-1)== b.charAt(b.length()-1))) {
                    return true;
                }else {
                    return lengthDifference == 1;
                }
            } else if (a.length() - b.length() == 1) {
                int j = 0;
                for (int i = 0; i< b.length(); i++){
                        if (b.charAt(i)!= a.charAt(j)){
                            lengthDifference++;
                            i--;
                        }
                        j++;
                        if (lengthDifference>1){
                        return false;
                    }
                }
                //checking if lengthdifference is 0, and the characters at the end of the strings are either similar or different.
                //if that's the case then return true
                //otherwise just return when there is a onecharacter length difference
                if (lengthDifference == 0 && (b.charAt(b.length()-1) != a.charAt(a.length()-1))){
                    return true;
                } else if (lengthDifference == 0 && (b.charAt(b.length()-1)== a.charAt(a.length()-1))) {
                    return true;
                }else {
                    return lengthDifference == 1;
                }
            }
        }
        return false;
    }

    //this method prints whether a solution is found or not..
    //it's one of the 2 outputs that must always appear despite the checkpoint
    public void printing(){
        if (!(dictionary.get(endIndex).visited)) {
        System.out.println("No solution, " + vistdwords + " words checked.");
        System.exit(0);
    } else {
        System.out.println("Solution, " + vistdwords + " words checked."); //once we've found a solution then output for M or W respectively
        outputmodes();
        System.exit(0);
    }
    }

    /**
     * output the modification to go from string a to string b
     * at this point we already know one of the morphs applies, we just have to know which one
     *
     * @param a - starting word for the morph
     * @param b - ending word for the morph
     * @return
     */
    private void modificationOutput(String a , String b){
        //need to find the first difference between String a and b
        int pos = 0;
        //length of the shorter string minus 1
        // if the 2 strings are different, we only wanna loop through the shorter string
        int maxPosition = Math.min(a.length(), b.length()) ;
        while (pos<maxPosition){
            //check for a difference
            if (a.charAt(pos)!= b.charAt(pos)){
                //we have found the position of the change
                break;
            }
            pos++;
        }
        //position is either 1) the position of the change or (2) the index of the last character in the longer string
        // change, swap, insert or delete?
        if (a.length() == b.length()){
            //TODO change or swap
            int charDifference = 0;
            for (int i = 0; i < a.length();i++){
                if (a.charAt(i)!=b.charAt(i)){
                    charDifference++;
                }
            }
            if (charDifference == 1){
                //once the chardifference is 1 we know that there was a change
                System.out.println("c," + pos + "," + b.charAt(pos));
            }else {
                //once char difference is more than one? then a swap happened
                System.out.println("s," + pos);
            }
        } else if (a.length()<b.length()) {
            //insert
            //format ya i <something> <something> kwenye videooo
            // string b will be longer, so we neeed the character from b at this position

            //Ex: 0123
            //from let to leet
            System.out.println("i," + pos + "," + b.charAt(pos));
        }
        else {
            //TODO delete
            //then we know string b is shorter and a deletion happened
            System.out.println("d," + pos  );
        }

    }

    public void outputmodes(){
        //Create an Arraylist to store the words
        ArrayList<String> backpath = new ArrayList<>();

        //initialize the currword to start at the end word instead, so that way we're moving backwards
        int currword = endIndex;

        //as long as we havent reached the beginning word then add the word to backpath array, then get the previous word
        while(currword != -1) {
            backpath.add(0,dictionary.get(currword).text);
            currword = dictionary.get(currword).previous;
        }

        System.out.println("Words in morph: " + backpath.size()); //print the number of words in the arraylist

        if (c.isWordOutput()){ //output for WORD
            for (int i = 0; i<backpath.size(); i++){
                System.out.println(backpath.get(i)); //print the words that are in Backpath..
            }
        }
        else{ //output for modification.. modification needs to call modification output so that we can specifically detect either s,c,i or d
            System.out.println(backpath.get(0)); //print out the starting word
            int j= 1; //initialize the count of the words
            for (int i = 0; i< backpath.size(); i++){
                //loop through the arraylist, then call on modification output method to compare the words and print the respective changes
                if (j >= backpath.size()){
                    break;
                }
                modificationOutput(backpath.get(i), backpath.get(j) ); //
                j++;
            }
        }

    }


    /**
     *  output all words in the dictionary
     */
    public void printDictionary() {
        for(WordInfo w : dictionary) {
            System.out.println(w.text);
        }
    }

    private static class WordInfo{
        String text;
        boolean visited;

        //previous to keep track of the precious word as we are backtracking
        int previous;

        public WordInfo(String text){
            this.text = text;
            this.visited = false;
        }

    }
}