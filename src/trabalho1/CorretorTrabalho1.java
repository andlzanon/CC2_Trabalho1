package trabalho1;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;

import java.io.IOException;

/**
 * Created by Andre on 23/08/2017.
 */
public class CorretorTrabalho1  {
    public static void main(String[] args) throws IOException, RecognitionException {

        SaidaParser saida = new SaidaParser();

        ANTLRInputStream input = new ANTLRInputStream(CorretorTrabalho1.class.getResourceAsStream(
                "casosDeTesteT1\\casosDeTesteT1\\1.arquivos_com_erros_sintaticos\\entrada\\3-algoritmo_2-2_apostila_LA_1_erro_linha_5.txt"));

        LALexer lexer = new LALexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LAParser parser = new LAParser(tokens);
        parser.addErrorListener(new T1SyntaxErrorListener(saida));
        parser.programa();
        if (!saida.isModificado()) {
            saida.println("Fim da analise. Sem erros sintaticos.");
            saida.println("Tabela de simbolos:");

            TabelaDeSimbolos.imprimirTabela(saida);
            System.out.println(saida);
        } else {
            saida.println("Fim da analise. Com erros sintaticos.");
            System.out.println(saida);
        }
    }
}
