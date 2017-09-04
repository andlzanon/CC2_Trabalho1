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
        //se o saida parser ainda nao foi modificado entra para verificar os erros lexicos
        if (!sp.isModificado()) {
            //acessa token do obejeto
            Token token = (Token)o;
            //define a string de erro
            String erro = "";
            //tokenText recebe texto do token
            String tokenText = token.getText();

            //se o tipo do token for 10 ou 11 significa que ou o token e comentario incompleto ou nao reconhecido
            //assim, para o tipo mais "generico" de erro o programa entrara nesse if
            if(token.getType() != 10 && token.getType() != 11){
                if (tokenText.contentEquals("<EOF>")) {
                    tokenText = "EOF";
                }
                erro = "Linha " + i + ": erro sintatico proximo a " + tokenText;
                sp.println(erro);
            }
            //se o tipo for 10 entao o token e nao declarado e precisa de uma saida diferente
            else if(token.getType() == 10){
                erro = "Linha " + i + ": " + tokenText + " - simbolo nao identificado";
                sp.println(erro);
            }
            //se o tipo for 11 entao o token e comentario nao fechado e precisa de uma saida diferente
            else if(token.getType() == 11){
                erro = "Linha " + (i+1) + ": comentario nao fechado";
                sp.println(erro);
            }
        }
    }

    public void reportAmbiguity(Parser parser, DFA dfa, int i, int i1, boolean bln, BitSet bitset, ATNConfigSet atncs) {

    }

    public void reportAttemptingFullContext(Parser parser, DFA dfa, int i, int i1, BitSet bitset, ATNConfigSet atncs) {
    }

    public void reportContextSensitivity(Parser parser, DFA dfa, int i, int i1, int i2, ATNConfigSet atncs) {
    }
}
