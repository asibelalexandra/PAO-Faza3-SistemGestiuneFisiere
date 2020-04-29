package pao.alexandra.gestiunefisiere.model.securitate;

import pao.alexandra.gestiunefisiere.model.grup.Grup;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GrupPermisiune extends Grup {

    private Set<UUID> moderatori = new HashSet<>();
    private Set<Permisiune> permisiuni = new HashSet<>();
    protected Set<UUID> dateAccesUtilizatori = new HashSet<>();
    protected Set<UUID> dateAccesGrupUtilizatori = new HashSet<>();

    public GrupPermisiune(String nume, UUID admin, Set<Permisiune> permisiuni) {
        super(nume);
        this.permisiuni.addAll(permisiuni);
        this.moderatori.add(admin);
    }

    public GrupPermisiune(String nume) {
        super(nume);
    }

    public GrupPermisiune(GrupPermisiune grupPermisiuni) {
        super(grupPermisiuni);
        this.moderatori = grupPermisiuni.moderatori;
        this.permisiuni = grupPermisiuni.permisiuni;
        this.dateAccesGrupUtilizatori = grupPermisiuni.dateAccesGrupUtilizatori;
        this.dateAccesUtilizatori = grupPermisiuni.dateAccesUtilizatori;
    }

    public GrupPermisiune adaugaAdmin(UUID admin, UUID utilizatorDeAdaugat) {
        if (moderatori.contains(admin)) {
            moderatori.add(utilizatorDeAdaugat);
        }
        return this;
    }

    public Set<UUID> getModeratori() {
        return moderatori;
    }

    public Set<Permisiune> getPermisiuni() {
        return permisiuni;
    }

    public Set<UUID> getDateAccesUtilizatori() {
        return dateAccesUtilizatori;
    }

    public Set<UUID> getDateAccesGrupUtilizatori() {
        return dateAccesGrupUtilizatori;
    }

    public GrupPermisiune adaugaUtilizator(UUID admin, UUID utilizatorDeAdaugat) {
        if (moderatori.contains(admin)) {
            dateAccesUtilizatori.add(utilizatorDeAdaugat);
        }
        return this;
    }

    public GrupPermisiune adaugaGrup(UUID admin, UUID grup) {
        if (moderatori.contains(admin)) {
            dateAccesGrupUtilizatori.add(grup);
        }
        return this;
    }

    public GrupPermisiune stergeUtilizator(UUID admin, UUID utilizatorDeSters) {
        if (moderatori.contains(admin)) {
            dateAccesUtilizatori.remove(utilizatorDeSters);
        }
        return this;
    }

    public GrupPermisiune stergeGrup(UUID admin, UUID grupDeSters) {
        if (moderatori.contains(admin)) {
            dateAccesGrupUtilizatori.remove(grupDeSters);
        }
        return this;
    }

    public boolean verificaAccesUtilizator(UUID utilizator, Permisiune permisiune) {
        return permisiuni.contains(permisiune) && (dateAccesUtilizatori.contains(utilizator) || moderatori.contains(utilizator)) && permisiune != Permisiune.FARA_PERMISIUNI;
    }

    public boolean verificaAccesGrup(UUID grupUtilizatori, Permisiune permisiune) {
        return permisiuni.contains(permisiune) && dateAccesGrupUtilizatori.contains(grupUtilizatori) && permisiune != Permisiune.FARA_PERMISIUNI;
    }
}
