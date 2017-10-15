package trabalho1;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by victor on 15/10/2017.
 */
public class LAGenerator extends LABaseVisitor {

    @Override
    public Object visitPrograma(LAParser.ProgramaContext ctx) {
        //programa : declaracoes 'algoritmo' corpo 'fim_algoritmo';

        String saida = "#include <stdio.h>\n#include <stdlib.h>\n\n" +
                (String) visitDeclaracoes(ctx.declaracoes()) +
                "int main() {\n" +
                (String) visitCorpo(ctx.corpo()) +
                "return 0;\n}\n";
        return saida;
    }

    @Override
    public Object visitDeclaracoes(LAParser.DeclaracoesContext ctx) {
        //declaracoes : decl_local_global declaracoes | ;

        LAParser.DeclaracoesContext aux = ctx;
        String saida;
        while(aux.declaracoes() != null) {
            saida += (String) visitDecl_local_global(ctx.decl_local_global()) + "\n";
            aux = aux.declaracoes();
        }
        return saida;
    }

    @Override
    public Object visitDecl_local_global(LAParser.Decl_local_globalContext ctx) {
        //decl_local_global : declaracao_local | declaracao_global;

        String saida;
        if(ctx.declaracao_local() != null){
            saida = (String) visitDeclaracao_local(ctx.declaracao_local());
        } else {
            saida = (String) visitDeclaracao_global(ctx.declaracao_global());
        }
        return saida;
    }

    @Override
    public Object visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        /*declaracao_local  : 'declare' variavel
		                    |'constante' IDENT ':' tipo_basico '=' valor_constante
		                    |'tipo' IDENT ':' tipo; */

        String saida;
        if(ctx.variavel() != null){
            saida = (String) visitVariavel(ctx.variavel());
        } else if(ctx.tipo_basico() != null){
            saida = "const " + (String) visitTipo_basico(ctx.tipo_basico()) + " " + ctx.IDENT().getText() + " = " +
                    (String) visitValor_constante(ctx.valor_constante()) + ";\n";
        } else {
            saida = "typedef " + (String) visitTipo(ctx.tipo()) + ctx.IDENT().getText() + ";\n";
        }
        return saida;
    }

    @Override
    public Object visitVariavel(LAParser.VariavelContext ctx) {
        //variavel : IDENT dimensao mais_var ':' tipo;

        String saida = (String) visitTipo(ctx.tipo()) + " " + ctx.IDENT().getText();
        if((String) visitTipo(ctx.tipo()).equals("char")){
            saida += "[100]";
        } else {
            saida += (String) visitDimensao(ctx.dimensao());
        }
        saida += (String) visitMais_var(ctx.mais_var()) + ";\n";
        return saida;
    }

    @Override
    public Object visitMais_var(LAParser.Mais_varContext ctx) {
        //mais_var: (',' IDENT dimensao)* ;
    }

    @Override
    public Object visitIdentificador(LAParser.IdentificadorContext ctx) {
        /* identificador returns [String ident, int linha] :
         * ponteiros_opcionais IDENT dimensao outros_ident
         * { $ident = $ponteiros_opcionais.ponteiro + $IDENT.getText() + $outros_ident.ident; $linha = $IDENT.line; }; */
    }

    @Override
    public Object visitPonteiros_opcionais(LAParser.Ponteiros_opcionaisContext ctx) {
        /* ponteiros_opcionais returns [String ponteiro]
         * @init { $ponteiro = ""; } :
         * '^' {$ponteiro = "^";}
         * ponteiros_opcionais | ; */
    }

    @Override
    public Object visitOutros_ident(LAParser.Outros_identContext ctx) {
        /*outros_ident returns [String ident]
         * @init { $ident = ""; }:
         * '.' identificador {$ident = "." + $identificador.ident;}
         * | ; */
    }

    @Override
    public Object visitDimensao(LAParser.DimensaoContext ctx) {
        //dimensao: '[' exp_aritmetica ']' dimensao | ;
    }

    @Override
    public Object visitTipo(LAParser.TipoContext ctx) {
        //tipo: registro | tipo_estendido;
    }

    @Override
    public Object visitMais_ident(LAParser.Mais_identContext ctx) {
        //mais_ident: mais_ident :(',' identificador)*;
    }

    @Override
    public Object visitMais_variaveis(LAParser.Mais_variaveisContext ctx) {
        //mais_variaveis: mais_variaveis : (variavel)* ;
    }

    @Override
    public Object visitTipo_basico(LAParser.Tipo_basicoContext ctx) {
        //tipo_basico: 'literal'|'inteiro'|'real'|'logico';
    }

    @Override
    public Object visitTipo_basico_ident(LAParser.Tipo_basico_identContext ctx) {
        //tipo_basico_ident: tipo_basico | IDENT;
    }


    @Override
    public Object visitTipo_estendido(LAParser.Tipo_estendidoContext ctx) {
        //tipo_estendido: ponteiros_opcionais tipo_basico_ident;
    }

    @Override
    public Object visitValor_constante(LAParser.Valor_constanteContext ctx) {
        //valor_constante: CADEIA | NUM_INT | NUM_REAL| 'verdadeiro' | 'falso';
    }

    @Override
    public Object visitRegistro(LAParser.RegistroContext ctx) {
        //registro: 'registro' variavel mais_variaveis 'fim_registro';
    }

    @Override
    public Object visitDeclaracao_global(LAParser.Declaracao_globalContext ctx) {
        /* declaracao_global: 'procedimento' IDENT '(' parametros_opcional ')' declaracoes_locais comandos 'fim_procedimento'
         *                |'funcao' IDENT '(' parametros_opcional ')' ':' tipo_estendido declaracoes_locais comandos 'fim_funcao'; */
    }

    @Override
    public Object visitParametros_opcional(LAParser.Parametros_opcionalContext ctx) {
        //parametros_opcional: parametro | ;
    }

    @Override
    public Object visitParametro(LAParser.ParametroContext ctx) {
        //parametro: var_opcional identificador mais_ident ':' tipo_estendido mais_parametros;
    }

    @Override
    public Object visitVar_opcional(LAParser.Var_opcionalContext ctx) {
        //var_opcional: 'var' | ;
    }

    @Override
    public Object visitMais_parametros(LAParser.Mais_parametrosContext ctx) {
        //mais_parametros: ',' parametro | ;
    }

    @Override
    public Object visitDeclaracoes_locais(LAParser.Declaracoes_locaisContext ctx) {
        //declaracoes_locais : declaracao_local declaracoes_locais | ;
    }

    @Override
    public Object visitCorpo(LAParser.CorpoContext ctx) {
        //corpo : declaracoes_locais comandos;
    }

    @Override
    public Object visitComandos(LAParser.ComandosContext ctx) {
        //comandos : (cmd)* ;
    }

    @Override
    public Object visitCmd(LAParser.CmdContext ctx) {
        /*cmd : 'leia' '(' identificador mais_ident ')'
                | 'escreva' '(' expressao mais_expressao ')'
                | 'se' expressao 'entao' comandos senao_opcional 'fim_se'
                | 'caso' exp_aritmetica 'seja' selecao senao_opcional 'fim_caso'
                | 'para' IDENT '<-' exp1=exp_aritmetica 'ate' exp2=exp_aritmetica 'faca' comandos 'fim_para'
                | 'enquanto' expressao 'faca' comandos 'fim_enquanto'
                | 'faca' comandos 'ate' expressao
                | '^' IDENT outros_ident dimensao '<-' expressao
                | IDENT chamada_atribuicao
                | 'retorne' expressao;*/
    }

    @Override
    public Object visitMais_expressao(LAParser.Mais_expressaoContext ctx) {
        //mais_expressao : ',' expressao mais_expressao | ;
    }

    @Override
    public Object visitSenao_opcional(LAParser.Senao_opcionalContext ctx) {
        //senao_opcional : 'senao' comandos | ;
    }

    @Override
    public Object visitChamada_atribuicao(LAParser.Chamada_atribuicaoContext ctx) {
        //chamada_atribuicao: '(' argumentos_opcional ')' | outros_ident dimensao '<-' expressao;
    }

    @Override
    public Object visitArgumentos_opcional(LAParser.Argumentos_opcionalContext ctx) {
        //argumentos_opcional: expressao mais_expressao | ;
    }

    @Override
    public Object visitSelecao(LAParser.SelecaoContext ctx) {
        //selecao: constantes ':' comandos mais_selecao;
    }

    @Override
    public Object visitMais_selecao(LAParser.Mais_selecaoContext ctx) {
        //mais_selecao: selecao | ;
    }

    @Override
    public Object visitConstantes(LAParser.ConstantesContext ctx) {
        //constantes: numero_intervalo mais_constantes;
    }

    @Override
    public Object visitMais_constantes(LAParser.Mais_constantesContext ctx) {
        // mais_constantes: ',' constantes | ;
    }

    @Override
    public Object visitNumero_intervalo(LAParser.Numero_intervaloContext ctx) {
        // numero_intervalo: op_unario NUM_INT intervalo_opcional;
    }

    @Override
    public Object visitIntervalo_opcional(LAParser.Intervalo_opcionalContext ctx) {
        // intervalo_opcional: '..' op_unario NUM_INT | ;
    }

    @Override
    public Object visitOp_unario(LAParser.Op_unarioContext ctx) {
        //op_unario: '-' | ;
    }

    @Override
    public Object visitExp_aritmetica(LAParser.Exp_aritmeticaContext ctx) {
        //exp_aritmetica: termo outros_termos;
    }

    @Override
    public Object visitOp_multiplicacao(LAParser.Op_multiplicacaoContext ctx) {
        //op_multiplicacao: '*'| '/';
    }

    @Override
    public Object visitOp_adicao(LAParser.Op_adicaoContext ctx) {
        //op_adicao : '+' | '-';
    }

    @Override
    public Object visitTermo(LAParser.TermoContext ctx) {
        //termo : fator outros_fatores;
    }

    @Override
    public Object visitOutros_termos(LAParser.Outros_termosContext ctx) {
        //outros_termos : op_adicao termo outros_termos | ;
    }

    @Override
    public Object visitFator(LAParser.FatorContext ctx) {
        //fator : parcela outras_parcelas;
    }

    @Override
    public Object visitOutros_fatores(LAParser.Outros_fatoresContext ctx){
        //outros_fatores : op_multiplicacao fator outros_fatores | ;
    }

    @Override
    public Object visitParcela(LAParser.ParcelaContext ctx) {
        //parcela : op_unario parcela_unario | parcela_nao_unario;
    }

    @Override
    public Object visitParcela_unario(LAParser.Parcela_unarioContext ctx) {
        /*parcela_unario returns [String valor]
         *   @init { $valor = ""; }:
         *        '^' IDENT outros_ident {$valor = $IDENT.getText() + $outros_ident.ident;} dimensao
         *        | IDENT chamada_partes {$valor = $IDENT.getText() + $chamada_partes.valor;}
         *        | NUM_INT {$valor = $NUM_INT.getText();}
         *        | NUM_REAL {$valor = $NUM_REAL.getText();}
         *        | '(' expressao ')';  */
    }

    @Override
    public Object visitParcela_nao_unario(LAParser.Parcela_nao_unarioContext ctx) {
        //parcela_nao_unario : '&' IDENT outros_ident dimensao | CADEIA;
    }

    @Override
    public Object visitOutras_parcelas(LAParser.Outras_parcelasContext ctx) {
        //outras_parcelas : '%' parcela outras_parcelas | ;
    }

    @Override
    public Object visitChamada_partes(LAParser.Chamada_partesContext ctx) {
        // chamada_partes :  '(' expressao mais_expressao ')'  | outros_ident dimensao | ;
    }

    @Override
    public Object visitExp_relacional(LAParser.Exp_relacionalContext ctx) {
        //exp_relacional : exp_aritmetica op_opcional;
    }

    @Override
    public Object visitOp_opcional(LAParser.Op_opcionalContext ctx) {
        //op_opcional : op_relacional exp_aritmetica | ;
    }

    @Override
    public Object visitOp_relacional(LAParser.Op_relacionalContext ctx) {
        //op_relacional : '=' | '<>'| '>=' | '<=' | '>' | '<';
    }

    @Override
    public Object visitExpressao(LAParser.ExpressaoContext ctx) {
        //expressao : termo_logico outros_termos_logicos;
    }

    @Override
    public Object visitOp_nao(LAParser.Op_naoContext ctx) {
        //op_nao : 'nao' | ;
    }

    @Override
    public Object visitTermo_logico(LAParser.Termo_logicoContext ctx) {
        //termo_logico : fator_logico outros_fatores_logicos;
    }

    @Override
    public Object visitOutros_termos_logicos(LAParser.Outros_termos_logicosContext ctx) {
        // outros_termos_logicos : 'ou' termo_logico outros_termos_logicos | ;
    }

    @Override
    public Object visitOutros_fatores_logicos(LAParser.Outros_fatores_logicosContext ctx) {
        // outros_fatores_logicos : 'e' fator_logico outros_fatores_logicos | ;
    }

    @Override
    public Object visitFator_logico(LAParser.Fator_logicoContext ctx) {
        //fator_logico : op_nao parcela_logica;
    }

    @Override
    public Object visitParcela_logica(LAParser.Parcela_logicaContext ctx) {
        //parcela_logica : 'verdadeiro' | 'falso' | exp_relacional;
    }