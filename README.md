# CC2_Trabalho1

## Trabalho 1 da Disciplina de Construção de Compiladores 2 da UFSCar (Universidade Federal de São Carlos)

Compilador responsável por fazer as análises sintáticas e semânticas da linguagem algoritmica LA da disciplina de Construção de
Algoritmos e Programação da UFSCar, além de gerar o código correspondente em linguagem C.

Referência a Biblioteca [ANTLR](http://www.antlr.org/)

# Instruções de uso

Antes de rodar o comando para compilar são necessarias duas acões:
1- Modificar no arquivo CorretorTrabalho1\CorretorTrabalho1.java o comando File saidaCasoTeste = new File(local_dos_casos_de_teste) para o seu local;

2- No IntelliJ entrar na barra superior em Build >> Build Artifacts... >> Build.


Então usar o comando:
java -jar <local do corretor do ava>.jar "java -jar <local do LAT1.jar>" gcc<necessario colocar o MINGW como variavel de ambiente> local_da_pasta_temp local_dos_casos_de_teste "RA's" correcao<sintatico, semantico ou tudo>


ex: java -jar C:\Users\André\Documents\BCC\017.2\CC2\T1\CorretorTrabalho1\CorretorTrabalho1.jar "java -jar C:\Users\André\Documents\GitHub\CC2_Trabalho1\out\artifacts\LAT1_jar\LAT1.jar" gcc C:\Users\André\Documents\GitHub\CC2_Trabalho1\temp C:\Users\André\Documents\GitHub\CC2_Trabalho1\src\trabalho1\casosDeTesteT1\casosDeTesteT1 "619922" sintatico



Caso o contribuidor tenha feito modificacoes no LA.g4 usar o comando na pasta src do projeto:
java -jar antlr-4.7-complete.jar -package trabalho1 LA.g4

# Alunos e Professores
Autores: 
André Levi Zanon, 619922; 

Rodrigo Ferrari de Souza, 619795; 

Victor Hugo Domingos de Abreu, 619841; 

Victor Santos Ferrari, 552437.

Professores: Helena Caseli e Daniel Lucrédio
