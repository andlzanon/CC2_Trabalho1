package trabalho1;

import java.util.ArrayList;

/**
 * Created by Andre on 12/10/2017.
 */
public class Registro {

    ArrayList<TabelaDeSimbolos> registros;

    public Registro(){
        this.registros = new ArrayList<>();
    }

    public ArrayList getRegistros(){
        return registros;
    }

    public void add(String nomeEscopo){
        registros.add(new TabelaDeSimbolos(nomeEscopo));
    }

    public int getIndex(String nomeEscopo){
        for(int i = 0; i < registros.size(); i++){
            if(registros.get(i).getEscopo().equals(nomeEscopo))
                return i;
        }

        return -1;
   }

   public boolean achouTipo(String tipoComparado){
        boolean achou = false;
        for (int i = 0; i < registros.size(); i++){
            if(registros.get(i).getEscopo().equals(tipoComparado))
                achou = true;
        }
        return achou;
    }

   public void addSimbolo(int i, String nome, String tipoVar, String tipo){
       registros.get(i).adicionarSimbolo(nome, tipoVar, tipo);
   }

   public TabelaDeSimbolos getTabela(int i){
        return registros.get(i);
   }
}
