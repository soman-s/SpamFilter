package sample;
import java.io.*;
import java.util.*;

public class WordCounter{

    private Map<String, Integer> wordCounts;
    private int numberOfFiles = 0;

    public WordCounter(File file){
        wordCounts = new TreeMap<>();
        parse(file);
    }

    /**
     * helper function for parseFile, handles any errors with file paths
     * @param file the file or directory that will be passed to the parseFile function
     */
    public void parse(File file){
        try{
            parseFile(file);
        }catch(FileNotFoundException e){
            System.err.println("Invalid input dir: " + file.getAbsolutePath());
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Reads every token in a file or directory and updates a map to keep track of results (how many files contain each token)
     * @param file The file or directry that will have it's contents searched.
     */
    public void parseFile(File file) throws IOException{
        //System.out.println("Starting parsing the file:" + file.getAbsolutePath());
        if(file.isDirectory()){
            //parse each file inside the directory
            File[] content = file.listFiles();
            for(File current: content){
                parseFile(current);
            }
        }else{
            numberOfFiles++;
            ArrayList<String> wordsInFile = new ArrayList<String>(50);
            Scanner scanner = new Scanner(file);
            // scanning token by token
            while (scanner.hasNext()){
                String  token = scanner.next();
                token = token.toUpperCase();
                if (isValidWord(token)){
                    if(!wordsInFile.contains(token)){
                        countWord(token);
                        wordsInFile.add(token);
                    }
                }
            }
        }
    }

    /**
     * checks if the word is valid based on a criteria defined in the function
     * @param word the word beinge evaulated.
     * @return if the word meets the criteria(T) or not(F).
     */
    private boolean isValidWord(String word){
        String allLetters = "^[a-zA-Z]+$";
        // returns true if the word is composed by only letters otherwise returns false;
        return word.matches(allLetters);

    }

    /**
     * Adds a key to the map / keeps track of how many files contain that key
     * @param word The key that the map will increase the value of / keep track of.
     */
    private void countWord(String word){
        if(wordCounts.containsKey(word)){
            int previous = wordCounts.get(word);
            wordCounts.put(word, previous+1);
        }else{
            wordCounts.put(word, 1);
        }
    }

    /**
     * @param word The word to search for in the map.
     * @return The number of files the word was found in.
     */
    public int getCount(String word){
        word = word.toUpperCase();
        if(wordCounts.containsKey(word)){
            return wordCounts.get(word);
        }

        else return 0;
    }

    /**
     * @return a set of the maps keys.
     */
    public Set<String> getKeys(){
        return wordCounts.keySet();
    }

    /**
     * @return How many files were counted when creating the map.
     */
    public int getFileCount(){
        return numberOfFiles;
    }

}