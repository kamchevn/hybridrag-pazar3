import React, { useCallback, useMemo, useState } from 'react';
import './App.css';
import { askHybrid } from './api/hybridClient';
import QueryForm from './components/QueryForm';
import InteractionList from './components/InteractionList';

function generateId() {
  return `${Date.now()}-${Math.random().toString(36).slice(2, 8)}`;
}

export default function App() {
  const [query, setQuery] = useState('');
  const [selectedModel, setSelectedModel] = useState('llama3.2:3b');
  const [customModel, setCustomModel] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const [validationMessage, setValidationMessage] = useState('');
  const [history, setHistory] = useState([]);

  const effectiveModel = useMemo(() => {
    return selectedModel === '__custom__' ? (customModel || '').trim() : selectedModel;
  }, [selectedModel, customModel]);

  const handleSubmit = useCallback(async () => {
    setError(null);
    setValidationMessage('');

    const trimmedQuery = (query || '').trim();
    if (!trimmedQuery) {
      setValidationMessage('Внесете прашање.');
      return;
    }
    if (!effectiveModel) {
      setValidationMessage('Изберете или внесете модел.');
      return;
    }

    setIsLoading(true);
    try {
      const result = await askHybrid(trimmedQuery, effectiveModel);
      const newItem = {
        id: generateId(),
        timestamp: new Date().toISOString(),
        query: trimmedQuery,
        model: effectiveModel,
        answer: result.answer || '',
        urls: Array.isArray(result.urls) ? result.urls : []
      };
      setHistory((prev) => [newItem, ...prev]);
    } catch (err) {
      setError(err?.message || 'Настана непозната грешка.');
    } finally {
      setIsLoading(false);
    }
  }, [query, effectiveModel]);

  return (
    <div className="app">
      <header className="app-header">
        <h1 className="app-title">Pazar3 AI Agent</h1>
        <div className="app-subtitle">
          Прашај за огласи за недвижности и добиј релевантни линкови.
        </div>
      </header>

      <main className="app-main">
        <section className="top-panel">
          <QueryForm
            query={query}
            onQueryChange={setQuery}
            selectedModel={selectedModel}
            onModelChange={setSelectedModel}
            customModel={customModel}
            onCustomModelChange={setCustomModel}
            onSubmit={handleSubmit}
            isLoading={isLoading}
            validationMessage={validationMessage}
          />
          {error ? (
            <div className="error-box" role="alert">
              {error}
            </div>
          ) : null}
        </section>

        <section className="history-panel">
          <InteractionList history={history} />
        </section>
      </main>
    </div>
  );
}



