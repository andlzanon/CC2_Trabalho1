package trabalho1;

public class EntradaTabelaDeSimbolos {
    private String nome, tipoVar, tipo;
    
    public EntradaTabelaDeSimbolos(String nome, String tipoVar, String tipo) {
        this.nome = nome;
        this.tipo = tipo;
        this.tipoVar = tipoVar;
    }
    
    public String getNome() {
        return nome;
    }
    
    public String getTipo() {
        return tipo;
    }

    public String getTipoVar() {return tipoVar; }
    
    @Override
    public String toString() { return tipoVar + " " + nome+"("+tipo+")"; }
}
