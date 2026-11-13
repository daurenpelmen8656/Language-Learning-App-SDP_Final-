const wordEmojis = { apple: "🍎", window: "🪟", book: "📘", language: "💬", pattern: "🧩" };
const wordGrid = document.querySelector("#word-grid");
const exerciseList = document.querySelector("#exercise-list");
const statusLine = document.querySelector("#status");

async function requestJson(path, options = {}) {
  const response = await fetch(path, options);
  const result = await response.json();
  if (!response.ok) throw new Error(result.error || "Request failed");
  return result;
}

function renderWords(words) {
  wordGrid.replaceChildren(...words.map((word) => {
    const card = document.createElement("article");
    card.className = "word-card";
    card.innerHTML = `<span class="word-emoji" aria-hidden="true">${wordEmojis[word.text] || "🌟"}</span><h2>${escapeHtml(word.text)}</h2><p>${escapeHtml(word.translation)}</p>`;
    return card;
  }));
}

function renderExercises(exercises) {
  exerciseList.replaceChildren(...exercises.map((exercise, index) => {
    const card = document.createElement("article");
    card.className = "exercise";
    card.innerHTML = `<label for="answer-${index}">${escapeHtml(exercise.question)}</label><form><input id="answer-${index}" autocomplete="off" aria-label="Your answer"><button type="submit">Check</button></form>`;
    card.querySelector("form").addEventListener("submit", (event) => submitAnswer(event, card, exercise));
    return card;
  }));
}

async function submitAnswer(event, card, exercise) {
  event.preventDefault();
  const answer = card.querySelector("input").value.trim();
  const correct = answer.toLowerCase() === exercise.correctAnswer.trim().toLowerCase();
  try {
    const result = await requestJson("/api/answer", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ word: exercise.word, correct }),
    });
    statusLine.textContent = correct ? `Nice! +10 XP · next review ${result.nextReviewDate}` : `Good try! Answer: ${exercise.correctAnswer}`;
    await refreshProgress();
  } catch (error) {
    statusLine.textContent = error.message;
  }
}

async function refreshProgress() {
  const progress = await requestJson("/api/progress");
  document.querySelector("#xp").textContent = progress.xp;
  document.querySelector("#streak").textContent = progress.streak;
  document.querySelector("#learned").textContent = progress.wordsLearned;
}

function escapeHtml(value) {
  return String(value).replace(/[&<>"']/g, (character) => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;",
  })[character]);
}

async function loadLesson() {
  try {
    const lesson = await requestJson("/api/lesson");
    document.querySelector("#lesson-title").textContent = lesson.title;
    renderWords(lesson.words);
    renderExercises(lesson.exercises);
    await refreshProgress();
  } catch (error) {
    document.querySelector("#lesson-title").textContent = "Could not load your lesson";
    statusLine.textContent = error.message;
  }
}

loadLesson();
