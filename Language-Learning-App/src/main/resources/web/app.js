const wordEmojis = { apple: "🍎", window: "🪟", book: "📘", language: "💬", pattern: "🧩" };
const PATTERN_EVENT_POLL_INTERVAL_MS = 2200;
const CORRECT_BEEP_START_HZ = 660;
const CORRECT_BEEP_END_HZ = 880;
const CORRECT_BEEP_SWEEP_SECONDS = 0.12;
const CORRECT_BEEP_DURATION_SECONDS = 0.18;
const gameDetails = {
  "listen-pick": { title: "Listen & Pick", icon: "🎧", instructions: "Listen to the word, then choose its meaning." },
  "picture-word": { title: "Picture → Word", icon: "🖼️", instructions: "Look closely. Which word belongs to this picture?" },
  "word-picture": { title: "Word → Picture", icon: "🧠", instructions: "Read the word and tap its picture." },
  translation: { title: "Translation", icon: "✍️", instructions: "Type the translation that fits the prompt." },
  quiz: { title: "Quick Quiz", icon: "🎯", instructions: "Choose the best answer." },
};

const wordGrid = document.querySelector("#word-grid");
const cardList = document.querySelector("#decorator-cards");
const questionArea = document.querySelector("#question-area");
const feedbackLine = document.querySelector("#feedback");
const burst = document.querySelector("#star-burst");
const patternList = document.querySelector("#pattern-list");
let lessonData;
let activeGame = "";
let questionIndex = 0;
let audioContext;

async function requestJson(path, options = {}) {
  const response = await fetch(path, options);
  const result = await response.json();
  if (!response.ok) throw new Error(result.error || "Request failed");
  return result;
}

function emojiFor(word) {
  return wordEmojis[word] || "🌟";
}

function renderWords(words) {
  wordGrid.replaceChildren(...words.map((word) => {
    const card = document.createElement("article");
    card.className = "word-card";
    const emoji = document.createElement("span");
    emoji.className = "emoji";
    emoji.textContent = emojiFor(word.text);
    emoji.setAttribute("aria-hidden", "true");
    const title = document.createElement("strong");
    title.textContent = word.text;
    const translation = document.createElement("small");
    translation.textContent = word.translation;
    card.append(emoji, title, translation);
    return card;
  }));
}

function renderDecoratorCards(cards) {
  const visibleDecorations = new Set([...document.querySelectorAll("[data-decoration]:checked")]
    .map((toggle) => toggle.dataset.decoration));
  cardList.replaceChildren(...cards.filter((card) => visibleDecorations.has(card.decoration)).map((card) => {
    const item = document.createElement("div");
    item.className = "decorator-card";
    const label = document.createElement("b");
    label.textContent = `${card.word} · ${card.decoration}: `;
    item.append(label, document.createTextNode(card.content));
    return item;
  }));
}

function setGame(gameName) {
  if (!lessonData) return;
  activeGame = gameName;
  questionIndex = 0;
  document.querySelectorAll(".game-tile").forEach((tile) => {
    const isActive = tile.dataset.game === gameName;
    tile.classList.toggle("active", isActive);
    tile.setAttribute("aria-pressed", String(isActive));
  });
  const details = gameDetails[gameName];
  document.querySelector("#game-title").textContent = details.title;
  document.querySelector("#game-icon").textContent = details.icon;
  document.querySelector("#game-kicker").textContent = "MINI-GAME SELECTED";
  document.querySelector("#game-instructions").textContent = details.instructions;
  feedbackLine.textContent = "";
  renderQuestion();
}

function getWord(index = questionIndex) {
  return lessonData.words[index % lessonData.words.length];
}

function makeQuestion() {
  const word = getWord();
  const translationExercise = lessonData.exercises.find((exercise) => exercise.type === "translation");
  const quizExercise = lessonData.exercises.find((exercise) => exercise.type === "quiz");
  if (activeGame === "translation" && translationExercise) {
    return { word: translationExercise.word, prompt: translationExercise.question, answer: translationExercise.correctAnswer, mode: "text" };
  }
  if (activeGame === "quiz" && quizExercise) {
    return { word: quizExercise.word, prompt: quizExercise.question, answer: quizExercise.correctAnswer, options: quizExercise.options, mode: "quiz" };
  }
  if (activeGame === "listen-pick") return { word: word.text, prompt: word.text, answer: word.translation, mode: "translation-choice" };
  if (activeGame === "picture-word") return { word: word.text, prompt: emojiFor(word.text), answer: word.text, mode: "word-choice" };
  return { word: word.text, prompt: word.text, answer: word.text, mode: "picture-choice" };
}

function renderQuestion() {
  if (!activeGame) return;
  const question = makeQuestion();
  const card = document.createElement("div");
  card.className = "question-card";
  if (question.mode === "picture-choice") {
    const prompt = document.createElement("p");
    prompt.className = "question-word";
    prompt.textContent = question.prompt;
    card.append(prompt);
  } else if (question.mode === "word-choice") {
    const picture = document.createElement("div");
    picture.className = "question-picture";
    picture.textContent = question.prompt;
    picture.setAttribute("aria-label", `Picture for ${question.word}`);
    card.append(picture);
  } else {
    const prompt = document.createElement("p");
    prompt.className = "question-prompt";
    prompt.textContent = question.prompt;
    card.append(prompt);
  }

  if (activeGame === "listen-pick") card.append(makeSpeakButton(question.prompt));
  if (question.mode === "text") {
    card.append(makeTextAnswer(question));
  } else {
    card.append(makeChoices(question));
  }
  questionArea.replaceChildren(card);
  feedbackLine.textContent = "";
  feedbackLine.className = "feedback";
}

function makeSpeakButton(word) {
  const button = document.createElement("button");
  button.className = "speak-button";
  button.type = "button";
  button.textContent = "🔊 Hear the word";
  button.addEventListener("click", () => speak(word));
  return button;
}

function speak(word) {
  if (!("speechSynthesis" in window)) {
    showFeedback("Speech is not available in this browser.", false);
    return;
  }
  window.speechSynthesis.cancel();
  window.speechSynthesis.speak(new SpeechSynthesisUtterance(word));
}

function makeTextAnswer(question) {
  const form = document.createElement("form");
  form.className = "answer-form";
  const input = document.createElement("input");
  input.className = "answer-input";
  input.type = "text";
  input.autocomplete = "off";
  input.placeholder = "Type your answer…";
  input.setAttribute("aria-label", "Type your answer");
  const button = document.createElement("button");
  button.className = "submit-button";
  button.type = "submit";
  button.textContent = "Check it";
  form.append(input, button);
  form.addEventListener("submit", (event) => {
    event.preventDefault();
    checkAnswer(input.value, question);
  });
  return form;
}

function makeChoices(question) {
  const choices = document.createElement("div");
  choices.className = "choice-list";
  const options = question.options || makeDistractors(question);
  options.forEach((option) => {
    const button = document.createElement("button");
    button.className = "choice-button";
    button.type = "button";
    const value = activeGame === "quiz" ? option.slice(0, 1) : option;
    button.textContent = activeGame === "word-picture" ? emojiFor(option) : option;
    button.setAttribute("aria-label", activeGame === "word-picture" ? option : String(option));
    button.addEventListener("click", () => checkAnswer(value, question));
    choices.append(button);
  });
  return choices;
}

function makeDistractors(question) {
  const words = lessonData.words;
  const correct = activeGame === "listen-pick"
    ? words.find((word) => word.text === question.word).translation
    : question.word;
  const other = words.find((word) => word.text !== question.word);
  const distractor = activeGame === "listen-pick" ? other.translation : other.text;
  return [correct, distractor].sort(() => Math.random() - 0.5);
}

async function checkAnswer(answer, question) {
  const correct = String(answer).trim().toLowerCase() === question.answer.trim().toLowerCase();
  const card = questionArea.querySelector(".question-card");
  card.classList.toggle("correct", correct);
  card.classList.toggle("wrong", !correct);
  if (correct) {
    playBeep();
    showStars();
    showFeedback("Brilliant! You got it! ⭐", true);
  } else {
    showFeedback(`Almost! The answer is ${question.answer}.`, false);
  }
  document.querySelectorAll(".choice-button, .submit-button, .speak-button").forEach((button) => {
    button.disabled = true;
  });
  try {
    const result = await requestJson("/api/answer", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ word: question.word, correct }),
    });
    if (correct && result.unlockedAchievements.length > 0) {
      showFeedback(`Achievement unlocked: ${result.unlockedAchievements.at(-1)} 🏆`, true);
    }
    await refreshProgress();
  } catch (error) {
    showFeedback(error.message, false);
  }
  const nextButton = document.createElement("button");
  nextButton.className = "next-button";
  nextButton.type = "button";
  nextButton.textContent = "Next word →";
  nextButton.addEventListener("click", () => {
    questionIndex++;
    renderQuestion();
  });
  card.append(nextButton);
}

function showFeedback(message, correct) {
  feedbackLine.textContent = message;
  feedbackLine.className = `feedback ${correct ? "correct" : "wrong"}`;
}

function playBeep() {
  const AudioContextType = window.AudioContext || window.webkitAudioContext;
  if (!AudioContextType) return;
  audioContext ||= new AudioContextType();
  const oscillator = audioContext.createOscillator();
  const gain = audioContext.createGain();
  oscillator.type = "sine";
  oscillator.frequency.setValueAtTime(CORRECT_BEEP_START_HZ, audioContext.currentTime);
  oscillator.frequency.exponentialRampToValueAtTime(CORRECT_BEEP_END_HZ,
    audioContext.currentTime + CORRECT_BEEP_SWEEP_SECONDS);
  gain.gain.setValueAtTime(0.12, audioContext.currentTime);
  gain.gain.exponentialRampToValueAtTime(0.001, audioContext.currentTime + CORRECT_BEEP_DURATION_SECONDS);
  oscillator.connect(gain);
  gain.connect(audioContext.destination);
  oscillator.start();
  oscillator.stop(audioContext.currentTime + CORRECT_BEEP_DURATION_SECONDS);
}

function showStars() {
  const stars = ["✦", "★", "✦", "★", "✦"];
  burst.replaceChildren(...stars.map((star, index) => {
    const piece = document.createElement("span");
    piece.textContent = star;
    piece.style.left = `${10 + index * 19}%`;
    piece.style.top = `${index % 2 === 0 ? 12 : 42}%`;
    return piece;
  }));
  window.setTimeout(() => burst.replaceChildren(), 900);
}

async function refreshProgress() {
  const progress = await requestJson("/api/progress");
  document.querySelector("#xp").textContent = progress.xp;
  document.querySelector("#streak").textContent = progress.streak;
  document.querySelector("#learned").textContent = progress.wordsLearned;
  document.querySelector("#due").textContent = progress.dueWords;
  const strategyValues = { Leitner: "leitner", Sm2: "sm2", SimpleInterval: "simple" };
  document.querySelector("#strategy-select").value = strategyValues[progress.strategy] || "leitner";
}

async function changeStrategy(event) {
  const selector = event.currentTarget;
  try {
    const result = await requestJson("/api/strategy", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name: selector.value }),
    });
    document.querySelector("#game-instructions").textContent = `${result.strategy} strategy is ready for your next review.`;
  } catch (error) {
    document.querySelector("#game-instructions").textContent = error.message;
    await refreshProgress();
  }
}

function patternTag(pattern) {
  return pattern.split("[")[0];
}

function renderPatternEvents(events) {
  const entries = events.map((event) => {
    const item = document.createElement("li");
    item.className = "pattern-event";
    const meta = document.createElement("div");
    meta.className = "event-meta";
    const tag = document.createElement("span");
    const pattern = patternTag(event.pattern);
    tag.className = `pattern-tag tag-${pattern.toLowerCase()}`;
    tag.textContent = pattern;
    const time = document.createElement("time");
    time.className = "event-time";
    time.dateTime = event.occurredAt;
    time.textContent = new Date(event.occurredAt).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit", second: "2-digit" });
    const message = document.createElement("p");
    message.textContent = event.message;
    meta.append(tag, time);
    item.append(meta, message);
    return item;
  });
  patternList.replaceChildren(...(entries.length ? entries : [emptyPatternMessage()]));
}

function emptyPatternMessage() {
  const item = document.createElement("li");
  item.className = "pattern-empty";
  item.textContent = "Waiting for pattern events…";
  return item;
}

async function refreshPatternEvents() {
  try {
    const events = await requestJson("/api/events");
    renderPatternEvents(events);
  } catch (error) {
    patternList.replaceChildren(emptyPatternMessage());
  }
}

async function loadLesson() {
  try {
    lessonData = await requestJson("/api/lesson");
    document.querySelector("#lesson-title").textContent = lessonData.title;
    renderWords(lessonData.words);
    renderDecoratorCards(lessonData.cards);
    await refreshProgress();
    await refreshPatternEvents();
  } catch (error) {
    document.querySelector("#lesson-title").textContent = "Could not load your lesson";
    document.querySelector("#game-instructions").textContent = error.message;
  }
}

document.querySelectorAll(".game-tile").forEach((tile) => {
  tile.addEventListener("click", () => setGame(tile.dataset.game));
});
document.querySelector("#strategy-select").addEventListener("change", changeStrategy);
document.querySelectorAll("[data-decoration]").forEach((toggle) => {
  toggle.addEventListener("change", () => lessonData && renderDecoratorCards(lessonData.cards));
});
window.setInterval(refreshPatternEvents, PATTERN_EVENT_POLL_INTERVAL_MS);
loadLesson();
