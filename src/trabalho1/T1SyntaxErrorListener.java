package trabalho1;

import java.util.BitSet;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

public class T1SyntaxErrorListener implements ANTLRErrorListener {

    SaidaParser sp;

    public T1SyntaxErrorListener(SaidaParser sp) {
        this.sp = sp;
    }

    public void syntaxError(Recognizer<?, ?> rcgnzr, Object o, int i, int i1, String string, RecognitionException re) {
        if (!sp.isModificado()) {

            Token token = (Token)o;
            String tokenText = token.getText();
            if (tokenText.contentEquals("<EOF>")) {
                tokenText = "EOF";
            }
            String erro = "Linha " + i + ": erro sintatico proximo a " + tokenText;
            sp.println(erro);
        }
    }

    public void reportAmbiguity(Parser parser, DFA dfa, int i, int i1, boolean bln, BitSet bitset, ATNConfigSet atncs) {

    }

    public void reportAttemptingFullContext(Parser parser, DFA dfa, int i, int i1, BitSet bitset, ATNConfigSet atncs) {
    }

    public void reportContextSensitivity(Parser parser, DFA dfa, int i, int i1, int i2, ATNConfigSet atncs) {
    }
}
