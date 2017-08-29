package trabalho1;

import org.antlr.v4.runtime.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Andre on 23/08/2017.
 */
public class CorretorTrabalho1 {
    public static void main(String[] args) throws IOException, RecognitionException {

        SaidaParser saida = new SaidaParser();

        //caminho do arquivo
        File file = new File(args[0]);
        /*
        /*transforma em uma Stream de dados: As duas linhas abaixo sao para ANTLR nao precisar do ANTLRInputStream que
        /*realiza a mesma funcao que as abaixo
        */
        //InputStream inputStream = new FileInputStream(file);
        //CharStream input = CharStreams.fromStream(inputStream);

        //InputStream casoDeTesteEntrada = CorretorTrabalho1.class.getResourceAsStream("local_da_entrada");
        /*colocar local_da_entrada ao inves de file.
        /*agora esta compilando por linha de comando.
        /*comando na pasta out/artifacts/LAT1_jar: java -jar LAT1.jar localdoarquivo
         */
        ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(file));

        LALexer lexer = new LALexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LAParser parser = new LAParser(tokens);
        //remove listeners para saida ser igual
        parser.removeErrorListeners();
        parser.addErrorListener(new T1SyntaxErrorListener(saida));
        parser.programa();
        System.out.println(saida);

    }
}
