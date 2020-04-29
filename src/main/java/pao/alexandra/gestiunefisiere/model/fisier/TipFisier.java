package pao.alexandra.gestiunefisiere.model.fisier;

public enum TipFisier {
    TEXT("txt"), FOTO("foto"), AUDIO("audio"), VIDEO("vid");

    private final String extension;

    TipFisier(String extension) {
        this.extension = extension;
    }

    @Override
    public String toString() {
        return extension;
    }
}
