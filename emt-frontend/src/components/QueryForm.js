import PropTypes from 'prop-types';
import React from 'react';

const MODEL_PRESETS = [
  { value: 'llama3.2:3b', label: 'llama3.2:3b' },
  { value: 'qwen2.5:3b-instruct', label: 'qwen2.5:3b-instruct' },
  { value: 'gemma:2b-instruct', label: 'gemma:2b-instruct' },
  { value: '__custom__', label: 'Custom (друго)' }
];

export default function QueryForm({
  query,
  onQueryChange,
  selectedModel,
  onModelChange,
  customModel,
  onCustomModelChange,
  onSubmit,
  isLoading,
  validationMessage
}) {
  const isCustom = selectedModel === '__custom__';

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit();
  };

  return (
    <form className="query-form" onSubmit={handleSubmit}>
      <label className="field-label" htmlFor="query">
        Внесете прашање на македонски
      </label>
      <textarea
        id="query"
        className="query-textarea"
        placeholder="Пр. Дај ми детали од огласи за стан за изнајмување во Аеродром"
        value={query}
        onChange={(e) => onQueryChange(e.target.value)}
        rows={4}
        disabled={isLoading}
      />

      <div className="model-row">
        <div className="model-select-block">
          <label className="field-label" htmlFor="model">
            Модел
          </label>
          <select
            id="model"
            className="model-select"
            value={selectedModel}
            onChange={(e) => onModelChange(e.target.value)}
            disabled={isLoading}
          >
            {MODEL_PRESETS.map((m) => (
              <option key={m.value} value={m.value}>
                {m.label}
              </option>
            ))}
          </select>
        </div>

        {isCustom && (
          <div className="custom-model-block">
            <label className="field-label" htmlFor="customModel">
              Внесете име на модел
            </label>
            <input
              id="customModel"
              className="custom-model-input"
              type="text"
              placeholder="пример: my-model:latest"
              value={customModel}
              onChange={(e) => onCustomModelChange(e.target.value)}
              disabled={isLoading}
            />
          </div>
        )}
      </div>

      {validationMessage ? (
        <div className="validation-message" role="alert">
          {validationMessage}
        </div>
      ) : null}

      <div className="actions-row">
        <button
          type="submit"
          className="primary-button"
          disabled={isLoading}
          aria-busy={isLoading}
        >
          Прашај агент
        </button>
        {isLoading && <span className="loading">Вчитување…</span>}
      </div>
    </form>
  );
}

QueryForm.propTypes = {
  query: PropTypes.string.isRequired,
  onQueryChange: PropTypes.func.isRequired,
  selectedModel: PropTypes.string.isRequired,
  onModelChange: PropTypes.func.isRequired,
  customModel: PropTypes.string.isRequired,
  onCustomModelChange: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
  isLoading: PropTypes.bool.isRequired,
  validationMessage: PropTypes.string
};



