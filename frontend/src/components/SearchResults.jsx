import { useState } from 'react';
import { translateKey } from '../translations';
import './SearchResults.css';

function PaginatedTable({ group, highlightedTerm, itemsPerPage = 15 }) {
  const [currentPage, setCurrentPage] = useState(0);

  if (!group.items || group.items.length === 0) {
    return <p className="no-results-text">Sem resultados para esta categoria.</p>;
  }

  const totalPages = Math.ceil(group.items.length / itemsPerPage);
  const startIdx = currentPage * itemsPerPage;
  const currentItems = group.items.slice(startIdx, startIdx + itemsPerPage);

  const formatCurrency = (key, val) => {
    const keyLower = key.toLowerCase();
    if (keyLower.includes('value') || keyLower.includes('cost')) {
      return `R$ ${Number(val).toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
    }
    return String(val);
  };

  return (
    <>
      <div className="table-responsive">
        <table className="results-table">
          <thead>
            <tr>
              {Object.keys(group.items[0]).map((key) => {
                const isFinancial = key.toLowerCase().includes('value') || key.toLowerCase().includes('cost');
                const isQuantity = key.toLowerCase().includes('quantity') || key.toLowerCase().includes('qnt');
                const className = isFinancial ? 'col-financial' : (isQuantity ? 'col-quantity' : '');
                return (
                  <th key={key} className={className}>
                    {translateKey(key)}
                  </th>
                );
              })}
            </tr>
          </thead>
          <tbody>
            {currentItems.map((item, idx) => {
              const isHighlighted = highlightedTerm && Object.values(item).some(val => String(val).toLowerCase() === String(highlightedTerm).toLowerCase());
              return (
                <tr key={idx} className={isHighlighted ? 'highlight-row' : ''}>
                  {Object.entries(item).map(([key, val], i) => {
                    const keyLower = key.toLowerCase();
                    const isFinancial = keyLower.includes('value') || keyLower.includes('cost');
                    const isQuantity = keyLower.includes('quantity') || keyLower.includes('qnt');
                    const className = isFinancial ? 'col-financial' : (isQuantity ? 'col-quantity' : '');
                    return (
                      <td key={i} className={className}>
                        {formatCurrency(key, val)}
                      </td>
                    );
                  })}
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      {totalPages > 1 && (
        <div className="pagination-global-controls">
          <button
            onClick={() => setCurrentPage(prev => Math.max(0, prev - 1))}
            disabled={currentPage === 0}
            className="pagination-btn"
          >
            Anterior
          </button>
          <span className="pagination-info">
            Página {currentPage + 1} de {totalPages}
          </span>
          <button
            onClick={() => setCurrentPage(prev => Math.min(totalPages - 1, prev + 1))}
            disabled={currentPage === totalPages - 1}
            className="pagination-btn"
          >
            Próximo
          </button>
        </div>
      )}
    </>
  );
}

function SearchResults({ results, highlightedTerm }) {
  if (!results || results.length === 0) {
    return (
      <div className="no-results">
        <p>Nenhum resultado encontrado para a busca.</p>
      </div>
    );
  }

  return (
    <div className="search-results-container">
      {results.map((group, groupIdx) => (
        <div key={group.category || groupIdx} className="result-group">
          <div className="group-header">
            <h2 className="group-title">{group.category}</h2>
            <span className="group-count">
              {group.items.length} {group.items.length === 1 ? 'resultado' : 'resultados'}
            </span>
          </div>
          <PaginatedTable group={group} highlightedTerm={highlightedTerm} />
        </div>
      ))}
    </div>
  );
}

export default SearchResults;
