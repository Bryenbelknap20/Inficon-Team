import org.apache.tika.Tika;
import java.io.File;

public class ConfluencePage {

    public static void main(String[] args){
        // Main メソッド等の内部で
        ConfluencePage cpScraper = new ConfluencePage("Inficon-Team/INFICON_Company_Information.pdf");
        cpScraper.scrape();
        cpScraper.printCategorizedLines(); // カテゴリごとにまとめられた行をすべてコンソールに表示
    }

    // 対象のファイルパス
    public String filePath;

    // セクション（カテゴリ名）ごとに該当する「行」のリストを保持するためのマップ
    public SimpleMap<String, java.util.List<String>> categorizedLines = new SimpleMap<>();

    // カテゴリ判定のための辞書
    private SimpleMap<String, String[]> dictionary = new SimpleMap<>();

    public ConfluencePage(String filePath) {
        this.filePath = filePath;
        initDictionary();
    }

    private void initDictionary() {
        // 辞書の定義 (QuestionFileScraperと同じものを使用)
        dictionary.put("Section 1: Company Overview", new String[]{"full", "name", "company", "legal", "company's",
                "address", "main", "phone", "number", "who", "parent", "country", "operation",
                "product", "products", "service", "services", "offer", });
        dictionary.put("Section 2: Operations", new String[]{"administrative", "hour", "hours", "operation", "manufacturing",
                "shift", "total", "number", "employees", "how many", "work", "office", "outside", "remote", "inficon kansas", "country", "countries", "operation", "operations"});
        dictionary.put("Section 3: Business Registration & Legal Information", new String[]{"tax", "tax id", "AG", "TIN",
                "classification", "CAGE", "Code", "SAM", "UEI", "GSA", "Contract", "number", "EMR", "state", "incorporated",
                "DUNS", "Federal", "ID", "Commercial", "Registration", "Register", "Court", "CPSR", "Certified",
                "Market", "Exchange", "listing", "Congressional", "District", "years", "business", "corporate",
                "structure", "exempt"});
        dictionary.put("Section 4: Certifications", new String[]{"ISO", "certificate", "number", "validity", "period", "certifications", "contact"});
        dictionary.put("Section 5: Industry Classifications", new String[]{"Primary", "NAICS", "Code", "Secondary", "additional", "SIC", "List", "all", "UNSPSC", "found"});
        dictionary.put("Section 6: Business Classification", new String[]{"business", "size", "classification", "entity", "type",
                "corporation", "corporate",
                "minority", "owned", "hub", "zone", "veteran", "native", "woman"});
        dictionary.put("Section 7: Leadership & Organization", new String[]{"CEO", "CFO", "Chairman", "Board" , "Directors", "members", "President",
                "VP", "Finance", "Treasurer", "General", "Counsel", "Secretary"});

        // どのカテゴリにも属さない行を入れるためのデフォルトカテゴリ
        dictionary.put("UNKNOWN", new String[]{});

        // categorizedLinesの初期化（各カテゴリのキーに対して空のリストをセットしておく）
        for (String key : dictionary.keys()) {
            categorizedLines.put(key, new java.util.ArrayList<>());
        }
    }

    public void scrape() {
        File file = new File(this.filePath);

        if (!file.exists()) {
            System.out.println("Error: can't find the file. -> " + this.filePath);
            return;
        }

        try {
            Tika tika = new Tika();
            String fullText = tika.parseToString(file);
            String[] lines = fullText.split("\\r?\\n");
            java.util.List<String> blocks = new java.util.ArrayList<>();
            StringBuilder currentBlock = new StringBuilder();

            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }

                boolean isHeading = isLikelyHeading(trimmed);

                if (isHeading) {
                    if (currentBlock.length() > 0) {
                        blocks.add(currentBlock.toString().trim());
                        currentBlock.setLength(0);
                    }
                    currentBlock.append(trimmed).append(" ");
                } else {
                    currentBlock.append(trimmed).append(" ");
                }
            }

            if (currentBlock.length() > 0) {
                blocks.add(currentBlock.toString().trim());
            }

            for (String block : blocks) {
                String section = analyzeBlockIntent(block);

                java.util.List<String> list = categorizedLines.get(section);
                if (list == null) {
                    list = new java.util.ArrayList<>();
                    categorizedLines.put(section, list);
                }
                list.add(block);
            }
        } catch (Exception e) {
            System.out.println("Tika error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isLikelyHeading(String line) {
        if (line.startsWith("•") || line.startsWith("■")) {
            return false;
        }

        // コロン付きは本文扱い
        if (line.contains(":")) {
            return false;
        }

        // カンマを含む行は一覧・本文の可能性が高い
        if (line.contains(",")) {
            return false;
        }

        String lower = line.toLowerCase();
        if (lower.contains("inc") || lower.contains("ltd") || lower.contains("ag")) {
            return false;
        }

        int wordCount = line.split("\\s+").length;

        return wordCount <= 6;
    }

    // 与えられた1行を単語に分解し、辞書を用いて最もスコアが高いセクションを返す
    private String analyzeBlockIntent(String block) {
        // 不要な単語リスト
        java.util.Set<String> stopWords = new java.util.HashSet<>(java.util.Arrays.asList(
                "a", "an", "the", "and", "or", "but", "if", "then", "else", "is", "are", "was", "were", "that", "this", "to", "of", "for", "in", "on", "at", "by"
        ));

        // 行を単語に分解 (スペース、タブ、カンマで分割)
        String[] words = block.split("[,\\s\\t]+");

        // この行に含まれる各カテゴリのスコアを記録するMap
        SimpleMap<String, Integer> categoryScores = new SimpleMap<>();

        for (String word : words) {
            String cleanWord = word.toLowerCase().trim();
            if (cleanWord.isEmpty() || stopWords.contains(cleanWord)) {
                continue;
            }

            // セクション判定 (辞書のキーワードと単語を照らし合わせる)
            for (String category : dictionary.keys()) {
                if (category.equals("UNKNOWN")) continue;

                String[] keywords = dictionary.get(category);
                for (String keyword : keywords) {
                    if (cleanWord.contains(keyword.toLowerCase())) {
                        Integer currentScore = categoryScores.get(category);
                        if (currentScore == null) currentScore = 0;
                        categoryScores.put(category, currentScore + 1); // マッチしたら1点加点
                    }
                }
            }
        }

        // 最もスコアの高いセクションを見つける
        String bestCategory = "UNKNOWN";
        int maxScore = 0;

        java.util.List<String> scoredCategories = categoryScores.keys();
        if (scoredCategories != null) {
            for (String cat : scoredCategories) {
                Integer score = categoryScores.get(cat);
                if (score != null && score > maxScore) {
                    maxScore = score;
                    bestCategory = cat;
                }
            }
        }

        return bestCategory;
    }

    // どのカテゴリにどんな行が振り分けられたか確認するための出力メソッド
//    public void printCategorizedLines() {
//        System.out.println("\n=== [" + filePath + "] RESULT ===");
//        for (String category : categorizedLines.keys()) {
//            java.util.List<String> lines = categorizedLines.get(category);
//            // 1行以上振り分けられたセクションのみ出力
//            if (lines != null && !lines.isEmpty()) {
//                System.out.println("\n【" + category + "】 (" + "Total" + " " + lines.size() + " " + "sentences)");
//                for (String line : lines) {
//                    System.out.println("  - " + line.trim());
//                }
//            }
//        }
//        System.out.println("=========================================\n");
//    }
    public void printCategorizedLines() {
        System.out.println("\n=== [" + filePath + "] RESULT ===");

        // 出力順を明示的に指定
        String[] orderedSections = {
                "Section 1: Company Overview",
                "Section 2: Operations",
                "Section 3: Business Registration & Legal Information",
                "Section 4: Certifications",
                "Section 5: Industry Classifications",
                "Section 6: Business Classification",
                "Section 7: Leadership & Organization",
                "UNKNOWN"
        };

        for (String category : orderedSections) {
            java.util.List<String> lines = categorizedLines.get(category);

            if (lines != null && !lines.isEmpty()) {
                System.out.println("\n【" + category + "】 (" + "Total" + " " +  lines.size() + " " + "sentences)");
                for (String line : lines) {
                    System.out.println("  - " + line.trim());
                }
            }
        }

        System.out.println("=========================================\n");
    }
}
