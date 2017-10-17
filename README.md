# CC2_Trabalho1

## Trabalho 1 da Disciplina de Construção de Compiladores 2 da UFSCar (Universidade Federal de São Carlos)

Compilador responsável por fazer as análises sintáticas e semânticas da linguagem algoritmica LA da disciplina de Construção de
Algoritmos e Programação da UFSCar, além de gerar o código correspondente em linguagem C.

Referência a Biblioteca [ANTLR] (http://www.antlr.org/).

## Apresentação

Este trabalho consiste na implementação de um compilador para a linguagem LA, capaz de realizar análise léxica, sintática, semântica da linguagem, e quando estes códigos escritos em linguagem LA estiverem corretos, realizar geração de código em linguagem C, ou exibir mensagens de erro, no caso de estar incorreto. A análise léxica e sintática deve ser feita de acordo com a especificação da gramática. Com relação à análise semântica, o compilador desenvolvido neste trabalho deve ser capaz de identificar os seguintes erros:
 * Identificador já declarado anteriormente no escopo;
 * Tipo não declarado;
 * Identificador não declarado;
 * Incompatibilidade entre argumentos e parâmetros formais (número, ordem e tipo) nas chamadas de procedimentos e funções;
 * Atribuição não compatível com o tipo declarado;
 * Uso do comando retorne em um escopo não permitido;

Para o desenvolvimento do trabalho, foi utilizado a IDE IntelliJ IDEA 2017.2.2, linguagem JAVA, e o gerador automático ANTLR (ANother Tool for Language Recognition). Como Sistema Operacional, os testes foram realizados no Windows 10.


# Instalação do ANTLR
Para instalar o ANTLR, basta fazer o download do mesmo no site: http://www.antlr.org/download.html

# Configuração do ANTLR no IntelliJ 

1 - Abra o IntelliJ IDEA.

2 - No windows: entre no menu File -> Settings -> Plugins -> Procure pelo Antlr V4 grammar plugin e instale-o.

3 - Reinicie o IntelliJ IDEA.

# Instruções de uso

Antes de rodar o comando para compilar são necessarias duas acões:

1- Da maneira que o nosso programa foi desenvolvido, só é possível rodar os testes de casos sintáticos e casos semânticos, para isso o compilador vem por padrão preparado para rodar os casos sintáticos a partir do comando java para correção sintática. Caso deseje rodar os casos de teste semânticos descomente os comandos de análise semântica e adicione como comentários os comandos para análise sintática. 

2- Abrir o projeto no IntelliJ e entrar na barra superior em Build >> Build Artifacts... >> Rebuild.

Então usar o comando: java -jar local do corretor do ava.jar "java -jar local do LAT1.jar" gcc (necessario colocar o MINGW como variavel de ambiente) local_da_pasta_temp local_dos_casos_de_teste "RA's" correcao<sintatico, semantico ou tudo>

ex: java -jar C:\Users\André\Documents\BCC\017.2\CC2\T1\CorretorTrabalho1\CorretorTrabalho1.jar "java -jar C:\Users\André\Documents\GitHub\CC2_Trabalho1\out\artifacts\LAT1_jar\LAT1.jar" gcc C:\Users\André\Documents\GitHub\CC2_Trabalho1\temp C:\Users\André\Documents\GitHub\CC2_Trabalho1\src\trabalho1\casosDeTesteT1\casosDeTesteT1 "619922" sintatico
Caso o contribuidor tenha feito modificacoes no LA.g4 usar o comando na pasta src do projeto: java -jar antlr-4.7-complete.jar -package trabalho1 LA.g4

Caso o contribuidor tenha feito modificacoes no LA.g4 usar o seguinte comando dentro da pasta src do projeto:
java -jar antlr-4.7-complete.jar -package trabalho1 LA.g4

# Alunos e Professores
Autores: 
André Levi Zanon, 619922; 

Rodrigo Ferrari de Souza, 619795; 

Victor Hugo Domingos de Abreu, 619841; 

Victor Santos Ferrari, 552437.

Professores: Helena Caseli e Daniel Lucrédio
