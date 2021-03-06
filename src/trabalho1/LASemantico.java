package trabalho1;

import org.antlr.v4.runtime.Token;
import java.util.ArrayList;

/**
 * Created by Andre on 04/10/2017.
 */
public class LASemantico extends LABaseVisitor {
    String grupo;
    static PilhaDeTabelas pilhaDeTabelas;
    //declaracao global de tipo para facilitar em momentos como em declaracoes de funcoes e procediemntos
    String tipo;
    //variaveis que definem se o escopo atual e um procedimento ou funcao
    boolean eProc, eFunc;

    //TabelaDeTipos
    ArrayList<String> TabelaDeTipos;


    public LASemantico() {
        //inicializacoes
        grupo = "<619922_619795_619841_552437>";
        pilhaDeTabelas = new PilhaDeTabelas();
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos("global"));

        //TabelaDeTipos, adiciona ja os tipos basicos
        TabelaDeTipos = new ArrayList<>();
        TabelaDeTipos.add("inteiro");
        TabelaDeTipos.add("real");
        TabelaDeTipos.add("literal");
        TabelaDeTipos.add("logico");

        //escopo e global, logo nao eProc e eFunc sao falsos
        eProc = false;
        eFunc = false;
    }

    @Override
    public Object visitPrograma(LAParser.ProgramaContext ctx) {
        //declaracoes 'algoritmo' corpo 'fim_algoritmo'
        if(ctx.children != null){
            visitDeclaracoes(ctx.declaracoes());
            visitCorpo(ctx.corpo());
        }
        return null;
    }

    @Override
    public String visitDeclaracoes(LAParser.DeclaracoesContext ctx) {
        //declaracoes : decl_local_global declaracoes |  ;
        if(ctx.children != null){
            visitDecl_local_global(ctx.decl_local_global());
            visitDeclaracoes(ctx.declaracoes());
        }
        return null;
    }

    @Override
    public String visitDecl_local_global(LAParser.Decl_local_globalContext ctx) {
        //decl_local_global : declaracao_local | declaracao_global;
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
            tipo = visitTipo_basico(ctx.tipo_basico());
            visitValor_constante(ctx.valor_constante());

            //verifica se constante esta na tabela de simbolos. se nao esta adiciona na tabela
            if(pilhaDeTabelas.existeSimbolo(ctx.IDENT().getText()))
                Mensagens.erroVariavelJaDeclarada(ctx.start.getLine(), ctx.IDENT().getText());
            else
                pilhaDeTabelas.topo().adicionarSimbolo(ctx.IDENT().getText(), tipo, "constante");

        }else if (ctx.getText().startsWith("tipo")){
            //adiciona o novo tipo na tabela de tipos. Ex.: tVinho
            TabelaDeTipos.add(ctx.IDENT().getText());
            visitTipo(ctx.tipo());
        }
        return null;
    }

    @Override
    public String visitVariavel(LAParser.VariavelContext ctx) {
        //variavel : IDENT dimensao mais_var ':' tipo;
        if(ctx.children != null){
            //a variavel tipo recebe o tipo da variavel visitando a regra Tipo
            tipo = visitTipo(ctx.tipo());

            //se o IDENT ja existe na pilhaDeTabelas ou na TabelaDeTipos entao o IDENT ja foi declarado anteriormente
            if(pilhaDeTabelas.existeSimbolo(ctx.IDENT().getText())|| TabelaDeTipos.contains(ctx.IDENT().getText())){
                Mensagens.erroVariavelJaDeclarada(ctx.getStart().getLine(), ctx.IDENT().getText());
            }
            //senao adiciona-se o a varivael no escopo atual
            else{
                pilhaDeTabelas.topo().adicionarSimbolo(ctx.IDENT().getText(), tipo, "variavel");
            }

            //visita o mais_var para adicionar as outras variaveis separadas por ','
            if(ctx.mais_var() != null){
                for(int i = 0; i < ctx.mais_var().IDENT().size(); i++){
                    //se nao existe na pilhaDeTabelas adiciona no escopo atual
                    if(!pilhaDeTabelas.existeSimbolo(ctx.mais_var().IDENT().get(i).getText())){
                        pilhaDeTabelas.topo().adicionarSimbolo(ctx.mais_var().IDENT().get(i).getText(), tipo, "variavel");
                    }else {
                        //metodo mais preciso de identificar a linha ja que pode declarando multiplas variaveis poode-se pular linhas
                        Token token = ctx.mais_var().IDENT().get(i).getSymbol();
                        int line = token.getLine();
                        Mensagens.erroVariavelJaDeclarada(line, ctx.mais_var().IDENT().get(i).toString());
                    }
                }
            }

            visitDimensao(ctx.dimensao());
            visitMais_var(ctx.mais_var());

            //Para debug:
            //System.out.println(pilhaDeTabelas.topo().toString());
        }

        return null;
    }

    @Override
    public String visitMais_var(LAParser.Mais_varContext ctx) {
        //mais_var: (',' IDENT dimensao)* ;
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
        /* identificador returns [String ident, int linha] :
         * ponteiros_opcionais IDENT dimensao outros_ident
         * { $ident = $ponteiros_opcionais.ponteiro + $IDENT.getText() + $outros_ident.ident; $linha = $IDENT.line; }; */


        //conforme acima, por meio do G4, junta-se os outros identificadores e ponteiros no indentificador
        if(ctx.children != null){
            visitPonteiros_opcionais(ctx.ponteiros_opcionais());
            visitDimensao(ctx.dimensao());
            visitOutros_ident(ctx.outros_ident());

            //ident recebe a uma String que e relativa aos ponteiros_opcionais + texto do identificador + "." + outros_ident
            String ident = ctx.ident;
            //separa a string a partir do ponto
            String[]array = ident.split("\\.");

            //se nao e funcao ou procedimento e necessario verificar
            if(!eProc && !eFunc){
                //se quando separado o tamanho for 2, entao trata-se o registro
                if(array.length >= 2){
                    for (int i = 0; i < array.length; i++){
                        //as variaveis do registro estao sendo armazenadas como variaveis, assim como o tipo do registro
                        //dessa maneira, verifica se ambos os lados do registro ja estao declarados
                        //e se nao estiver, um erro e acusado
                        if(!pilhaDeTabelas.topo().existeSimbolo(array[i])){
                            Mensagens.erroVariavelNaoExiste(ctx.start.getLine(), ctx.ident);
                        }
                    }
                }
                //tratemento de identicador que nao e registro
                else{
                    if(!pilhaDeTabelas.existeSimbolo(ctx.ident)) {
                        Mensagens.erroVariavelNaoExiste(ctx.start.getLine(), ctx.ident);
                    }
                }
            }

            //somente vem para esse else quando trata-se dos parâmetros uma funcao ou de um procedimento
            //assim, sempre sao adicionados na pilha de tabelas
            else {
                pilhaDeTabelas.topo().adicionarSimbolo(ctx.ident, tipo, "parametro");
            }

        }

        return null;
    }

    @Override
    public String visitPonteiros_opcionais(LAParser.Ponteiros_opcionaisContext ctx) {
        /* ponteiros_opcionais returns [String ponteiro]
         * @init { $ponteiro = ""; } :
         * '^' {$ponteiro = "^";}
         * ponteiros_opcionais | ; */

        //no g4 ja visita-se os ponteiros adicionais para adiciona-los na string ident
        if(ctx.children != null){
            //visitPonteiros_opcionais(ctx.ponteiros_opcionais());
        }

        return null;
    }

    @Override
    public String visitOutros_ident(LAParser.Outros_identContext ctx) {
        /*outros_ident returns [String ident]
         * @init { $ident = ""; }:
         * '.' identificador {$ident = "." + $identificador.ident;}
         * | ; */

        //no g4 ja visita-se os outros_ident para adiciona-los na string ident
        if(ctx.children != null){
            //visitIdentificador(ctx.identificador());
        }

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
            //retorna o tipo da variavel do registro
            return visitTipo(ctx.registro().variavel().tipo());
        }
        else{
            return visitTipo_estendido(ctx.tipo_estendido());
        }

    }

    @Override
    public String visitMais_ident(LAParser.Mais_identContext ctx) {
        //mais_ident: mais_ident :(',' identificador)*;

        if(ctx.children != null){
            for(int i = 0; i < ctx.identificador().size(); i++){
                LAParser.IdentificadorContext identificadorContext = ctx.identificador().get(i);
                visitIdentificador(identificadorContext);
            }
        }
        return null;
    }

    @Override
    public String visitMais_variaveis(LAParser.Mais_variaveisContext ctx) {
        //mais_variaveis: mais_variaveis : (variavel)* ;

        if(ctx.children != null){
            for(int i = 0; i < ctx.variavel().size(); i++){
                LAParser.VariavelContext variavelContext = ctx.variavel().get(i);
                visitVariavel(variavelContext);
            }
        }
        return null;
    }

    @Override
    public String visitTipo_basico(LAParser.Tipo_basicoContext ctx) {
        //tipo_basico: 'literal'|'inteiro'|'real'|'logico';
        //retorna o texto. Sobe na arvore caso precise
        return ctx.getText();
    }

    @Override
    public String visitTipo_basico_ident(LAParser.Tipo_basico_identContext ctx) {
        //tipo_basico_ident: tipo_basico | IDENT;

        if(ctx.tipo_basico() != null){
            //retorna texto do tipo basico
            return ctx.tipo_basico().getText();
        }

        else {
            //verifica na TabelaDeTipos se o tipo IDENT.getText() existe
            //se nao existir, erro!
            if (!TabelaDeTipos.contains(ctx.IDENT().getText())) { //adicionar posteriormente registro
                Mensagens.tipoNaoDeclarado(ctx.getStart().getLine(), ctx.IDENT().getText());
            }
            else{
                return ctx.IDENT().getText();
            }

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
        /* declaracao_global: 'procedimento' IDENT '(' parametros_opcional ')' declaracoes_locais comandos 'fim_procedimento'
         *                |'funcao' IDENT '(' parametros_opcional ')' ':' tipo_estendido declaracoes_locais comandos 'fim_funcao'; */

        if(ctx.getText().startsWith("procedimento")){
            //e procedimento = true
            eProc = true;
            //verifica se nome da funcao ja existe na pilha de tabelas
            if(pilhaDeTabelas.existeSimbolo(ctx.identProc.getText())){
                Mensagens.erroVariavelJaDeclarada(ctx.start.getLine(), ctx.IDENT().getText());
            }
            else{
                //se nao existe, adiciona o nome no topo
                pilhaDeTabelas.topo().adicionarSimbolo(ctx.IDENT().getText(), "procedimento", "procedimento");
                //cria uma nova tabela para o escopo
                TabelaDeSimbolos tabelaProc = new TabelaDeSimbolos(ctx.IDENT().getText());
                //copia as variaveis globais para o escopo
                pilhaDeTabelas.topo().CopiaVariaveis(tabelaProc);
                //empilha tabela
                pilhaDeTabelas.empilhar(tabelaProc);
            }

            visitParametros_opcional(ctx.parametros_opcional());
            visitDeclaracoes_locais(ctx.declaracoes_locais());
            visitComandos(ctx.comandos());
            //fim da funcao. E proc torna-se falso
            eProc = false;
            //desempilha escopo do procedimento
            pilhaDeTabelas.desempilhar();
        }else{
            //analogo ao do procedimento
            eFunc = true;
            if(pilhaDeTabelas.existeSimbolo(ctx.identFunc.getText())){
                Mensagens.erroVariavelJaDeclarada(ctx.start.getLine(), ctx.IDENT().getText());
            }
            else{
                pilhaDeTabelas.topo().adicionarSimbolo(ctx.IDENT().getText(), ctx.tipo_estendido().getText(), "funcao");
                TabelaDeSimbolos tabelaFunc = new TabelaDeSimbolos(ctx.IDENT().getText());
                pilhaDeTabelas.topo().CopiaVariaveis(tabelaFunc);
                pilhaDeTabelas.empilhar(tabelaFunc);
            }

            visitParametros_opcional(ctx.parametros_opcional());
            visitTipo_estendido(ctx.tipo_estendido());
            visitDeclaracoes_locais(ctx.declaracoes_locais());
            visitComandos(ctx.comandos());
            pilhaDeTabelas.desempilhar();
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
        //comandos : (cmd)* ;
        if(ctx.children != null){
            for(int i = 0; i < ctx.cmd().size(); i++){
                LAParser.CmdContext cmdContext = ctx.cmd().get(i);
                visitCmd(cmdContext);
            }
        }
        return null;
    }

    @Override
    public String visitCmd(LAParser.CmdContext ctx) {
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

        //e necessario ser leia( pq no caso 15, por exemplo, uma funcao e declarada como leiaVinho
        if(ctx.getText().startsWith("leia(")){
            visitIdentificador(ctx.identificador());
            visitMais_ident(ctx.mais_ident());
            //e necessario ser escreva( pq no caso 15, por exemplo, uma funcao e declarada como escrevaVinho
        }else if (ctx.getText().startsWith("escreva(")){
            visitExpressao(ctx.expressao());
            visitMais_expressao(ctx.mais_expressao());
        }else if (ctx.getText().startsWith("se")){
            visitExpressao(ctx.expressao());
            visitComandos(ctx.comandos());
            visitSenao_opcional(ctx.senao_opcional());
        }else if(ctx.getText().startsWith("para")){
            visitExp_aritmetica(ctx.exp1);
            visitExp_aritmetica(ctx.exp2);
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

            String ident = "^" + ctx.IDENT().getText();

            if(!pilhaDeTabelas.existeSimbolo( ctx.IDENT().getText()))
                    Mensagens.erroVariavelNaoExiste(ctx.start.getLine(),  ctx.IDENT().getText());

            //incompatibilidade de tipos
            if(ctx.expressao() != null){
                //System.out.println("var: " +ctx.IDENT().getText() + " tipo1: " +pilhaDeTabelas.topo().gettipoVar(ctx.IDENT().getText()));
                //System.out.println("tipo2: " +MergeTipos.mergeTipos(ctx.expressao()));

                //tipo 2 e o tipo da expressao
                String tipo2 = MergeTipos.mergeTipos(ctx.expressao());

                //verifica erro
                if(pilhaDeTabelas.topo().gettipoVar(ctx.IDENT().getText()) != null){
                    String incompat = MergeTipos.regraTipos(pilhaDeTabelas.topo().gettipoVar(ctx.IDENT().getText()), tipo2);

                    if(incompat.equals("erro")){
                        Token token = ctx.IDENT().getSymbol();
                        int line = token.getLine();
                        Mensagens.incompatibilidadeDeTipos(line, ident);
                    }
                }

            }

        }else if(ctx.getText().startsWith("retorne")){
            //unico local que retorne e possivel e em funcoes
            //logo, se esta no escopo global ou em procedimento. ERRO!
            if(pilhaDeTabelas.topo().getEscopo().equals("global") || eProc){
                Mensagens.retornoEscopoErrado(ctx.start.getLine());
            }


        }else if(ctx.IDENT() != null){
            visitChamada_atribuicao(ctx.chamada_atribuicao());

            String ident = ctx.IDENT().getText();

            //adcionando as strings dos outros_identificadores, ou seja, dos registros
            if(ctx.chamada_atribuicao().outros_ident() != null){
                ident += ctx.chamada_atribuicao().outros_ident().ident;
            }

            //adiciona a dimensao se existir
            if(ctx.chamada_atribuicao().dimensao() != null){
                ident += ctx.chamada_atribuicao().dimensao().getText();
            }

            //separa a string
            String[]array = ident.split("\\.");
            String tipo1 = ctx.IDENT().getText();
            String tipo2;

            //analogo ao caso do visitIdentificador. Verifica se o tamanho do array e 2 devido ao tratamento de registros
            if(array.length >= 2){
                for (int i = 0; i < array.length; i++){
                    if(!pilhaDeTabelas.topo().existeSimbolo(array[i])){
                        Mensagens.erroVariavelNaoExiste(ctx.start.getLine(),  ctx.IDENT().getText());
                    }
                }

                //tipo1 e o ultimo atributo do registro. se for o caso
                tipo1 = array[array.length-1];
            }
            else{
                if(!pilhaDeTabelas.existeSimbolo( ctx.IDENT().getText()))
                    Mensagens.erroVariavelNaoExiste(ctx.start.getLine(),  ctx.IDENT().getText());
            }

            //incompatibilidade de tipos
            if(ctx.chamada_atribuicao().expressao() != null){
                //System.out.println("var: " +ctx.IDENT().getText() + " tipo1: " +pilhaDeTabelas.topo().gettipoVar(ctx.IDENT().getText()));
                //System.out.println("tipo2: " +MergeTipos.mergeTipos(ctx.chamada_atribuicao().expressao()));

                //tipo 2 e o tipo da expressao
                tipo2 = MergeTipos.mergeTipos(ctx.chamada_atribuicao().expressao());

                //verifica erro
                if(pilhaDeTabelas.topo().gettipoVar(ctx.IDENT().getText()) != null){
                    String incompat = MergeTipos.regraTipos(pilhaDeTabelas.topo().gettipoVar(tipo1), tipo2);

                    if(incompat.equals("erro")){
                        Token token = ctx.IDENT().getSymbol();
                        int line = token.getLine();
                        Mensagens.incompatibilidadeDeTipos(line, ident);
                    }
                }

            }

        }else if(ctx.getText().startsWith("retorne")){
            //unico local que retorne e possivel e em funcoes
            //logo, se esta no escopo global ou em procedimento. ERRO!
            if(pilhaDeTabelas.topo().getEscopo().equals("global") || eProc){
                Mensagens.retornoEscopoErrado(ctx.start.getLine());
            }
            visitExpressao(ctx.expressao());
        }
        return null;
    }

    @Override
    public String visitMais_expressao(LAParser.Mais_expressaoContext ctx) {
        //mais_expressao : ',' expressao mais_expressao | ;
        if(ctx.children != null){
            for(int i = 0; i < ctx.expressao().size(); i++){
                LAParser.ExpressaoContext expressaoContext = ctx.expressao().get(i);
                visitExpressao(expressaoContext);
            }
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
            for (int i = 0; i < ctx.op_adicao().size(); i++) {
                LAParser.Op_adicaoContext op_adicaoContext = ctx.op_adicao().get(i);
                visitOp_adicao(op_adicaoContext);
                LAParser.TermoContext termoContext = ctx.termo().get(i);
                visitTermo(termoContext);
            }
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
            for (int i = 0; i < ctx.op_multiplicacao().size(); i++) {
                LAParser.Op_multiplicacaoContext op_multiplicacaoContext = ctx.op_multiplicacao().get(i);
                visitOp_multiplicacao(op_multiplicacaoContext);
                LAParser.FatorContext fatorContext = ctx.fator().get(i);
                visitFator(fatorContext);
            }
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
        /*parcela_unario returns [String valor]
         *   @init { $valor = ""; }:
         *        '^' IDENT outros_ident {$valor = $IDENT.getText() + $outros_ident.ident;} dimensao
         *        | IDENT chamada_partes {$valor = $IDENT.getText() + $chamada_partes.valor;}
         *        | NUM_INT {$valor = $NUM_INT.getText();}
         *        | NUM_REAL {$valor = $NUM_REAL.getText();}
         *        | '(' expressao ')';  */

        if (ctx.getText().startsWith("^")) {
            //analogo ao visitIdentificador()
            String ident = ctx.valor;
            String[]array = ident.split("\\.");

            if(array.length >= 2){
                for (int i = 0; i < array.length; i++){
                    if(!pilhaDeTabelas.topo().existeSimbolo(array[i])){
                        Mensagens.erroVariavelNaoExiste(ctx.start.getLine(), ctx.valor);
                    }
                }
            }
            else{
                if(!pilhaDeTabelas.existeSimbolo(ctx.valor))
                    Mensagens.erroVariavelNaoExiste(ctx.start.getLine(), ctx.valor);
            }

            visitOutros_ident(ctx.outros_ident());
            visitDimensao(ctx.dimensao());

        } else if (ctx.IDENT() != null) {
            //analogo ao visitIdentificador()
            String ident = ctx.valor;
            String[]array = ident.split("\\.");

            if(array.length >= 2){
                for (int i = 0; i < array.length; i++){
                    if(!pilhaDeTabelas.topo().existeSimbolo(array[i])){
                        Mensagens.erroVariavelNaoExiste(ctx.start.getLine(), ctx.valor);
                    }
                }
            }
            else{
                if(!pilhaDeTabelas.existeSimbolo(ctx.valor))
                    Mensagens.erroVariavelNaoExiste(ctx.start.getLine(), ctx.valor);
            }

            visitChamada_partes(ctx.chamada_partes());

        } else if (ctx.getText().startsWith("(")) {
            visitExpressao(ctx.expressao());
        }
        else if(ctx.NUM_INT() != null){
            return ctx.NUM_INT().getText();
        }
        else if(ctx.NUM_REAL() != null){
            return ctx.NUM_REAL().getText();
        }

        return null;
    }

    @Override
    public String visitParcela_nao_unario(LAParser.Parcela_nao_unarioContext ctx) {
        //parcela_nao_unario : '&' IDENT outros_ident dimensao | CADEIA;
        if (ctx.outros_ident() != null) {
            //verifica se existe o IDENT na pilhaDeTabelas
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
            for (int i = 0; i < ctx.parcela().size(); i++) {
                LAParser.ParcelaContext parcelaContext = ctx.parcela().get(i);
                visitParcela(parcelaContext);
            }
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
            for(int i = 0; i < ctx.termo_logico().size(); i++) {
                LAParser.Termo_logicoContext termo_logicoContext = ctx.termo_logico().get(i);
                visitTermo_logico(termo_logicoContext);
            }
        }
        return null;
    }

    @Override
    public String visitOutros_fatores_logicos(LAParser.Outros_fatores_logicosContext ctx) {
        // outros_fatores_logicos : 'e' fator_logico outros_fatores_logicos | ;
        if (ctx.children != null) {
            for(int i = 0; i < ctx.fator_logico().size(); i++) {
                LAParser.Fator_logicoContext fator_logicoContext = ctx.fator_logico().get(i);
                visitFator_logico(fator_logicoContext);
            }
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