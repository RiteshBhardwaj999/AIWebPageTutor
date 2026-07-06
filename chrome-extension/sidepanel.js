const resultHistory = [];

// Listen for messages from background script
chrome.runtime.onMessage.addListener((msg) => {
  if (msg.action !== "displayResult") return;

  const data = msg.data;
  const welcomeEl = document.getElementById("welcome");
  const loadingEl = document.getElementById("loading");
  const errorEl = document.getElementById("error");
  const resultEl = document.getElementById("result");
  const historyEl = document.getElementById("history-panel");

  // Hide all sections
  welcomeEl.classList.add("hidden");
  loadingEl.classList.add("hidden");
  errorEl.classList.add("hidden");
  resultEl.classList.add("hidden");

  if (data.loading) {
    loadingEl.classList.remove("hidden");
    document.getElementById("loading-action").textContent =
      formatActionName(data.actionType) + "...";
    document.getElementById("loading-text").textContent =
      data.text ? `"${data.text}..."` : "";
    return;
  }

  if (data.error) {
    errorEl.classList.remove("hidden");
    document.getElementById("error-text").textContent = data.error;
    return;
  }

  // Show result
  resultEl.classList.remove("hidden");
  document.getElementById("action-badge").textContent =
    formatActionName(data.actionType);
  document.getElementById("source-text").textContent =
    data.sourceText || "";
  document.getElementById("ai-result").textContent = data.result;

  // Add to local history
  resultHistory.unshift(data);
  if (resultHistory.length > 20) resultHistory.pop();
  renderHistory();
  historyEl.classList.remove("hidden");
});

function formatActionName(type) {
  if (!type) return "Result";
  const names = {
    EXPLAIN: "Explanation",
    SUMMARIZE: "Summary",
    QUIZ: "Quiz",
    FLASHCARD: "Flashcards",
    DIAGRAM: "Diagram",
    TRANSLATE: "Translation",
    EXAMPLES: "Examples",
    CODE_EXAMPLES: "Code Examples",
    RESOURCES: "Resources",
  };
  return names[type] || type;
}

function renderHistory() {
  const list = document.getElementById("history-list");
  list.innerHTML = "";

  resultHistory.forEach((item, idx) => {
    if (idx === 0) return; // skip current (already shown above)
    const div = document.createElement("div");
    div.className = "history-item";
    div.innerHTML = `
      <span class="badge">${formatActionName(item.actionType)}</span>
      <div class="preview">${(item.sourceText || "").substring(0, 60)}...</div>
    `;
    div.addEventListener("click", () => {
      document.getElementById("action-badge").textContent =
        formatActionName(item.actionType);
      document.getElementById("source-text").textContent =
        item.sourceText || "";
      document.getElementById("ai-result").textContent = item.result;
      document.getElementById("result").classList.remove("hidden");
      window.scrollTo({ top: 0, behavior: "smooth" });
    });
    list.appendChild(div);
  });
}
