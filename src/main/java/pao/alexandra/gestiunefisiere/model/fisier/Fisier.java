package pao.alexandra.gestiunefisiere.model.fisier;

import java.util.UUID;

public class Fisier {

    private UUID folderParinte;
    private String nume;
    private TipFisier tipFisier;

    public Fisier(String nume, TipFisier tipFisier) {
        this.nume = nume;
        this.tipFisier = tipFisier;
    }

    public Fisier(UUID folderParinte, String nume, TipFisier tipFisier) {
        this.folderParinte = folderParinte;
        this.nume = nume;
        this.tipFisier = tipFisier;
    }

    public Fisier(Fisier fisier) {
        this.folderParinte = fisier.folderParinte;
        this.nume = fisier.nume;
        this.tipFisier = fisier.tipFisier;
    }

    public String getNume() {
        return nume;
    }

    public Fisier schimbaNume(String numeNou) {
        this.nume = numeNou;
        return this;
    }

    public TipFisier getTipFisier() {
        return tipFisier;
    }

    public UUID getFolderParinte() {
        return folderParinte;
    }

    public Fisier setFolderParinte(UUID folderParinte) {
        this.folderParinte = folderParinte;
        return this;
    }

    @Override
    public String toString() {
        return getNume() + "." + tipFisier.toString();
    }
}
