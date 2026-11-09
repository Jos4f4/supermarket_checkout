# 🛒 Supermarket Checkout (Mercadinho São Francisco)

[![Java Version](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![UI](https://img.shields.io/badge/GUI-Java%20Swing-blue.svg)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

> **Engenharia de Software I** — MVP de um sistema de Frente de Caixa (POS) Desktop simulando o ambiente computacional offline dos anos 90 para o **Mercadinho São Francisco**.

---

## 📌 Sobre o Projeto

O **`supermarket_checkout`** é um Produto Mínimo Viável (MVP) de uma aplicação desktop em **Java Swing**, desenvolvido para resolver os problemas operacionais do Mercadinho São Francisco (Coroado, Manaus). 

O sistema opera de forma **100% offline**, registrando vendas com múltiplos itens, suportando o cancelamento de produtos antes do fechamento, gerenciando troco em pagamentos em dinheiro, controlando fiado por limite por cliente cadastrado e gerando o relatório de fechamento diário categorizado por forma de pagamento.

---

## 🚀 Funcionalidades Principais

* 📦 **Registro de Itens:** Adição rápida de produtos via código com cálculo de subtotal automático.
* ❌ **Cancelamento de Itens:** Remoção de itens do carrinho antes da finalização da compra com recalculo instantâneo do total.
* 💵 **Pagamento em Dinheiro & Troco:** Validação do valor recebido e cálculo exato do troco.
* 💳 **Pagamento via Cartão:** Conclusão direta de vendas por cartão de crédito/débito.
* 📝 **Venda no Fiado com Limite:** Liberação exclusiva para clientes cadastrados (via CPF) com validação de limite de crédito.
* 📊 **Fechamento do Dia:** Consolidação dos valores totais acumulados no dia divididos por forma de pagamento (Dinheiro, Cartão e Fiado).
* 🛡️ **Precisão Financeira:** Armazenamento interno de valores monetários em **centavos** (`long`), prevenindo erros de arredondamento por ponto flutuante.

---

## 🏗️ Estrutura do Projeto

O código foi desenvolvido seguindo os princípios de separação de responsabilidades (MVC) com convenções de nomenclatura em inglês no código-fonte e interface visual adaptada em Português:

```text
supermarket_checkout/
├── src/
│   └── com/
│       └── mercadinho/
│           └── checkout/
│               ├── model/
│               │   ├── CartItem.java       # Representa um item adicionado ao carrinho
│               │   ├── Customer.java       # Representa o cliente cadastrado (Fiado)
│               │   └── Product.java        # Representa o produto do catálogo
│               └── view/
│                   └── CheckoutFrame.java  # Tela principal da interface gráfica (Swing)
├── .gitignore                              # Regras de exclusão Git (Eclipse/Builds)
└── README.md                               # Documentação do projeto
```

---

## 🧪 Roteiro de Testes de Aceitação (Formulário F-03)

O sistema conta com 6 cenários de teste principais para homologação ao vivo:

| ID | Cenário de Teste | Entradas / Passos | Resultado Esperado |
| :---: | :--- | :--- | :--- |
| **CT-01** | **Cálculo de Troco** | Item `78910` (R$ 25,00) + Pagamento Dinheiro `R$ 30,00` | Venda finalizada com troco de **R$ 5,00** |
| **CT-02** | **Item Cancelado** | Itens `78910` e `78911` $\rightarrow$ Selecionar `78911` $\rightarrow$ *Cancelar Item* | Item removido, total atualizado para **R$ 25,00** |
| **CT-03** | **Fiado Autorizado** | Total R$ 25,00 $\rightarrow$ Forma: `FIADO` $\rightarrow$ CPF `123456` | Venda autorizada. Limite reduz de R$ 500,00 para **R$ 475,00** |
| **CT-04** | **Fiado Bloqueado** | 25x Item `78910` (R$ 625,00) $\rightarrow$ Forma: `FIADO` $\rightarrow$ CPF `123456` | Alerta de erro: *"Venda excede o limite fiado do cliente"* |
| **CT-05** | **Pagamento Cartão** | Item `78911` (R$ 8,50) $\rightarrow$ Forma: `CARTAO` | Venda concluída e somada no total de cartões |
| **CT-06** | **Fechamento do Dia** | Clique no botão *Ver Fechamento do Dia* | Relatório consolidado com soma por forma de pagamento |

---

## 🛠️ Como Executar

### Pré-requisitos
* **Java Development Kit (JDK):** Versão 17 ou superior (testado na JDK 21).
* **IDE:** Eclipse IDE, IntelliJ IDEA ou VS Code.

### Passos
1. Clone o repositório:
   ```bash
   git clone https://github.com/Jos4f4/supermarket_checkout.git
   ```
2. Abra o seu **Eclipse IDE**.
3. Acesse **File > Import... > Existing Projects into Workspace**.
4. Selecione a pasta clonada do repositório `supermarket_checkout`.
5. Abra o arquivo `CheckoutFrame.java` (em `src/com/mercadinho/checkout/view/`).
6. Clique com o botão direito $\rightarrow$ **Run As > Java Application**.

---

## 👥 Equipe do Trabalho

Trabalho desenvolvido para a disciplina de **Engenharia de Software I**.

* Josafá e Grupo de Engenharia de Software.

---
*Mercadinho São Francisco — Do problema ao programa em uma semana.* 🛒