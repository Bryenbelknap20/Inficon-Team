
    public class TestMatching {
        public static void main(String[] args) {
            // Confluence側
            ConfluencePage cp = new ConfluencePage("Inficon-Team/INFICON_Company_Information.pdf");
            cp.scrape();

            // 質問ファイル側
            String[] targetFiles = {
                    "Inficon-Team/Q1.txt",
                    "Inficon-Team/Q2.txt",
                    "Inficon-Team/Q3.txt",
                    "Inficon-Team/Q4.pdf"
            };

            for (String fileName : targetFiles) {
                QuestionFileScraper qfs = new QuestionFileScraper(fileName);
                qfs.scrape();

                Matching matcher = new Matching(qfs, cp);

                String section = qfs.analyzeIntent();
                String answer = matcher.findBestAnswer();

                System.out.println("====================================");
                System.out.println("Question File: " + fileName);
                System.out.println("Detected Section: " + section);
                System.out.println("Best Answer: " + answer);
                System.out.println("====================================");
            }
        }
    }

