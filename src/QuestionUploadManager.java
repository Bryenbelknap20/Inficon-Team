import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class QuestionUploadManager {

    public List<QuestionAnalysisResult> analyzeFiles(List<File> uploadedFiles) {
        List<QuestionAnalysisResult> results = new ArrayList<>();

        for (File file : uploadedFiles) {
            QuestionFileScraper scraper = new QuestionFileScraper(file.getPath());
            QuestionAnalysisResult result = scraper.analyze();
            results.add(result);
        }

        return results;
    }

    public List<QuestionAnalysisResult> analyzeFolder(String folderPath) {
        List<QuestionAnalysisResult> results = new ArrayList<>();
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Error: folder not found -> " + folderPath);
            return results;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            return results;
        }

        List<File> uploadedFiles = new ArrayList<>();
        for (File file : files) {
            if (file.isFile()) {
                uploadedFiles.add(file);
            }
        }

        return analyzeFiles(uploadedFiles);
    }
}
