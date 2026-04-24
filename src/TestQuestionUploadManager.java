import java.util.List;

public class TestQuestionUploadManager {
    public static void main(String[] args) {
        QuestionUploadManager manager = new QuestionUploadManager();

        List<QuestionAnalysisResult> results =
                manager.analyzeFolder("Inficon-Team");

        for (QuestionAnalysisResult result : results) {
            System.out.println("====================================");
            System.out.println("File: " + result.fileName);
            System.out.println("Success: " + result.success);
            System.out.println("Intent: " + result.intent);

            if (!result.success) {
                System.out.println("Error: " + result.errorMessage);
            }

            System.out.println("====================================");
        }
    }
}
