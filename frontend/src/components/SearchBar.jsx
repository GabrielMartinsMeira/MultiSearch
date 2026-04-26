import React, { useState, useEffect, useRef } from 'react';
import './SearchBar.css';

function SearchBar({ query, onQueryChange, suggestions = [], isSearching, onSearch, onSuggestionSelect }) {
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [activeSuggestionIndex, setActiveSuggestionIndex] = useState(0);
  const containerRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (containerRef.current && !containerRef.current.contains(event.target)) {
        setShowSuggestions(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleInputChange = (e) => {
    setShowSuggestions(true);
    setActiveSuggestionIndex(0);
    onQueryChange(e.target.value);
  };

  const handleSuggestionClick = (sug) => {
    setShowSuggestions(false);
    if (onSuggestionSelect) {
      onSuggestionSelect(sug);
    } else {
      onQueryChange(sug);
    }
  };

  const handleKeyDown = (e) => {
    if (!showSuggestions || suggestions.length === 0) {
      if (e.key === 'Enter') {
        setShowSuggestions(false);
        if (onSearch) onSearch();
      }
      return;
    }

    if (e.key === 'ArrowDown') {
      e.preventDefault();
      setActiveSuggestionIndex((prev) => 
        prev < suggestions.length - 1 ? prev + 1 : prev
      );
    } else if (e.key === 'ArrowUp') {
      e.preventDefault();
      setActiveSuggestionIndex((prev) => (prev > 0 ? prev - 1 : 0));
    } else if (e.key === 'Enter') {
      e.preventDefault();
      if (suggestions[activeSuggestionIndex]) {
        handleSuggestionClick(suggestions[activeSuggestionIndex].text);
      } else {
        setShowSuggestions(false);
        if (onSearch) onSearch();
      }
    } else if (e.key === 'Escape') {
      setShowSuggestions(false);
    }
  };

  return (
    <div className="search-bar-container" ref={containerRef}>
      <div className="search-input-wrapper">
        <svg className="search-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <circle cx="11" cy="11" r="8"></circle>
          <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
        </svg>
        <input
          type="text"
          className="search-input"
          placeholder="Pesquisar..."
          value={query}
          onChange={handleInputChange}
          onKeyDown={handleKeyDown}
          onFocus={() => setShowSuggestions(true)}
        />
        {showSuggestions && query.trim() !== '' && (
          <ul className="suggestions-dropdown">
            {suggestions.length > 0 ? (
              suggestions.map((sugObj, idx) => (
                <li
                  key={idx}
                  className={`suggestion-item ${idx === activeSuggestionIndex ? 'active' : ''}`}
                  onClick={() => handleSuggestionClick(sugObj.text)}
                >
                  {(() => {
                    const parts = sugObj.text.split(new RegExp(`(${query})`, 'gi'));
                    return parts.map((part, i) => 
                      part.toLowerCase() === query.toLowerCase() 
                        ? <strong key={i}>{part}</strong> 
                        : part
                    );
                  })()}
                  <span className="suggestion-group">({sugObj.group})</span>
                </li>
              ))
            ) : !isSearching ? (
              <li className="suggestion-item no-suggestions">
                Nenhuma sugestão encontrada
              </li>
            ) : (
              <li className="suggestion-item no-suggestions">
                Buscando...
              </li>
            )}
          </ul>
        )}
      </div>
    </div>
  );
}

export default SearchBar;
