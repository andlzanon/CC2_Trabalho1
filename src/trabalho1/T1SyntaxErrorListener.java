package trabalho1;

import java.util.BitSet;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

public class T1SyntaxErrorListener implements ANTLRErrorListener {

    SaidaParser sp;

    public T1SyntaxErrorListener(SaidaParser sp) {
        this.sp = sp;
    }

    public void syntaxError(Recognizer<?, ?> rcgnzr, Object o, int i, int i1, String string, RecognitionException re) {
        if (!sp.isModificado()) {
            // como na string contem uma frase padronizada em "no viable alternative at input 'near word'"
            //acha todas as palavras da frase da string
            String[] comAspas = string.split(" ");
            //pega ultima palavra que contem a palavra mais proxima ao erro com aspas simples
            String erroSintatico = comAspas[comAspas.length - 1];
            //remove aspas simples
            erroSintatico = erroSintatico.replace("'", "");
            //caso o erro seja proximo a <EOF> e necessario remover <>
            erroSintatico = erroSintatico.replace("<", "");
            erroSintatico = erroSintatico.replace(">", "");
            sp.println("Linha " + i + ": erro sintatico proximo a " + erroSintatico);
            sp.println("Fim da compilacao");
        }
    }

    public void reportAmbiguity(Parser parser, DFA dfa, int i, int i1, boolean bln, BitSet bitset, ATNConfigSet atncs) {

    }

    public void reportAttemptingFullContext(Parser parser, DFA dfa, int i, int i1, BitSet bitset, ATNConfigSet atncs) {
    }

    public void reportContextSensitivity(Parser parser, DFA dfa, int i, int i1, int i2, ATNConfigSet atncs) {
    }
}
