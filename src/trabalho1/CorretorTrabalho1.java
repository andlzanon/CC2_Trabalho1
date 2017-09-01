package trabalho1;

import org.antlr.v4.runtime.*;

import java.io.*;

/**
 * Created by Andre on 23/08/2017.
 */
public class CorretorTrabalho1 {
    public static void main(String[] args) throws IOException, RecognitionException {

        SaidaParser saida = new SaidaParser();

        //caminho do arquivo
        File file = new File(args[0]);

        //File file = new File("C:\\Users\\André\\Documents\\GitHub\\CC2_Trabalho1\\src\\trabalho1\\casosDeTesteT1\\casosDeTesteT1\\1.arquivos_com_erros_sintaticos\\entrada\\1-algoritmo_2-2_apostila_LA_1_erro_linha_3_acusado_linha_10.txt");

        ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(file));

        LALexer lexer = new LALexer(input);
        lexer.removeErrorListeners();
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LAParser parser = new LAParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new T1SyntaxErrorListener(saida));
        parser.programa();

        File saidaCasoTeste = new File("C:\\Users\\André\\Documents\\GitHub\\CC2_Trabalho1\\temp\\saidaProduzida\\saidaSintatico\\" + file.getName());
        saidaCasoTeste.createNewFile();
        PrintWriter pw = new PrintWriter(new FileWriter(saidaCasoTeste));
        pw.println(saida);
        pw.println("Fim da compilacao");
        pw.close();

        //System.out.println(saida);
    }
}
