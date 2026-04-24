public class QuestionAnalysisResult {
    public String fileName;
    public String rawText;
    public String intent;
    public boolean success;
    public String errorMessage;

    public QuestionAnalysisResult(String fileName, String rawText, String intent, boolean success, String errorMessage) {
        this.fileName = fileName;
        this.rawText = rawText;
        this.intent = intent;
        this.success = success;
        this.errorMessage = errorMessage;
    }
}
