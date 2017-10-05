package trabalho1;

/**
 * Created by Andre on 04/10/2017.
 */
public class LASemantico extends LABaseVisitor {
    @Override
    public Object visitPrograma(LAParser.ProgramaContext ctx) {
        return super.visitPrograma(ctx);
    }

    @Override
    public Object visitDeclaracoes(LAParser.DeclaracoesContext ctx) {
        return super.visitDeclaracoes(ctx);
    }

    @Override
    public Object visitDecl_local_global(LAParser.Decl_local_globalContext ctx) {
        return super.visitDecl_local_global(ctx);
    }

    @Override
    public Object visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        return super.visitDeclaracao_local(ctx);
    }

    @Override
    public Object visitVariavel(LAParser.VariavelContext ctx) {
        return super.visitVariavel(ctx);
    }

    @Override
    public Object visitMais_var(LAParser.Mais_varContext ctx) {
        return super.visitMais_var(ctx);
    }

    @Override
    public Object visitIdentificador(LAParser.IdentificadorContext ctx) {
        return super.visitIdentificador(ctx);
    }

    @Override
    public Object visitPonteiros_opcionais(LAParser.Ponteiros_opcionaisContext ctx) {
        return super.visitPonteiros_opcionais(ctx);
    }

    @Override
    public Object visitOutros_ident(LAParser.Outros_identContext ctx) {
        return super.visitOutros_ident(ctx);
    }

    @Override
    public Object visitDimensao(LAParser.DimensaoContext ctx) {
        return super.visitDimensao(ctx);
    }

    @Override
    public Object visitTipo(LAParser.TipoContext ctx) {
        return super.visitTipo(ctx);
    }

    @Override
    public Object visitMais_ident(LAParser.Mais_identContext ctx) {
        return super.visitMais_ident(ctx);
    }

    @Override
    public Object visitMais_variaveis(LAParser.Mais_variaveisContext ctx) {
        return super.visitMais_variaveis(ctx);
    }

    @Override
    public Object visitTipo_basico(LAParser.Tipo_basicoContext ctx) {
        return super.visitTipo_basico(ctx);
    }

    @Override
    public Object visitTipo_basico_ident(LAParser.Tipo_basico_identContext ctx) {
        return super.visitTipo_basico_ident(ctx);
    }

    @Override
    public Object visitTipo_estendido(LAParser.Tipo_estendidoContext ctx) {
        return super.visitTipo_estendido(ctx);
    }

    @Override
    public Object visitValor_constante(LAParser.Valor_constanteContext ctx) {
        return super.visitValor_constante(ctx);
    }

    @Override
    public Object visitRegistro(LAParser.RegistroContext ctx) {
        return super.visitRegistro(ctx);
    }

    @Override
    public Object visitDeclaracao_global(LAParser.Declaracao_globalContext ctx) {
        return super.visitDeclaracao_global(ctx);
    }

    @Override
    public Object visitParametros_opcional(LAParser.Parametros_opcionalContext ctx) {
        return super.visitParametros_opcional(ctx);
    }

    @Override
    public Object visitParametro(LAParser.ParametroContext ctx) {
        return super.visitParametro(ctx);
    }

    @Override
    public Object visitVar_opcional(LAParser.Var_opcionalContext ctx) {
        return super.visitVar_opcional(ctx);
    }

    @Override
    public Object visitMais_parametros(LAParser.Mais_parametrosContext ctx) {
        return super.visitMais_parametros(ctx);
    }

    @Override
    public Object visitDeclaracoes_locais(LAParser.Declaracoes_locaisContext ctx) {
        return super.visitDeclaracoes_locais(ctx);
    }

    @Override
    public Object visitCorpo(LAParser.CorpoContext ctx) {
        return super.visitCorpo(ctx);
    }

    @Override
    public Object visitComandos(LAParser.ComandosContext ctx) {
        return super.visitComandos(ctx);
    }

    @Override
    public Object visitCmd(LAParser.CmdContext ctx) {
        return super.visitCmd(ctx);
    }

    @Override
    public Object visitMais_expressao(LAParser.Mais_expressaoContext ctx) {
        return super.visitMais_expressao(ctx);
    }

    @Override
    public Object visitSenao_opcional(LAParser.Senao_opcionalContext ctx) {
        return super.visitSenao_opcional(ctx);
    }

    @Override
    public Object visitChamada_atribuicao(LAParser.Chamada_atribuicaoContext ctx) {
        return super.visitChamada_atribuicao(ctx);
    }

    @Override
    public Object visitArgumentos_opcional(LAParser.Argumentos_opcionalContext ctx) {
        return super.visitArgumentos_opcional(ctx);
    }

    @Override
    public Object visitSelecao(LAParser.SelecaoContext ctx) {
        return super.visitSelecao(ctx);
    }

    @Override
    public Object visitMais_selecao(LAParser.Mais_selecaoContext ctx) {
        return super.visitMais_selecao(ctx);
    }

    @Override
    public Object visitConstantes(LAParser.ConstantesContext ctx) {
        return super.visitConstantes(ctx);
    }

    @Override
    public Object visitMais_constantes(LAParser.Mais_constantesContext ctx) {
        return super.visitMais_constantes(ctx);
    }

    @Override
    public Object visitNumero_intervalo(LAParser.Numero_intervaloContext ctx) {
        return super.visitNumero_intervalo(ctx);
    }

    @Override
    public Object visitIntervalo_opcional(LAParser.Intervalo_opcionalContext ctx) {
        return super.visitIntervalo_opcional(ctx);
    }

    @Override
    public Object visitOp_unario(LAParser.Op_unarioContext ctx) {
        return super.visitOp_unario(ctx);
    }

    @Override
    public Object visitExp_aritmetica(LAParser.Exp_aritmeticaContext ctx) {
        return super.visitExp_aritmetica(ctx);
    }

    @Override
    public Object visitOp_multiplicacao(LAParser.Op_multiplicacaoContext ctx) {
        return super.visitOp_multiplicacao(ctx);
    }

    @Override
    public Object visitOp_adicao(LAParser.Op_adicaoContext ctx) {
        return super.visitOp_adicao(ctx);
    }

    @Override
    public Object visitTermo(LAParser.TermoContext ctx) {
        return super.visitTermo(ctx);
    }

    @Override
    public Object visitOutros_termos(LAParser.Outros_termosContext ctx) {
        return super.visitOutros_termos(ctx);
    }

    @Override
    public Object visitFator(LAParser.FatorContext ctx) {
        return super.visitFator(ctx);
    }

    @Override
    public Object visitOutros_fatores(LAParser.Outros_fatoresContext ctx) {
        return super.visitOutros_fatores(ctx);
    }

    @Override
    public Object visitParcela(LAParser.ParcelaContext ctx) {
        return super.visitParcela(ctx);
    }

    @Override
    public Object visitParcela_unario(LAParser.Parcela_unarioContext ctx) {
        return super.visitParcela_unario(ctx);
    }

    @Override
    public Object visitParcela_nao_unario(LAParser.Parcela_nao_unarioContext ctx) {
        return super.visitParcela_nao_unario(ctx);
    }

    @Override
    public Object visitOutras_parcelas(LAParser.Outras_parcelasContext ctx) {
        return super.visitOutras_parcelas(ctx);
    }

    @Override
    public Object visitChamada_partes(LAParser.Chamada_partesContext ctx) {
        return super.visitChamada_partes(ctx);
    }

    @Override
    public Object visitExp_relacional(LAParser.Exp_relacionalContext ctx) {
        return super.visitExp_relacional(ctx);
    }

    @Override
    public Object visitOp_opcional(LAParser.Op_opcionalContext ctx) {
        return super.visitOp_opcional(ctx);
    }

    @Override
    public Object visitOp_relacional(LAParser.Op_relacionalContext ctx) {
        return super.visitOp_relacional(ctx);
    }

    @Override
    public Object visitExpressao(LAParser.ExpressaoContext ctx) {
        return super.visitExpressao(ctx);
    }

    @Override
    public Object visitOp_nao(LAParser.Op_naoContext ctx) {
        return super.visitOp_nao(ctx);
    }

    @Override
    public Object visitTermo_logico(LAParser.Termo_logicoContext ctx) {
        return super.visitTermo_logico(ctx);
    }

    @Override
    public Object visitOutros_termos_logicos(LAParser.Outros_termos_logicosContext ctx) {
        return super.visitOutros_termos_logicos(ctx);
    }

    @Override
    public Object visitOutros_fatores_logicos(LAParser.Outros_fatores_logicosContext ctx) {
        return super.visitOutros_fatores_logicos(ctx);
    }

    @Override
    public Object visitFator_logico(LAParser.Fator_logicoContext ctx) {
        return super.visitFator_logico(ctx);
    }

    @Override
    public Object visitParcela_logica(LAParser.Parcela_logicaContext ctx) {
        return super.visitParcela_logica(ctx);
    }
}
