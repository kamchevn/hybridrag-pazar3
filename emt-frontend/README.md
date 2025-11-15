# Pazar3 AI Agent (React + CRA)

Едноставна React апликација (Create React App, JavaScript) која комуницира со backend за пребарување Pazar3 огласи преку AI агент.

## Технологии
- React 18 (Create React App, без Vite)
- JavaScript (без TypeScript)
- Fetch API за HTTP барања

## Структура
```
.
├─ public/
│  └─ index.html
├─ src/
│  ├─ api/
│  │  └─ hybridClient.js
│  ├─ components/
│  │  ├─ InteractionItem.js
│  │  ├─ InteractionList.js
│  │  └─ QueryForm.js
│  ├─ App.css
│  ├─ App.js
│  └─ index.js
└─ package.json
```

## Backend очекувања
Апликацијата очекува backend на `http://localhost:8080` (освен ако не е сменето преку env) со следниот endpoint:

```
GET /api/hybrid/ask?q=<QUERY>&model=<MODEL_NAME>
```

Пример:
```
GET http://localhost:8080/api/hybrid/ask?q=Дај ми детали од огласи за стан за изнајмување во Аеродром&model=llama3.2:3b
```

JSON одговорот треба да содржи најмалку:
- `answer`: string (LLM одговор на македонски)
- `listings`: низа објекти, секој со поле `url` (string)

Фронтендот ги прикажува само:
- `answer`
- сите `listings[i].url` вредности (филтрирани и дедуплицирани)

## Конфигурација (ENV)
Може да поставите специфична backend база преку `.env`:

```
REACT_APP_API_BASE_URL=http://localhost:8080
```

Ако ова не е поставено, апликацијата ќе користи `http://localhost:8080` по дифолт.

## Инсталација и старт
1) Инсталирај dependences:
```
npm install
```

2) (Опционално) Креирај `.env` и постави `REACT_APP_API_BASE_URL` како погоре.

3) Стартувај дев сервер:
```
npm start
```

4) Отвори во прелистувач: `http://localhost:3000`

## Користење
- Внесете прашање на македонски во text area (пример: „Дај ми детали од огласи за стан за изнајмување во Аеродром“)
- Изберете модел (`llama3.2:3b`, `qwen2.5:3b-instruct`, или `Custom`)
- Ако изберете `Custom`, внесете име на модел
- Кликнете „Прашај агент“
- Ќе се прикажат:
  - Одговорот (`answer`)
  - Листа од линкови (`listings[i].url`), ако се присутни

## Напомени
- Додека се вчитува, копчето и полињата се оневозможени.
- Историјата останува зачувана локално во состојба (state) додека трае сесијата.
- Погрешки (HTTP/мрежа/JSON) се прикажуваат во видливи error-box пораки.



