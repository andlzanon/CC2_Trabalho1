package trabalho1;

public class Mensagens {
    public static void erroVariavelNaoExiste(int numLinha, String variavel) {
        Saida.println(numLinha+","+":Variavel "+variavel+" nao amarrada");
    }

    public static void tipoNaoDeclarado(int numLinha, String tipoErrado){
        Saida.println("Linha " +numLinha + ": tipo " +tipoErrado + "nao declarado");
    }
}
