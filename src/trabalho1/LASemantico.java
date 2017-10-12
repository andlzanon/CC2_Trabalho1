package trabalho1;


import org.antlr.v4.runtime.Token;

/**
 * Created by Andre on 04/10/2017.
 */
public class LASemantico extends LABaseVisitor {
    String grupo;
    PilhaDeTabelas pilhaDeTabelas;
    String tipo;

    public LASemantico() {
        grupo = "<619922_619795_619841_552437>";
    }

    @Override
    public Object visitPrograma(LAParser.ProgramaContext ctx) {
        //declaracoes 'algoritmo' corpo 'fim_algoritmo'
        pilhaDeTabelas = new PilhaDeTabelas();
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos("global"));

        if(ctx != null){
            visitDeclaracoes(ctx.declaracoes());
            visitCorpo(ctx.corpo());
        }
        return null;
    }

    @Override
    public String visitDeclaracoes(LAParser.DeclaracoesContext ctx) {
        if(ctx.children != null){
            visitDecl_local_global(ctx.decl_local_global());
            visitDeclaracoes(ctx.declaracoes());
        }
        return null;
    }

    @Override
    public String visitDecl_local_global(LAParser.Decl_local_globalContext ctx) {
        if(ctx.declaracao_local() != null)
            visitDeclaracao_local(ctx.declaracao_local());
        else if (ctx.declaracao_global() != null)
            visitDeclaracao_global(ctx.declaracao_global());
        return null;
    }

    @Override
    public String visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        /*declaracao_local  : 'declare' variavel
		          |'constante' IDENT ':' tipo_basico '=' valor_constante
		          |'tipo' IDENT ':' tipo;*/

        if(ctx.getText().startsWith("declare"))
            visitVariavel(ctx.variavel());
        else if (ctx.getText().startsWith("constante")){
            visitTipo_basico(ctx.tipo_basico()); //Adicionar Erros
            visitValor_constante(ctx.valor_constante());
        }else if (ctx.getText().startsWith("tipo"))
            visitTipo(ctx.tipo());
        return null;
    }

    @Override
    public String visitVariavel(LAParser.VariavelContext ctx) {
        //variavel : IDENT dimensao mais_var ':' tipo;
        if(ctx.children != null){
            visitDimensao(ctx.dimensao());
            visitMais_var(ctx.mais_var());
            tipo = visitTipo(ctx.tipo());

            if(pilhaDeTabelas.existeSimbolo(ctx.IDENT().getText())){
                Mensagens.erroVariavelJaDeclarada(ctx.getStart().getLine(), ctx.IDENT().getText());
            }
            else{
                pilhaDeTabelas.topo().adicionarSimbolo(ctx.IDENT().getText(), tipo, "variavel");
            }

            if(ctx.mais_var() != null){
                for(int i = 0; i < ctx.mais_var().IDENT().size(); i++){
                    if(!pilhaDeTabelas.existeSimbolo(ctx.mais_var().IDENT().get(i).toString())){
                        pilhaDeTabelas.topo().adicionarSimbolo(ctx.mais_var().IDENT().get(i).toString(), tipo, "variavel");
                    }
                    else{
                        Token token = ctx.mais_var().IDENT().get(i).getSymbol();
                        int line = token.getLine();
                        Mensagens.erroVariavelJaDeclarada(line, ctx.mais_var().IDENT().get(i).toString());
                    }
                }
            }
        }

        return null;
    }

    @Override
    public String visitMais_var(LAParser.Mais_varContext ctx) {
        //mais_var : ',' IDENT dimensao mais_var | ;
        if(ctx.getText().startsWith(",")){
            for(int i = 0; i < ctx.dimensao().size(); i++){
                LAParser.DimensaoContext dimensaoContext = ctx.dimensao().get(i);
                visitDimensao(dimensaoContext);
            }
        }
        return null;
    }

    @Override
    public String visitIdentificador(LAParser.IdentificadorContext ctx) {
        //identificador : ponteiros_opcionais IDENT dimensao outros_ident;
        if(ctx.children != null){
            if(!pilhaDeTabelas.existeSimbolo(ctx.IDENT().getText())){
                Mensagens.erroVariavelNaoExiste(ctx.start.getLine(), ctx.IDENT().getText());
            }

            visitPonteiros_opcionais(ctx.ponteiros_opcionais()); //Adicionar Erros
            visitDimensao(ctx.dimensao());
            visitOutros_ident(ctx.outros_ident());
        }
        return null;
    }

    @Override
    public String visitPonteiros_opcionais(LAParser.Ponteiros_opcionaisContext ctx) {
        //ponteiros_opcionais : '^' ponteiros_opcionais | ;
        if(ctx.children != null)
            visitPonteiros_opcionais(ctx.ponteiros_opcionais());
        return null;
    }

    @Override
    public String visitOutros_ident(LAParser.Outros_identContext ctx) {
        //outros_ident: '.' identificador | ;
        if(ctx.children != null)
            visitIdentificador(ctx.identificador());
        return null;
    }

    @Override
    public String visitDimensao(LAParser.DimensaoContext ctx) {
        //dimensao: '[' exp_aritmetica ']' dimensao | ;
        if(ctx.children != null) {
            visitExp_aritmetica(ctx.exp_aritmetica());
            visitDimensao(ctx.dimensao());
        }
        return null;
    }

    @Override
    public String visitTipo(LAParser.TipoContext ctx) {
        //tipo: registro | tipo_estendido;
        if(ctx.registro() != null){
            visitRegistro(ctx.registro());
            return "registro";
        }
        else
            return visitTipo_estendido(ctx.tipo_estendido());
    }

    @Override
    public String visitMais_ident(LAParser.Mais_identContext ctx) {
        //mais_ident: ',' identificador mais_ident | ;
        if(ctx.children != null){
            visitIdentificador(ctx.identificador());
            visitMais_ident(ctx.mais_ident());
        }
        return null;
    }

    @Override
    public String visitMais_variaveis(LAParser.Mais_variaveisContext ctx) {
        //mais_variaveis: variavel mais_variaveis | ;
        if(ctx.children != null){
            visitVariavel(ctx.variavel());
            visitMais_variaveis(ctx.mais_variaveis());
        }
        return null;
    }

    @Override
    public String visitTipo_basico(LAParser.Tipo_basicoContext ctx) {
        //tipo_basico: 'literal'|'inteiro'|'real'|'logico';
        return ctx.getText();
    }

    @Override
    public String visitTipo_basico_ident(LAParser.Tipo_basico_identContext ctx) {
        //tipo_basico_ident: tipo_basico | IDENT;
        if(ctx.tipo_basico() != null){
            return ctx.tipo_basico().getText();
        }
        else {
            if ((!pilhaDeTabelas.existeTipoVar(ctx.IDENT().getText()))) { //adicionar posteriormente registro
                Mensagens.tipoNaoDeclarado(ctx.getStart().getLine(), ctx.IDENT().getText());
            }
            else
                return null; //tratar depois
        }
        return null;
    }

    @Override
    public String visitTipo_estendido(LAParser.Tipo_estendidoContext ctx) {
        //tipo_estendido: ponteiros_opcionais tipo_basico_ident;
        if(ctx.children != null){
            visitPonteiros_opcionais(ctx.ponteiros_opcionais());
            return visitTipo_basico_ident(ctx.tipo_basico_ident());
        }
        return null;
    }

    @Override
    public String visitValor_constante(LAParser.Valor_constanteContext ctx) {
        //valor_constante: CADEIA | NUM_INT | NUM_REAL| 'verdadeiro' | 'falso';
        return ctx.getText();
    }

    @Override
    public String visitRegistro(LAParser.RegistroContext ctx) {
        //registro: 'registro' variavel mais_variaveis 'fim_registro';
        if(ctx.children != null){
            visitVariavel(ctx.variavel());
            visitMais_variaveis(ctx.mais_variaveis());
        }
        return null;
    }

    @Override
    public String visitDeclaracao_global(LAParser.Declaracao_globalContext ctx) {
        // declaracao_global: 'procedimento' IDENT '(' parametros_opcional ')' declaracoes_locais comandos 'fim_procedimento'
        //                |'funcao' IDENT '(' parametros_opcional ')' ':' tipo_estendido declaracoes_locais comandos 'fim_funcao';
        if(ctx.getText().startsWith("procedimento")){
            visitParametros_opcional(ctx.parametros_opcional()); //Adicionar Erro
            visitDeclaracoes_locais(ctx.declaracoes_locais());
            visitComandos(ctx.comandos());
        }else{
            visitParametros_opcional(ctx.parametros_opcional()); //Adicionar Erro
            visitTipo_estendido(ctx.tipo_estendido());
            visitDeclaracoes_locais(ctx.declaracoes_locais());
            visitComandos(ctx.comandos());
        }
        return null;
    }

    @Override
    public String visitParametros_opcional(LAParser.Parametros_opcionalContext ctx) {
        //parametros_opcional: parametro | ;
        if(ctx.children != null)
            visitParametro(ctx.parametro());
        return null;
    }

    @Override
    public String visitParametro(LAParser.ParametroContext ctx) {
        //parametro: var_opcional identificador mais_ident ':' tipo_estendido mais_parametros;
        if(ctx.children != null) {
            visitVar_opcional(ctx.var_opcional());
            visitIdentificador(ctx.identificador());
            visitMais_ident(ctx.mais_ident());
            visitTipo_estendido(ctx.tipo_estendido());
            visitMais_parametros(ctx.mais_parametros());
        }
        return null;
    }

    @Override
    public String visitVar_opcional(LAParser.Var_opcionalContext ctx) {
        //var_opcional: 'var' | ;
        return ctx.getText();
    }

    @Override
    public String visitMais_parametros(LAParser.Mais_parametrosContext ctx) {
        //mais_parametros: ',' parametro | ;
        if(ctx.children != null)
            visitParametro(ctx.parametro());
        return null;
    }

    @Override
    public String visitDeclaracoes_locais(LAParser.Declaracoes_locaisContext ctx) {
        //declaracoes_locais : declaracao_local declaracoes_locais | ;
        if(ctx.children != null){
            visitDeclaracao_local(ctx.declaracao_local());
            visitDeclaracoes_locais(ctx.declaracoes_locais());
        }
        return null;
    }

    @Override
    public String visitCorpo(LAParser.CorpoContext ctx) {
        //corpo : declaracoes_locais comandos;
        if(ctx.children != null){
            visitDeclaracoes_locais(ctx.declaracoes_locais());
            visitComandos(ctx.comandos());
        }
        return null;
    }

    @Override
    public String visitComandos(LAParser.ComandosContext ctx) {
        //comandos : cmd comandos | ;
        if(ctx.children != null){
            visitCmd(ctx.cmd());
            visitComandos(ctx.comandos());
        }
        return null;
    }

    @Override
    public String visitCmd(LAParser.CmdContext ctx) {
        /*cmd : 'leia' '(' identificador mais_ident ')'
                | 'escreva' '(' expressao mais_expressao ')'
                | 'se' expressao 'entao' comandos senao_opcional 'fim_se'
                | 'caso' exp_aritmetica 'seja' selecao senao_opcional 'fim_caso'
                | 'para' IDENT '<-' exp_aritmetica 'ate' exp_aritmetica 'faca' comandos 'fim_para'
                | 'enquanto' expressao 'faca' comandos 'fim_enquanto'
                | 'faca' comandos 'ate' expressao
                | '^' IDENT outros_ident dimensao '<-' expressao
                | IDENT chamada_atribuicao
                | 'retorne' expressao;*/
        if(ctx.getText().startsWith("leia")){
            visitIdentificador(ctx.identificador());
            visitMais_ident(ctx.mais_ident());
        }else if (ctx.getText().startsWith("escreva")){
            visitExpressao(ctx.expressao());
            visitMais_expressao(ctx.mais_expressao());
        }else if (ctx.getText().startsWith("se")){
            visitExpressao(ctx.expressao());
            visitComandos(ctx.comandos());
            visitSenao_opcional(ctx.senao_opcional());
        }else if(ctx.getText().startsWith("para")){
            visitExp_aritmetica(ctx.exp_aritmetica().get(0));
            visitExp_aritmetica(ctx.exp_aritmetica().get(1));
            visitComandos(ctx.comandos());
        }else if(ctx.getText().startsWith("enquanto")){
            visitExpressao(ctx.expressao());
            visitComandos(ctx.comandos());
        }else if(ctx.getText().startsWith("faca")){
            visitComandos(ctx.comandos());
            visitExpressao(ctx.expressao());
        }else if(ctx.getText().startsWith("^")){
            visitOutros_ident(ctx.outros_ident());
            visitDimensao(ctx.dimensao());
            visitExpressao(ctx.expressao());
        }else if(ctx.getText().startsWith("IDENT")){
            visitChamada_atribuicao(ctx.chamada_atribuicao());
        }else if(ctx.getText().startsWith("retorne")){
            visitExpressao(ctx.expressao());
        }
        return null;
    }

    @Override
    public String visitMais_expressao(LAParser.Mais_expressaoContext ctx) {
        //mais_expressao : ',' expressao mais_expressao | ;
        if(ctx.children != null){
            visitExpressao(ctx.expressao());
            visitMais_expressao(ctx.mais_expressao());
        }
        return null;
    }

    @Override
    public String visitSenao_opcional(LAParser.Senao_opcionalContext ctx) {
        //senao_opcional : 'senao' comandos | ;
        if(ctx.children != null)
            visitComandos(ctx.comandos());
        return null;
    }

    @Override
    public String visitChamada_atribuicao(LAParser.Chamada_atribuicaoContext ctx) {
        //chamada_atribuicao: '(' argumentos_opcional ')' | outros_ident dimensao '<-' expressao;
        if(ctx.getText().startsWith("("))
            visitArgumentos_opcional(ctx.argumentos_opcional());
        else{
            visitOutros_ident(ctx.outros_ident());
            visitDimensao(ctx.dimensao());
            visitExpressao(ctx.expressao());
        }
        return null;
    }

    @Override
    public String visitArgumentos_opcional(LAParser.Argumentos_opcionalContext ctx) {
        //argumentos_opcional: expressao mais_expressao | ;
        if(ctx.children != null){
            visitExpressao(ctx.expressao());
            visitMais_expressao(ctx.mais_expressao());
        }
        return null;
    }

    @Override
    public String visitSelecao(LAParser.SelecaoContext ctx) {
        //selecao: constantes ':' comandos mais_selecao;
        if(ctx.children != null){
            visitConstantes(ctx.constantes());
            visitComandos(ctx.comandos());
            visitMais_selecao(ctx.mais_selecao());
        }
        return null;
    }

    @Override
    public String visitMais_selecao(LAParser.Mais_selecaoContext ctx) {
        //mais_selecao: selecao | ;
        if (ctx.children != null)
            visitSelecao(ctx.selecao());
        return null;
    }

    @Override
    public String visitConstantes(LAParser.ConstantesContext ctx) {
        //constantes: numero_intervalo mais_constantes;
        if (ctx.children != null) {
            visitNumero_intervalo(ctx.numero_intervalo());
            visitMais_constantes(ctx.mais_constantes());
        }
        return null;
    }

    @Override
    public String visitMais_constantes(LAParser.Mais_constantesContext ctx) {
        // mais_constantes: ',' constantes | ;
        if (ctx.children != null)
            visitConstantes(ctx.constantes());
        return null;
    }

    @Override
    public String visitNumero_intervalo(LAParser.Numero_intervaloContext ctx) {
        // numero_intervalo: op_unario NUM_INT intervalo_opcional;
        if (ctx.children != null) {
            visitOp_unario(ctx.op_unario());
            visitIntervalo_opcional(ctx.intervalo_opcional());
        }
        return null;
    }

    @Override
    public String visitIntervalo_opcional(LAParser.Intervalo_opcionalContext ctx) {
        // intervalo_opcional: '..' op_unario NUM_INT | ;
        if (ctx.children != null)
            visitOp_unario(ctx.op_unario());
        return null;
    }

    @Override
    public String visitOp_unario(LAParser.Op_unarioContext ctx) {
        //op_unario: '-' | ;
        return ctx.getText();
    }

    @Override
    public String visitExp_aritmetica(LAParser.Exp_aritmeticaContext ctx) {
        //exp_aritmetica: termo outros_termos;
        if(ctx.children != null){
            visitTermo(ctx.termo());
            visitOutros_termos(ctx.outros_termos());
        }
        return null;
    }

    @Override
    public String visitOp_multiplicacao(LAParser.Op_multiplicacaoContext ctx) {
        //op_multiplicacao: '*'| '/';
        return ctx.getText();
    }

    @Override
    public String visitOp_adicao(LAParser.Op_adicaoContext ctx) {
        //op_adicao : '+' | '-';
        return ctx.getText();
    }

    @Override
    public String visitTermo(LAParser.TermoContext ctx) {
        //termo : fator outros_fatores;
        if(ctx.children != null){
            visitFator(ctx.fator());
            visitOutros_fatores(ctx.outros_fatores());
        }
        return null;
    }

    @Override
    public String visitOutros_termos(LAParser.Outros_termosContext ctx) {
        //outros_termos : op_adicao termo outros_termos | ;
        if (ctx.children != null) {
            visitOp_adicao(ctx.op_adicao());
            visitTermo(ctx.termo());
            visitOutros_termos(ctx.outros_termos());
        }
        return null;
    }

    @Override
    public String visitFator(LAParser.FatorContext ctx) {
        //fator : parcela outras_parcelas;
        if(ctx.children != null){
            visitParcela(ctx.parcela());
            visitOutras_parcelas(ctx.outras_parcelas());
        }
        return null;
    }

    @Override
    public String visitOutros_fatores(LAParser.Outros_fatoresContext ctx){
        //outros_fatores : op_multiplicacao fator outros_fatores | ;
        if (ctx.children != null) {
            visitOp_multiplicacao(ctx.op_multiplicacao());
            visitFator(ctx.fator());
            visitOutros_fatores(ctx.outros_fatores());
        }
        return null;
    }

    @Override
    public String visitParcela(LAParser.ParcelaContext ctx) {
        //parcela : op_unario parcela_unario | parcela_nao_unario;
        if (ctx.op_unario() != null) {
            visitOp_unario(ctx.op_unario());
            visitParcela_unario(ctx.parcela_unario());
        } else
            visitParcela_nao_unario(ctx.parcela_nao_unario());
        return null;
    }

    @Override
    public String visitParcela_unario(LAParser.Parcela_unarioContext ctx) {
        /*parcela_unario : '^' IDENT outros_ident dimensao
                | IDENT chamada_partes
                | NUM_INT
                | NUM_REAL
                | '(' expressao ')';*/

        if (ctx.getText().startsWith("^")) {
            visitOutros_ident(ctx.outros_ident());
            visitDimensao(ctx.dimensao());
        } else if (ctx.getText().startsWith("IDENT")) {
            visitChamada_partes(ctx.chamada_partes());
        } else if (ctx.getText().startsWith("(")) {
            visitExpressao(ctx.expressao());
        }

        if(!pilhaDeTabelas.topo().existeSimbolo(ctx.IDENT().getText())){
            Mensagens.erroVariavelNaoExiste(ctx.start.getLine(), ctx.IDENT().getText());
        }
        return null;
    }

    @Override
    public String visitParcela_nao_unario(LAParser.Parcela_nao_unarioContext ctx) {
        //parcela_nao_unario : '&' IDENT outros_ident dimensao | CADEIA;
        if (ctx.outros_ident() != null) {
            if(!pilhaDeTabelas.existeSimbolo(ctx.IDENT().getText())){
                Mensagens.erroVariavelNaoExiste(ctx.start.getLine(), ctx.IDENT().getText());
            }
            visitOutros_ident(ctx.outros_ident());
            visitDimensao(ctx.dimensao());
        }
        return null;
    }

    @Override
    public String visitOutras_parcelas(LAParser.Outras_parcelasContext ctx) {
        //outras_parcelas : '%' parcela outras_parcelas | ;
        if (ctx.children != null) {
            visitParcela(ctx.parcela());
            visitOutras_parcelas(ctx.outras_parcelas());
        }
        return null;
    }

    @Override
    public String visitChamada_partes(LAParser.Chamada_partesContext ctx) {
        // chamada_partes :  '(' expressao mais_expressao ')'  | outros_ident dimensao | ;
        if (ctx.expressao() != null) {
            visitExpressao(ctx.expressao());
            visitMais_expressao(ctx.mais_expressao());
        } else
        if (ctx.outros_ident() != null) {
            visitOutros_ident(ctx.outros_ident());
            visitDimensao(ctx.dimensao());
        }
        return null;
    }

    @Override
    public String visitExp_relacional(LAParser.Exp_relacionalContext ctx) {
        //exp_relacional : exp_aritmetica op_opcional;
        if(ctx.children != null){
            visitExp_aritmetica(ctx.exp_aritmetica());
            visitOp_opcional(ctx.op_opcional());
        }
        return null;
    }

    @Override
    public String visitOp_opcional(LAParser.Op_opcionalContext ctx) {
        //op_opcional : op_relacional exp_aritmetica | ;
        if (ctx.children != null) {
            visitOp_relacional(ctx.op_relacional());
            visitExp_aritmetica(ctx.exp_aritmetica());
        }
        return null;
    }

    @Override
    public String visitOp_relacional(LAParser.Op_relacionalContext ctx) {
        //op_relacional : '=' | '<>'| '>=' | '<=' | '>' | '<';
        return ctx.getText();
    }

    @Override
    public String visitExpressao(LAParser.ExpressaoContext ctx) {
        //expressao : termo_logico outros_termos_logicos;
        if(ctx.children != null){
            visitTermo_logico(ctx.termo_logico());
            visitOutros_termos_logicos(ctx.outros_termos_logicos());
        }
        return null;
    }

    @Override
    public String visitOp_nao(LAParser.Op_naoContext ctx) {
        //op_nao : 'nao' | ;
        return ctx.getText();
    }

    @Override
    public String visitTermo_logico(LAParser.Termo_logicoContext ctx) {
        //termo_logico : fator_logico outros_fatores_logicos;
        if(ctx.children != null){
            visitFator_logico(ctx.fator_logico());
            visitOutros_fatores_logicos(ctx.outros_fatores_logicos());
        }
        return null;
    }

    @Override
    public String visitOutros_termos_logicos(LAParser.Outros_termos_logicosContext ctx) {
        // outros_termos_logicos : 'ou' termo_logico outros_termos_logicos | ;
        if (ctx.children != null) {
            visitTermo_logico(ctx.termo_logico());
            visitOutros_termos_logicos(ctx.outros_termos_logicos());
        }
        return null;
    }

    @Override
    public String visitOutros_fatores_logicos(LAParser.Outros_fatores_logicosContext ctx) {
        // outros_fatores_logicos : 'e' fator_logico outros_fatores_logicos | ;
        if (ctx.children != null) {
            visitFator_logico(ctx.fator_logico());
            visitOutros_fatores_logicos(ctx.outros_fatores_logicos());
        }
        return null;
    }

    @Override
    public String visitFator_logico(LAParser.Fator_logicoContext ctx) {
        //fator_logico : op_nao parcela_logica;
        if(ctx.children != null){
            visitOp_nao(ctx.op_nao());
            visitParcela_logica(ctx.parcela_logica());
        }
        return null;
    }

    @Override
    public String visitParcela_logica(LAParser.Parcela_logicaContext ctx) {
        //parcela_logica : 'verdadeiro' | 'falso' | exp_relacional;
        if(ctx.exp_relacional() != null){
            visitExp_relacional(ctx.exp_relacional());
        }else{
            return ctx.getText();
        }
        return null;
    }
}