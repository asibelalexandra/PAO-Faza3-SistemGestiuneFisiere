package pao.alexandra.gestiunefisiere.model.utilizator;

import pao.alexandra.gestiunefisiere.model.grup.Grup;

import java.util.ArrayList;
import java.util.List;

public class GrupUtilizatori extends Grup {

    private List<Utilizator> utilizatori = new ArrayList<>();

    public GrupUtilizatori(String nume) {
        super(nume);
    }

    public GrupUtilizatori(GrupUtilizatori grupUtilizatori) {
        super(grupUtilizatori);
        this.utilizatori = grupUtilizatori.utilizatori;
    }

    public Grup adaugaUtilizator(Utilizator utilizator) {
        utilizatori.add(utilizator);
        return this;
    }

    public Grup stergeUtilizator(Utilizator utilizator) {
        utilizatori.remove(utilizator);
        return this;
    }

    public boolean contineUtilizator(Utilizator utilizator) {
        return utilizatori.contains(utilizator);
    }
}
