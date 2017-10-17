
package trabalho1;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import trabalho1.LAParser.*;

import java.io.*;

/**
 * Created by Andre on 23/08/2017.
 */
public class CorretorTrabalho1 {
    public static void main(String[] args) throws IOException, RecognitionException {

        SaidaParser saida = new SaidaParser();

        /*caminho do arquivo*/
        File file = new File(args[0]);

        ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(file));

        LALexer lexer = new LALexer(input);
        lexer.removeErrorListeners();
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LAParser parser = new LAParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new T1SyntaxErrorListener(saida));
        ProgramaContext arvore = parser.programa();

        //saida do analisador sintatico
        //Comentar aqui para rodar o sintatico e descomentar o semantico
        PrintWriter pw = new PrintWriter(new FileWriter(args[1]));
        pw.println(saida);
        pw.println("Fim da compilacao");
        pw.close();
        pw.flush();

        /*saida do analisador semantico
        //Descomentar para funcionar e comentar o semantico
        LASemantico laSemantico = new LASemantico();
        laSemantico.visitPrograma(arvore);
        PrintWriter pws = new PrintWriter(new File(args[1]));
        pws.println(Saida.getTexto());
        pws.println("Fim da compilacao");
        pws.close();
        pws.flush();*/
    }
}