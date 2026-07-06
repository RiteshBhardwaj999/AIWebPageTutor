/**
 * AI Webpage Tutor - Spring Boot Backend
 *
 * This project has been restructured as a Spring Boot application.
 * The application entry point is now:
 *   src/main/java/com/aiwebpagetutor/AiWebpageTutorApplication.java
 *
 * To run: mvn spring-boot:run
 * Or run AiWebpageTutorApplication.main() from your IDE.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== AI Webpage Tutor ===");
        System.out.println("This project is now a Spring Boot application.");
        System.out.println("Run 'mvn spring-boot:run' or use AiWebpageTutorApplication.main()");
        System.out.println();
        System.out.println("API Endpoints:");
        System.out.println("  POST /api/auth/register   - Register a new user");
        System.out.println("  POST /api/auth/login      - Login and get JWT token");
        System.out.println("  POST /api/ai/explain      - Explain selected text");
        System.out.println("  POST /api/ai/summarize    - Summarize content");
        System.out.println("  POST /api/ai/quiz         - Generate quiz questions");
        System.out.println("  POST /api/ai/flashcards   - Generate flashcards");
        System.out.println("  POST /api/ai/diagram      - Generate diagram (Mermaid)");
        System.out.println("  POST /api/ai/translate    - Translate text");
        System.out.println("  POST /api/ai/examples     - Real-world examples");
        System.out.println("  POST /api/ai/code-examples- Code examples");
        System.out.println("  POST /api/ai/resources    - Related resources");
        System.out.println("  GET  /api/history         - Learning history");
        System.out.println("  GET  /api/skills          - Tracked skills");
        System.out.println("  GET  /api/skills/recommendations - AI recommendations");
        System.out.println("  GET  /api/export/history/markdown    - Export history");
        System.out.println("  GET  /api/export/flashcards/markdown - Export flashcards");
    }
}
