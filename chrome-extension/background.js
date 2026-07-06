// API_BASE is defined in config.js
importScripts("config.js");

// Create context menus on install
chrome.runtime.onInstalled.addListener(() => {
  const actions = [
    { id: "explain", title: "Explain This" },
    { id: "summarize", title: "Summarize" },
    { id: "quiz", title: "Generate Quiz" },
    { id: "flashcards", title: "Make Flashcards" },
    { id: "diagram", title: "Create Diagram" },
    { id: "translate", title: "Translate" },
    { id: "examples", title: "Real-World Examples" },
    { id: "code-examples", title: "Code Examples" },
    { id: "resources", title: "Related Resources" },
  ];

  chrome.contextMenus.create({
    id: "ai-tutor-parent",
    title: "AI Tutor",
    contexts: ["selection"],
  });

  actions.forEach((action) => {
    chrome.contextMenus.create({
      id: `ai-tutor-${action.id}`,
      parentId: "ai-tutor-parent",
      title: action.title,
      contexts: ["selection"],
    });
  });
});

// Handle context menu clicks
chrome.contextMenus.onClicked.addListener(async (info, tab) => {
  if (!info.menuItemId.startsWith("ai-tutor-")) return;
  const actionType = info.menuItemId.replace("ai-tutor-", "");
  if (actionType === "parent") return;

  const text = info.selectionText;
  await performAiAction(actionType, text, tab.url, tab.title, tab);
});

// Handle messages from content script and popup
chrome.runtime.onMessage.addListener((msg, sender, sendResponse) => {
  if (msg.action === "aiAction") {
    performAiAction(msg.type, msg.text, msg.sourceUrl, msg.pageTitle, sender.tab)
      .then(() => sendResponse({ success: true }))
      .catch((err) => sendResponse({ error: err.message }));
    return true; // async response
  }

  if (msg.action === "openSidePanel") {
    if (sender.tab) {
      chrome.sidePanel.open({ tabId: sender.tab.id });
    }
  }

  if (msg.action === "showInPanel") {
    // Forward to side panel
    chrome.runtime.sendMessage({ action: "displayResult", data: msg.data });
  }
});

async function performAiAction(actionType, text, sourceUrl, pageTitle, tab) {
  // Open side panel to show loading
  if (tab && tab.id) {
    try {
      await chrome.sidePanel.open({ tabId: tab.id });
    } catch (e) {
      // Side panel might not be supported or already open
    }
  }

  // Send loading state to panel
  chrome.runtime.sendMessage({
    action: "displayResult",
    data: { loading: true, actionType, text: text.substring(0, 100) },
  });

  try {
    const data = await chrome.storage.local.get("jwt_token");
    const token = data.jwt_token;

    if (!token) {
      chrome.runtime.sendMessage({
        action: "displayResult",
        data: { error: "Please login first. Click the extension icon to login." },
      });
      return;
    }

    const response = await fetch(`${API_BASE}/ai/${actionType}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({ text, sourceUrl, pageTitle }),
    });

    if (!response.ok) {
      const err = await response.json().catch(() => ({}));
      throw new Error(err.error || `Request failed (${response.status})`);
    }

    const result = await response.json();

    chrome.runtime.sendMessage({
      action: "displayResult",
      data: {
        loading: false,
        actionType: result.actionType,
        result: result.result,
        historyId: result.historyId,
        sourceText: text,
      },
    });
  } catch (err) {
    chrome.runtime.sendMessage({
      action: "displayResult",
      data: { error: err.message },
    });
  }
}
