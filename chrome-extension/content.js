// AI Webpage Tutor - Content Script
(function () {
  const ACTIONS = [
    { id: "explain", label: "Explain", icon: "\uD83D\uDCA1" },
    { id: "summarize", label: "Summarize", icon: "\uD83D\uDCDD" },
    { id: "quiz", label: "Quiz", icon: "\u2753" },
    { id: "flashcards", label: "Flashcards", icon: "\uD83C\uDCCF" },
    { id: "diagram", label: "Diagram", icon: "\uD83D\uDCC8" },
    { id: "translate", label: "Translate", icon: "\uD83C\uDF10" },
    { id: "examples", label: "Examples", icon: "\uD83C\uDF1F" },
    { id: "code-examples", label: "Code", icon: "\uD83D\uDCBB" },
    { id: "resources", label: "Resources", icon: "\uD83D\uDCDA" },
  ];

  let toolbar = null;
  let selectedText = "";

  function createToolbar() {
    toolbar = document.createElement("div");
    toolbar.id = "ai-tutor-toolbar";

    const row = document.createElement("div");
    row.className = "ai-tutor-row";

    ACTIONS.forEach((action) => {
      const btn = document.createElement("button");
      btn.dataset.action = action.id;
      btn.innerHTML = `<span class="ai-tutor-icon">${action.icon}</span>${action.label}`;
      btn.addEventListener("click", (e) => {
        e.preventDefault();
        e.stopPropagation();
        handleAction(action.id, btn);
      });
      row.appendChild(btn);
    });

    toolbar.appendChild(row);
    document.body.appendChild(toolbar);
  }

  function showToolbar(x, y) {
    if (!toolbar) createToolbar();
    toolbar.style.display = "block";
    toolbar.style.left = `${x}px`;
    toolbar.style.top = `${y + 10}px`;

    // Adjust if off-screen
    requestAnimationFrame(() => {
      const rect = toolbar.getBoundingClientRect();
      if (rect.right > window.innerWidth) {
        toolbar.style.left = `${window.innerWidth - rect.width - 10}px`;
      }
      if (rect.bottom > window.innerHeight) {
        toolbar.style.top = `${y - rect.height - 10}px`;
      }
    });
  }

  function hideToolbar() {
    if (toolbar) toolbar.style.display = "none";
  }

  // Listen for text selection
  document.addEventListener("mouseup", (e) => {
    // Ignore clicks on the toolbar itself
    if (toolbar && toolbar.contains(e.target)) return;

    setTimeout(() => {
      const selection = window.getSelection();
      const text = selection.toString().trim();

      if (text.length > 3) {
        selectedText = text;
        const range = selection.getRangeAt(0);
        const rect = range.getBoundingClientRect();
        showToolbar(
          rect.left + window.scrollX,
          rect.bottom + window.scrollY
        );
      } else {
        hideToolbar();
      }
    }, 10);
  });

  // Hide toolbar on click elsewhere
  document.addEventListener("mousedown", (e) => {
    if (toolbar && !toolbar.contains(e.target)) {
      hideToolbar();
    }
  });

  async function handleAction(actionId, btn) {
    btn.classList.add("loading");
    btn.textContent = "...";

    chrome.runtime.sendMessage(
      {
        action: "aiAction",
        type: actionId,
        text: selectedText,
        sourceUrl: window.location.href,
        pageTitle: document.title,
      },
      (response) => {
        btn.classList.remove("loading");
        // Restore button label
        const actionDef = ACTIONS.find((a) => a.id === actionId);
        btn.innerHTML = `<span class="ai-tutor-icon">${actionDef.icon}</span>${actionDef.label}`;

        if (response && response.error) {
          chrome.runtime.sendMessage({
            action: "showInPanel",
            data: { error: response.error },
          });
        }
        // Response is shown in the side panel via the background script
      }
    );
  }

  // Listen for messages from background to show results inline if needed
  chrome.runtime.onMessage.addListener((msg) => {
    if (msg.action === "getSelectedText") {
      const text = window.getSelection().toString().trim();
      // Handled by background script context menu
    }
  });
})();
