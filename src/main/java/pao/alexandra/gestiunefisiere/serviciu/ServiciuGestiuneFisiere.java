package pao.alexandra.gestiunefisiere.serviciu;

import pao.alexandra.gestiunefisiere.model.fisier.Fisier;
import pao.alexandra.gestiunefisiere.model.fisier.Folder;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ServiciuGestiuneFisiere {

    private UUID idRoot;
    private Map<UUID, Folder> foldere = new ConcurrentHashMap<>();
    private Map<UUID, Fisier> fisiere = new ConcurrentHashMap<>();

    public ServiciuGestiuneFisiere(Folder root) {
        idRoot = UUID.nameUUIDFromBytes("0L".getBytes());
        foldere.put(idRoot, root);
    }

    public ServiciuGestiuneFisiere() {}

    public UUID getRoot() {
        return idRoot;
    }

    public Map<UUID, Folder> getFoldere() {
        return foldere;
    }

    public Map<UUID, Fisier> getFisiere() {
        return fisiere;
    }

    public void setIdRoot(UUID idRoot) {
        this.idRoot = idRoot;
    }

    public void setFoldere(Map<UUID, Folder> foldere) {
        this.foldere = foldere;
    }

    public void setFisiere(Map<UUID, Fisier> fisiere) {
        this.fisiere = fisiere;
    }

    public Optional<Folder> cautaFolder(UUID id) {
        return Optional.ofNullable(this.foldere.get(id));
    }

    public Optional<Map.Entry<UUID, Folder>> cautaFolderDupaNume(String nume) {
        Optional<Map.Entry<UUID, Folder>> folderGasit = Optional.empty();
        for (Map.Entry<UUID, Folder> folder : this.foldere.entrySet()) {
            if (folder.getValue().getNume().equals(nume)) {
                folderGasit = Optional.of(folder);
            }
        }
        return folderGasit;
    }

    public Optional<Map.Entry<UUID, Fisier>> cautaFisier(Fisier f) {
        Optional<Map.Entry<UUID, Fisier>> fisierGasit = Optional.empty();
        for (Map.Entry<UUID, Fisier> fisier : this.fisiere.entrySet()) {
            if (fisier.getValue().equals(f)) {
                fisierGasit = Optional.of(fisier);
            }
        }
        return fisierGasit;
    }

    public Optional<Map.Entry<UUID, Fisier>> cautaFisierDupaNume(UUID idFolderParinte, String numeFisier) {
        Optional<Map.Entry<UUID, Fisier>> fisierGasit = Optional.empty();
        for (Map.Entry<UUID, Fisier> fisier : this.fisiere.entrySet()) {
            if (fisier.getValue().getFolderParinte().equals(idFolderParinte) && fisier.getValue().toString().equals(numeFisier)) {
                fisierGasit = Optional.of(fisier);
            }
        }
        return fisierGasit;
    }

    public UUID adaugaFolder(UUID id, UUID idFolderParinte, String numeFolder) {
        Optional<Map.Entry<UUID, Folder>> folderGasit = cautaFolderDupaNume(numeFolder);
        if (folderGasit.isPresent()) {
            return folderGasit.get().getKey();
        } else {
            Folder folderExistent = this.foldere.get(idFolderParinte);
            this.foldere.putIfAbsent(id, new Folder(idFolderParinte, numeFolder, folderExistent.getPermisiuni()));
            return id;
        }
    }

    public UUID adaugaFisier(UUID id, UUID idFolderParinte, Fisier fisier) {
        fisier.setFolderParinte(idFolderParinte);
        Optional<Map.Entry<UUID, Fisier>> fisierGasit = cautaFisierDupaNume(idFolderParinte, fisier.toString());
        if (!fisierGasit.isPresent()) {
            this.fisiere.putIfAbsent(id, new Fisier(fisier));
        }
        return id;
    }

    public void stergeFolder(UUID id) {
        this.foldere.forEach((uuid, folder) -> {
            if (folder.getParinteId() != null && folder.getParinteId().equals(id)) {
                stergeFolder(uuid);
            }
        });
        this.foldere.remove(id);
        for (Map.Entry<UUID, Fisier> fisier : this.fisiere.entrySet()) {
            if (fisier.getValue().getFolderParinte().equals(id)) {
                stergeFisier(fisier.getKey());
            }
        }
    }

    public void stergeFisier(UUID id) {
        this.fisiere.remove(id);
    }

    public void stergeFisier(UUID idFolderParinte, Fisier fisier) {
        Optional<Map.Entry<UUID, Fisier>> fisierGasit = cautaFisier(new Fisier(fisier).setFolderParinte(idFolderParinte));
        if (fisierGasit.isPresent()) {
            this.fisiere.remove(fisierGasit.get().getKey());
        }
    }

    public void redenumesteFolder(UUID id, String numeNou) {
        this.foldere.get(id).schimbaNume(numeNou);
    }

    public void redenumesteFisier(UUID id, String numeNou) {
        this.fisiere.get(id).schimbaNume(numeNou);
    }

    public void adaugaPermisiune(UUID id, UUID grupPermisiuni) {
        this.foldere.get(id).getPermisiuni().add(grupPermisiuni);
    }

    private String toString(int indentare, UUID idFolder) {
        StringBuilder sb = new StringBuilder();
        Folder folderCurent = this.foldere.get(idFolder);
        sb.append("|--").append(folderCurent.getNume()).append(System.lineSeparator());

        this.foldere
                .entrySet()
                .stream()
                .filter(folder -> {
                    UUID parinteId = folder.getValue().getParinteId();
                    return parinteId != null && parinteId.equals(idFolder);
                })
                .sorted(Comparator.comparing(entry -> entry.getValue().getNume()))
                .forEachOrdered(folder -> {
                    sb.append(indenteaza(indentare + 1));
                    sb.append(toString(indentare + 1, folder.getKey()));
                });

        this.fisiere
                .values()
                .stream()
                .filter(fisier -> fisier.getFolderParinte().equals(idFolder))
                .sorted(Comparator.comparing(Fisier::toString))
                .forEachOrdered(fisier -> {
                    sb.append(indenteaza(indentare + 1));
                    sb.append("|").append(fisier.toString()).append(System.lineSeparator());
                });

        return sb.toString();
    }

    private String indenteaza(int value) {
        StringBuilder sb = new StringBuilder();
        for (int index = 0; index < value; index++) {
            sb.append("\t");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return toString(0, UUID.nameUUIDFromBytes("0L".getBytes()));
    }
}
