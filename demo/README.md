# Guião de Demonstração


## 1. Preparação do sistema

Para testar o sistema e todos os seus componentes, é necessário preparar um ambiente com dados para proceder à verificação dos testes.

### 1.1. Lançar o *registry*

Para lançar o *ZooKeeper*, ir à pasta `zookeeper/bin` e correr o comando  
`./zkServer.sh start` (Linux) ou `zkServer.cmd` (Windows).

É possível também lançar a consola de interação com o *ZooKeeper*, novamente na pasta `zookeeper/bin` e correr `./zkCli.sh` (Linux) ou `zkCli.cmd` (Windows).

### 1.2. Compilar o projeto

Primeiramente, é necessário compilar e instalar todos os módulos e suas dependências --  *rec*, *hub*, *app*, etc.
Para isso, basta ir à pasta *root* do projeto e correr o seguinte comando:

```sh
$ mvn clean install -DskipTests
```
**Nota:** Para poder correr o script *app* ou *rec* ou *hubP diretamente é necessário fazer `mvn install` e adicionar ao *PATH* ou utilizar diretamente os executáveis gerados na pasta `target/appassembler/bin/`.

### 1.3. Lançar e testar o *rec*

Para proceder aos testes, é preciso em primeiro lugar lançar o servidor *rec* .
Para isso basta ir à pasta *rec* e executar:

```sh
$ rec localhost 2181 localhost 8091 1
```
Para uma 2ª réplica:

```sh
$ rec localhost 2181 localhost 8092 2
```
(e por aí adiante...)

Este comando vai colocar o *rec* no endereço *localhost* e na porta *8091*.

Para confirmar o funcionamento do servidor com um *ping*, fazer:

```sh
$ cd rec-tester
$ mvn compile exec:java
```

Para executar toda a bateria de testes de integração, fazer:

```sh
$ mvn verify
```

Todos os testes devem ser executados sem erros.


### 1.4. Lançar o *hub*

Ir à pasta *hub* e executar:

```sh
$ localhost 2181 localhost 8081 1 ../../../../data/users.csv ../../../../data/stations.csv initRec
```


### 1.5. *App*

Iniciar a aplicação com a utilizadora alice:

```sh
$ app localhost 2181 alice +35191102030 38.7380 -9.3000
```


Abrir outra consola, e iniciar a aplicação com o utilizador bruno.

Depois de lançar todos os componentes, tal como descrito acima, já temos o que é necessário para usar o sistema através dos comandos.

## 2. Teste dos comandos

Nesta secção vamos correr os comandos necessários para testar todas as operações do sistema.
Cada subsecção é respetiva a cada operação presente no *hub*.

### 2.1. *balance*

```sh
> balance
```

### 2.2 *top-up*

```sh
> top-up 100
> top-up 50
```


### 2.3 *info*

```sh
> info istt
```

### 2.4 *locate-station*

```sh
> scan 3
> tag 38.7376 -9.3031 loc1
> move loc1
> scan 5
```

### 2.5 *bike-up*

```sh
> info istt
> bike-up istt
```


### 2.6 *bike-down*

```sh
> move 38.6867 -9.3117
> bike-down istt
> bike-down stao
```

### 2.7 *ping*

```sh
> ping
```

### 2.8 *sys-status*

```sh
> sys-status
```

----

