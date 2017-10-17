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

        /*testa exemplo unico
        File file = new File("C:\\Users\\André\\Documents\\GitHub\\CC2_Trabalho1\\src\\trabalho1\\casosDeTesteT1\\casosDeTesteT1\\" +
                "2.arquivos_com_erros_semanticos\\entrada\\13.algoritmo_9-4_apostila_LA.txt");
        ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(file));
        LALexer lexer = new LALexer(input);
        lexer.removeErrorListeners();
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LAParser parser = new LAParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new T1SyntaxErrorListener(saida));
        ProgramaContext arvore = parser.programa();
        LASemantico laSemantico = new LASemantico();
        laSemantico.visitPrograma(arvore);
        System.out.println(Saida.getTexto());*/


        /*saida do analisador sintatico
        File saidaCasoTesteSintatico = new File("C:\\Users\\André\\Documents\\GitHub\\CC2_Trabalho1\\temp\\saidaProduzida\\saidaSintatico\\"
         + file.getName());
        PrintWriter pw = new PrintWriter(new FileWriter(saidaCasoTesteSintatico));
        pw.println(saida);
        pw.println("Fim da compilacao");
        pw.close();
        pw.flush();*/

        /*saida do analisador semantico*/
        LASemantico laSemantico = new LASemantico();
        laSemantico.visitPrograma(arvore);
        File saidaCasoTesteSemantico = new File("C:\\Users\\André\\Documents\\GitHub\\CC2_Trabalho1\\temp\\saidaProduzida\\saidaSemanticoComErros\\"
                + file.getName());
        PrintWriter pws = new PrintWriter(new FileWriter(saidaCasoTesteSemantico));
        pws.println(Saida.getTexto());
        pws.println("Fim da compilacao");
        pws.close();
        pws.flush();
    }
}
