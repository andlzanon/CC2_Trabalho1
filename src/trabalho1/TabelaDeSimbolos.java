package trabalho1;

import java.util.ArrayList;
import java.util.List;

public class TabelaDeSimbolos {
    private String escopo;
    private List<EntradaTabelaDeSimbolos> simbolos;

    private static TabelaDeSimbolos instancia;
    
    public TabelaDeSimbolos(String escopo) {
        simbolos = new ArrayList<EntradaTabelaDeSimbolos>();
        this.escopo = escopo;
    }
    
    public void adicionarSimbolo(String nome, String tipoVar, String tipo) {
        simbolos.add(new EntradaTabelaDeSimbolos(nome, tipoVar, tipo));
    }
    
    public void adicionarSimbolos(List<String> nomes, String tipoVar, String tipo) {
        for(String s:nomes) {
            simbolos.add(new EntradaTabelaDeSimbolos(s, tipoVar, tipo));
        }
    }
    
    public boolean existeSimbolo(String nome) {
        for(EntradaTabelaDeSimbolos etds:simbolos) {
            if(etds.getNome().equals(nome)) {
                return true;
            }
        }
        return false;
    }

    public boolean existeTipoVar(String tipoVar) {
        for(EntradaTabelaDeSimbolos etds:simbolos) {
            if(etds.getTipoVar().equals(tipoVar)) {
                return true;
            }
        }
        return false;
    }

    private static TabelaDeSimbolos getInstancia() {
        if (instancia == null) {
            instancia = new TabelaDeSimbolos("global");
        }
        return instancia;
    }

    public static void imprimirTabela(SaidaParser out) {
        for (EntradaTabelaDeSimbolos etds : getInstancia().simbolos) {
            out.println(etds.toString());
        }
    }
    
    @Override
    public String toString() {
        String ret = "Escopo: "+escopo;
        for(EntradaTabelaDeSimbolos etds:simbolos) {
            ret += "\n   "+etds;
        }
        return ret;
    }
}
