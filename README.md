# MultiSearch - Sistema de Busca Centralizada (ERP)

O **MultiSearch** é uma solução Full Stack desenvolvida para centralizar a busca de informações em sistemas ERP. Ele permite que o usuário pesquise em diversas tabelas simultaneamente (Pedidos de Venda, Materiais, Equipamentos e Mão de Obra) e visualize os resultados de forma organizada e intuitiva. Atualmente, o projeto está configurado com dados de uma **Indústria de Ferramentas**.

<img width="1902" height="906" alt="Main screen MultiSearch" src="https://github.com/user-attachments/assets/939b58d4-33c2-4b5d-b2b8-480d69288605" />

## 🛠️ Tecnologias Utilizadas

### Frontend
- **React.js**: Framework principal para construção da interface reativa.
- **Vite**: Ferramenta de build e servidor de desenvolvimento ultra-rápido.
- **Vanilla CSS3**: Estilização personalizada com variáveis CSS para suporte a temas.
- **LocalStorage**: Persistência da preferência de Dark Mode.
- **Vitest & React Testing Library**: Suíte de testes unitários e de integração do frontend.
- **ESLint**: Garantia de padrões e qualidade do código Javascript.

### Backend
- **Java 17+ / Spring Boot 3**: Framework para construção da API REST.
- **Maven**: Gerenciador de dependências e automação de build.
- **Jackson**: Processamento e parseamento dinâmico de arquivos JSON.
- **JUnit 5, Mockito & Hamcrest**: Infraestrutura robusta para testes unitários e de integração.
- **Spring DevTools**: Reinicialização automática durante o desenvolvimento.
- **Maven Surefire Report**: Geração de relatórios visuais de qualidade dos testes.

---

## 🔌 Endpoints da API (Backend)

Você pode testar a API diretamente pelo navegador ou via ferramentas como Postman nos seguintes endereços:

- **Busca Filtrada**: `GET http://localhost:8080/api/search?query=termo`
  - *Retorna os objetos que contêm o termo pesquisado em qualquer campo.*
- **Listagem Completa**: `GET http://localhost:8080/api/all`
  - *Retorna todos os dados de todos os arquivos JSON integrados.*
- **Redirecionamento**: `GET http://localhost:8080/`
  - *Redireciona automaticamente para o endpoint de listagem completa.*

**Exemplo de Resposta (JSON)**:
```json
[
  {
    "category": "Pedidos de Venda",
    "items": [
      { "SalesOrderID": 90001, "Customer": "Construtora Silva", "TotalValue": 5000.0 }
    ]
  },
  {
    "category": "Equipamentos",
    "items": []
  }
]
```

---

## 📋 Pré-requisitos

Antes de começar, certifique-se de ter instalado em sua máquina:
- **Java 17+**
- **Node.js 18+** (e `npm`)
- **Maven** (opcional, o projeto já inclui o wrapper `./mvnw.cmd`)

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

## 🧪 Testes do Backend

Os testes utilizam **JUnit 5**, **MockMvc** e **Mockito**, organizados em 3 classes com responsabilidades distintas seguindo a **Test Pyramid**.

#### Executar os testes:
```powershell
cd backend
./mvnw.cmd test
```

#### 📊 Relatório Visual de Testes:
O projeto está configurado para gerar um relatório HTML detalhado. Para gerar e abrir:
```powershell
./mvnw.cmd test surefire-report:report
# O relatório será gerado em: backend/target/site/surefire-report.html
```

---

### 🏗️ Arquitetura dos Testes

| Classe | Tipo | Estratégia |
|---|---|---|
| `SearchControllerTest` | **Unit Test** | `@WebMvcTest` + `@MockBean DataService` — testa o controller HTTP em isolamento real |
| `RootControllerTest` | **Unit Test** | `@WebMvcTest` + sem dependências — testa o redirecionamento raiz |
| `DataServiceTest` | **Integration Test** | JUnit puro + dados de teste reais em `testdata/` — testa a lógica de negócio |

> **Por que separar?** `@WebMvcTest` sobe apenas a camada web, sem contexto Spring completo — é mais rápido e testa o controller em verdadeiro isolamento. `DataServiceTest` usa dados reais de `testdata/` para validar a lógica de parsing e filtragem sem contaminar os dados de produção.

---

### 📋 SearchControllerTest — Controller HTTP em Isolamento

O `DataService` é **mockado com Mockito** (`@MockBean`), permitindo testar o controller independentemente da lógica de dados.

| Teste | Descrição |
|---|---|
| `allEndpoint_returns200WithJsonAndItems` | `GET /api/all` → HTTP 200, Content-Type JSON, 5 categorias com dados reais |
| `allEndpoint_returnsAllCategoryNames` | Verifica os 5 nomes de categoria na resposta |
| `allEndpoint_callsGetAllDataOnce` | Confirma que o controller chama `getAllData()` exatamente 1x e nunca `search()` |
| `allEndpoint_whenServiceThrows_returns500` | Serviço lança exceção → controller retorna HTTP 500 |
| `searchEndpoint_withQuery_returnsSpecificResults` | `GET /api/search?query=Construtora` → HTTP 200, validando se a resposta traz corretamente dados reais mockados (`SalesOrderID`, `Customer` etc). |
| `searchEndpoint_delegatesQueryToService` | Confirma que o termo da query é repassado corretamente ao service via `Mockito.verify` |
| `searchEndpoint_withEmptyQuery_returns200` | Query vazia → HTTP 200 sem erros e retorna o conjunto padrão de dados |
| `searchEndpoint_withoutQueryParam_returns200` | Sem parâmetro `query` → HTTP 200 usando valor padrão vazio |
| `searchEndpoint_withAccentedChars_preservesEncoding` | Acentos (`Mão`) → Valida que o encoding UTF-8 é preservado e repassado intocado ao backend |
| `searchEndpoint_withWhitespaceOnlyQuery_returns200` | Teste de limite: query apenas com espaços (`   `) |
| `searchEndpoint_withSpecialCharsQuery_returns200` | Teste de limite: sanitização de strings XSS (`<script>`) comportando-se como texto literal |
| `searchEndpoint_withVeryLongQuery_doesNotCrash` | Teste de estresse: query longa (1000 chars) não causa erro interno (HTTP 500) |
| `searchEndpoint_whenServiceThrows_returns500` | Serviço lança exceção → controller retorna HTTP 500 corretamente |
| `searchEndpoint_postMethod_isRejected` | Garante que requisições HTTP POST são rejeitadas com erro de cliente (4xx) |

---

### 📋 RootControllerTest — Redirecionamento Raiz

| Teste | Descrição |
|---|---|
| `rootEndpoint_redirectsToApiAll` | `GET /` → redireciona com **HTTP 302 Found** para `/api/all` |
| `rootEndpoint_postMethod_isRejected` | Garante que requisições POST na raiz são rejeitadas (4xx) |

---

### 📋 DataServiceTest — Lógica de Negócio com Dados Isolados (`@Tag("integration")`)

Dados de teste localizados em `backend/src/test/resources/testdata/` — nenhum arquivo de produção é tocado.

| Teste | Descrição |
|---|---|
| `search_withEmptyQuery_returnsAllCategories` | Query vazia → retorna todas as 5 categorias |
| `search_withNullQuery_returnsAllCategories` | Query nula → comportamento igual à query vazia |
| `search_withExactCustomerMatch_returnsCorrectResults` | `"Construtora Silva"` → encontrado em Pedidos de Venda |
| `search_withPartialMatch_returnsFilteredResults` | `"Furadeira"` → correspondência exata em Pedidos de Venda e Equipamentos |
| `search_withNoMatch_returnsEmptyItems` | Termo inexistente → valida se retornou as 5 categorias, todas vazias |
| `search_isCaseInsensitive` | `"CONSTRUTORA"` → valida contagens idênticas **por categoria** entre Upper/Lower |
| `getAllData_returnsSameAsEmptySearch` | `getAllData()` equivale a `search("")` validando categoria por categoria |
| `search_withAccentedCharacters_works` | `"Turno"` → encontrado em `"Mão de Obra"` (UTF-8 correto) |
| `search_withNumericQuery_matchesIds` | `"90001"` → encontrado como ID numérico em Pedidos de Venda |
| `search_invalidDataDir_doesNotThrow` | `search()` com diretório inválido → fallback seguro sem erro |
| `getAllData_invalidDataDir_doesNotThrow` | `getAllData()` com diretório inválido → fallback seguro sem erro |
| `search_categoriesHaveCorrectNames` | Validação estrita: exatamente as 5 categorias traduzidas (sem extras) |

---

## 📦 Estrutura de Arquivos

- `backend/data/`: Contém os arquivos JSON originais de integração.
- `backend/src/main/java/.../multisearch/`:
  - `controller/`: Endpoints REST (`SearchController`, `RootController`).
  - `service/`: Lógica de negócio, leitura e filtro de dados (`DataService`).
  - `dto/`: Modelos de transferência de dados (`SearchResultDTO`, etc).
  - `exception/`: Tratamento global de erros (`GlobalExceptionHandler`).
- `backend/src/test/`: Cobertura de testes unitários e de integração.
- `frontend/src/`: Código fonte da aplicação React (componentes, validações, CSS).
- `frontend/data/`: Dados processados para fallback ou uso local.

---

## 🤝 Contribuição e Licença

Este é um projeto de demonstração técnica e portfólio. Sinta-se livre para clonar, explorar a arquitetura e modificar o código para seus próprios estudos.

Distribuído sob a licença **MIT**.
