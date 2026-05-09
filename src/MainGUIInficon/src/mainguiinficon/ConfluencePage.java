package mainguiinficon;

import java.util.ArrayList;
import java.util.List;

/**
 * ConfluencePage
 * Holds all INFICON company data parsed from INFICON_Company_Information.pdf
 * organized into question→answer pairs by section.
 * No external dependencies required.
 */
public class ConfluencePage {

    // ── Each entry = one Q&A pair from the confluence page ───────
    public static class Entry {
        public final String section;
        public final String question;
        public final String answer;

        public Entry(String section, String question, String answer) {
            this.section  = section;
            this.question = question;
            this.answer   = answer;
        }
    }

    private final List<Entry> entries = new ArrayList<>();

    public ConfluencePage() {
        load();
    }

    // ── Returns all entries ───────────────────────────────────────
    public List<Entry> getEntries() {
        return entries;
    }

    // ── Find the best matching answer for a question string ───────
    public Entry findBestMatch(String question) {
    if (question == null || question.trim().isEmpty()) return null;

    String q = question.toLowerCase().trim()
                       .replaceAll("\\s*:\\s*$", "")  // strip trailing colon
                       .trim();

    Entry bestEntry = null;
    int   bestScore = 0;

    for (Entry e : entries) {
        // Also strip colon from entry question for comparison
        String eq = e.question.toLowerCase()
                              .replaceAll("\\s*:\\s*$", "")
                              .trim();
        int score = scoreMatch(q, eq, e.answer.toLowerCase());
        if (score > bestScore) {
            bestScore = score;
            bestEntry = e;
        }
    }
    return bestEntry;
}

    // ── Simple keyword overlap scoring ────────────────────────────
    private int scoreMatch(String query, String entryQuestion, String entryAnswer) {
        String[] queryWords = query.split("[\\s,?]+");
        int score = 0;
        for (String word : queryWords) {
            if (word.length() < 3) continue; // skip tiny words
            if (entryQuestion.contains(word)) score += 2; // question match = stronger signal
            if (entryAnswer.contains(word))   score += 1;
        }
        return score;
    }

    // ── All INFICON data loaded directly from the confluence PDF ──
    private void load() {

        // ── SECTION 1: Company Overview ───────────────────────────
        add("Section 1: Company Overview",
            "What is the full legal name of the company?",
            "INFICON, Inc.");

        add("Section 1: Company Overview",
            "What is the company's primary address?",
            "Two Technology Place, East Syracuse, NY 13057");

        add("Section 1: Company Overview",
            "What is the main telephone number?",
            "315-434-1100");

        add("Section 1: Company Overview",
            "Who is the parent company?",
            "INFICON Holding AG (Address: 15 B Hintergasse, Bad Ragaz, Switzerland, 7310)");

        add("Section 1: Company Overview",
            "In which countries does the company have operations?",
            "United States, Great Britain, Germany, France, Finland, Sweden, Liechtenstein, Switzerland, Italy, Malaysia, Singapore, China, Korea, Japan, Taiwan");

        add("Section 1: Company Overview",
            "What products and services does the company offer?",
            "Residual Gas Analyzers; Gas Chromatograph/Mass Spectrometers; Thin Film Deposition Monitors and Controllers; Leak Detectors; Smart Manufacturing Solutions");

        // ── SECTION 2: Operations ─────────────────────────────────
        add("Section 2: Operations",
            "What are the administrative hours of operation?",
            "Monday–Friday, 8:00 AM – 5:00 PM");

        add("Section 2: Operations",
            "What are the manufacturing shift hours?",
            "1st Shift: Mon–Fri 6:00 AM – 2:30 PM; 2nd Shift: Mon–Fri 3:00 PM – 11:30 PM. Weekend Shift: ELIMINATED as of 9/2025");

        add("Section 2: Operations",
            "What is the total number of employees?",
            "571 (as of 11.6.2025)");

        add("Section 2: Operations",
            "How many employees work at the Main Office?",
            "326 employees");

        add("Section 2: Operations",
            "How many employees work outside the main office or remote?",
            "247 employees");

        add("Section 2: Operations",
            "How many employees work at INFICON Kansas?",
            "51 employees");

        // ── SECTION 3: Business Registration & Legal ──────────────
        add("Section 3: Business Registration & Legal Information",
            "What is the company's Tax ID (TIN/EIN)?",
            "16-1591542");

        add("Section 3: Business Registration & Legal Information",
            "What is the INFICON Holding AG TIN?",
            "740 584");

        add("Section 3: Business Registration & Legal Information",
            "What is the tax classification?",
            "C Corporation");

        add("Section 3: Business Registration & Legal Information",
            "What is the CAGE Code?",
            "56507");

        add("Section 3: Business Registration & Legal Information",
            "What is the SAM UEI?",
            "DK29GPFWHZA4");

        add("Section 3: Business Registration & Legal Information",
            "What is the GSA Contract number?",
            "GS-07F-0067T");

        add("Section 3: Business Registration & Legal Information",
            "What is the EMR (Experience Modification Rate)?",
            "0.69");

        add("Section 3: Business Registration & Legal Information",
            "In which state is the company incorporated?",
            "Delaware");

        add("Section 3: Business Registration & Legal Information",
            "What is the DUNS Number?",
            "05-055-2082");

        add("Section 3: Business Registration & Legal Information",
            "What is the Federal ID?",
            "16-1591542");

        add("Section 3: Business Registration & Legal Information",
            "What is the Commercial Registration Number?",
            "CHE-101.283.338");

        add("Section 3: Business Registration & Legal Information",
            "What is the Register Court?",
            "St. Gallen");

        add("Section 3: Business Registration & Legal Information",
            "Is the company CPSR Certified?",
            "No");

        add("Section 3: Business Registration & Legal Information",
            "When was INFICON Inc. incorporated?",
            "August 11, 2000");

        add("Section 3: Business Registration & Legal Information",
            "When was INFICON Holding AG incorporated?",
            "July 2000");

        add("Section 3: Business Registration & Legal Information",
            "What is the Market Exchange listing?",
            "SWX:IFCN (Registration #19.1158)");

        add("Section 3: Business Registration & Legal Information",
            "Which Congressional District is the company in?",
            "27th");

        add("Section 3: Business Registration & Legal Information",
            "How many years has the company been in business?",
            "57 years (established 1969)");

        add("Section 3: Business Registration & Legal Information",
            "What is the corporate structure?",
            "Publicly held subsidiary of INFICON Holding AG");

        add("Section 3: Business Registration & Legal Information",
            "Is the company tax exempt?",
            "No (Non tax exempt)");

        // ── SECTION 4: Certifications ─────────────────────────────
        add("Section 4: Certifications",
            "What is the ISO 9001 certificate number and validity period?",
            "Certificate #54265 | Valid: 11/23/2024 – 11/22/2027");

        add("Section 4: Certifications",
            "What is the ISO 14001 certificate number and validity period?",
            "Certificate #54265 | Valid: 11/23/2024 – 11/22/2027");

        add("Section 4: Certifications",
            "Who is the certifications contact?",
            "Madison Kerr");

        // ── SECTION 5: Industry Classifications ───────────────────
        add("Section 5: Industry Classifications",
            "What is the Primary NAICS Code?",
            "334516 — Analytical Lab Instrument Manufacturing");

        add("Section 5: Industry Classifications",
            "What is the Secondary NAICS Code?",
            "334513 — Instruments and Related Products Manufacturing for Measuring, Displaying, and Controlling Industrial Process Variables");

        add("Section 5: Industry Classifications",
            "List all additional NAICS Codes.",
            "334515; 333248; 532490 (Note: Codes may differ for software products)");

        add("Section 5: Industry Classifications",
            "What is the Primary SIC Code?",
            "3823 — Manufacturing Process Control Instruments");

        add("Section 5: Industry Classifications",
            "List all additional SIC Codes.",
            "38120000 (Search and Navigation Equipment); 38239905 (Industrial Process Control Instruments); 3812 (Manufacturing Search/Navigation Equipment)");

        add("Section 5: Industry Classifications",
            "Where can UNSPSC Codes be found?",
            "Under the specific product number");

        // ── SECTION 6: Business Classification ───────────────────
        add("Section 6: Business Classification",
            "What is the business size classification?",
            "Large business (not classified as small business)");

        add("Section 6: Business Classification",
            "What is the entity type?",
            "Corporation");

        add("Section 6: Business Classification",
            "Is the company Minority owned?",
            "No");

        add("Section 6: Business Classification",
            "Is the company in a Hub Zone?",
            "No");

        add("Section 6: Business Classification",
            "Is the company Veteran owned?",
            "No");

        add("Section 6: Business Classification",
            "Is the company Native owned?",
            "No");

        add("Section 6: Business Classification",
            "Is the company Woman owned?",
            "No");

        // ── SECTION 7: Leadership & Organization ──────────────────
        add("Section 7: Leadership & Organization",
            "Who is the CEO of INFICON Holding AG?",
            "Oliver Wyrsch (effective January 1, 2023)");

        add("Section 7: Leadership & Organization",
            "Who is the CFO of INFICON Holding AG?",
            "Matthias Tröndle (retiring June 29, 2026); successor: Dimitrij Lisak");

        add("Section 7: Leadership & Organization",
            "Who is the Chairman of the Board of Directors?",
            "Dr. Beat E. Lüthi");

        add("Section 7: Leadership & Organization",
            "Who are the Board of Directors members?",
            "Vanessa Frey (Audit Committee Member); Beat Siegrist (Audit Committee Member, Compensation & HR Committee Chairman); Dr. Reto Suter (Compensation & HR Committee Member, Audit Committee Chairman); Lukas Winkler (Compensation & HR Committee Member)");

        add("Section 7: Leadership & Organization",
            "Who is the President of INFICON Inc.?",
            "Hannah Henley");

        add("Section 7: Leadership & Organization",
            "Who is the VP Finance & Treasurer of INFICON Inc.?",
            "Sean Maloney");

        add("Section 7: Leadership & Organization",
            "Who is the General Counsel & Secretary of INFICON Inc.?",
            "David Kailer");
        // ── Supplier form field mappings ──────────────────────────
        add("Section 1: Company Overview",
            "Company Name :",
            "INFICON, Inc.");

        add("Section 1: Company Overview",
            "Mailing Address :",
            "Two Technology Place, East Syracuse, NY 13057");

        add("Section 1: Company Overview",
            "Phone Number :",
            "315-434-1100");

        add("Section 3: Business Registration & Legal Information",
            "Tax ID (please provide copy of W-9) :",
            "16-1591542");

        add("Section 1: Company Overview",
            "Sales/ Orders email :",
            "Contact INFICON directly for sales/orders email.");

        add("Section 7: Leadership & Organization",
            "Accounting :",
            "VP Finance & Treasurer: Sean Maloney");

        add("Section 7: Leadership & Organization",
            "Customer Service :",
            "Contact INFICON at 315-434-1100");

        add("Section 7: Leadership & Organization",
            "Quality Manager :",
            "Contact INFICON at 315-434-1100");
        }

    private void add(String section, String question, String answer) {
        entries.add(new Entry(section, question, answer));
    }
}