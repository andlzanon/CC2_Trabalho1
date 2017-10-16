grammar LA;

@members{
    String grupo = "<619922_619795_619841_552437>";
    private final int ERRO_COMENT = 11;
    private final int ERRO_TOKEN = 10;
}

programa :
            declaracoes 'algoritmo' corpo 'fim_algoritmo'
            ;

declaracoes : decl_local_global declaracoes |  ;

decl_local_global : declaracao_local | declaracao_global;

declaracao_local :
                 'declare' variavel
                 | 'constante' IDENT ':' tipo_basico '=' valor_constante
                 | 'tipo' IDENT ':' tipo;

variavel : IDENT dimensao mais_var ':' tipo ;

mais_var: (',' IDENT dimensao)* ;


identificador returns [String ident, int linha] :
            ponteiros_opcionais IDENT dimensao outros_ident
            { $ident = $ponteiros_opcionais.ponteiro + $IDENT.getText() + $outros_ident.ident; $linha = $IDENT.line; };

ponteiros_opcionais returns [String ponteiro]
@init { $ponteiro = ""; } :
            '^' {$ponteiro = "^";}
            ponteiros_opcionais | ;

outros_ident returns [String ident]
@init { $ident = ""; }:
    '.' identificador {$ident = "." + $identificador.ident;}
     | ;

dimensao : '[' exp_aritmetica ']' dimensao
            | ;

tipo :  registro |
        tipo_estendido ;

mais_ident :
    (',' identificador)*;

mais_variaveis : (variavel)* ;


tipo_basico: 'literal'
            | 'inteiro'
            | 'real'
            | 'logico'   ;


tipo_basico_ident :
        tipo_basico  |
        IDENT
        ;


tipo_estendido : ponteiros_opcionais tipo_basico_ident
                ;


valor_constante : CADEIA | NUM_INT | NUM_REAL | 'verdadeiro' | 'falso';

registro :  'registro'
            variavel mais_variaveis
            'fim_registro';

declaracao_global :'procedimento' identProc=IDENT '(' parametros_opcional ')' declaracoes_locais comandos 'fim_procedimento'
| 'funcao' identFunc=IDENT '(' parametros_opcional ')' ':' tipo_estendido declaracoes_locais comandos 'fim_funcao';

parametros_opcional : parametro | ;

parametro : var_opcional identificador mais_ident ':' tipo_estendido mais_parametros;

var_opcional : 'var' | ;

mais_parametros : ',' parametro | ;

declaracoes_locais : declaracao_local declaracoes_locais | ;

corpo : declaracoes_locais comandos;

comandos : (cmd)* ;

cmd : 'leia' '(' identificador mais_ident')'
| 'escreva' '(' expressao mais_expressao ')'
| 'se' expressao 'entao' comandos senao_opcional 'fim_se'
| 'caso' exp_aritmetica 'seja' selecao senao_opcional 'fim_caso'
| 'para' IDENT '<-' exp1=exp_aritmetica 'ate' exp2=exp_aritmetica 'faca' comandos 'fim_para'
| 'enquanto' expressao 'faca' comandos 'fim_enquanto'
| 'faca' comandos 'ate' expressao
| '^' IDENT outros_ident dimensao '<-' expressao
| IDENT chamada_atribuicao
| 'retorne' expressao;

mais_expressao : ',' expressao mais_expressao | ;

senao_opcional : 'senao' comandos | ;

chamada_atribuicao : '(' argumentos_opcional ')' | outros_ident dimensao '<-' expressao;

argumentos_opcional : expressao mais_expressao | ;

selecao : constantes ':' comandos mais_selecao;

mais_selecao : selecao | ;

constantes : numero_intervalo mais_constantes;

mais_constantes : ',' constantes | ;

numero_intervalo : op_unario NUM_INT intervalo_opcional;

intervalo_opcional : '..' op_unario NUM_INT | ;

op_unario : '-' | ;

exp_aritmetica : termo outros_termos;

op_multiplicacao : '*' | '/';

op_adicao : '+' | '-';

termo : fator outros_fatores;

outros_termos : (op_adicao termo)*;

fator : parcela outras_parcelas;

outros_fatores: (op_multiplicacao fator)*;

parcela : op_unario parcela_unario | parcela_nao_unario;

parcela_unario returns [String valor]
@init { $valor = ""; }:
                 '^' IDENT outros_ident {$valor = $IDENT.getText() + $outros_ident.ident;} dimensao
                 | IDENT chamada_partes {$valor = $IDENT.getText() + $chamada_partes.valor;}
                 | NUM_INT {$valor = $NUM_INT.getText();}
                 | NUM_REAL {$valor = $NUM_REAL.getText();}
                 | '(' expressao ')';

parcela_nao_unario returns [String valor]
@init { $valor = ""; }:
                    '&' IDENT outros_ident dimensao {$valor = $IDENT.getText() + $outros_ident.ident;}
                     | CADEIA;

outras_parcelas : ('%' parcela)*;

chamada_partes returns [String valor]
 @init { $valor = ""; }:
 '(' expressao mais_expressao ')' | outros_ident dimensao {$valor = $outros_ident.ident;} | ;

exp_relacional : exp_aritmetica op_opcional;

op_opcional : op_relacional exp_aritmetica | ;

op_relacional : '=' | '<>' | '>=' | '<=' | '>' | '<';

expressao : termo_logico outros_termos_logicos;

op_nao : 'nao' | ;

termo_logico : fator_logico outros_fatores_logicos;

outros_termos_logicos : ('ou' termo_logico)*;

outros_fatores_logicos : ('e' fator_logico)*;

fator_logico : op_nao parcela_logica;

parcela_logica : 'verdadeiro' | 'falso' | exp_relacional;

/*Tokens: */
IDENT      : ('a'..'z' | 'A'..'Z' | '_') ('a'..'z' | 'A'..'Z' | '0'..'9' | '_')*;

NUM_INT    : ('+' | '-')? ('0'..'9')('0'..'9')*;

NUM_REAL   : ('+' | '-')? ('0'..'9')('0'..'9')* '.' ('0'..'9')+;

CADEIA     : '"' ~('\n' | '\r' | '\'' | '\t')* '\'' | '"' ~('\n' | '\r' | '"' | '\t')* '"';

COMENTARIO : '{' ~('\n'|'}')* '}' {skip();};

WS	       :	(' ' | '\t' | '\r' | '\n') {skip();};

/* Tokens errados: para tratarmos os mesmos foram setados tipos especificos(setType(int)) para cada um
 * assim para os tokens nao declarados foi setado o inteiro ERRO_TOKEN que possui por valor 10 e para os
 * os comentarios incompletos foi declarado ERRO_COMENT com o valor 11. Ambos os valores foram escolhidos
 * de forma parcial
 */
NAO_DECL   : ('@'| '|' | '!' | '"') {setType(ERRO_TOKEN);};

COMENTARIO_INCOMPLETO : '{' ~('\n'|'}')*'\n' {setType(ERRO_COMENT);};