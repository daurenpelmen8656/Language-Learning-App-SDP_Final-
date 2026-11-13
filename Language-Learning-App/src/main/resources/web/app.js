const wordEmojis = { apple: "🍎", window: "🪟", book: "📘", language: "💬", pattern: "🧩" };
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
  cardList.replaceChildren(...cards.map((card) => {
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
    await requestJson("/api/answer", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ word: question.word, correct }),
    });
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
  oscillator.frequency.setValueAtTime(660, audioContext.currentTime);
  oscillator.frequency.exponentialRampToValueAtTime(880, audioContext.currentTime + 0.12);
  gain.gain.setValueAtTime(0.12, audioContext.currentTime);
  gain.gain.exponentialRampToValueAtTime(0.001, audioContext.currentTime + 0.18);
  oscillator.connect(gain);
  gain.connect(audioContext.destination);
  oscillator.start();
  oscillator.stop(audioContext.currentTime + 0.18);
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
}

async function loadLesson() {
  try {
    lessonData = await requestJson("/api/lesson");
    document.querySelector("#lesson-title").textContent = lessonData.title;
    renderWords(lessonData.words);
    renderDecoratorCards(lessonData.cards);
    await refreshProgress();
  } catch (error) {
    document.querySelector("#lesson-title").textContent = "Could not load your lesson";
    document.querySelector("#game-instructions").textContent = error.message;
  }
}

document.querySelectorAll(".game-tile").forEach((tile) => {
  tile.addEventListener("click", () => setGame(tile.dataset.game));
});
loadLesson();
