public class Matching {


        private QuestionFileScraper questionScraper;
        private ConfluencePage confluencePage;

        public Matching(QuestionFileScraper questionScraper, ConfluencePage confluencePage) {
            this.questionScraper = questionScraper;
            this.confluencePage = confluencePage;
        }

        public String findBestAnswer() {
            // 1. 質問ファイルのカテゴリを取得
            String section = questionScraper.analyzeIntent();

            // 2. そのカテゴリのConfluence候補文を取得
            java.util.List<String> candidates = confluencePage.categorizedLines.get(section);

            if (candidates == null || candidates.isEmpty()) {
                return "UNKNOWN: No candidate lines found in section -> " + section;
            }

            // 3. 質問ファイル内の単語頻度を使って候補文をスコアリング
            String bestAnswer = "UNKNOWN";
            int maxScore = 0;

//            for (String candidate : candidates) {
//                int score = calculateScore(questionScraper.wordFrequencyMap, candidate);
//
//                if (score > maxScore) {
//                    maxScore = score;
//                    bestAnswer = candidate;
//                }
//            }
//
//            // 4. スコアが0ならUNKNOWN扱い
//            if (maxScore == 0) {
//                return "UNKNOWN: No strong match found in section -> " + section;
//            }
//
//            return bestAnswer;
            String bestBlock = "";
            for (String candidate : candidates) {
                int score = calculateScore(questionScraper.wordFrequencyMap, candidate);

                if (score > maxScore) {
                    maxScore = score;
                    bestBlock = candidate;
                }
            }

// blockの中から最適な行を選ぶ
            return extractBestLine(bestBlock);
        }

    private String extractBestLine(String block) {
        if (block == null || block.isEmpty()) {
            return "UNKNOWN";
        }

        String[] parts = block.split("•|■");
        String bestLine = block;
        int maxScore = 0;

        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            int score = calculateScore(questionScraper.wordFrequencyMap, trimmed);

            if (score > maxScore) {
                maxScore = score;
                bestLine = trimmed;
            }
        }

        return bestLine;
    }

        private int calculateScore(SimpleMap<String, Integer> questionWords, String candidateLine) {
            int score = 0;

            // candidateLine を小文字化して単語に分解
            String[] candidateWords = candidateLine.toLowerCase().split("[,\\s\\t]+");

            java.util.Set<String> candidateSet = new java.util.HashSet<>();
            for (String word : candidateWords) {
                String cleanWord = word.trim();
                if (!cleanWord.isEmpty()) {
                    candidateSet.add(cleanWord);
                }
            }

            // 質問ファイル側の単語と一致するものを加点
            for (String qWord : questionWords.keys()) {
                if (candidateSet.contains(qWord)) {
//                    Integer count = questionWords.get(qWord);
//                    if (count != null) {
//                        score += count;
//                    }
                    score++;
                }
            }

            return score;
        }

}

