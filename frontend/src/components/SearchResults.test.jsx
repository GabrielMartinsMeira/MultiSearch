import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import SearchResults from './SearchResults';

const mockResults = [
  {
    category: 'Pedidos de Venda',
    items: [
      {
        SalesOrderID: 50001,
        MaterialID: 'PF-02',
        TotalValue: 4200,
        Customer: 'Construtora Silva',
        Quantity: 6,
        DeliveryDate: '2026-06-02',
        MaterialName: 'Parafusadeira a Bateria 20V',
      },
      {
        SalesOrderID: 50002,
        MaterialID: 'FT-01',
        TotalValue: 3150,
        Customer: 'Reformas Express',
        Quantity: 5,
        DeliveryDate: '2026-06-03',
        MaterialName: 'Furadeira de Impacto Profissional',
      },
    ],
  },
  {
    category: 'Equipamentos',
    items: [
      {
        EquipmentName: 'Furadeira de Impacto Profissional',
        EquipmentID: 'FT-01',
      },
    ],
  },
];

describe('SearchResults', () => {
  it('renders "Nenhum resultado" message when results are empty', () => {
    render(<SearchResults results={[]} highlightedTerm="" />);
    expect(screen.getByText('Nenhum resultado encontrado para a busca.')).toBeInTheDocument();
  });

  it('renders category headers with correct names', () => {
    render(<SearchResults results={mockResults} highlightedTerm="" />);
    expect(screen.getByText('Pedidos de Venda')).toBeInTheDocument();
    expect(screen.getByText('Equipamentos')).toBeInTheDocument();
  });

  it('renders item count for each category', () => {
    render(<SearchResults results={mockResults} highlightedTerm="" />);
    expect(screen.getByText('2 resultados')).toBeInTheDocument();
    expect(screen.getByText('1 resultado')).toBeInTheDocument();
  });

  it('renders table data correctly', () => {
    render(<SearchResults results={mockResults} highlightedTerm="" />);
    expect(screen.getByText('Construtora Silva')).toBeInTheDocument();
    expect(screen.getByText('Reformas Express')).toBeInTheDocument();
    expect(screen.getAllByText('Furadeira de Impacto Profissional').length).toBeGreaterThanOrEqual(1);
  });

  it('formats currency values correctly', () => {
    render(<SearchResults results={mockResults} highlightedTerm="" />);
    const currencyElements = screen.getAllByText(/R\$/);
    expect(currencyElements.length).toBeGreaterThan(0);
  });

  it('highlights row when highlightedTerm matches a cell value', () => {
    const { container } = render(
      <SearchResults results={mockResults} highlightedTerm="Construtora Silva" />
    );
    const highlightedRows = container.querySelectorAll('.highlight-row');
    expect(highlightedRows.length).toBeGreaterThan(0);
  });

  it('does not show pagination when items fit in one page', () => {
    render(<SearchResults results={mockResults} highlightedTerm="" />);
    expect(screen.queryByText('Próximo')).not.toBeInTheDocument();
  });

  it('shows pagination when items exceed page size', () => {
    const manyItems = Array.from({ length: 20 }, (_, i) => ({
      SalesOrderID: 50000 + i,
      Customer: `Cliente ${i}`,
      TotalValue: 1000 + i,
    }));
    const bigResults = [{ category: 'Muitos Itens', items: manyItems }];

    render(<SearchResults results={bigResults} highlightedTerm="" />);
    expect(screen.getByText('Próximo')).toBeInTheDocument();
    expect(screen.getByText('Anterior')).toBeInTheDocument();
    expect(screen.getByText('Página 1 de 2')).toBeInTheDocument();
  });

  it('navigates pages correctly', () => {
    const manyItems = Array.from({ length: 20 }, (_, i) => ({
      SalesOrderID: 50000 + i,
      Customer: `Cliente ${i}`,
      TotalValue: 1000 + i,
    }));
    const bigResults = [{ category: 'Muitos Itens', items: manyItems }];

    render(<SearchResults results={bigResults} highlightedTerm="" />);

    expect(screen.getByText('Página 1 de 2')).toBeInTheDocument();
    expect(screen.getByText('Cliente 0')).toBeInTheDocument();

    fireEvent.click(screen.getByText('Próximo'));
    expect(screen.getByText('Página 2 de 2')).toBeInTheDocument();
    expect(screen.getByText('Cliente 15')).toBeInTheDocument();

    fireEvent.click(screen.getByText('Anterior'));
    expect(screen.getByText('Página 1 de 2')).toBeInTheDocument();
  });

  it('disables Anterior button on first page', () => {
    const manyItems = Array.from({ length: 20 }, (_, i) => ({
      SalesOrderID: 50000 + i,
      Customer: `Cliente ${i}`,
    }));
    const bigResults = [{ category: 'Test', items: manyItems }];

    render(<SearchResults results={bigResults} highlightedTerm="" />);
    expect(screen.getByText('Anterior')).toBeDisabled();
  });

  it('renders translated column headers', () => {
    render(<SearchResults results={mockResults} highlightedTerm="" />);
    expect(screen.getByText('Cliente')).toBeInTheDocument();
  });
});
