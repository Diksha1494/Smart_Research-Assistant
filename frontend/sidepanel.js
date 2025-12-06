document.addEventListener('DOMContentLoaded', () => {
    chrome.storage.local.get(['researchNotes'], function(result) {
       if (result.researchNotes) {
        document.getElementById('notes').value = result.researchNotes;
       } 
    });

    document.getElementById('summarizeBtn').addEventListener('click', summarizeText);
    document.getElementById('saveNotesBtn').addEventListener('click', saveNotes);
});

document.getElementById("generateCitationBtn").addEventListener("click", () => {

    // 👉 You already summarize the current page
    // So we reuse tab URL
    chrome.tabs.query({ active: true, currentWindow: true }, (tabs) => {

        const pageUrl = tabs[0].url;
        const format = document.getElementById("citationFormat").value;
        const output = document.getElementById("citationResult");

        fetch(
            `http://localhost:8080/api/citation?url=${encodeURIComponent(pageUrl)}&format=${format}`,
            { method: "POST" }
        )
        .then(res => res.text())
        .then(data => {
            output.value = data;
        })
        .catch(err => {
            output.value = "Error generating citation";
            console.error(err);
        });
    });
});

document.getElementById("copyCitationBtn").addEventListener("click", () => {
    const citation = document.getElementById("citationResult");
    citation.select();
    document.execCommand("copy");
});

async function summarizeText() {
    try {
        const [tab] = await chrome.tabs.query({ active:true, currentWindow: true});
        const [{ result }] = await chrome.scripting.executeScript({
            target: {tabId: tab.id},
            function: () => window.getSelection().toString()
        });

        if (!result) {
            showResult('Please select some text first');
            return;
        }

        const response = await fetch('http://localhost:8080/api/research/process', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ content: result, operation: 'summarize'})
        });

        if (!response.ok) {
            throw new Error(`API Error: ${response.status}`);
        }

        const text = await response.text();
        showResult(text.replace(/\n/g,'<br>'));

    } catch (error) {
        showResult('Error: ' + error.message);
    }
}


async function saveNotes() {
    const notes = document.getElementById('notes').value;
    chrome.storage.local.set({ 'researchNotes': notes}, function() {
        alert('Notes saved successfully');
    });
}


function showResult(content) {
    document.getElementById('results').innerHTML =  `<div class="result-item"><div class="result-content">${content}</div></div>`;
}