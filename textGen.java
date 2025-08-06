import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class textGen {
    ArrayList<String[]> words;
    HashMap<String, ArrayList<String>> wordPairs;
    ArrayList<String> sentences;

    public textGen() {
        words = new ArrayList<String[]>();
        wordPairs = new HashMap<String, ArrayList<String>>();
        try {
            File sampleData = new File("sampleData.txt");
            Scanner scanner = new Scanner(sampleData);
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine().toLowerCase();
                // System.out.println(data);
                String[] line = data.split(" ");
                if (line.length == 0)
                    continue;
                words.add(line);
            }
            scanner.close();
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void loop() {
        File storage = new File("wordPairMap.txt");
        File sampleData = new File("sampleData.txt");

        try {
            if (!storage.exists()) {
                storage.createNewFile();

                // Build wordPairs from sampleData
                for (String[] line : words) {
                    for (int j = 0; j < line.length - 1; j++) {
                        String current = line[j];
                        String next = line[j + 1];

                        if (!wordPairs.containsKey(current)) {
                            wordPairs.put(current, new ArrayList<>());
                        }
                        wordPairs.get(current).add(next);
                    }
                }

                // Write wordPairs to file
                try (FileWriter writer = new FileWriter(storage)) {
                    for (String key : wordPairs.keySet()) {
                        String nextWordsString = String.join(",", wordPairs.get(key));
                        writer.write(key + " -> " + nextWordsString + "\n");
                    }
                }

            } else {
                // rebuild wordPairs if sampleData is modified
                if (sampleData.lastModified() > storage.lastModified()) {
                    wordPairs.clear();
                    for (String[] line : words) {
                        for (int j = 0; j < line.length - 1; j++) {
                            String current = line[j];
                            String next = line[j + 1];

                            if (!wordPairs.containsKey(current)) {
                                wordPairs.put(current, new ArrayList<>());
                            }
                            wordPairs.get(current).add(next);
                        }
                    }
                    try (FileWriter writer = new FileWriter(storage)) {
                        for (String key : wordPairs.keySet()) {
                            String nextWordsString = String.join(",", wordPairs.get(key));
                            writer.write(key + " -> " + nextWordsString + "\n");
                        }
                    } catch (Exception ex) {
                        System.out.println("An error occurred while writing to wordPairMap.txt.");
                        ex.printStackTrace();
                    }

                }
                // Read and display existing wordPairs

                try (Scanner scanner = new Scanner(storage)) {
                    while (scanner.hasNextLine()) {
                        String data = scanner.nextLine();
                        // System.out.println(data);
                        String[] parts = data.split(" -> ");
                        String key = parts[0];
                        String[] values = parts[1].split(",");
                        wordPairs.put(key, new ArrayList<>(Arrays.asList(values)));
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("An error occurred while handling wordPairMap.txt.");
            e.printStackTrace();
        }
    }

    public ArrayList<String> getNextWords(String word) {
        return wordPairs.getOrDefault(word, new ArrayList<>());
    }

    public void printData() {
        for (String[] line : words) {
            for (String word : line) {
                System.out.print(word + " ");
            }
            System.out.println();
        }
    }

    public void generateText(String inputData, int steps) {
        sentences = new ArrayList<>();
        String[] inputArray = inputData.toLowerCase().split("\\s+");
        // for(String word : inputArray) {
        //     System.out.print(word + " ");
        // }
    
        if (inputArray.length == 0) return;
    
        for(int i = 0; i < inputArray.length; i++) {
            String currentWord = inputArray[i];
            sentences.add(currentWord);
            for (int j = 0; j < steps; j++) {
                ArrayList<String> nextWords = getNextWords(currentWord);
        
                if (nextWords == null || nextWords.isEmpty()) break;
        
                // Filter out words already used to avoid repetition
                ArrayList<String> filteredNextWords = new ArrayList<>(nextWords);
                filteredNextWords.removeAll(sentences);
        
                // If all next words were already used, fall back to original list
                if (filteredNextWords.isEmpty()) {
                    filteredNextWords = nextWords;
                }
        
                int randomIndex = (int) (Math.random() * filteredNextWords.size());
                String nextWord = filteredNextWords.get(randomIndex);
        
                sentences.add(nextWord);
                currentWord = nextWord;
            }
        }
        System.out.println("Generated Text:");
        System.out.println(String.join(" ", sentences));
    }


    public static void main(String[] args) {
        textGen test = new textGen();
        test.loop(); // Build the wordPairs map
        test.generateText("hows the weather", 10);
    }

}
