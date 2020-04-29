package pao.alexandra.gestiunefisiere.controller;

import pao.alexandra.gestiunefisiere.model.fisier.Fisier;
import pao.alexandra.gestiunefisiere.model.fisier.Folder;
import pao.alexandra.gestiunefisiere.model.securitate.Permisiune;
import pao.alexandra.gestiunefisiere.model.utilizator.Utilizator;
import pao.alexandra.gestiunefisiere.serviciu.*;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Pattern;

public class ControllerGestiuneFisiere {

    private ServiciuGestiuneFisiere serviciuGestiuneFisiere;
    private ServiciuPermisiuni serviciuPermisiuni;
    private ServiciuUtilizatori serviciuUtilizatori;
    private ServiciuGrupUtilizatori serviciuGrupUtilizatori;
    private ServiciuCSV serviciuCSV;
    private ServiciuAudit serviciuAudit;

    private String delimitatorSistemOperare = Pattern.quote(File.separator);

    public ControllerGestiuneFisiere(UUID adminId, Utilizator admin) throws IOException {
        serviciuGestiuneFisiere = new ServiciuGestiuneFisiere();
        serviciuPermisiuni = new ServiciuPermisiuni();
        serviciuUtilizatori = new ServiciuUtilizatori();
        serviciuGrupUtilizatori = new ServiciuGrupUtilizatori();
        serviciuCSV = new ServiciuCSV();
        serviciuAudit = new ServiciuAudit();

        adaugaUtilizator(adminId, admin);
        serviciuPermisiuni.adaugaGrupPermisiuneNoua(UUID.nameUUIDFromBytes("0".getBytes()), "admin", adminId, Permisiune.administrator());
        serviciuPermisiuni.adaugaGrupPermisiuneNoua(UUID.nameUUIDFromBytes("1".getBytes()), "simplu", adminId, new HashSet<>(Collections.singletonList(Permisiune.CITIRE)));

        UUID rootId = UUID.nameUUIDFromBytes("0L".getBytes());
        Folder root = new Folder(null, "root", serviciuPermisiuni.getPermisiuni().keySet());
        serviciuGestiuneFisiere.setIdRoot(rootId);
        serviciuGestiuneFisiere.getFoldere().put(UUID.nameUUIDFromBytes("0L".getBytes()), root);
    }

    public ControllerGestiuneFisiere() throws IOException {
        serviciuGestiuneFisiere = new ServiciuGestiuneFisiere();
        serviciuPermisiuni = new ServiciuPermisiuni();
        serviciuUtilizatori = new ServiciuUtilizatori();
        serviciuGrupUtilizatori = new ServiciuGrupUtilizatori();
        serviciuCSV = new ServiciuCSV();
        serviciuAudit = new ServiciuAudit();

        importaCSV();
    }

    public UUID adaugaFolder(UUID utilizator, String cale) throws IOException {
        UUID folderCurent = UUID.nameUUIDFromBytes("0L".getBytes());
        for (String numeFolderCurent : caleDinString(cale)) {
            folderCurent = serviciuGestiuneFisiere.adaugaFolder(genereazaId(), folderCurent, numeFolderCurent);
        }
        Optional<Folder> folder = serviciuGestiuneFisiere.cautaFolder(folderCurent);
        if (folder.isPresent() && serviciuPermisiuni.verificaPermisiune(utilizator, folder.get(), Permisiune.SCRIERE)) {
            serviciuAudit.genereazaAudit("FOLDER_ADAUGAT", new Timestamp(new Date().getTime()), Thread.currentThread().getName());
            return folderCurent;
        } else {
            return null;
        }
    }

    public UUID adaugaFisier(UUID utilizator, String cale, Fisier fisier) throws IOException {
        UUID folderCurent = adaugaFolder(utilizator, cale);
        Optional<Folder> folder = serviciuGestiuneFisiere.cautaFolder(folderCurent);
        if (folder.isPresent() && serviciuPermisiuni.verificaPermisiune(utilizator, folder.get(), Permisiune.SCRIERE)) {
            serviciuAudit.genereazaAudit("FISIER_ADAUGAT", new Timestamp(new Date().getTime()), Thread.currentThread().getName());
            return serviciuGestiuneFisiere.adaugaFisier(genereazaId(), folderCurent, fisier);
        } else {
            return null;
        }
    }

    public void stergeFolder(UUID utilizator, String cale) throws IOException {
        UUID folderCurent = UUID.nameUUIDFromBytes("0L".getBytes());
        boolean existaFolder = true;
        for (String numeFolderCurent : caleDinString(cale)) {
            Optional<Map.Entry<UUID, Folder>> folderGasit = serviciuGestiuneFisiere.cautaFolderDupaNume(numeFolderCurent);
            if (folderGasit.isPresent() && folderGasit.get().getValue().getParinteId() != null && folderGasit.get().getValue().getParinteId().equals(folderCurent)) {
                folderCurent = folderGasit.get().getKey();
            } else {
                existaFolder = false;
                return;
            }
        }
        if (existaFolder) {
            Optional<Folder> folder = serviciuGestiuneFisiere.cautaFolder(folderCurent);
            if (folder.isPresent() && serviciuPermisiuni.verificaPermisiune(utilizator, folder.get(), Permisiune.STERGERE)) {
                serviciuAudit.genereazaAudit("FOLDER_STERS", new Timestamp(new Date().getTime()), Thread.currentThread().getName());
                serviciuGestiuneFisiere.stergeFolder(folderCurent);
            }
        }
    }

    public void stergeFisier(UUID utilizator, String cale, Fisier fisier) throws IOException {
        UUID folderCurent = UUID.nameUUIDFromBytes("0L".getBytes());
        boolean existaFolder = true;
        for (String numeFolderCurent : caleDinString(cale)) {
            Optional<Map.Entry<UUID, Folder>> folderGasit = serviciuGestiuneFisiere.cautaFolderDupaNume(numeFolderCurent);
            if (folderGasit.isPresent() && folderGasit.get().getValue().getParinteId().equals(folderCurent)) {
                folderCurent = folderGasit.get().getKey();
            } else {
                existaFolder = false;
                return;
            }
        }
        if (existaFolder) {
            Optional<Folder> folder = serviciuGestiuneFisiere.cautaFolder(folderCurent);
            if (folder.isPresent() && serviciuPermisiuni.verificaPermisiune(utilizator, folder.get(), Permisiune.STERGERE)) {
                serviciuAudit.genereazaAudit("FISIER_STERS", new Timestamp(new Date().getTime()), Thread.currentThread().getName());
                serviciuGestiuneFisiere.stergeFisier(folderCurent, fisier);
            }
        }
    }

    public void redenumesteFolder(UUID utilizator, String cale, String numeNou) throws IOException {
        UUID folderCurent = UUID.nameUUIDFromBytes("0L".getBytes());
        boolean existaFolder = true;
        for (String numeFolderCurent : caleDinString(cale)) {
            Optional<Map.Entry<UUID, Folder>> folderGasit = serviciuGestiuneFisiere.cautaFolderDupaNume(numeFolderCurent);
            if (folderGasit.isPresent() && folderGasit.get().getValue().getParinteId().equals(folderCurent)) {
                folderCurent = folderGasit.get().getKey();
            } else {
                existaFolder = false;
                return;
            }
        }
        if (existaFolder) {
            Optional<Folder> folder = serviciuGestiuneFisiere.cautaFolder(folderCurent);
            if (folder.isPresent() && serviciuPermisiuni.verificaPermisiune(utilizator, folder.get(), Permisiune.EDITARE)) {
                serviciuAudit.genereazaAudit("FOLDER_REDENUMIT", new Timestamp(new Date().getTime()), Thread.currentThread().getName());
                serviciuGestiuneFisiere.redenumesteFolder(folderCurent, numeNou);
            }
        }
    }

    public void redenumesteFisier(UUID utilizator, String cale, String fisierExistent, String numeNou) throws IOException {
        UUID folderCurent = UUID.nameUUIDFromBytes("0L".getBytes());
        boolean existaFolder = true;
        for (String numeFolderCurent : caleDinString(cale)) {
            Optional<Map.Entry<UUID, Folder>> folderGasit = serviciuGestiuneFisiere.cautaFolderDupaNume(numeFolderCurent);
            if (folderGasit.isPresent() && folderGasit.get().getValue().getParinteId().equals(folderCurent)) {
                folderCurent = folderGasit.get().getKey();
            } else {
                existaFolder = false;
                return;
            }
        }
        if (existaFolder) {
            UUID finalFolderCurent = folderCurent;
            Optional<Map.Entry<UUID, Fisier>> fisierOptional = serviciuGestiuneFisiere.cautaFisierDupaNume(folderCurent, fisierExistent);
            if (fisierOptional.isPresent()){
                Optional<Folder> folder = serviciuGestiuneFisiere.cautaFolder(finalFolderCurent);
                if (folder.isPresent() && serviciuPermisiuni.verificaPermisiune(utilizator, folder.get(), Permisiune.EDITARE)) {
                    serviciuAudit.genereazaAudit("FOLDER_ADAUGAT", new Timestamp(new Date().getTime()), Thread.currentThread().getName());
                    serviciuGestiuneFisiere.redenumesteFisier(fisierOptional.get().getKey(), numeNou);
                }
            }
        }
    }

    public UUID adaugaUtilizator(Utilizator utilizator) throws IOException {
        serviciuAudit.genereazaAudit("UTILIZATOR_ADAUGAT", new Timestamp(new Date().getTime()), Thread.currentThread().getName());
        return serviciuUtilizatori.adaugaUtilizator(genereazaId(), utilizator);
    }

    public UUID adaugaUtilizator(UUID id, Utilizator utilizator) throws IOException {
        serviciuAudit.genereazaAudit("UTILIZATOR_ADAUGAT", new Timestamp(new Date().getTime()), Thread.currentThread().getName());
        return serviciuUtilizatori.adaugaUtilizator(id, utilizator);
    }

    public UUID adaugaGrupPermisiuneNou(UUID utilizator, String numeGrupPermisiune, Set<Permisiune> permisiuni) throws IOException {
        serviciuAudit.genereazaAudit("GRUP_PERMISIUNI_ADAUGAT", new Timestamp(new Date().getTime()), Thread.currentThread().getName());
        return serviciuPermisiuni.adaugaGrupPermisiuneNoua(genereazaId(), numeGrupPermisiune, utilizator, permisiuni);
    }

    public UUID adaugaUtilizatorLaGrupPermisiune(UUID utilizator, UUID idUtilizatorNou, UUID grupPermisiune) throws IOException {
        serviciuAudit.genereazaAudit("UTILIZATOR_GRUP_PERMISIUNE_ADAUGAT", new Timestamp(new Date().getTime()), Thread.currentThread().getName());
        return serviciuPermisiuni.adaugaUtilizatorLaGrupPermisiune(grupPermisiune, utilizator, idUtilizatorNou);
    }

    public void adaugaGrupPermisiuneLaFolder(UUID utilizator, String cale, UUID idGrupPermisiune) throws IOException {
        UUID folderCurent = UUID.nameUUIDFromBytes("0L".getBytes());
        boolean existaFolder = true;
        for (String numeFolderCurent : caleDinString(cale)) {
            Optional<Map.Entry<UUID, Folder>> folderGasit = serviciuGestiuneFisiere.cautaFolderDupaNume(numeFolderCurent);
            if (folderGasit.isPresent() && folderGasit.get().getValue().getParinteId().equals(folderCurent)) {
                folderCurent = folderGasit.get().getKey();
            } else {
                existaFolder = false;
                return;
            }
        }
        if (existaFolder) {
            Optional<Folder> folder = serviciuGestiuneFisiere.cautaFolder(folderCurent);
            if (folder.isPresent() && serviciuPermisiuni.verificaPermisiune(utilizator, folder.get(), Permisiune.EDITARE)) {
                serviciuAudit.genereazaAudit("FOLDER_GRUP_PERMISIUNE_ADAUGAT", new Timestamp(new Date().getTime()), Thread.currentThread().getName());
                serviciuGestiuneFisiere.adaugaPermisiune(folderCurent, idGrupPermisiune);
            }
        }
    }

    public void afiseazaIerarhieFoldere(UUID utilizator) throws IOException {
        Optional<Folder> folder = serviciuGestiuneFisiere.cautaFolder(serviciuGestiuneFisiere.getRoot());
        if (folder.isPresent() && serviciuPermisiuni.verificaPermisiune(utilizator, folder.get(), Permisiune.CITIRE)) {
            serviciuAudit.genereazaAudit("AFISARE_IERARHIE", new Timestamp(new Date().getTime()), Thread.currentThread().getName());
            System.out.println(serviciuGestiuneFisiere.toString());
        }
    }

    private UUID genereazaId() {
        return UUID.randomUUID();
    }

    private List<String> caleDinString(String cale) {
        return Arrays.asList(cale.split(delimitatorSistemOperare));
    }

    public void importaCSV() throws IOException {
        serviciuUtilizatori.setUtilizatori(serviciuCSV.citireUtilizatori());
        serviciuPermisiuni.setPermisiuni(serviciuCSV.citireGrupPermisiuni());
        serviciuGestiuneFisiere.setFoldere(serviciuCSV.citesteFoldere());
        serviciuGestiuneFisiere.setFisiere(serviciuCSV.citesteFisere());
        UUID folderRoot = UUID.nameUUIDFromBytes("0L".getBytes());
        for (Map.Entry<UUID, Folder> folder : serviciuGestiuneFisiere.getFoldere().entrySet()) {
            if (folder.getValue().getParinteId().equals(folder.getKey())) {
                folder.getValue().setParentId(null);
                folderRoot = folder.getKey();
            }
        }
        serviciuGestiuneFisiere.setIdRoot(folderRoot);
        serviciuAudit.genereazaAudit("DATE_CSV_CITITE", new Timestamp(new Date().getTime()), Thread.currentThread().getName());
    }

    public void exportaCSV() throws IOException {
        serviciuCSV.scriereUtilizatori(serviciuUtilizatori.getUtilizatori());
        serviciuCSV.scriereGrupPermisiuni(serviciuPermisiuni.getPermisiuni());
        serviciuCSV.scrieFoldere(serviciuGestiuneFisiere.getFoldere());
        serviciuCSV.scrieFisiere(serviciuGestiuneFisiere.getFisiere());
        serviciuAudit.genereazaAudit("DATE_CSV_SCRISE", new Timestamp(new Date().getTime()), Thread.currentThread().getName());
    }
}
