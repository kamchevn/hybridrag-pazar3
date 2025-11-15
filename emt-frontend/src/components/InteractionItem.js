import PropTypes from 'prop-types';
import React from 'react';

export default function InteractionItem({ item, index }) {
  const hasUrls = Array.isArray(item.urls) && item.urls.length > 0;
  return (
    <div className="interaction-item">
      <div className="interaction-meta">
        <span className="meta-badge">#{index + 1}</span>
        <span className="meta-time">
          {new Date(item.timestamp).toLocaleString()}
        </span>
        <span className="meta-model">Модел: {item.model}</span>
      </div>

      <div className="interaction-block">
        <div className="interaction-label">Прашање</div>
        <div className="interaction-content">{item.query}</div>
      </div>

      <div className="interaction-block">
        <div className="interaction-label">Одговор</div>
        <pre className="interaction-answer">{item.answer || '—'}</pre>
      </div>

      <div className="interaction-block">
        <div className="interaction-label">Релевантни огласи</div>
        {hasUrls ? (
          <ul className="url-list">
            {item.urls.map((u, i) => (
              <li key={u + i} className="url-item">
                <a href={u} target="_blank" rel="noopener noreferrer" title={u}>
                  Listing {i + 1}
                </a>
                <div className="url-raw" aria-hidden>
                  {u}
                </div>
              </li>
            ))}
          </ul>
        ) : (
          <div className="no-urls">Нема најдени линкови од огласи.</div>
        )}
      </div>
    </div>
  );
}

InteractionItem.propTypes = {
  item: PropTypes.shape({
    id: PropTypes.string.isRequired,
    timestamp: PropTypes.string.isRequired,
    query: PropTypes.string.isRequired,
    model: PropTypes.string.isRequired,
    answer: PropTypes.string,
    urls: PropTypes.arrayOf(PropTypes.string)
  }).isRequired,
  index: PropTypes.number.isRequired
};



