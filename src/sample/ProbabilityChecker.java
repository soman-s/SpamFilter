package sample;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.io.*;
import java.util.*;

public class ProbabilityChecker {

    Map<String, Double> spamProbability = new TreeMap<>();
    int filesCounted = 0;
    int truePositives = 0;
    int trueNegatives = 0;
    int falsePositives = 0;
    int falseNegatives = 0;

    /**
     * The constructor for the class
     * @param spamProbability takes a map that contains the spam probabilities of certain words
     */
    public ProbabilityChecker(Map spamProbability){
        this.spamProbability = spamProbability;
    }

    /**
     * helper function for calculate2, handles any errors with file paths
     * @param file the directory that will be passed to the parseFile function
     * @param isSpam a boolean value that tells the program if the file is spam or not,
     *               this is needed to tell if the result was a false positive/false negative/ true positive/ true negative
     */
    public double calculate(File file, boolean isSpam){
        try{
            return calculate2(file,isSpam);
        }catch(FileNotFoundException e){
            System.err.println("Invalid input dir: " + file.getAbsolutePath());
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * This function goes through all the words in a file and compares those words to the Map of probabilities,
     * it uses the map to calculate the total probability of the file being spam considering all the words within it
     * formula is 1/(1+e^n) where n = the summation from 1 to N of ln(1-P(s|w))-ln(P(s|w))
     * @param file the file that will be analysed
     * @param isSpam a boolean value that tells the program if the file is spam or not,
     *               this is needed to tell if the result was a false positive/false negative/ true positive/ true negative
     * @return a double that represents a percent chance the file is spam
     */
    public double calculate2(File file, boolean isSpam)throws IOException{
        filesCounted++;
        double n = 0;
        Scanner scanner = new Scanner(file);
        // scanning token by token
        while(scanner.hasNext()){
            String token = scanner.next();
            token = token.toUpperCase();
            if (isValidWord(token)){
                if(spamProbability.containsKey(token)) {
                    n += (Math.log(1-spamProbability.get(token)) - Math.log(spamProbability.get(token)));
                }
            }
        }

        double probability = 1 / (1+Math.pow(Math.E,n));
        // if probability is greater than 0.5 than program marks as spam
        if(probability >= 0.5 && isSpam){
            truePositives++;
        }else if(probability<0.5 && !isSpam){
            trueNegatives++;
        }else if(probability >= 0.5 && !isSpam){
            falsePositives++;
        }else{
            falseNegatives++;
        }

        return probability;
    }

    /**
     * checks if a passed word is valid according to a set criteria
     * @param token takes a map that contains the spam probabilities of certain words
     * @return true if the word is valid false otherwise
     */
    public boolean isValidWord(String token){
        String allLetters = "^[a-zA-Z]+$";
        // returns true if the word is composed by only letters otherwise returns false;
        return token.matches(allLetters);
    }

    /**
     * returns how accurate the ProbabilityChecker is
     * @return a double that contains a percentage accuracy
     */
    public double getAccuracy(){
        if(filesCounted == 0){
            return 0.0;
        }
        return (double)(trueNegatives + truePositives) / (double)filesCounted;
    }

    /**
     * returns the precision of the ProbabilityChecker
     * @return a double that contains a percentage precision
     */
    public double getPrecision(){
        if(filesCounted == 0){
            return 0.0;
        }
        return (double)truePositives / (double)(truePositives + falsePositives);
    }

}
