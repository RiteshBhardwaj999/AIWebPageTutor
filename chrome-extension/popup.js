document.addEventListener("DOMContentLoaded", async () => {
  const authScreen = document.getElementById("auth-screen");
  const dashScreen = document.getElementById("dashboard-screen");

  // Check if logged in
  const token = await getToken();
  if (token) {
    showDashboard();
  }

  // Toggle login/register
  document.getElementById("show-register").addEventListener("click", (e) => {
    e.preventDefault();
    document.getElementById("login-form").classList.add("hidden");
    document.getElementById("register-form").classList.remove("hidden");
  });
  document.getElementById("show-login").addEventListener("click", (e) => {
    e.preventDefault();
    document.getElementById("register-form").classList.add("hidden");
    document.getElementById("login-form").classList.remove("hidden");
  });

  // Login
  document.getElementById("login-btn").addEventListener("click", async () => {
    const email = document.getElementById("login-email").value;
    const password = document.getElementById("login-password").value;
    try {
      hideError();
      await login(email, password);
      showDashboard();
    } catch (err) {
      showError(err.message);
    }
  });

  // Register
  document.getElementById("register-btn").addEventListener("click", async () => {
    const name = document.getElementById("reg-name").value;
    const email = document.getElementById("reg-email").value;
    const password = document.getElementById("reg-password").value;
    try {
      hideError();
      await register(email, password, name);
      showDashboard();
    } catch (err) {
      showError(err.message);
    }
  });

  // Logout
  document.getElementById("logout-btn").addEventListener("click", async () => {
    await logout();
    dashScreen.classList.add("hidden");
    authScreen.classList.remove("hidden");
  });

  // Dashboard actions
  document.getElementById("btn-history").addEventListener("click", async () => {
    try {
      const data = await apiCall("/history");
      showResult(formatHistory(data));
    } catch (err) { showResult("Error: " + err.message); }
  });

  document.getElementById("btn-skills").addEventListener("click", async () => {
    try {
      const data = await apiCall("/skills");
      showResult(formatSkills(data));
    } catch (err) { showResult("Error: " + err.message); }
  });

  document.getElementById("btn-recommend").addEventListener("click", async () => {
    try {
      showResult("Getting AI recommendations...");
      const data = await apiCall("/skills/recommendations");
      showResult(typeof data === "string" ? data : JSON.stringify(data, null, 2));
    } catch (err) { showResult("Error: " + err.message); }
  });

  document.getElementById("btn-export-history").addEventListener("click", async () => {
    try {
      const blob = await apiCall("/export/history/markdown");
      downloadBlob(blob, "learning-history.md");
      showResult("History exported!");
    } catch (err) { showResult("Error: " + err.message); }
  });

  document.getElementById("btn-export-cards").addEventListener("click", async () => {
    try {
      const blob = await apiCall("/export/flashcards/markdown");
      downloadBlob(blob, "flashcards.md");
      showResult("Flashcards exported!");
    } catch (err) { showResult("Error: " + err.message); }
  });

  document.getElementById("btn-open-panel").addEventListener("click", async () => {
    chrome.runtime.sendMessage({ action: "openSidePanel" });
  });

  // Helpers
  async function showDashboard() {
    authScreen.classList.add("hidden");
    dashScreen.classList.remove("hidden");
    const user = await getUser();
    if (user) {
      document.getElementById("user-name").textContent = user.displayName || user.email;
    }
  }

  function showError(msg) {
    const el = document.getElementById("auth-error");
    el.textContent = msg;
    el.classList.remove("hidden");
  }

  function hideError() {
    document.getElementById("auth-error").classList.add("hidden");
  }

  function showResult(text) {
    const el = document.getElementById("dash-result");
    el.textContent = text;
    el.classList.remove("hidden");
  }

  function formatHistory(data) {
    if (!data.length) return "No learning history yet. Start selecting text on webpages!";
    return data.slice(0, 10).map(h =>
      `[${h.actionType}] ${h.pageTitle || "Untitled"}\n${h.createdAt}\n${h.sourceText?.substring(0, 80)}...\n`
    ).join("\n");
  }

  function formatSkills(data) {
    if (!data.length) return "No skills tracked yet. Start learning!";
    return data.map(s =>
      `${s.name} (${s.category || "General"}) - studied ${s.interactionCount}x`
    ).join("\n");
  }

  function downloadBlob(blob, filename) {
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = filename;
    a.click();
    URL.revokeObjectURL(url);
  }
});
