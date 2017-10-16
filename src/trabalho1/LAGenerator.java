package trabalho1;

import com.sun.org.apache.xpath.internal.operations.String;

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
        String saida = "";
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
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitMais_var(LAParser.Mais_varContext ctx) {
        //mais_var: (',' IDENT dimensao)* ;

        String saida;
        LAParser.Mais_varContext atual = ctx
        while(atual.IDENT() != null){
            saida = ", " + (String) atual.IDENT() + (String) visitDimensao(ctx.dimensao());
            atual = atual.IDENT();
        }
        return saida;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitIdentificador(LAParser.IdentificadorContext ctx) {
        /* identificador returns [String ident, int linha] :
         * ponteiros_opcionais IDENT dimensao outros_ident
         * { $ident = $ponteiros_opcionais.ponteiro + $IDENT.getText() + $outros_ident.ident; $linha = $IDENT.line; }; */

        String linha = (String) visitOutros_ident(ctx.outros_ident());

        return ctx.IDENT().getText() + linha;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitPonteiros_opcionais(LAParser.Ponteiros_opcionaisContext ctx) {
        /* ponteiros_opcionais returns [String ponteiro]
         * @init { $ponteiro = ""; } :
         * '^' {$ponteiro = "^";}
         * ponteiros_opcionais | ; */

        String saida = "";

        while(ctx.ponteiros_opcionais() != null){
            saida += "*";
            ctx = ctx.ponteiros_opcionais();
        }

        return saida;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitOutros_ident(LAParser.Outros_identContext ctx) {
        /*outros_ident returns [String ident]
         * @init { $ident = ""; }:
         * '.' identificador {$ident = "." + $identificador.ident;}
         * | ; */

        if(ctx.identificador() != null){
            return "" + "." + (String) visitIdentificador(ctx.identificador());
        }
        return "";
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitDimensao(LAParser.DimensaoContext ctx) {
        //dimensao: '[' exp_aritmetica ']' dimensao | ;

        if(ctx.exp_aritmetica() != null){
            for (LAParser.Exp_aritmeticaContext exp : ctx.exp_aritmetica()) {
                return "" + "[" + visitExp_aritmetica(exp) + "]";
            }
        }
        return "";
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitTipo(LAParser.TipoContext ctx) {
        //tipo: registro | tipo_estendido;

        if(ctx.registro() != null){
            return (String) visitRegistro(ctx.registro());
        } else if(ctx.tipo_estendido() != null){
            return (String) visitTipo_estendido(ctx.tipo_estendido());
        }
        return "";
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitMais_ident(LAParser.Mais_identContext ctx) {
        //mais_ident: (',' identificador)*;

        String saida;
        LAParser.Mais_identContext atual = ctx;
        while(atual.identificador() != null){
            saida = ", " + (String) visitIdentificador(ctx.identificador());
            atual = atual.identificador();
        }
        return saida;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitMais_variaveis(LAParser.Mais_variaveisContext ctx) {
        //mais_variaveis: (variavel)* ;

        String saida;
        LAParser.Mais_variaveisContext atual = ctx;
        while(atual.variavel() != null){
            saida += (String) visitVariavel(ctx.variavel());
            atual = atual.variavel();
        }
        return saida;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitTipo_basico(LAParser.Tipo_basicoContext ctx) {
        //tipo_basico: 'literal'|'inteiro'|'real'|'logico';

        if(ctx != null){
            switch (ctx.getStart().getText()){
                case "INTEIRO":
                    return "int";
                case "REAL":
                    return "float";
                case "LITERAL":
                    return "char";
                case "BOOLEAN":
                    return "boolean";
            }
        }
        return "";
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitTipo_basico_ident(LAParser.Tipo_basico_identContext ctx) {
        //tipo_basico_ident: tipo_basico | IDENT;

        if(ctx.tipo_basico() != null){
            return (String) visitTipo_basico(ctx.tipo_basico());
        } else{
            return ctx.IDENT().getText();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitTipo_estendido(LAParser.Tipo_estendidoContext ctx) {
        //tipo_estendido: ponteiros_opcionais tipo_basico_ident;

        return "" + visitTipo_basico_ident(ctx.tipo_basico_ident()) + visitPonteiros_opcionais(ctx.ponteiros_opcionais());
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitValor_constante(LAParser.Valor_constanteContext ctx) {
        //valor_constante: CADEIA | NUM_INT | NUM_REAL| 'verdadeiro' | 'falso';

        return ctx.getText();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitRegistro(LAParser.RegistroContext ctx) {
        //registro: 'registro' variavel mais_variaveis 'fim_registro';

        String saida;
        saida = "struct {\n";
        LAParser.Mais_variaveisContext aux = ctx.mais_variaveis();
        while(aux != null && aux.variavel() != null){
            saida += (String) visitVariavel(aux.variavel());
            aux = aux.variavel();
        }
        saida += "\n} ";
        return saida;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitDeclaracao_global(LAParser.Declaracao_globalContext ctx) {
        /* declaracao_global: 'procedimento' IDENT '(' parametros_opcional ')' declaracoes_locais comandos 'fim_procedimento'
         *                |'funcao' IDENT '(' parametros_opcional ')' ':' tipo_estendido declaracoes_locais comandos 'fim_funcao'; */

        String saida;
        if(ctx.identProc != null){
            saida = "void " + ctx.identProc.getText() + "(" + visitParametros_opcional(ctx.parametros_opcional()) + ") {\n";
            saida += (String) visitDeclaracoes_locais(ctx.declaracoes_locais()) + (String) visitComandos(ctx.comandos()) + "}\n";
        } else {
            saida = (String) visitTipo_estendido(ctx.tipo_estendido()) + " " + ctx.identFunc.getText() + "(" +
                    (String) visitParametros_opcional(ctx.parametros_opcional()) + ") {\n";
            saida += (String) visitDeclaracoes_locais(ctx.declaracoes_locais()) + (String) visitComandos(ctx.comandos()) + "}\n";
        }
        return saida;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitParametros_opcional(LAParser.Parametros_opcionalContext ctx) {
        //parametros_opcional: parametro | ;

        if(ctx.parametro() != null){
            return (String) visitParametro(ctx.parametro());
        }
        return "";
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitParametro(LAParser.ParametroContext ctx) {
        //parametro: var_opcional identificador mais_ident ':' tipo_estendido mais_parametros;

        String saida;
        saida = (String) visitTipo_estendido(ctx.tipo_estendido()) + " " + (String) visitIdentificador(ctx.identificador());
        if(ctx.tipo_estendido().tipo_basico_ident().tipo_basico() == "literal"){
            saida += [100];
        }
        return saida;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*@Override
    public Object visitVar_opcional(LAParser.Var_opcionalContext ctx) {
        //var_opcional: 'var' | ;

    }*/
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitMais_parametros(LAParser.Mais_parametrosContext ctx) {
        //mais_parametros: ',' parametro | ;

        String saida;
        while(ctx.parametro() != null){
            saida += ", " + (String) visitParametro(ctx.parametro());
            ctx = ctx.parametro();
        }
        return saida;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitDeclaracoes_locais(LAParser.Declaracoes_locaisContext ctx) {
        //declaracoes_locais : declaracao_local declaracoes_locais | ;

        String saida = "";

        while(ctx.declaracoes_locais() != null){
            saida += (String) visitDeclaracao_local(ctx.declaracao_local()) + "\n";
            ctx = ctx.declaracoes_locais();
        }
        return saida;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitCorpo(LAParser.CorpoContext ctx) {
        //corpo : declaracoes_locais comandos;

        if(ctx != null){
            return (String) visitDeclaracoes_locais(ctx.declaracoes_locais()) + "\n" + visitComandos(ctx.comandos());
        }
        return "";
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitComandos(LAParser.ComandosContext ctx) {
        //comandos : (cmd)* ;

        String saida = "";

        while(ctx.cmd() != null){
            LAParser.CmdContext comando;
            saida += (String) visitCmd(ctx.cmd(comando)) + "\n";
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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

        String saida;
        if("leia".equals(ctx.getStart().getText())){
            saida = "scanf(\"";


        }
    }

    @Override
    public Object visitMais_expressao(LAParser.Mais_expressaoContext ctx) {
        //mais_expressao : ',' expressao mais_expressao | ;

        String saida;
        while(ctx.mais_expressao() != null){
            saida += ", " (String) visitExpressao(ctx.expressao()) + (String) visitMais_expressao(ctx.mais_expressao());
            ctx = ctx.mais_expressao();
        }
        return saida;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitSenao_opcional(LAParser.Senao_opcionalContext ctx) {
        //senao_opcional : 'senao' comandos | ;

        if(ctx.comandos() != null){
            return " else {" + "\t" + visitComandos(ctx.comandos()) + "\t";
        }
        return "";
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitChamada_atribuicao(LAParser.Chamada_atribuicaoContext ctx) {
        //chamada_atribuicao: '(' argumentos_opcional ')' | outros_ident dimensao '<-' expressao;

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitArgumentos_opcional(LAParser.Argumentos_opcionalContext ctx) {
        //argumentos_opcional: expressao mais_expressao | ;

        if(ctx.expressao() != null){
            return visitExpressao(ctx.expressao()) + visitMais_expressao(ctx.mais_expressao());
        }
        return "";
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitSelecao(LAParser.SelecaoContext ctx) {
        //selecao: constantes ':' comandos mais_selecao;

        String out = (String) visitConstantes(ctx.constantes())
                + (String) visitComandos(ctx.comandos())
                + "break;\n"
                + (String) visitMais_selecao(ctx.mais_selecao());

        return out;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitMais_selecao(LAParser.Mais_selecaoContext ctx) {
        //mais_selecao: selecao | ;

        if(ctx.selecao() != null){
            return (String) visitSelecao(ctx.selecao());
        }

        return "";
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitConstantes(LAParser.ConstantesContext ctx) {
        //constantes: numero_intervalo mais_constantes;

        String out = (String) visitNumero_intervalo(ctx.numero_intervalo());
        out += (String) visitMais_constantes(ctx.mais_constantes());
        return out;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitMais_constantes(LAParser.Mais_constantesContext ctx) {
        // mais_constantes: ',' constantes | ;

        String saida;
        while(ctx.constantes() != null){
            saida += ", " + (String) visitConstantes(ctx.constantes());
            ctx = ctx.constantes();
        }
        return saida;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitNumero_intervalo(LAParser.Numero_intervaloContext ctx) {
        // numero_intervalo: op_unario NUM_INT intervalo_opcional;

        String saida;
        int fim;
        int comeco = Integer.parseInt((String) visitOp_unario(ctx.op_unario()) + ctx.NUM_INT().getText());
        if ((String) visitIntervalo_opcional(ctx.intervalo_opcional()) != null) {
            fim = Integer.parseInt((String) visitIntervalo_opcional(ctx.intervalo_opcional()));
        } else {
            fim = comeco;
        }
        for (int i = comeco; i <= fim; ++i) {
            saida += "case " + i + ":\n";
        }
        return saida;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitIntervalo_opcional(LAParser.Intervalo_opcionalContext ctx) {
        // intervalo_opcional: '..' op_unario NUM_INT | ;

        if(ctx.op_unario() != null){
            return (String) visitOp_unario(ctx.op_unario()) + ctx.NUM_INT().getText();
        }
        return "";
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitOp_unario(LAParser.Op_unarioContext ctx) {
        //op_unario: '-' | ;

        return ctx.getText();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitExp_aritmetica(LAParser.Exp_aritmeticaContext ctx) {
        //exp_aritmetica: termo outros_termos;

        String saida = (String) visitTermo(ctx.termo()) + (String) visitOutros_termos(ctx.outros_termos());
        return saida;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitOp_multiplicacao(LAParser.Op_multiplicacaoContext ctx) {
        //op_multiplicacao: '*'| '/';

        return ctx.getText();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitOp_adicao(LAParser.Op_adicaoContext ctx) {
        //op_adicao : '+' | '-';

        return ctx.getText();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitTermo(LAParser.TermoContext ctx) {
        //termo : fator outros_fatores;

        return (String) visitFator(ctx.fator()) + (String) visitOutros_fatores(ctx.outros_fatores());
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object visitOutros_termos(LAParser.Outros_termosContext ctx) {
        //outros_termos : op_adicao termo outros_termos | ;

        String saida;
        while (ctx.outros_termos() != null) {
            saida += (String) visitOp_adicao(ctx.op_adicao()) + (String) visitTermo(ctx.termo()) +
                    (String) visitOutros_termos(ctx.outros_termos());
            ctx = ctx.outros_termos();
        }
        return saida;
    }

    @Override
    public Object visitFator(LAParser.FatorContext ctx) {
        //fator : parcela outras_parcelas;

        String saida = (String) visitParcela(ctx.parcela()) + (String) visitOutras_parcelas(ctx.outras_parcelas());
        return saida;
    }

    @Override
    public Object visitOutros_fatores(LAParser.Outros_fatoresContext ctx){
        //outros_fatores : op_multiplicacao fator outros_fatores | ;

        String saida;
        while (ctx.outros_fatores() != null) {
            saida += (String) visitOp_multiplicacao(ctx.op_multiplicacao()) +
                    (String) visitFator(ctx.fator()) + (String) visitOutros_fatores();
            ctx = ctx.outros_fatores();
        }
        return saida;
    }

    @Override
    public Object visitParcela(LAParser.ParcelaContext ctx) {
        //parcela : op_unario parcela_unario | parcela_nao_unario;

        String saida;
        if (ctx.op_unario() != null) {
            saida = (String) visitOp_unario(ctx.op_unario()) + (String) visitParcela_unario(ctx.parcela_unario());
        } else {
            saida = (String) visitParcela_nao_unario(ctx.parcela_nao_unario());
        }
        return saida;
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
        String saida;
        if(ctx.expressao() != null){
            saida = "(" + (String) visitExpressao(ctx.expressao()) + (String) visitMais_expressao(ctx.mais_expressao()) + ")";
        } else if(ctx.outros_ident() != null){
            saida = (String) visitOutros_ident(ctx.outros_ident()) + (String) visitDimensao(ctx.dimensao());
        }
        return saida;
    }

    @Override
    public Object visitExp_relacional(LAParser.Exp_relacionalContext ctx) {
        //exp_relacional : exp_aritmetica op_opcional;
        String saida =  (String) visitExp_aritmetica(ctx.exp_aritmetica()) +
                (String) visitOp_opcional(ctx.op_opcional());
        return saida;
    }

    @Override
    public Object visitOp_opcional(LAParser.Op_opcionalContext ctx) {
        //op_opcional : op_relacional exp_aritmetica | ;

        String saida;
        LAParser.Op_opcionalContext aux = ctx;
        while(aux.op_relacional() != null) {
            String saida =  (String) visitOp_relacional(ctx.op_relacional()) +
                    (String) visitExp_aritmetica(ctx.exp_aritmetica());
            aux = aux.op_relacional();
        }
        return saida;
    }

    @Override
    public Object visitOp_relacional(LAParser.Op_relacionalContext ctx) {
        //op_relacional : '=' | '<>'| '>=' | '<=' | '>' | '<';

        String saida;
        if(ctx.getText() == "="){
            saida = " == ";
        } else if(ctx.getText() == "<>") {
            saida = " != ";
        } else {
            saida = ctx.getText();
        }
        return saida;
    }

    @Override
    public Object visitExpressao(LAParser.ExpressaoContext ctx) {
        //expressao : termo_logico outros_termos_logicos;

        String saida =  (String) visitTermo_logico(ctx.termo_logico()) +
                (String) visitOutros_termos_logicos(ctx.outros_termos_logicos());
        return saida;
    }

    @Override
    public Object visitOp_nao(LAParser.Op_naoContext ctx) {
        //op_nao : 'nao' | ;
        String saida;
        if(ctx.getText().equals("nao")){
            saida = " not ";
        }
        return saida;
    }

    @Override
    public Object visitTermo_logico(LAParser.Termo_logicoContext ctx) {
        //termo_logico : fator_logico outros_fatores_logicos;

        String saida =  (String) visitFator_logico(ctx.fator_logico()) +
                (String) visitOutros_fatores_logicos(ctx.outros_fatores_logicos());
        return saida;
    }

    @Override
    public Object visitOutros_termos_logicos(LAParser.Outros_termos_logicosContext ctx) {
        // outros_termos_logicos : 'ou' termo_logico outros_termos_logicos | ;

        String saida;
        if(ctx.outros_termos_logicos() != null){
            saida = "|| " + (String) visitTermo_logico(ctx.termo_logico()) +
                    (String) visitOutros_termos_logicos(ctx.outros_termos_logicos());
        }
        return saida;
    }

    @Override
    public Object visitOutros_fatores_logicos(LAParser.Outros_fatores_logicosContext ctx) {
        // outros_fatores_logicos : 'e' fator_logico outros_fatores_logicos | ;

        String saida;
        if(ctx.outros_fatores_logicos() != null){
            saida = "&& " + (String) visitFator_logico(ctx.fator_logico()) +
                    (String) visitOutros_fatores_logicos(ctx.outros_fatores_logicos());
        }
        return saida;
    }

    @Override
    public Object visitFator_logico(LAParser.Fator_logicoContext ctx) {
        //fator_logico : op_nao parcela_logica;

        String saida = (String) visitOp_nao(ctx.op_nao()) + (String) visitParcela_logica(ctx.parcela_logica());
        return saida;
    }

    @Override
    public Object visitParcela_logica(LAParser.Parcela_logicaContext ctx) {
        //parcela_logica : 'verdadeiro' | 'falso' | exp_relacional;

        String saida;
        if(ctx.getStart().getText().equals("verdadeiro")){
            saida += "true";
        } else if(ctx.getStart().getText().equals("falso")) {
            saida += "false";
        } else {
            saida += (String) visitExp_relacional(ctx.exp_relacional());
        }
        return saida;
    }
}