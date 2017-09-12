package trabalho1;

public class Mensagens {

    public static void erroVariavelNaoExiste(int numLinha, String variavel) {
        Saida.println("Linha " +numLinha + ": identificador " +variavel + " nao declarado");
    }

    public static void erroVariavelJaDeclarada(int numLinha, String variavel){
        Saida.println("Linha " +numLinha + ": identificador " +variavel + " ja declarado anteriormente");
    }

    public static void tipoNaoDeclarado(int numLinha, String tipoErrado){
        Saida.println("Linha " +numLinha + ": tipo " +tipoErrado + " nao declarado");
    }
}
