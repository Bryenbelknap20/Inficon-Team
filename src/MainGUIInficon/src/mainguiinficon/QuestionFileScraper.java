/*
package mainguiinficon;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.*;
import org.apache.tika.Tika;

public class QuestionFileScraper {

    public java.util.List<String> myArray = new java.util.ArrayList<>();
    public String qFile;

    // Constructor — receives the full absolute path from the GUI
    public QuestionFileScraper(String qFile) {
        this.qFile = qFile;
    }

    public void scrape() {
        File file = new File(this.qFile);

        if (!file.exists()) {
            System.out.println("Error: can't find the file. -> " + this.qFile);
            return;
        }

        // Reset before each new file
        wordFrequencyMap = new SimpleMap<>();
        myArray.clear();

        String line = "";
        try {
            Tika tika = new Tika();
            line = tika.parseToString(file);
            tokenize(line);
        } catch (Exception e) {
            e.printStackTrace();  // ← this prints the FULL error with line numbers

        }
    }

    public SimpleMap<String, Integer> wordFrequencyMap = new SimpleMap<>();

    private void tokenize(String text) {
        java.util.Set<String> stopWords = new java.util.HashSet<>(java.util.Arrays.asList(
                "a", "an", "the", "and", "or", "but", "if", "then", "else",
                "is", "are", "was", "were", "that", "this", "to", "of", "for",
                "in", "on", "at", "by"
        ));

        String[] words = text.split("[,\\s\\t]+");

        for (String word : words) {
            String cleanWord = word.toLowerCase().trim();

            if (!cleanWord.isEmpty() && !stopWords.contains(cleanWord)) {
                Integer count = wordFrequencyMap.get(cleanWord);
                if (count == null) {
                    wordFrequencyMap.put(cleanWord, 1);
                } else {
                    wordFrequencyMap.put(cleanWord, count + 1);
                }
                myArray.add(cleanWord);
            }
        }
    }

    public String analyzeIntent() {
        SimpleMap<String, Integer> categoryScores = new SimpleMap<>();

        SimpleMap<String, String[]> dictionary = new SimpleMap<>();
        dictionary.put("TECHNICAL_ISSUE",    new String[]{"error", "broken", "setup", "how", "manual", "not working"});
        dictionary.put("BILLING_INQUIRY",    new String[]{"price", "cost", "invoice", "billing", "payment", "money"});
        dictionary.put("LOGISTICS_INFO",     new String[]{"shipping", "delivery", "track", "when", "arrival", "address"});

        dictionary.put("Section 1: Company Overview", new String[]{
            "full", "name", "company", "legal", "company's", "address", "main",
            "phone", "number", "who", "parent", "country", "operation",
            "product", "products", "service", "services", "offer"
        });
        dictionary.put("Section 2: Operations", new String[]{
            "administrative", "hour", "hours", "operation", "manufacturing",
            "shift", "total", "number", "employees", "how many", "work",
            "office", "outside", "remote", "inficon kansas"
        });
        dictionary.put("Section 3: Business Registration & Legal Information", new String[]{
            "tax", "tax id", "AG", "TIN", "classification", "CAGE", "Code",
            "SAM", "UEI", "GSA", "Contract", "number", "EMR", "state",
            "incorporated", "DUNS", "Federal", "ID", "Commercial",
            "Registration", "Register", "Court", "CPSR", "Certified",
            "Market", "Exchange", "listing", "Congressional", "District",
            "years", "business", "corporate", "structure", "exempt"
        });
        dictionary.put("Section 4: Certifications", new String[]{
            "ISO", "certificate", "number", "validity", "period",
            "certifications", "contact"
        });
        dictionary.put("Section 5: Industry Classifications", new String[]{
            "Primary", "NAICS", "Code", "Secondary", "additional",
            "SIC", "List", "all", "UNSPSC", "found"
        });
        dictionary.put("Section 6: Business Classification", new String[]{
            "business", "size", "classification", "entity", "type",
            "Minority", "owned", "Hub", "Zone", "Veteran", "Native", "Woman"
        });
        dictionary.put("Section 7: Leadership & Organization", new String[]{
            "CEO", "CFO", "Chairman", "Board", "Directors", "members",
            "President", "VP", "Finance", "Treasurer", "General",
            "Counsel", "Secretary"
        });

        for (String word : wordFrequencyMap.size() > 0
                ? wordFrequencyMap.keys()
                : new java.util.ArrayList<String>()) {
            for (String category : dictionary.keys()) {
                for (String keyword : dictionary.get(category)) {
                    if (word.contains(keyword)) {
                        int count = wordFrequencyMap.get(word);
                        int currentScore = categoryScores.containsKey(category)
                                ? categoryScores.get(category) : 0;
                        categoryScores.put(category, currentScore + count);
                    }
                }
            }
        }

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
        java.util.List<String> keys = wordFrequencyMap.keys();
        if (keys.isEmpty()) { System.out.println("No words found."); return; }
        keys.sort((a, b) -> wordFrequencyMap.get(b).compareTo(wordFrequencyMap.get(a)));
        for (int i = 0; i < Math.min(limit, keys.size()); i++) {
            String word = keys.get(i);
            System.out.println((i + 1) + ". " + word + " (" + wordFrequencyMap.get(word) + " times)");
        }
    }
}


// ── Result helper class ───────────────────────────────────────────────────────
class Result {
    String fileName;
    String intent;

    Result(String fileName, String intent) {
        this.fileName = fileName;
        this.intent   = intent;
    }
}


// ── SimpleMap — custom hash map ───────────────────────────────────────────────
class SimpleMap<K, V> implements Serializable {
    private static final long serialVersionUID = 1L;

    static final class Node<K, V> implements Serializable {
        final K key;
        V value;
        Node<K, V> next;

        Node(K k, V v, Node<K, V> n) {
            key = k; value = v; next = n;
        }
    }

    private Node<K, V>[] table;
    private int size = 0;

    @SuppressWarnings("unchecked")
    public SimpleMap() { table = new Node[16]; }

    public void put(K key, V value) {
        int i = key.hashCode() & (table.length - 1);
        for (Node<K, V> e = table[i]; e != null; e = e.next) {
            if (key.equals(e.key)) { e.value = value; return; }
        }
        table[i] = new Node<>(key, value, table[i]);
        if (++size >= table.length * 0.75f) resize();
    }

    public V get(K key) {
        int i = key.hashCode() & (table.length - 1);
        for (Node<K, V> e = table[i]; e != null; e = e.next)
            if (key.equals(e.key)) return e.value;
        return null;
    }

    public boolean containsKey(K key) { return get(key) != null; }

    public java.util.List<K> keys() {
        java.util.List<K> list = new java.util.ArrayList<>();
        for (Node<K, V> bucket : table)
            for (Node<K, V> e = bucket; e != null; e = e.next)
                list.add(e.key);
        return list;
    }

    public java.util.List<V> values() {
        java.util.List<V> list = new java.util.ArrayList<>();
        for (Node<K, V> bucket : table)
            for (Node<K, V> e = bucket; e != null; e = e.next)
                list.add(e.value);
        return list;
    }

    public int size() { return size; }

    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] oldTable = table;
        int newCapacity = oldTable.length << 1;
        Node<K, V>[] newTable = new Node[newCapacity];
        for (Node<K, V> e : oldTable) {
            while (e != null) {
                Node<K, V> next = e.next;
                int j = e.key.hashCode() & (newTable.length - 1);
                e.next = newTable[j];
                newTable[j] = e;
                e = next;
            }
        }
        table = newTable;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(table.length);
        s.writeInt(size);
        for (Node<K, V> bucket : table)
            for (Node<K, V> e = bucket; e != null; e = e.next) {
                s.writeObject(e.key);
                s.writeObject(e.value);
            }
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        int cap   = s.readInt();
        int count = s.readInt();
        table = new Node[cap];
        for (int i = 0; i < count; i++) put((K) s.readObject(), (V) s.readObject());
    }
}
*/