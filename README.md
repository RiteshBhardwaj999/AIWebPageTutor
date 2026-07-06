# AI Webpage Tutor

An AI-powered browser learning companion. Select any text on any webpage and instantly get explanations, quizzes, flashcards, diagrams, translations, and more.

## Features

- **Explain** - Simplifies selected text with real-world analogies
- **Summarize** - Bullet-point summaries of articles and content
- **Quiz** - Auto-generates multiple-choice questions
- **Flashcards** - Creates study flashcards from any content
- **Diagram** - Generates Mermaid.js diagrams of concepts
- **Translate** - Translates text to any language
- **Real-World Examples** - Relatable examples for any concept
- **Code Examples** - Practical code snippets for technical content
- **Related Resources** - Suggests topics for further learning
- **Learning History** - Tracks everything you've studied
- **Skill Tracking** - Monitors topics you've explored
- **AI Recommendations** - Suggests what to learn next
- **Markdown Export** - Export notes and flashcards as `.md` files

## Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Spring Boot 3.3, Java 17 |
| Database | H2 (dev) / PostgreSQL (prod) |
| Auth | JWT (JSON Web Tokens) |
| AI | Groq API (Llama 3.3 70B) |
| Extension | Chrome Manifest V3 |
| Frontend | HTML, CSS, JavaScript |

## Project Structure

```
AiWebpageTutor/
├── pom.xml
├── src/main/java/com/aiwebpagetutor/
│   ├── AiWebpageTutorApplication.java    # Entry point
│   ├── config/
│   │   ├── SecurityConfig.java           # JWT + stateless auth
│   │   ├── CorsConfig.java              # CORS for Chrome extension
│   │   └── GlobalExceptionHandler.java   # Error handling
│   ├── controller/
│   │   ├── AiController.java            # 9 AI endpoints
│   │   ├── AuthController.java          # Register + Login
│   │   ├── HistoryController.java       # Learning history
│   │   ├── SkillController.java         # Skill tracking
│   │   └── ExportController.java        # Markdown export
│   ├── dto/
│   │   ├── request/                     # AiRequest, AuthRequest
│   │   └── response/                    # AiResponse, AuthResponse
│   ├── model/                           # User, LearningHistory, Skill, Flashcard
│   ├── repository/                      # JPA repositories
│   ├── security/                        # JwtUtil, JwtFilter
│   └── service/                         # GeminiService, AuthService, etc.
├── src/main/resources/
│   └── application.properties
└── chrome-extension/
    ├── manifest.json                    # Manifest V3
    ├── background.js                    # Context menus, AI API calls
    ├── content.js / content.css         # Floating toolbar on text selection
    ├── popup.html / popup.js / popup.css  # Login/register + dashboard
    ├── sidepanel.html / sidepanel.js / sidepanel.css  # AI response display
    ├── api.js                           # Shared API utility
    └── icons/
```

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- Chrome browser
- Groq API key (free at [console.groq.com](https://console.groq.com))

### 1. Configure the API key

Edit `src/main/resources/application.properties`:

```properties
ai.api.key=YOUR_GROQ_API_KEY_HERE
```

### 2. Run the backend

```bash
mvn spring-boot:run
```

The server starts at `http://localhost:8080`.

### 3. Load the Chrome extension

1. Open `chrome://extensions/`
2. Enable **Developer mode** (top right)
3. Click **Load unpacked**
4. Select the `chrome-extension/` folder

### 4. Use it

1. Click the extension icon and **register/login**
2. Go to any webpage and **select text**
3. Use the **floating toolbar** or **right-click > AI Tutor** menu
4. Results appear in the **Chrome side panel**

## API Endpoints

### Auth (no token required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login, returns JWT |

### AI (JWT required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/ai/explain` | Simplify text |
| POST | `/api/ai/summarize` | Summarize content |
| POST | `/api/ai/quiz` | Generate quiz |
| POST | `/api/ai/flashcards` | Generate flashcards |
| POST | `/api/ai/diagram` | Generate Mermaid diagram |
| POST | `/api/ai/translate` | Translate text |
| POST | `/api/ai/examples` | Real-world examples |
| POST | `/api/ai/code-examples` | Code examples |
| POST | `/api/ai/resources` | Related resources |

### Data (JWT required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/history` | Get learning history |
| GET | `/api/history/type/{type}` | Filter history by type |
| DELETE | `/api/history/{id}` | Delete history entry |
| GET | `/api/skills` | Get tracked skills |
| POST | `/api/skills/track` | Track a skill |
| GET | `/api/skills/recommendations` | AI learning recommendations |
| GET | `/api/export/history/markdown` | Download history as .md |
| GET | `/api/export/flashcards/markdown` | Download flashcards as .md |

### Request format (AI endpoints)

```json
{
  "text": "Selected text from webpage",
  "sourceUrl": "https://example.com/page",
  "pageTitle": "Page Title",
  "targetLanguage": "Spanish"
}
```

## Switching AI Providers

The backend uses the OpenAI-compatible chat completions format, so you can swap providers by changing `application.properties`:

**Groq (default):**
```properties
ai.api.base-url=https://api.groq.com/openai/v1
ai.api.key=your-groq-key
ai.api.model=llama-3.3-70b-versatile
```

**OpenRouter:**
```properties
ai.api.base-url=https://openrouter.ai/api/v1
ai.api.key=your-openrouter-key
ai.api.model=meta-llama/llama-3.3-70b-instruct
```

**OpenAI:**
```properties
ai.api.base-url=https://api.openai.com/v1
ai.api.key=your-openai-key
ai.api.model=gpt-4o-mini
```

## Production Deployment

1. Switch to PostgreSQL in `application.properties`
2. Set a strong `jwt.secret`
3. Update CORS origins in `CorsConfig.java` for your domain
4. Build: `mvn clean package`
5. Run: `java -jar target/ai-webpage-tutor-0.0.1-SNAPSHOT.jar`
6. Deploy with Docker + Render (Dockerfile not included yet)
