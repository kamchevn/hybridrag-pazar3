import PropTypes from 'prop-types';
import React from 'react';
import InteractionItem from './InteractionItem';

export default function InteractionList({ history }) {
  if (!Array.isArray(history) || history.length === 0) {
    return (
      <div className="empty-history">
        Историјата е празна. Поставете прашање погоре.
      </div>
    );
  }

  return (
    <div className="interaction-list">
      {history.map((item, idx) => (
        <InteractionItem key={item.id} item={item} index={idx} />
      ))}
    </div>
  );
}

InteractionList.propTypes = {
  history: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.string.isRequired
    })
  ).isRequired
};



