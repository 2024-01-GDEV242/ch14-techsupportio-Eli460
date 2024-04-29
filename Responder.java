import java.util.*;
import java.io.*;

/**
 * The responder class represents a response generator object.
 * It is used to generate an automatic response, based on specified input.
 * Input is presented to the responder as a set of words, and based on those
 * words the responder will generate a String that represents the response.
 *
 * Internally, the reponder uses a HashMap to associate words with response
 * strings and a list of default responses. If any of the input words is found
 * in the HashMap, the corresponding response is returned. If none of the input
 * words is recognized, one of the default responses is randomly chosen.
 * 
 * @author David J. Barnes and Michael Kölling.
 * @version 2016.02.29
 */
public class Responder
{
    private HashMap<String, String> responseMap;
    private ArrayList<String> defaultResponses;
    private static final String FILE_OF_DEFAULT_RESPONSES = "default.txt";
    private Random randomGenerator;
    /**
     * Construct a Responder
     */
    public Responder()
    {
        responseMap = new HashMap<>();
        defaultResponses = new ArrayList<>();
        fillResponseMap();
        fillDefaultResponses();
        randomGenerator = new Random();
        
    }
        public String generateResponse(HashSet<String> words)
    {
        Iterator<String> it = words.iterator();
        while(it.hasNext()) {
            String word = it.next();
            String response = responseMap.get(word);
            if(response != null) {
                return response;
            }
        }
        /** If we get here, none of the words from the input line was recognized.
         * In this case we pick one of our default responses (what we say when
         * we cannot think of anything else to say...)
         */
        return pickDefaultResponse();
    }
    
/** Reads from a specific file in this case FILE_OF_DEFAULT_RESPONSES 
 * it will read the file line by line until it reaches the end 
 * in the case it cannot find a response it will print out 
 * a default message in this case "Could you elaborate on that?"
 */

private void fillDefaultResponses() {
    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_OF_DEFAULT_RESPONSES))) {
        String line;
        String[] responseArray = new String[10]; // No Response has longer than 10 lines
        int responseIndex = 0;
        String key = null;
        // Will read each line from the file
        while ((line = reader.readLine()) != null) {
            // Will check if the line is empty
            if (line.trim().isEmpty()) {
                if (key != null && responseIndex > 0) {
                    defaultResponses.add(String.join("\n", Arrays.copyOf(responseArray, responseIndex)));
                    Arrays.fill(responseArray, null);
                    responseIndex = 0;
                }
                key = null;
                continue;
            }
            if (key == null) {
                key = line;
            } else {
                responseArray[responseIndex++] = line;
            }
        }
        // Add the last response if any
        if (responseIndex > 0) {
            defaultResponses.add(String.join("\n", Arrays.copyOf(responseArray, responseIndex)));
        }
    } catch (IOException e) {
        System.err.println("Unable to read " + FILE_OF_DEFAULT_RESPONSES);
    }
    // Ensure at least one default response
    if (defaultResponses.isEmpty()) {
        defaultResponses.add("Could you elaborate on that?");
    }
}

/** Reads from a specific file in this case responses.txt 
 * that contains all of the phrases on a separate files 
 */
private void fillResponseMap() {
    try (BufferedReader reader = new BufferedReader(new FileReader("responses.txt"))) {
        String line;
        String key = null;
        String[] valueArray = new String[5]; // No response has longer than 5 lines
        int valueIndex = 0;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) {
                if (key != null && valueIndex > 0) {
                    responseMap.put(key, String.join("\n", Arrays.copyOf(valueArray, valueIndex)));
                    Arrays.fill(valueArray, null);
                    valueIndex = 0;
                }
                key = null;
                continue;
            }
            if (key == null) {
                String[] parts = line.split(",", 2); // Split only at the first comma
                key = parts[0].trim();
                if (parts.length > 1) {
                    valueArray[valueIndex++] = parts[1].trim();
                }
            } else {
                valueArray[valueIndex++] = line;
            }
        }
        // Add the last response if any
        if (key != null && valueIndex > 0) {
            responseMap.put(key, String.join("\n", Arrays.copyOf(valueArray, valueIndex)));
        }
    } catch (IOException e) {
        System.err.println("Unable to read responses file.");
    }
}



    /**
     * Randomly select and return one of the default responses.
     * @return     A random default response
     */
    private String pickDefaultResponse()
    {
        // Pick a random number for the index in the default response list.
        // The number will be between 0 (inclusive) and the size of the list (exclusive).
        int index = randomGenerator.nextInt(defaultResponses.size());
        return defaultResponses.get(index);
    }
}
