package pao.alexandra.gestiunefisiere.model.grup;

public class Grup {

    private String nume;

    public Grup(String nume) {
        this.nume = nume;
    }

    public Grup(Grup grup) {
        this.nume = grup.nume;
    }

    public String getNume() {
        return nume;
    }

    public Grup schimbaNume(String numeNou) {
        this.nume = numeNou;
        return this;
    }
}
