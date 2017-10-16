package trabalho1;

public class Mensagens {

    public static boolean primeiro = true;

    public static void erroVariavelNaoExiste(int numLinha, String variavel) {
        //string do erro cometido
        String erro = "Linha " +numLinha + ": identificador " +variavel + " nao declarado";
        //verifica se e necessario pular uma linha antes de escrever a string para
        //nao pular uma linha no comeco ou antes do "Fim da compilacao"
        if (!primeiro){
            erro = "\n" + erro;
        }
        //primeiro igual a false, ou seja, apos a primeira string todas pulam uma linha
        //antes de escrever na Saida
        primeiro = false;
        //escreve o erro
        Saida.println(erro);
    }

    public static void erroVariavelJaDeclarada(int numLinha, String variavel){
        String erro = "Linha " +numLinha + ": identificador " +variavel + " ja declarado anteriormente";

        if (!primeiro){
            erro = "\n" + erro;
        }
        primeiro = false;
        Saida.println(erro);
    }

    public static void tipoNaoDeclarado(int numLinha, String tipoErrado){
        String erro = "Linha " +numLinha + ": tipo " +tipoErrado + " nao declarado";

        if (!primeiro){
            erro = "\n" + erro;
        }
        primeiro = false;

        Saida.println(erro);
    }

    public static void retornoEscopoErrado(int numLinha){
        String erro = "Linha " +numLinha + ": comando retorne nao permitido nesse escopo";

        if (!primeiro){
            erro = "\n" + erro;
        }
        primeiro = false;

        Saida.println(erro);
    }

    public static void incompatibilidadeDeTipos(int numLinha, String variavel){
        String erro = "Linha " +numLinha + ": atribuicao nao compativel para " +variavel;

        if (!primeiro){
            erro = "\n" + erro;
        }
        primeiro = false;

        Saida.println(erro);
    }
}
