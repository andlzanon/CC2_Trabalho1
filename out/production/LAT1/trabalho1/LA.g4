grammar LA;

@members{
    String grupo = "<619922_619795_619841_552437>";

    PilhaDeTabelas pilhaDeTabelas = new PilhaDeTabelas();
    private final int ERRO_COMENT = 11;
    private final int ERRO_TOKEN = 10;
}

programa :
            { pilhaDeTabelas.empilhar(new TabelaDeSimbolos("global")); }
            declaracoes 'algoritmo' corpo 'fim_algoritmo'
            { pilhaDeTabelas.desempilhar(); };

declaracoes : decl_local_global declaracoes |  ;

decl_local_global : declaracao_local | declaracao_global;

declaracao_local :
                 'declare' variavel
                 | 'constante' IDENT ':' tipo_basico '=' valor_constante
                 | 'tipo' IDENT ':' tipo;


// variavel necessita ter nome linha e coluna
variavel returns [String nome, int linha, String tipo_var]:
            IDENT dimensao mais_var ':'
            tipo { $nome  = $IDENT.getText(); $linha = $IDENT.line; $tipo_var = $tipo.tipo_var; } ;



mais_var returns [ List<String> nomes ]
@init { $nomes = new ArrayList<String>(); } :
            ',' IDENT
            dimensao mais_var
            { $nomes.add($IDENT.getText());} |  ;


identificador : ponteiros_opcionais IDENT dimensao outros_ident;

ponteiros_opcionais returns [String ponteiro]
@init { $ponteiro = ""; } :
            '^' {$ponteiro += "^";}
            ponteiros_opcionais | ;

outros_ident : '.' identificador | ;

dimensao : '[' exp_aritmetica ']' dimensao | ;

tipo returns [String tipo_var] :
            registro {$tipo_var = $registro.tipoRegistro;} |
            tipo_estendido {$tipo_var = $tipo_estendido.tipoCompleto;};

mais_ident : ',' identificador mais_ident | ;

mais_variaveis : variavel mais_variaveis | ;


tipo_basico returns [String tipo_var]
            : 'literal' {$tipo_var = "literal";}
            | 'inteiro' {$tipo_var = "inteiro";}
            | 'real'    {$tipo_var = "real";}
            | 'logico'  {$tipo_var = "logico";} ;


tipo_basico_ident returns [String tipo_var] :
        tipo_basico { $tipo_var = $tipo_basico.tipo_var; } |
        IDENT { if(!pilhaDeTabelas.existeTipoVar($IDENT.getText()))
                    Mensagens.tipoNaoDeclarado($IDENT.line, $IDENT.getText()); } ;


tipo_estendido returns [String tipoCompleto]
            : ponteiros_opcionais tipo_basico_ident
            {$tipoCompleto = $ponteiros_opcionais.ponteiro + $tipo_basico_ident.tipo_var;};


valor_constante : CADEIA | NUM_INT | NUM_REAL | 'verdadeiro' | 'falso';

registro returns [String tipoRegistro] :
            'registro' {$tipoRegistro = "registro";}
            variavel mais_variaveis
            'fim_registro';

declaracao_global :'procedimento' IDENT '(' parametros_opcional ')' declaracoes_locais comandos 'fim_procedimento'
| 'funcao' IDENT '(' parametros_opcional ')' ':' tipo_estendido declaracoes_locais comandos 'fim_funcao';

parametros_opcional : parametro | ;

parametro : var_opcional identificador mais_ident ':' tipo_estendido mais_parametros;

var_opcional : 'var' | ;

mais_parametros : ',' parametro | ;

declaracoes_locais : declaracao_local declaracoes_locais | ;

corpo : declaracoes_locais comandos;

comandos : cmd comandos | ;

//parte mais baixa da arvore para a declaracao de variaveis
//adiciona na pilha de tabelas aqui
cmd : 'leia' '(' identificador mais_ident ')'
| 'escreva' '(' expressao mais_expressao ')'
| 'se' expressao 'entao' comandos senao_opcional 'fim_se'
| 'caso' exp_aritmetica 'seja' selecao senao_opcional 'fim_caso'
| 'para' IDENT '<-' exp_aritmetica 'ate' exp_aritmetica 'faca' comandos 'fim_para'
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

outros_termos : op_adicao termo outros_termos | ;

fator : parcela outras_parcelas;

outros_fatores : op_multiplicacao fator outros_fatores | ;

parcela : op_unario parcela_unario | parcela_nao_unario;

parcela_unario : '^' IDENT outros_ident dimensao | IDENT chamada_partes | NUM_INT | NUM_REAL | '(' expressao ')';

parcela_nao_unario : '&' IDENT outros_ident dimensao | CADEIA;

outras_parcelas : '%' parcela outras_parcelas | ;

chamada_partes : '(' expressao mais_expressao ')' | outros_ident dimensao | ;

exp_relacional : exp_aritmetica op_opcional;

op_opcional : op_relacional exp_aritmetica | ;

op_relacional : '=' | '<>' | '>=' | '<=' | '>' | '<';

expressao : termo_logico outros_termos_logicos;

op_nao : 'nao' | ;

termo_logico : fator_logico outros_fatores_logicos;

outros_termos_logicos : 'ou' termo_logico outros_termos_logicos | ;

outros_fatores_logicos : 'e' fator_logico outros_fatores_logicos | ;

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