# CatalogoApp

Aplicacao web em Spring Boot para gerenciamento de produtos, categorias, usuarios e auditoria de estoque.

O projeto foi desenvolvido para praticar uma arquitetura MVC com persistencia em banco relacional, controle de acesso por perfil e regras de negocio aplicadas na camada de servico.

## Tecnologias

- Java 17
- Spring Boot 3.4.2
- Spring MVC
- Spring Data JPA
- Spring Security
- Thymeleaf
- PostgreSQL
- H2 Database, disponivel apenas como alternativa local
- Bootstrap 5
- Maven Wrapper

## Funcionalidades

- Login com Spring Security.
- Usuario administrador inicial criado automaticamente.
- Cadastro de usuarios com perfil `ADMIN` ou `USER`.
- Cadastro de categorias.
- Cadastro, listagem, edicao e exclusao de produtos.
- Busca de produtos por nome.
- Filtro por categoria.
- Controle de quantidade em estoque.
- Validacao para impedir quantidade negativa.
- Mensagem de sucesso ao cadastrar ou editar produto, com horario da alteracao.
- Tela de auditoria para administradores.
- Ordenacao da auditoria pela data de atualizacao mais recente.
- Destaque visual para produtos com estoque baixo, menor que 5.
- Bloqueio de rotas administrativas para usuarios comuns.

## Perfis de Acesso

### Administrador

O administrador pode:

- Cadastrar, editar e excluir produtos.
- Cadastrar categorias.
- Cadastrar usuarios.
- Acessar a auditoria de produtos.

Login inicial:

```text
usuario: admin
senha: admin123
```

### Usuario comum

O usuario comum pode:

- Consultar a lista de produtos.
- Usar busca e filtros.

Ele nao pode:

- Cadastrar produtos.
- Editar produtos.
- Excluir produtos.
- Acessar a auditoria.
- Acessar rotas administrativas diretamente pela URL.

## Banco de Dados

O projeto esta configurado para PostgreSQL:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/catalogo_db
spring.datasource.username=postgres
spring.datasource.password=123456
```

Principais tabelas:

- `tb_usuario`
- `tb_categoria`
- `tb_produto`

Campos importantes em `tb_produto`:

- `id_produto`
- `nome`
- `valor`
- `quantidade`
- `data_atualizacao`
- `id_categoria_fk`

## Executando com Docker

Se estiver usando Docker, suba o PostgreSQL com:

```powershell
docker start catalogo-postgres
```

Se o container ainda nao existir, crie com:

```powershell
docker run --name catalogo-postgres `
  -e POSTGRES_DB=catalogo_db `
  -e POSTGRES_USER=postgres `
  -e POSTGRES_PASSWORD=123456 `
  -p 5432:5432 `
  -v catalogo-postgres-data:/var/lib/postgresql/data `
  -d postgres:16
```

Para verificar se o banco esta pronto:

```powershell
docker exec catalogo-postgres pg_isready -U postgres -d catalogo_db
```

## Executando a Aplicacao

Entre na pasta do projeto:

```powershell
cd "C:\Users\Thiago\Desktop\catalogo-master\catalogo-master"
```

Execute com Maven Wrapper:

```powershell
.\mvnw.cmd spring-boot:run
```

Se estiver usando o JDK portatil criado localmente:

```powershell
$env:JAVA_HOME="C:\Users\Thiago\Documents\New project\tools\jdk17\jdk-17.0.19+10"
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
.\mvnw.cmd spring-boot:run
```

Acesse:

```text
http://localhost:8080/produtos
```

## Como Ver os Dados no Banco

Entrar no PostgreSQL pelo container:

```powershell
docker exec -it catalogo-postgres psql -U postgres -d catalogo_db
```

Listar tabelas:

```sql
\dt
```

Consultar produtos:

```sql
select
  p.id_produto,
  p.nome,
  p.quantidade,
  p.valor,
  c.nome as categoria,
  p.data_atualizacao
from tb_produto p
join tb_categoria c on c.id_categoria = p.id_categoria_fk;
```

Sair do `psql`:

```sql
\q
```

## Fluxo da Aplicacao

1. O usuario acessa `/produtos`.
2. A listagem pode ser vista publicamente.
3. O login e feito em `/login`.
4. Apos login como administrador, as opcoes administrativas aparecem.
5. O formulario de produto envia os dados para `/produtos/salvar`.
6. O `ProdutoController` recebe o `ProdutoModel`.
7. O `ProdutoService` aplica as regras de negocio.
8. Se a quantidade for negativa, o produto nao e salvo e o erro volta ao formulario.
9. Se os dados forem validos, o produto recebe `data_atualizacao` e e salvo pelo `ProdutoRepository`.
10. O usuario e redirecionado para `/produtos` com mensagem de sucesso.
11. A auditoria em `/produtos/auditoria` lista os produtos por ultima atualizacao.

## Estrutura do Projeto

```text
src/main/java/br/com/fatec/catalogo
  config
  controllers
  models
  repositories
  security
  services

src/main/resources
  templates
  application.properties
```

Responsabilidades:

- `models`: entidades JPA.
- `repositories`: acesso ao banco com Spring Data JPA.
- `services`: regras de negocio.
- `controllers`: rotas e fluxo entre tela e service.
- `security`: configuracao de login, logout e permissoes.
- `templates`: paginas Thymeleaf.

## Requisitos da Atividade

Status dos itens pedidos:

- Atributo `quantidade` em produto: OK
- Validacao contra quantidade negativa no `ProdutoService`: OK
- Mensagem de erro no formulario: OK
- Painel de auditoria exclusivo para administrador: OK
- Produtos ordenados por data de atualizacao: OK
- Destaque para estoque baixo menor que 5: OK
- Mensagem de sucesso com horario: OK
- Botoes administrativos ocultos para usuario comum: OK
- Bloqueio de acesso direto as rotas administrativas: OK
- Dados persistidos no PostgreSQL: OK

## Testes

Rodar testes automatizados:

```powershell
.\mvnw.cmd test
```

Resultado esperado:

```text
BUILD SUCCESS
```

## Observacoes

- O sistema cria automaticamente o usuario `admin / admin123` quando nao ha usuarios cadastrados.
- O PostgreSQL precisa estar rodando antes da aplicacao.
- A aplicacao usa `spring.jpa.hibernate.ddl-auto=update`, portanto o Hibernate cria/atualiza as tabelas conforme as entidades.
