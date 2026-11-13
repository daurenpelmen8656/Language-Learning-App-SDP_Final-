const wordEmojis = {
  apple: "🍎", window: "🪟", book: "📘", language: "💬", pattern: "🧩", student: "🎓",
  water: "💧", sun: "☀️", house: "🏠", school: "🏫", teacher: "👩‍🏫", friend: "🫶",
  family: "👨‍👩‍👧‍👦", computer: "💻", table: "🪑", chair: "🪑", door: "🚪", city: "🏙️",
  food: "🍽️", music: "🎵", hello: "👋", goodbye: "👋", please: "🙏", "thank you": "🙏",
  yes: "👍", no: "👎", morning: "🌅",
  evening: "🌇", night: "🌙", mother: "👩", father: "👨", child: "🧒", cat: "🐈",
  dog: "🐕", bird: "🐦", fish: "🐟", bread: "🍞", milk: "🥛", tea: "🍵", coffee: "☕",
  red: "🔴", blue: "🔵", green: "🟢", small: "🐜", big: "🐘", happy: "😄",
  car: "🚗", train: "🚆", road: "🛣️", phone: "📱",
  one: "1️⃣", two: "2️⃣", three: "3️⃣", four: "4️⃣", five: "5️⃣", read: "📖", write: "✍️",
  run: "🏃", eat: "🍴", sleep: "😴",
};
const PATTERN_EVENT_POLL_INTERVAL_MS = 2200;
const CORRECT_BEEP_START_HZ = 660;
const CORRECT_BEEP_END_HZ = 880;
const CORRECT_BEEP_SWEEP_SECONDS = 0.12;
const CORRECT_BEEP_DURATION_SECONDS = 0.18;
const gameDetails = {
  "listen-pick": { title: "listenGame", icon: "🎧", instructions: "listenHintFull" },
  "picture-word": { title: "pictureWordGame", icon: "🖼️", instructions: "pictureWordHintFull" },
  "word-picture": { title: "wordPictureGame", icon: "🧠", instructions: "wordPictureHintFull" },
  translation: { title: "translationGame", icon: "✍️", instructions: "translationHintFull" },
  quiz: { title: "quizGame", icon: "🎯", instructions: "quizHintFull" },
};
const languageTexts = {
  en: {
    appTitle: "Language Learning App", homeLabel: "Language Learning App home", statsLabel: "Your learning stats",
    xpLabel: "XP", streakLabel: "streak", learnedLabel: "learned", dueLabel: "due today",
    languageLabel: "Interface language", strategyLabel: "Review strategy", simpleStrategy: "Simple",
    cardExtras: "🎨 Card extras", exampleDecoration: "Example", audioDecoration: "Audio", imageDecoration: "Image hint",
    heroEyebrow: "A LITTLE PLAY GOES A LONG WAY", heroTitle: "Your next word adventure awaits!",
    heroDescription: "Choose a mini-game, meet a few new words, and collect your stars.", hello: "hello!",
    gamesEyebrow: "PICK YOUR PLAY", gamesTitle: "Five ways to learn", listenGame: "Listen & Pick",
    listenHint: "Hear it. Find it.", pictureWordGame: "Picture → Word", pictureWordHint: "Spot the right word.",
    wordPictureGame: "Word → Picture", wordPictureHint: "Match it to a picture.", translationGame: "Translation",
    translationHint: "Type what it means.", quizGame: "Quick Quiz", quizHint: "Pick the best answer.",
    playLayoutLabel: "Selected game and vocabulary", ready: "READY WHEN YOU ARE", pickGame: "Pick a game to begin",
    chooseGame: "Choose one of the colorful tiles above.", questionPlaceholder: "Your next question will pop up here.",
    vocabularyEyebrow: "TODAY'S WORD CREW", vocabularyTitle: "Meet the words", loadingWords: "Gathering your words…",
    liveDemo: "LIVE DEMO", patternsTitle: "Patterns in action", live: "LIVE",
    patternsCaption: "A peek at the helpers behind each answer.", waitingEvents: "Waiting for pattern events…",
    footerMade: "Made for curious minds", footerWords: "One word at a time", miniGameSelected: "MINI-GAME SELECTED",
    listenHintFull: "Listen to the word, then choose its meaning.", pictureWordHintFull: "Look closely. Which word belongs to this picture?",
    wordPictureHintFull: "Read the word and tap its picture.", translationHintFull: "Type the translation that fits the prompt.",
    quizHintFull: "Choose the best answer.", speechUnavailable: "Speech is not available in this browser.",
    hearWord: "🔊 Hear the word", answerPlaceholder: "Type your answer…", answerLabel: "Type your answer",
    checkAnswer: "Check it", correctFeedback: "Brilliant! You got it! ⭐", wrongFeedback: "Almost! The answer is {answer}.",
    unlocked: "Achievement unlocked: {name} 🏆", nextWord: "Next word →", strategyReady: "{strategy} strategy is ready for your next review.",
    loadingError: "Could not load your lesson", translatePrompt: "Translate this word: {word}",
    requestFailed: "The request could not be completed.", pictureLabel: "Picture for {word}",
    quizPrompt: "Choose the meaning of {word}:", cardDecoration: "{word} · {decoration}: ",
    exampleName: "Example", audioName: "Audio", imageName: "Image hint",
  },
  ru: {
    appTitle: "Приложение для изучения языков", homeLabel: "Главная Language Learning App", statsLabel: "Ваша статистика",
    xpLabel: "опыта", streakLabel: "дней подряд", learnedLabel: "изучено", dueLabel: "повторить сегодня",
    languageLabel: "Язык интерфейса", strategyLabel: "Алгоритм повторения", simpleStrategy: "Простой",
    cardExtras: "🎨 Дополнения карточек", exampleDecoration: "Пример", audioDecoration: "Аудио", imageDecoration: "Подсказка-картинка",
    heroEyebrow: "УЧИТЬСЯ МОЖНО ИГРАЯ", heroTitle: "Впереди новое приключение со словами!",
    heroDescription: "Выберите мини-игру, познакомьтесь с новыми словами и соберите звёзды.", hello: "привет!",
    gamesEyebrow: "ВЫБЕРИТЕ ИГРУ", gamesTitle: "Пять способов учиться", listenGame: "Слушай и выбирай",
    listenHint: "Услышь слово. Найди перевод.", pictureWordGame: "Картинка → слово", pictureWordHint: "Найди подходящее слово.",
    wordPictureGame: "Слово → картинка", wordPictureHint: "Подбери картинку к слову.", translationGame: "Перевод",
    translationHint: "Напиши перевод.", quizGame: "Быстрая викторина", quizHint: "Выбери правильный ответ.",
    playLayoutLabel: "Выбранная игра и словарь", ready: "ГОТОВЫ НАЧАТЬ?", pickGame: "Выберите игру",
    chooseGame: "Нажмите на одну из цветных карточек выше.", questionPlaceholder: "Здесь появится следующий вопрос.",
    vocabularyEyebrow: "СЛОВА НА СЕГОДНЯ", vocabularyTitle: "Знакомьтесь со словами", loadingWords: "Загружаем слова…",
    liveDemo: "ДЕМО В РЕАЛЬНОМ ВРЕМЕНИ", patternsTitle: "Паттерны в действии", live: "В ЭФИРЕ",
    patternsCaption: "Посмотрите, как паттерны помогают приложению.", waitingEvents: "Ждём события паттернов…",
    footerMade: "Для любознательных", footerWords: "По одному слову за раз", miniGameSelected: "ИГРА ВЫБРАНА",
    listenHintFull: "Послушайте слово и выберите его перевод.", pictureWordHintFull: "Посмотрите на картинку. Какое слово подходит?",
    wordPictureHintFull: "Прочитайте слово и выберите картинку.", translationHintFull: "Введите перевод слова.",
    quizHintFull: "Выберите правильный ответ.", speechUnavailable: "В этом браузере нет озвучивания.",
    hearWord: "🔊 Послушать слово", answerPlaceholder: "Введите ответ…", answerLabel: "Введите ответ",
    checkAnswer: "Проверить", correctFeedback: "Отлично! Верный ответ! ⭐", wrongFeedback: "Почти! Правильный ответ: {answer}.",
    unlocked: "Достижение открыто: {name} 🏆", nextWord: "Следующее слово →", strategyReady: "Алгоритм «{strategy}» выбран для повторения.",
    loadingError: "Не удалось загрузить урок", translatePrompt: "Переведите слово: {word}",
    requestFailed: "Не удалось выполнить запрос.", pictureLabel: "Картинка для слова «{word}»",
    quizPrompt: "Выберите перевод слова «{word}»:", cardDecoration: "{word} · {decoration}: ",
    exampleName: "Пример", audioName: "Аудио", imageName: "Подсказка-картинка",
  },
  kk: {
    appTitle: "Тіл үйрену қолданбасы", homeLabel: "Тіл үйрену қолданбасының басты беті", statsLabel: "Оқу статистикасыңыз",
    xpLabel: "ұпай", streakLabel: "күн қатарынан", learnedLabel: "үйренілді", dueLabel: "бүгін қайталау",
    languageLabel: "Интерфейс тілі", strategyLabel: "Қайталау әдісі", simpleStrategy: "Қарапайым",
    cardExtras: "🎨 Карточка безендірулері", exampleDecoration: "Мысал", audioDecoration: "Аудио", imageDecoration: "Сурет ишарасы",
    heroEyebrow: "ОЙЫН АРҚЫЛЫ ҮЙРЕН", heroTitle: "Жаңа сөздер әлеміне қош келдіңіз!",
    heroDescription: "Шағын ойынды таңдап, жаңа сөздермен танысып, жұлдыз жинаңыз.", hello: "сәлем!",
    gamesEyebrow: "ОЙЫНДЫ ТАҢДАҢЫЗ", gamesTitle: "Үйренудің бес жолы", listenGame: "Тыңда да таңда",
    listenHint: "Тыңда. Дұрыс аудармасын тап.", pictureWordGame: "Сурет → сөз", pictureWordHint: "Сәйкес сөзді тап.",
    wordPictureGame: "Сөз → сурет", wordPictureHint: "Сөзге сәйкес суретті таңда.", translationGame: "Аударма",
    translationHint: "Аудармасын жаз.", quizGame: "Жылдам викторина", quizHint: "Дұрыс жауапты таңда.",
    playLayoutLabel: "Таңдалған ойын және сөздік", ready: "БАСТАУҒА ДАЙЫНСЫЗ БА?", pickGame: "Ойынды бастаңыз",
    chooseGame: "Жоғарыдағы түрлі түсті ойындардың бірін таңдаңыз.", questionPlaceholder: "Келесі сұрақ осында шығады.",
    vocabularyEyebrow: "БҮГІНГІ СӨЗДЕР", vocabularyTitle: "Сөздермен танысыңыз", loadingWords: "Сөздер жүктелуде…",
    liveDemo: "ТІКЕЛЕЙ ДЕМО", patternsTitle: "Паттерндердің жұмысы", live: "ТІКЕЛЕЙ",
    patternsCaption: "Паттерндер қолданбаға қалай көмектесетінін көріңіз.", waitingEvents: "Паттерн оқиғалары күтілуде…",
    footerMade: "Білімге құштар жандарға", footerWords: "Күніне бір сөз", miniGameSelected: "ОЙЫН ТАҢДАЛДЫ",
    listenHintFull: "Сөзді тыңдап, аудармасын таңдаңыз.", pictureWordHintFull: "Суретке қараңыз. Қай сөз сәйкес келеді?",
    wordPictureHintFull: "Сөзді оқып, сәйкес суретті таңдаңыз.", translationHintFull: "Сөздің аудармасын енгізіңіз.",
    quizHintFull: "Дұрыс жауапты таңдаңыз.", speechUnavailable: "Бұл браузерде сөзді дыбыстау жоқ.",
    hearWord: "🔊 Сөзді тыңдау", answerPlaceholder: "Жауабыңызды жазыңыз…", answerLabel: "Жауабыңызды жазыңыз",
    checkAnswer: "Тексеру", correctFeedback: "Жарайсыз! Дұрыс жауап! ⭐", wrongFeedback: "Сәл қалды! Дұрыс жауап: {answer}.",
    unlocked: "Жетістік ашылды: {name} 🏆", nextWord: "Келесі сөз →", strategyReady: "«{strategy}» әдісі қайталауға дайын.",
    loadingError: "Сабақты жүктеу мүмкін болмады", translatePrompt: "Сөзді аударыңыз: {word}",
    requestFailed: "Сұрауды орындау мүмкін болмады.", pictureLabel: "«{word}» сөзіне арналған сурет",
    quizPrompt: "«{word}» сөзінің аудармасын таңдаңыз:", cardDecoration: "{word} · {decoration}: ",
    exampleName: "Мысал", audioName: "Аудио", imageName: "Сурет ишарасы",
  },
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
const LANGUAGE_STORAGE_KEY = "language-learning-app-language";
let currentLanguage = loadSavedLanguage();

function loadSavedLanguage() {
  try {
    const savedLanguage = localStorage.getItem(LANGUAGE_STORAGE_KEY);
    return languageTexts[savedLanguage] ? savedLanguage : "ru";
  } catch (error) {
    return "ru";
  }
}

function translate(key) {
  return languageTexts[currentLanguage][key] || languageTexts.en[key] || key;
}

function applyLanguage(language) {
  currentLanguage = languageTexts[language] ? language : "ru";
  document.documentElement.lang = currentLanguage;
  document.querySelector("#language-select").value = currentLanguage;
  try {
    localStorage.setItem(LANGUAGE_STORAGE_KEY, currentLanguage);
  } catch (error) {
    // The selected language still works when browser storage is unavailable.
  }
  document.querySelectorAll("[data-i18n]").forEach((element) => {
    element.textContent = translate(element.dataset.i18n);
  });
  document.querySelectorAll("[data-i18n-aria]").forEach((element) => {
    element.setAttribute("aria-label", translate(element.dataset.i18nAria));
  });
  document.title = translate("appTitle");
  if (lessonData) {
    renderWords(lessonData.words);
    renderDecoratorCards(lessonData.cards);
    if (activeGame) setGame(activeGame);
  }
}

async function requestJson(path, options = {}) {
  const response = await fetch(path, options);
  const result = await response.json();
  if (!response.ok) throw new Error(result.error || translate("requestFailed"));
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
    translation.textContent = displayTranslation(word);
    card.append(emoji, title, translation);
    return card;
  }));
}

function expectedTranslation(word) {
  return currentLanguage === "kk" ? word.kazakhTranslation : word.translation;
}

function displayTranslation(word) {
  if (currentLanguage === "en") return `${word.translation} · ${word.kazakhTranslation}`;
  return expectedTranslation(word);
}

function decorationName(decoration) {
  const names = { Example: "exampleName", Audio: "audioName", ImageHint: "imageName" };
  return translate(names[decoration] || decoration);
}

function renderDecoratorCards(cards) {
  const visibleDecorations = new Set([...document.querySelectorAll("[data-decoration]:checked")]
    .map((toggle) => toggle.dataset.decoration));
  cardList.replaceChildren(...cards.filter((card) => visibleDecorations.has(card.decoration)).map((card) => {
    const item = document.createElement("div");
    item.className = "decorator-card";
    const label = document.createElement("b");
    label.textContent = translate("cardDecoration")
      .replace("{word}", card.word)
      .replace("{decoration}", decorationName(card.decoration));
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
  document.querySelector("#game-title").textContent = translate(details.title);
  document.querySelector("#game-icon").textContent = details.icon;
  document.querySelector("#game-kicker").textContent = translate("miniGameSelected");
  document.querySelector("#game-instructions").textContent = translate(details.instructions);
  feedbackLine.textContent = "";
  renderQuestion();
}

function getWord(index = questionIndex) {
  return lessonData.words[index % lessonData.words.length];
}

function makeQuestion() {
  const word = getWord();
  const translationExercises = lessonData.exercises.filter((exercise) => exercise.type === "translation");
  const quizExercises = lessonData.exercises.filter((exercise) => exercise.type === "quiz");
  const translationExercise = translationExercises[questionIndex % translationExercises.length];
  const quizExercise = quizExercises[questionIndex % quizExercises.length];
  if (activeGame === "translation" && translationExercise) {
    const exerciseWord = lessonData.words.find((entry) => entry.text === translationExercise.word);
    return {
      word: translationExercise.word,
      prompt: translate("translatePrompt").replace("{word}", exerciseWord.text),
      answer: expectedTranslation(exerciseWord),
      mode: "text",
    };
  }
  if (activeGame === "quiz" && quizExercise) {
    const exerciseWord = lessonData.words.find((entry) => entry.text === quizExercise.word);
    const wordIndex = lessonData.words.indexOf(exerciseWord);
    const distractor = lessonData.words[(wordIndex + 1) % lessonData.words.length];
    const options = [
      `A) ${expectedTranslation(exerciseWord)}`,
      `B) ${expectedTranslation(distractor)}`,
    ];
    return {
      word: quizExercise.word,
      prompt: translate("quizPrompt").replace("{word}", exerciseWord.text),
      answer: quizExercise.correctAnswer,
      options,
      mode: "quiz",
    };
  }
  if (activeGame === "listen-pick") return { word: word.text, prompt: word.text, answer: expectedTranslation(word), mode: "translation-choice" };
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
    picture.setAttribute("aria-label", translate("pictureLabel").replace("{word}", question.word));
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
  button.textContent = translate("hearWord");
  button.addEventListener("click", () => speak(word));
  return button;
}

function speak(word) {
  if (!("speechSynthesis" in window)) {
    showFeedback(translate("speechUnavailable"), false);
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
  input.placeholder = translate("answerPlaceholder");
  input.setAttribute("aria-label", translate("answerLabel"));
  const button = document.createElement("button");
  button.className = "submit-button";
  button.type = "submit";
  button.textContent = translate("checkAnswer");
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
  const wordIndex = words.findIndex((word) => word.text === question.word);
  const currentWord = words[wordIndex];
  const other = words[(wordIndex + 1) % words.length];
  const correct = activeGame === "listen-pick" ? expectedTranslation(currentWord) : question.word;
  const distractor = activeGame === "listen-pick" ? expectedTranslation(other) : other.text;
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
    showFeedback(translate("correctFeedback"), true);
  } else {
    showFeedback(translate("wrongFeedback").replace("{answer}", question.answer), false);
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
      showFeedback(translate("unlocked").replace("{name}", result.unlockedAchievements.at(-1)), true);
    }
    await refreshProgress();
  } catch (error) {
    showFeedback(error.message, false);
  }
  const nextButton = document.createElement("button");
  nextButton.className = "next-button";
  nextButton.type = "button";
  nextButton.textContent = translate("nextWord");
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
    document.querySelector("#game-instructions").textContent = translate("strategyReady")
      .replace("{strategy}", result.strategy);
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
  item.textContent = translate("waitingEvents");
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
    document.querySelector("#lesson-title").textContent = translate("heroTitle");
    renderWords(lessonData.words);
    renderDecoratorCards(lessonData.cards);
    await refreshProgress();
    await refreshPatternEvents();
  } catch (error) {
    document.querySelector("#lesson-title").textContent = translate("loadingError");
    document.querySelector("#game-instructions").textContent = error.message;
  }
}

document.querySelectorAll(".game-tile").forEach((tile) => {
  tile.addEventListener("click", () => setGame(tile.dataset.game));
});
document.querySelector("#language-select").addEventListener("change", (event) => {
  applyLanguage(event.currentTarget.value);
});
document.querySelector("#strategy-select").addEventListener("change", changeStrategy);
document.querySelectorAll("[data-decoration]").forEach((toggle) => {
  toggle.addEventListener("change", () => lessonData && renderDecoratorCards(lessonData.cards));
});
window.setInterval(refreshPatternEvents, PATTERN_EVENT_POLL_INTERVAL_MS);
applyLanguage(currentLanguage);
loadLesson();
