import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.*;
import org.apache.tika.Tika;

public class QuestionFileScraper {

    public static void main(String[] args) {
//        // Defining the file
//        QuestionFileScraper scraper = new QuestionFileScraper("Q1.txt");
//
//        // Run
//        scraper.scrape();
//
//        // Show the result
//        String intent = scraper.analyzeIntent();
//        System.out.println("category: " + intent);
//
//        // for debug. show how many times each words appeared
//        scraper.printTopWords(5);


        // List of the file which we want to analyze
        //Should be editing later
        String[] targetFiles = {"Q1.txt", "Q2.txt", "Q3.txt", "Q4.pdf"};

        // List of the result, showing which file asking about what
        java.util.List<Result> allResults = new java.util.ArrayList<>();

        for (String fileName : targetFiles) {
            QuestionFileScraper scraper = new QuestionFileScraper(fileName);
            scraper.scrape();

            String intent = scraper.analyzeIntent();

            // keep the pair of (fileName, intent) here
            allResults.add(new Result(fileName, intent));
        }

        // check
        for (Result res : allResults) {
            System.out.println("File: " + res.fileName + " -> Intent: " + res.intent);
        }
    }

    public java.util.List<String> myArray = new java.util.ArrayList<>();
    public String qFile;
    public QuestionFileScraper(String qFile) {
        this.qFile = qFile;
    }

    public void scrape() {
        // Prepare the file to read. Should be remake lately
        // Use "Apache" to change multiple file tyoe into .txt
        File file = new File(this.qFile);

        // Check the file is existing or not
        if (!file.exists()) {
            System.out.println("Error: can't find the file. -> " + this.qFile);
            return;
        }

        // Before doing new file, reset the table.
        wordFrequencyMap = new SimpleMap<>();

//        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                // Call tokenize
//                tokenize(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        String line = "";
        try {
            // Making Tika instance
            Tika tika = new Tika();

            // Tika will change every file type into line.txt
            line = tika.parseToString(file);

            // through line.txt to tokenizeS
            tokenize(line);

        } catch (Exception e) {
            System.out.println("Tika error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public SimpleMap<String, Integer> wordFrequencyMap = new SimpleMap<>();

    private void tokenize(String text) {
        //Unimportant words list
        java.util.Set<String> stopWords = new java.util.HashSet<>(java.util.Arrays.asList(
                "a", "an", "the", "and", "or", "but", "if", "then", "else", "is", "are", "was", "were", "that", "this", "to", "of", "for", "in", "on", "at", "by"
        ));

        // " ", tab, or comma will separate the sentence.
        String[] words = text.split("[,\\s\\t]+");

        for (String word : words) {
            String cleanWord = word.toLowerCase().trim();

            // If the word is not empty and not in the stopWords list, add it to the table.
            if (!cleanWord.isEmpty() && !stopWords.contains(cleanWord)) {
                // --- ここで SimpleMap に回数を記録 ---
                Integer count = wordFrequencyMap.get(cleanWord);
                if (count == null) {
                    wordFrequencyMap.put(cleanWord, 1);
                } else {
                    wordFrequencyMap.put(cleanWord, count + 1);
                }

                //no need maybe
                myArray.add(cleanWord);
            }
        }
    }

    public String analyzeIntent() {
        // Map which collecting the score of each category
        SimpleMap<String, Integer> categoryScores = new SimpleMap<>();

        // --- Define what file asking about ---
        // If there is this word, than it asking about this. Should edit later
        SimpleMap<String, String[]> dictionary = new SimpleMap<>();
        dictionary.put("TECHNICAL_ISSUE", new String[]{"error", "broken", "setup", "how", "manual", "not working"});
        dictionary.put("BILLING_INQUIRY", new String[]{"price", "cost", "invoice", "billing", "payment", "money"});
        dictionary.put("LOGISTICS_INFO", new String[]{"shipping", "delivery", "track", "when", "arrival", "address"});

        dictionary.put("Section 1: Company Overview", new String[]{"full", "name", "company", "legal", "company's",
                        "address", "main", "phone", "number", "who", "parent", "company", "country", "operation",
                        "product", "products", "service", "services", "offer"});
        dictionary.put("Section 2: Operations", new String[]{"administrative", "hour", "hours", "operation", "manufacturing",
                        "shift", "total", "number", "employees", "how many", "work", "office", "outside", "remote", "inficon kansas"});
        dictionary.put("Section 3: Business Registration & Legal Information", new String[]{"tax", "tax id", "AG", "TIN",
                        "classification", "CAGE", "Code", "SAM", "UEI", "GSA", "Contract", "number", "EMR", "state", "incorporated",
                        "DUNS", "Number", "Federal", "ID", "Commercial", "Registration", "Number", "Register", "Court", "CPSR", "Certified",
                        "incorporated", "Market", "Exchange", "listing", "Congressional", "District", "years", "business", "corporate",
                        "structure", "exempt"});
        dictionary.put("Section 4: Certifications", new String[]{"ISO", "certificate", "number", "validity", "period", "certifications", "contact"});
        dictionary.put("Section 5: Industry Classifications", new String[]{"Primary", "NAICS", "Code", "Secondary", "additional", "SIC", "List", "all", "UNSPSC", "found"});
        dictionary.put("Section 6: Business Classification", new String[]{"business", "size", "classification", "entity", "type", "Minority", "owned",
                        "Hub", "Zone", "Veteran", "owned", "Native", "Woman"});
        dictionary.put("Section 7: Leadership & Organization", new String[]{"CEO", "CFO", "Chairman", "Board" , "Directors", "members", "President",
                        "VP", "Finance", "Treasurer", "General", "Counsel", "Secretary"});

        // --- Scoring method ---
        // Check the whole words of wordFrequencyMap
        for (String word : wordFrequencyMap.size() > 0 ? wordFrequencyMap.keys() : new java.util.ArrayList<String>()) {
            for (String category : dictionary.keys()) {
                for (String keyword : dictionary.get(category)) {
                    if (word.contains(keyword)) {
                        // Add score when they appear
                        int count = wordFrequencyMap.get(word);
                        int currentScore = categoryScores.containsKey(category) ? categoryScores.get(category) : 0;
                        categoryScores.put(category, currentScore + count);
                    }
                }
            }
        }

        // --- Looking for the top score category ---
        String bestCategory = "UNKNOWN";
        int maxScore = 0;

        for (String cat : categoryScores.keys()) {
            if (categoryScores.get(cat) > maxScore) {
                maxScore = categoryScores.get(cat);
                bestCategory = cat;
            }
        }

        return bestCategory;
    }

    public void printTopWords(int limit) {
        System.out.println("\n--- [" + this.qFile + "] Top Keywords ---");

        // Get whole keys(Words) from SimpleMap
        java.util.List<String> keys = wordFrequencyMap.keys();

        if (keys.isEmpty()) {
            System.out.println("No words found.");
            return;
        }

        // regenerate depending on score(which means how many time they appeared)
        keys.sort((a, b) -> wordFrequencyMap.get(b).compareTo(wordFrequencyMap.get(a)));

        // Show wanted limited word
        for (int i = 0; i < Math.min(limit, keys.size()); i++) {
            String word = keys.get(i);
            System.out.println((i + 1) + ". " + word + " (" + wordFrequencyMap.get(word) + " times)");
        }
    }
}




class Result {
    String fileName;
    String intent;

    Result(String fileName, String intent) {
        this.fileName = fileName;
        this.intent = intent;
    }
}

class SimpleMap<K, V> implements Serializable {
    private static final long serialVersionUID = 1L;

    static final class Node<K, V> implements Serializable {
        final K key;
        V value;
        Node<K, V> next;

        Node(K k, V v, Node<K, V> n) {
            key = k;
            value = v;
            next = n;
        }
    }
    //Table Structure
    private Node<K, V>[] table;
    private int size = 0;

    @SuppressWarnings("unchecked")
    public SimpleMap() {
        table = new Node[16]; // Power of 2
    }

    // put. put new element to table
    public void put(K key, V value) {
        int h = key.hashCode();
        int i = h & (table.length - 1);
        for (Node<K, V> e = table[i]; e != null; e = e.next) {
            if (key.equals(e.key)) {
                e.value = value;
                return;
            }
        }
        table[i] = new Node<>(key, value, table[i]);
        if (++size >= table.length * 0.75f) resize();//75%埋まったら増築
    }

    //get
    public V get(K key) {
        int h = key.hashCode();
        int i = h & (table.length - 1);
        for (Node<K, V> e = table[i]; e != null; e = e.next) {
            if (key.equals(e.key)) return e.value;
        }
        return null;
    }

    //checking is there anything inside some key
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    //新しくリストを作って、そこにeがnullになるまで値を転送し続ける。そのリストを返す。
    public java.util.List<K> keys() {
        java.util.List<K> list = new java.util.ArrayList<>();
        for (Node<K, V> bucket : table) {
            for (Node<K, V> e = bucket; e != null; e = e.next) {
                list.add(e.key);
            }
        }
        return list;
    }

    public java.util.List<V> values() {
        java.util.List<V> list = new java.util.ArrayList<>();
        for (Node<K, V> bucket : table) {
            for (Node<K, V> e = bucket; e != null; e = e.next) {
                list.add(e.value);//eキーに格納されているvalueをリストにして返す。
            }
        }
        return list;
    }

    public int size() { return size; }

    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] oldTable = table;
        int oldCapacity = oldTable.length;
        int newCapacity = oldCapacity << 1;
        Node<K, V>[] newTable = new Node[newCapacity];

        for (int i = 0; i < oldCapacity; ++i) {
            Node<K, V> e = oldTable[i];
            while (e != null) {
                Node<K, V> next = e.next;
                int h = e.key.hashCode();
                int j = h & (newTable.length - 1);
                e.next = newTable[j];
                newTable[j] = e;
                e = next;
            }
        }
        table = newTable;
    }
    //いったん保存
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(table.length);
        s.writeInt(size);//容量と要素の個数
        for (Node<K, V> bucket : table) {
            for (Node<K, V> e = bucket; e != null; e = e.next) {
                s.writeObject(e.key);
                s.writeObject(e.value);//全Nodeを書き出し
            }
        }
    }

    //復元
    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        int cap = s.readInt();
        int count = s.readInt();
        table = new Node[cap];
        for (int i = 0; i < count; i++) {//全てのキーと値を読んで格納
            K key = (K) s.readObject();
            V val = (V) s.readObject();
            put(key, val);
        }
    }
}