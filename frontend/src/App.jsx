import { useState, useEffect } from 'react';
import SearchBar from './components/SearchBar';
import SearchResults from './components/SearchResults';
import { translateKey } from './translations';
import './App.css';
// import data from '../data/data.json'; 

function App() {
  const [query, setQuery] = useState('');
  const [isDarkMode, setIsDarkMode] = useState(() => {
    const saved = localStorage.getItem('darkMode');
    return saved ? JSON.parse(saved) : false;
  });

  useEffect(() => {
    localStorage.setItem('darkMode', JSON.stringify(isDarkMode));
    if (isDarkMode) {
      document.body.classList.add('dark-mode');
    } else {
      document.body.classList.remove('dark-mode');
    }
  }, [isDarkMode]);

  /* 
  const categoryNames = {
    sales_orders: 'Pedidos de Venda',
    purchase_orders: 'Pedidos de Compra',
    equipments: 'Equipamentos',
    materials: 'Materiais',
    workforce: 'Mão de Obra'
  };

  const resultsAndSuggestions = useMemo(() => {
    if (!query.trim()) return { results: null, suggestions: [] };
    const lowerQuery = query.toLowerCase();
    const matchedCategories = [];
    const suggestionsMap = new Map();

    for (const [key, items] of Object.entries(data)) {
      const matchedItems = items.filter(item => {
        let isMatch = false;
        for (const [propName, val] of Object.entries(item)) {
          const stringValue = String(val);
          if (stringValue.toLowerCase().includes(lowerQuery)) {
            isMatch = true;
            if (!suggestionsMap.has(stringValue)) {
              suggestionsMap.set(stringValue, { text: stringValue, group: translateKey(propName) });
            }
          }
        }
        return isMatch;
      });
      if (matchedItems.length > 0) {
        matchedCategories.push({ category: categoryNames[key] || key, items: matchedItems });
      }
    }
    return { results: matchedCategories, suggestions: Array.from(suggestionsMap.values()).slice(0, 6) };
  }, [query]);
  */

  const [results, setResults] = useState(null);
  const [suggestions, setSuggestions] = useState([]);
  const [isSearching, setIsSearching] = useState(false);
  const [backendError, setBackendError] = useState(null);

  const handleSearch = (termToSearch) => {
    const activeTerm = termToSearch !== undefined ? termToSearch : query;
    setIsSearching(true);
    setBackendError(null);

    const endpoint = activeTerm.trim()
      ? `http://localhost:8080/api/search?query=${encodeURIComponent(activeTerm)}`
      : `http://localhost:8080/api/all`;

    fetch(endpoint)
      .then(res => {
        if (!res.ok) throw new Error('Falha na resposta do servidor');
        return res.json();
      })
      .then(data => {
        setResults(data);
        setIsSearching(false);
      })
      .catch(err => {
        console.error("Erro ao buscar:", err);
        setBackendError("Não foi possível conectar ao servidor. Verifique se o backend está rodando.");
        setIsSearching(false);
      });
  };

  useEffect(() => {
    if (!query.trim()) {
      setSuggestions([]);
      return;
    }

    setIsSearching(true);
    const abortController = new AbortController();
    const timer = setTimeout(() => {
      fetch(`http://localhost:8080/api/search?query=${encodeURIComponent(query)}`, { signal: abortController.signal })
        .then(res => res.json())
        .then(data => {
          setBackendError(null);

          if (data && Array.isArray(data)) {
            const suggestionsMap = new Map();
            const lowerQuery = query.toLowerCase();

            for (const group of data) {
              if (!group.items) continue;
              for (const item of group.items) {
                for (const [propName, val] of Object.entries(item)) {
                  const stringValue = String(val);

                  if (stringValue.toLowerCase().startsWith(lowerQuery)) {
                    suggestionsMap.set(stringValue.toLowerCase(), { text: stringValue, group: translateKey(propName) });
                    if (suggestionsMap.size >= 6) break;
                  }
                }
                if (suggestionsMap.size >= 6) break;
              }
              if (suggestionsMap.size >= 6) break;
            }
            setSuggestions(Array.from(suggestionsMap.values()));
          }
          setIsSearching(false);
        })
        .catch(err => {
          if (err.name === 'AbortError') return;
          console.error("Erro ao buscar no backend:", err);
          setBackendError("Não foi possível conectar ao servidor.");
          setIsSearching(false);
        });
    }, 50);

    return () => {
      clearTimeout(timer);
      abortController.abort();
    };
  }, [query]);

  const handleSuggestionSelect = (term) => {
    setQuery(term);
    handleSearch(term);
  };

  return (
    <div className="app-container">
      <header className="header-top">
        <img src={isDarkMode ? "/logo_dark.png" : "/logo.png"} alt="MultiSearch Logo" className="logo-small" />
        <div className="search-wrapper">
          <SearchBar
            query={query}
            onQueryChange={setQuery}
            suggestions={suggestions}
            isSearching={isSearching}
            onSearch={() => handleSearch()}
            onSuggestionSelect={handleSuggestionSelect}
          />
        </div>
        <div className="theme-toggle">
          <svg className="dark-icon" xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"></path>
          </svg>
          <span className="theme-text">Dark Mode</span>
          <label className="switch">
            <input type="checkbox" checked={isDarkMode} onChange={() => setIsDarkMode(!isDarkMode)} />
            <span className="slider round"></span>
          </label>
        </div>
      </header>

      <main className="main-content">
        {backendError && (
          <div className="backend-error-banner">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <circle cx="12" cy="12" r="10"></circle>
              <line x1="12" y1="8" x2="12" y2="12"></line>
              <line x1="12" y1="16" x2="12.01" y2="16"></line>
            </svg>
            <span>{backendError}</span>
          </div>
        )}

        {results !== null && Array.isArray(results) && (
          <div className="results-container">
            <div className="total-results-info">
              {results.reduce((acc, cat) => acc + (cat.items?.length || 0), 0)} resultados no total
            </div>
            <SearchResults results={results} highlightedTerm={query} />
          </div>
        )}
      </main>
    </div>
  );
}

export default App;
