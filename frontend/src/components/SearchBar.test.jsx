import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import SearchBar from './SearchBar';

describe('SearchBar', () => {
  const defaultProps = {
    query: '',
    onQueryChange: vi.fn(),
    suggestions: [],
    isSearching: false,
    onSearch: vi.fn(),
    onSuggestionSelect: vi.fn(),
  };

  it('renders the search input', () => {
    render(<SearchBar {...defaultProps} />);
    const input = screen.getByPlaceholderText('Pesquisar...');
    expect(input).toBeInTheDocument();
  });

  it('calls onQueryChange when typing', () => {
    const onQueryChange = vi.fn();
    render(<SearchBar {...defaultProps} onQueryChange={onQueryChange} />);
    const input = screen.getByPlaceholderText('Pesquisar...');
    fireEvent.change(input, { target: { value: 'teste' } });
    expect(onQueryChange).toHaveBeenCalledWith('teste');
  });

  it('displays suggestions when provided and input is focused', () => {
    const suggestions = [
      { text: 'Furadeira de Impacto', group: 'Nome do Equipamento' },
      { text: 'Furadeira Elétrica', group: 'Nome do Equipamento' },
    ];
    render(
      <SearchBar {...defaultProps} query="Fur" suggestions={suggestions} />
    );
    const input = screen.getByPlaceholderText('Pesquisar...');
    fireEvent.focus(input);
    expect(screen.getByRole('option', { name: /Furadeira de Impacto/ })).toBeInTheDocument();
    expect(screen.getByRole('option', { name: /Furadeira Elétrica/ })).toBeInTheDocument();
  });

  it('shows "Nenhuma sugestão encontrada" when no suggestions and not searching', () => {
    render(
      <SearchBar {...defaultProps} query="xyz" suggestions={[]} isSearching={false} />
    );
    const input = screen.getByPlaceholderText('Pesquisar...');
    fireEvent.focus(input);
    expect(screen.getByText('Nenhuma sugestão encontrada')).toBeInTheDocument();
  });

  it('shows "Buscando..." when searching with no suggestions yet', () => {
    render(
      <SearchBar {...defaultProps} query="abc" suggestions={[]} isSearching={true} />
    );
    const input = screen.getByPlaceholderText('Pesquisar...');
    fireEvent.focus(input);
    expect(screen.getByText('Buscando...')).toBeInTheDocument();
  });

  it('calls onSearch when Enter is pressed with no suggestions visible', () => {
    const onSearch = vi.fn();
    render(<SearchBar {...defaultProps} query="test" onSearch={onSearch} />);
    const input = screen.getByPlaceholderText('Pesquisar...');
    fireEvent.keyDown(input, { key: 'Enter' });
    expect(onSearch).toHaveBeenCalled();
  });

  it('calls onSuggestionSelect when a suggestion is clicked', () => {
    const onSuggestionSelect = vi.fn();
    const suggestions = [
      { text: 'Furadeira de Impacto', group: 'Equipamento' },
    ];
    render(
      <SearchBar
        {...defaultProps}
        query="Fur"
        suggestions={suggestions}
        onSuggestionSelect={onSuggestionSelect}
      />
    );
    const input = screen.getByPlaceholderText('Pesquisar...');
    fireEvent.focus(input);
    fireEvent.click(screen.getByRole('option', { name: 'Furadeira de Impacto' }));
    expect(onSuggestionSelect).toHaveBeenCalledWith('Furadeira de Impacto');
  });

  it('navigates suggestions with arrow keys', () => {
    const suggestions = [
      { text: 'Item A', group: 'Grupo 1' },
      { text: 'Item B', group: 'Grupo 2' },
    ];
    render(
      <SearchBar {...defaultProps} query="Item" suggestions={suggestions} />
    );
    const input = screen.getByPlaceholderText('Pesquisar...');
    fireEvent.focus(input);

    const options = screen.getAllByRole('option');
    expect(options[0]).toHaveAttribute('aria-selected', 'true');
    expect(options[1]).toHaveAttribute('aria-selected', 'false');

    fireEvent.keyDown(input, { key: 'ArrowDown' });
    const updatedOptions = screen.getAllByRole('option');
    expect(updatedOptions[0]).toHaveAttribute('aria-selected', 'false');
    expect(updatedOptions[1]).toHaveAttribute('aria-selected', 'true');
  });

  it('closes suggestions on Escape', () => {
    const suggestions = [
      { text: 'Test Item', group: 'Group' },
    ];
    render(
      <SearchBar {...defaultProps} query="Test" suggestions={suggestions} />
    );
    const input = screen.getByPlaceholderText('Pesquisar...');
    fireEvent.focus(input);
    expect(screen.getByRole('option', { name: 'Test Item' })).toBeInTheDocument();

    fireEvent.keyDown(input, { key: 'Escape' });
    expect(screen.queryByRole('option')).not.toBeInTheDocument();
  });
});
