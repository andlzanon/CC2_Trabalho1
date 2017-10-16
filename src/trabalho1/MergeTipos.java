package trabalho1;

public class MergeTipos {

    public MergeTipos(){ }

    public static String mergeTipos(LAParser.ExpressaoContext ctx){

        String tipo = mergeTipos(ctx.termo_logico());

        if(ctx.outros_termos_logicos() == null) {
            return tipo;
        }
        else {
            for(int i = 0; i < ctx.outros_termos_logicos().termo_logico().size(); i++) {
                LAParser.Termo_logicoContext tlc = ctx.outros_termos_logicos().termo_logico().get(i);
                String tipo2 = mergeTipos(tlc);
                tipo = regraTipos(tipo, tipo2);
            }
        }

        return tipo;
    }

    public static String mergeTipos(LAParser.Termo_logicoContext ctx) {
        String tipo = mergeTipos(ctx.fator_logico());

        if(ctx.outros_fatores_logicos() == null) {
            return tipo;
        }
        else {
            for(int i = 0; i < ctx.outros_fatores_logicos().fator_logico().size(); i++) {
                LAParser.Fator_logicoContext flc = ctx.outros_fatores_logicos().fator_logico().get(i);
                String tipo2 = mergeTipos(flc);
                tipo = regraTipos(tipo, tipo2);
            }
        }

        return tipo;
    }

    public static String mergeTipos(LAParser.Fator_logicoContext ctx) {
        String tipo = mergeTipos(ctx.parcela_logica());
        return tipo;
    }

    public static String mergeTipos(LAParser.Parcela_logicaContext ctx) {
        if(ctx.getText().startsWith("verdadeiro") || ctx.getText().startsWith("falso")){
            return "logico";
        }
        else{
            String tipo = mergeTipos(ctx.exp_relacional());
            return tipo;
        }
    }

    public static String mergeTipos(LAParser.Exp_relacionalContext ctx) {
        String tipo = "erro";
        if(ctx.op_opcional() != null){
            if(ctx.op_opcional().op_relacional()!=null){
                tipo = "logico";
            }
            else{
                tipo = mergeTipos(ctx.exp_aritmetica());
            }
        }
        else
            tipo = mergeTipos(ctx.exp_aritmetica());

        return tipo;
    }

    public static String mergeTipos(LAParser.Exp_aritmeticaContext ctx){
        String tipo = mergeTipos(ctx.termo());

        if(ctx.outros_termos() == null) {
            return tipo;
        }
        else {
            for(int i = 0; i < ctx.outros_termos().termo().size(); i++) {
                LAParser.TermoContext tc = ctx.outros_termos().termo().get(i);
                String tipo2 = mergeTipos(tc);
                tipo = regraTipos(tipo, tipo2);
            }
        }

        return tipo;
    }


    public static String mergeTipos(LAParser.TermoContext ctx){
        String tipo = mergeTipos(ctx.fator());

        if(ctx.outros_fatores() == null) {
            return tipo;
        }
        else {
            for(int i = 0; i < ctx.outros_fatores().fator().size(); i++) {
                LAParser.FatorContext fc = ctx.outros_fatores().fator().get(i);
                String tipo2 = mergeTipos(fc);
                tipo = regraTipos(tipo, tipo2);
            }
        }

        return tipo;
    }

    public static String mergeTipos(LAParser.FatorContext ctx){
        String tipo = mergeTipos(ctx.parcela());

        if(ctx.outras_parcelas() == null) {
            return tipo;
        }
        else {
            for(int i = 0; i < ctx.outras_parcelas().parcela().size(); i++) {
                LAParser.ParcelaContext pc = ctx.outras_parcelas().parcela().get(i);
                String tipo2 = mergeTipos(pc);
                tipo = regraTipos(tipo, tipo2);
            }
        }

        return tipo;
    }


    public static String mergeTipos(LAParser.ParcelaContext ctx){
        String tipo;

        if(ctx.parcela_unario() != null){
            tipo = mergeTipos(ctx.parcela_unario());
        }
        else
            tipo = mergeTipos(ctx.parcela_nao_unario());

        return tipo;
    }

    public static String mergeTipos(LAParser.Parcela_unarioContext ctx){
        String tipo = "";
        String[]array;
        String ident;
        TabelaDeSimbolos tabelaDeSimbolos = LASemantico.pilhaDeTabelas.topo();

        //NUM_INT
        if(ctx.NUM_INT() != null)
            tipo =  "inteiro";
        //NUM_REAL
        else if(ctx.NUM_REAL() != null){
            tipo = "real";
        }
        //'^' IDENT outros_ident
        else if(ctx.getText().startsWith("^")){
            ident = ctx.valor;
            array = ident.split("\\.");

            if(array.length >= 2){
                tipo = tabelaDeSimbolos.gettipoVar(array[array.length-1]);
            }
            else{
                tipo = tabelaDeSimbolos.gettipoVar(ctx.IDENT().getText());
            }
        }
        //IDENT chamada_partes
        else if(ctx.IDENT() != null){
            ident = ctx.valor;
            array = ident.split("\\.");

            if(array.length >= 2){
                tipo = tabelaDeSimbolos.gettipoVar(array[array.length-1]);
            }
            else{
                tipo = tabelaDeSimbolos.gettipoVar(ctx.IDENT().getText());
            }
        }
        //'(' expressao ')'
        else if (ctx.getText().startsWith("(")){
            tipo = mergeTipos(ctx.expressao());
        }

        return tipo;
    }

    public static String mergeTipos(LAParser.Parcela_nao_unarioContext ctx){
        String tipo = "";
        String[]array;
        String ident;
        TabelaDeSimbolos tabelaDeSimbolos = LASemantico.pilhaDeTabelas.topo();


        if(ctx.CADEIA() != null)
            return "literal";
        else{
            ident = ctx.valor;
            array = ident.split("\\.");

            if(array.length >= 2){
                tipo = tabelaDeSimbolos.gettipoVar(array[array.length-1]);
            }
            else{
                tipo = tabelaDeSimbolos.gettipoVar(ctx.IDENT().getText());
            }

        }

        return tipo;
    }

    public static String regraTipos(String tipo1, String tipo2) {
        if(tipo1.equals(tipo2)){
            return tipo1;
        } else if(tipo1.equals("real") && tipo2.equals("inteiro") ||
                tipo1.equals("inteiro") && tipo2.equals("real")) {
            return "real";
        }
        else {
            return "erro";
        }
    }

}
