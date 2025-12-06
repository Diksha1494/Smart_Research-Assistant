Perfect — simpler is better 👍
Here is a **clean, minimal GitHub README** with:

✅ **Author section removed**
✅ **Resume highlight removed**
✅ Simple, clear wording

👉 **Copy–paste directly into `README.md`**

---

```md
# 📚 Smart Research Assistant

Smart Research Assistant is a **Chrome extension** that helps users **summarize web content**, take **research notes**, and **generate academic citations** automatically.  
It is designed to support **ethical and plagiarism-free research**.

---

## 🚀 Features

- Website and text summarization  
- Chrome extension side panel UI  
- Research notes section  
- Automatic citation generator  
  - APA  
  - MLA  
  - IEEE  
- One-click copy citation  

---

## 🛠 Tech Stack

### Frontend
- HTML  
- CSS  
- JavaScript  

### Backend
- Spring Boot  
- REST APIs  
- Jsoup (HTML parsing and metadata extraction)  

---

## 🧠 How It Works

1. User opens any webpage  
2. Extension captures the active tab URL or content  
3. Request is sent to the Spring Boot backend  
4. Backend processes the data:
   - Generates summary  
   - Extracts metadata using Jsoup  
   - Formats citations  
5. Results are displayed in the side panel  

---

## 📌 Citation Generator

Supports the following citation formats:
- APA  
- MLA  
- IEEE  

Metadata includes:
- Page title  
- Author (if available)  
- Publication date  
- Website name  
- URL  

Fallback logic is applied if metadata is missing.

---

## 📂 Project Structure

```

Smart_Research-Assistant/
├── backend/     # Spring Boot application
└── frontend/    # Chrome extension files

````

## ⚙️ Run Locally

### Backend
```bash
cd backend
./mvnw spring-boot:run
````

Runs on:

```
http://localhost:8080
---
### Frontend (Chrome Extension)

1. Open Chrome
2. Go to `chrome://extensions`
3. Enable **Developer Mode**
4. Click **Load unpacked**
5. Select the `frontend` folder

---

## 🎯 Use Cases

* Academic research
* Assignments and reports
* Quick article understanding
* Ethical content usage with citations

---

## 🧪 Future Improvements

* PDF summarization
* Citation history
* Backend cloud deployment
* Dark mode  


