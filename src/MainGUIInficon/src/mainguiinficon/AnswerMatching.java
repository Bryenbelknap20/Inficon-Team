package mainguiinficon;
 
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
 
/**
 * AnswerMatching
 * Reads question files (.txt OR .pdf) and matches each question
 * against ConfluencePage data. No Tika or PDFBox required.
 */
public class AnswerMatching {
 
    // ── One row in the results table ─────────────────────────────
    public static class MatchResult {
        public final String questionFile;
        public final String question;
        public final String detectedSection;
        public final String bestAnswer;
 
        public MatchResult(String questionFile, String question,
                           String detectedSection, String bestAnswer) {
            this.questionFile    = questionFile;
            this.question        = question;
            this.detectedSection = detectedSection;
            this.bestAnswer      = bestAnswer;
        }
    }
 
    private final ConfluencePage confluence;
 
    public AnswerMatching() {
        this.confluence = new ConfluencePage();
    }
 
    // ── Match a single question string ───────────────────────────
    public MatchResult matchQuestion(String questionText, String sourceFileName) {
        ConfluencePage.Entry best = confluence.findBestMatch(questionText);
        if (best == null) {
            return new MatchResult(sourceFileName, questionText, "UNKNOWN", "No match found");
        }
        return new MatchResult(sourceFileName, questionText, best.section, best.answer);
    }
 
    // ── Read a file (txt or pdf) and match every question ────────
    public List<MatchResult> matchFile(File file) {
        List<MatchResult> results = new ArrayList<>();
 
        if (!file.exists()) {
            System.out.println("File not found: " + file.getAbsolutePath());
            return results;
        }
 
        String fileName = file.getName().toLowerCase();
        List<String> questions;
 
        if (fileName.endsWith(".pdf")) {
            questions = readPDF(file);
        } else {
            questions = readTextFile(file);
        }
 
        for (String question : questions) {
            if (question.trim().isEmpty()) continue;
            results.add(matchQuestion(question.trim(), file.getName()));
        }
 
        return results;
    }
 
    // ── Read a plain .txt file ────────────────────────────────────
    private List<String> readTextFile(File file) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    lines.add(trimmed);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading text file: " + e.getMessage());
        }
        return lines;
    }
 
    // ── Read a PDF using only the JDK — no external libs needed ──
    // Works for simple text-based PDFs (which Q4.pdf and the
    // questionnaire PDFs are). Extracts readable ASCII text by
    // scanning the raw PDF byte stream for text between parentheses
    // in PDF text-showing operators (Tj, TJ).
    private List<String> readPDF(File file) {
    List<String> questions = new ArrayList<>();
    try {
        org.apache.pdfbox.pdmodel.PDDocument doc =
            org.apache.pdfbox.pdmodel.PDDocument.load(file);
        org.apache.pdfbox.text.PDFTextStripper stripper =
            new org.apache.pdfbox.text.PDFTextStripper();
        String text = stripper.getText(doc);
        doc.close();

        System.out.println("PDF raw text: " + text);

        String[] lines = text.split("[\r\n]+");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.length() > 5 && looksLikeQuestion(trimmed)) {
                questions.add(trimmed);
            }
        }
    } catch (Exception e) {
        System.out.println("PDFBox error: " + e.getMessage());
        e.printStackTrace();
    }
    return questions;
}
 
    // ── A line looks like a question if it ends with ? or starts
    //    with a question word ────────────────────────────────────
private boolean looksLikeQuestion(String line) {
    String lower = line.toLowerCase().trim();
    // Standard questions
    if (lower.endsWith("?")
        || lower.startsWith("what")
        || lower.startsWith("who")
        || lower.startsWith("when")
        || lower.startsWith("where")
        || lower.startsWith("which")
        || lower.startsWith("how")
        || lower.startsWith("is ")
        || lower.startsWith("in which")
        || lower.startsWith("list")
        || lower.startsWith("where can")) {
        return true;
    }
    // ── Form field labels ending with : ──────────────────────────
    if (lower.endsWith(":")) {
        // Only match meaningful field labels, not noise like "city:" or "zip:"
        String[] formFields = {
            "company name", "company name :",
            "mailing address", "remit to address",
            "phone number", "phone",
            "tax id", "tax id (please provide copy of w-9)",
            "sales/ orders email", "sales/orders email",
            "signature", "date",
            "accounting", "customer service",
            "quality manager"
        };
        for (String field : formFields) {
            if (lower.startsWith(field)) {
                return true;
            }
        }
    }
    return false;
}
 
    // ── Fallback: scan raw text for question-like sentences ───────
    private List<String> fallbackPDFExtract(String raw) {
        List<String> questions = new ArrayList<>();
 
        // Split on newlines and common PDF separators
        String[] lines = raw.split("[\r\n]+");
        for (String line : lines) {
            // Clean up non-printable chars
            String cleaned = line.replaceAll("[^\\x20-\\x7E]", " ")
                                 .replaceAll("\\s+", " ")
                                 .trim();
            if (cleaned.length() > 8 && looksLikeQuestion(cleaned)) {
                questions.add(cleaned);
            }
        }
        return questions;
    }
 
    // ── Get all entries from confluence (for the full table view) ─
    public List<ConfluencePage.Entry> getAllEntries() {
        return confluence.getEntries();
    }
}