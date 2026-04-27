# MultiSearch - Sistema de Busca Centralizada (ERP)

O **MultiSearch** é uma solução Full Stack desenvolvida para centralizar a busca de informações em sistemas ERP. Ele permite que o usuário pesquise em diversas tabelas simultaneamente (Pedidos de Venda, Materiais, Equipamentos e Mão de Obra) e visualize os resultados de forma organizada e intuitiva. Atualmente, o projeto está configurado com dados de uma **Indústria de Ferramentas**.

## 🛠️ Tecnologias Utilizadas

### Frontend
- **React.js**: Framework principal para construção da interface reativa.
- **Vite**: Ferramenta de build e servidor de desenvolvimento ultra-rápido.
- **Vanilla CSS3**: Estilização personalizada com variáveis CSS para suporte a temas.
- **LocalStorage**: Utilizado para persistir a preferência de Dark Mode do usuário.

### Backend
- **Java 17+ / Spring Boot 3**: Framework para construção da API REST.
- **Maven**: Gerenciador de dependências e automação de build.
- **Jackson**: Processamento e parseamento dinâmico de arquivos JSON.
- **Spring DevTools**: Reinicialização automática durante o desenvolvimento.

---

## 🔌 Endpoints da API (Backend)

Você pode testar a API diretamente pelo navegador ou via ferramentas como Postman nos seguintes endereços:

- **Busca Filtrada**: `GET http://localhost:8080/api/search?query=termo`
  - *Retorna os objetos que contêm o termo pesquisado em qualquer campo.*
- **Listagem Completa**: `GET http://localhost:8080/api/all`
  - *Retorna todos os dados de todos os arquivos JSON integrados.*
- **Redirecionamento**: `GET http://localhost:8080/`
  - *Redireciona automaticamente para o endpoint de listagem completa.*

---

## 🚀 Como Executar o Projeto

#### Backend:
```powershell
cd backend
./mvnw.cmd spring-boot:run
```
O servidor iniciará em: `http://localhost:8080`

#### Frontend:
```powershell
cd frontend
npm install
npm run dev
```
O frontend iniciará em: `http://localhost:5173`

---

## 📦 Estrutura de Arquivos

- `backend/data`: Contém os arquivos JSON originais (Equipamentos, Materiais, Pedidos, Mão de Obra).
- `backend/src`: Código fonte da API Spring Boot (Java).
- `frontend/src`: Código fonte da aplicação React (Vite).
- `frontend/data`: Dados processados e consolidados para uso no frontend.
- `backend/target`: Pasta de build do Backend.
