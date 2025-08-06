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
        File sampleData= new File("sampleData.txt");

        try {
            if (!storage.exists()) {
                storage.createNewFile();

                // Build wordPairs from sampleData
                for (String[] line : words) {
                    for (int j = 0; j < line.length - 1; j++) {
                        String current = line[j];
                        String next = line[j + 1];

                        if(!wordPairs.containsKey(current)) {
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
                //rebuild wordPairs if sampleData is modified
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
        String[] inputArray = inputData.toLowerCase().split(" ");

        if (inputArray.length == 0)
            return;

        String currentWord = inputArray[0];
        sentences.add(currentWord);

        for (int i = 0; i < steps; i++) {
            ArrayList<String> nextWords = getNextWords(currentWord);
            if (nextWords.isEmpty())
                break;

            int randomIndex = (int) (Math.random() * nextWords.size());
            currentWord = nextWords.get(randomIndex);
            sentences.add(currentWord);
    
        }

        System.out.println("Generated Text:");
        System.out.println(String.join(" ", sentences));
    }
    public static void textSelection(String word) {
        

    }

    public static void main(String[] args) {
        textGen test = new textGen();
        test.loop(); // Build the wordPairs map
        test.generateText("How are you", 10);

        // String currentWord = "The".toLowerCase(); // Starting word
        // int steps = 10; // Number of words to generate

        // System.out.print(currentWord + " ");

        // for (int i = 0; i < steps; i++) {
        // ArrayList<String> nextWords = test.getNextWords(currentWord);
        // if (nextWords.isEmpty()) break; // Stop if no next word

        // // For now, just pick the first one
        // currentWord = nextWords.get(0);
        // System.out.print(currentWord + " ");
        // }
        // test.generateText("The quick brown fox jumps over the lazy dog");
    }

}
