import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.*;
import org.apache.tika.Tika;
import java.io.File;

public class QuestionFileScraper {

    public String rawText = "";
    public String qFile;
    public SimpleMap<String, Integer> wordFrequencyMap = new SimpleMap<>();
    private SimpleMap<String, String[]> dictionary = new SimpleMap<>();

    public QuestionFileScraper(String qFile) {
        this.qFile = qFile;
        initDictionary();
    }

    private void initDictionary() {
        dictionary.put("Section 1: Company Overview", new String[]{
                "full", "name", "company", "legal", "company's",
                "address", "main", "phone", "number", "who", "parent",
                "company", "companys", "country", "operation",
                "product", "products", "service", "services", "offer"
        });

        dictionary.put("Section 2: Operations", new String[]{
                "administrative", "hour", "hours", "operation", "manufacturing",
                "shift", "total", "number", "employees", "how many", "work",
                "office", "outside", "remote", "inficon kansas",
                "country", "countries", "operation", "operations"
        });

        dictionary.put("Section 3: Business Registration & Legal Information", new String[]{
                "tax", "tax id", "ag", "tin",
                "classification", "cage", "code", "sam", "uei", "gsa", "contract",
                "number", "emr", "state", "incorporated",
                "duns", "federal", "id", "commercial", "registration",
                "register", "court", "cpsr", "certified",
                "market", "exchange", "listing", "congressional", "district",
                "years", "business", "corporate", "structure", "exempt"
        });

        dictionary.put("Section 4: Certifications", new String[]{
                "iso", "certificate", "number", "validity", "period",
                "certifications", "contact"
        });

        dictionary.put("Section 5: Industry Classifications", new String[]{
                "primary", "naics", "code", "secondary", "additional",
                "sic", "list", "all", "unspsc", "found"
        });

        dictionary.put("Section 6: Business Classification", new String[]{
                "business", "size", "classification", "entity", "type",
                "corporation", "corporate",
                "minority", "owned", "hub", "zone", "veteran", "native", "woman"
        });

        dictionary.put("Section 7: Leadership & Organization", new String[]{
                "ceo", "cfo", "chairman", "board", "directors", "members",
                "president", "vp", "finance", "treasurer", "general",
                "counsel", "secretary", "leadership", "organization", "officers"
        });
    }

    public boolean scrape() {
        File file = new File(this.qFile);

        if (!file.exists()) {
            System.out.println("Error: can't find the file. -> " + this.qFile);
            rawText = "";
            wordFrequencyMap = new SimpleMap<>();
            return false;
        }

        wordFrequencyMap = new SimpleMap<>();
        rawText = "";

        try {
            Tika tika = new Tika();
            rawText = tika.parseToString(file);
            tokenize(rawText);
            return true;

        } catch (Exception e) {
            System.out.println("Tika error: " + e.getMessage());
            e.printStackTrace();
            rawText = "";
            wordFrequencyMap = new SimpleMap<>();
            return false;
        }
    }

    private void tokenize(String text) {
        java.util.Set<String> stopWords = new java.util.HashSet<>(java.util.Arrays.asList(
                "a", "an", "the", "and", "or", "but", "if", "then", "else",
                "is", "are", "was", "were", "that", "this", "to", "of",
                "for", "in", "on", "at", "by"
        ));

        String[] words = text.split("[,\\s\\t]+");

        for (String word : words) {
            String cleanWord = word.toLowerCase()
                    .replaceAll("[^a-z0-9]", "")
                    .trim();

            if (!cleanWord.isEmpty() && !stopWords.contains(cleanWord)) {
                Integer count = wordFrequencyMap.get(cleanWord);
                if (count == null) {
                    wordFrequencyMap.put(cleanWord, 1);
                } else {
                    wordFrequencyMap.put(cleanWord, count + 1);
                }
            }
        }
    }

    public String analyzeIntent() {
        SimpleMap<String, Integer> categoryScores = new SimpleMap<>();

        for (String word : wordFrequencyMap.keys()) {
            for (String category : dictionary.keys()) {
                for (String keyword : dictionary.get(category)) {
                    if (word.contains(keyword.toLowerCase())) {
                        int count = wordFrequencyMap.get(word);
                        int currentScore = categoryScores.containsKey(category)
                                ? categoryScores.get(category)
                                : 0;
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

    public QuestionAnalysisResult analyze() {
        boolean success = scrape();

        if (!success) {
            return new QuestionAnalysisResult(
                    qFile,
                    "",
                    "UNKNOWN",
                    false,
                    "File could not be loaded."
            );
        }

        String intent = analyzeIntent();

        return new QuestionAnalysisResult(
                qFile,
                rawText,
                intent,
                true,
                ""
        );
    }

    public void printTopWords(int limit) {
        System.out.println("\n--- [" + this.qFile + "] Top Keywords ---");

        java.util.List<String> keys = wordFrequencyMap.keys();

        if (keys.isEmpty()) {
            System.out.println("No words found.");
            return;
        }

        keys.sort((a, b) -> wordFrequencyMap.get(b).compareTo(wordFrequencyMap.get(a)));

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