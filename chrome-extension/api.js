// API_BASE is defined in config.js

async function getToken() {
  const data = await chrome.storage.local.get("jwt_token");
  return data.jwt_token || null;
}

async function saveToken(token) {
  await chrome.storage.local.set({ jwt_token: token });
}

async function saveUser(user) {
  await chrome.storage.local.set({ user_info: user });
}

async function getUser() {
  const data = await chrome.storage.local.get("user_info");
  return data.user_info || null;
}

async function clearAuth() {
  await chrome.storage.local.remove(["jwt_token", "user_info"]);
}

async function apiCall(endpoint, method = "GET", body = null) {
  const token = await getToken();
  const headers = { "Content-Type": "application/json" };
  if (token) headers["Authorization"] = `Bearer ${token}`;

  const opts = { method, headers };
  if (body) opts.body = JSON.stringify(body);

  const res = await fetch(`${API_BASE}${endpoint}`, opts);

  if (!res.ok) {
    const err = await res.json().catch(() => ({ error: "Request failed" }));
    throw new Error(err.error || `HTTP ${res.status}`);
  }

  const contentType = res.headers.get("content-type");
  if (contentType && contentType.includes("application/json")) {
    return res.json();
  }
  // For file downloads (markdown export)
  if (contentType && contentType.includes("octet-stream")) {
    return res.blob();
  }
  return res.text();
}

// Auth
async function register(email, password, displayName) {
  const data = await apiCall("/auth/register", "POST", { email, password, displayName });
  await saveToken(data.token);
  await saveUser({ email: data.email, displayName: data.displayName });
  return data;
}

async function login(email, password) {
  const data = await apiCall("/auth/login", "POST", { email, password });
  await saveToken(data.token);
  await saveUser({ email: data.email, displayName: data.displayName });
  return data;
}

async function logout() {
  await clearAuth();
}

// AI endpoints
async function aiAction(action, text, sourceUrl, pageTitle, targetLanguage) {
  return apiCall(`/ai/${action}`, "POST", { text, sourceUrl, pageTitle, targetLanguage });
}
