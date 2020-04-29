package pao.alexandra.gestiunefisiere.serviciu;

import pao.alexandra.gestiunefisiere.model.fisier.Fisier;
import pao.alexandra.gestiunefisiere.model.fisier.Folder;
import pao.alexandra.gestiunefisiere.model.fisier.TipFisier;
import pao.alexandra.gestiunefisiere.model.securitate.GrupPermisiune;
import pao.alexandra.gestiunefisiere.model.securitate.Permisiune;
import pao.alexandra.gestiunefisiere.model.utilizator.Utilizator;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServiciuCSV {

    private String FOLDER_CSV = "date";

    private String HEADER_UTILIZATORI = "id, nume, pernume, varsta";
    private String HEADER_GRUPURI_PERMISIUNI = "id, nume";
    private String HEADER_GRUPURI_PERMISIUNI_MODERATORI = "id_grup, id_utilizator";
    private String HEADER_GRUPURI_PERMISIUNI_UTILIZATORI = "id_grup, id_utilizator";
    private String HEADER_GRUPURI_PERMISIUNI_LISTA = "id_grup, permisiune";
    private String HEADER_GRUPURI_PERMISIUNI_GRUP_UTILIZATORI = "id_grup, id_grup_utilizatori";
    private String HEADER_FOLDERE = "id, id_folder_parinte, nume";
    private String HEADER_PERMISIUNI_FOLDERE = "id_folder, id_grup_permisiuni";
    private String HEADER_FISIERE = "id, id_folder_parinte, nume, extensie";

    private String FISIER_UTILIZATORI = "utilizatori.csv";
    private String FISIER_GRUPURI_PERMISIUNI = "grupuri_permisiuni.csv";
    private String FISIER_GRUPURI_PERMISIUNI_MODERATORI = "grupuri_permisiuni_mod.csv";
    private String FISIER_GRUPURI_PERMISIUNI_UTILIZATORI = "grupuri_permisiuni_utilizatori.csv";
    private String FISIER_GRUPURI_PERMISIUNI_LISTA = "grupuri_permisiuni_lista.csv";
    private String FISIER_GRUPURI_PERMISIUNI_GRUP_UTILIZATORI = "grupuri_permisiuni_grup.csv";
    private String FISIER_LISTA_FOLDERE = "foldere.csv";
    private String FISIER_LISTA_PERMISIUNI_FOLDERE = "foldere_permisiuni.csv";
    private String FISIER_LISTA_FISIERE = "fisiere.csv";

    public void scriereUtilizatori(Map<UUID, Utilizator> utilizatori) throws IOException {
        List<String> listaUtilizatori = utilizatori
                .entrySet()
                .stream()
                .map((entry) -> {
                    UUID uuid = entry.getKey();
                    Utilizator utilizator = entry.getValue();
                    return stringCSVDinLista(uuid.toString(), utilizator.getNume(), utilizator.getPrenume(), utilizator.getVarsta().toString());
                })
                .collect(Collectors.toList());

        scrieFisierCSV(FOLDER_CSV, FISIER_UTILIZATORI, HEADER_UTILIZATORI, listaUtilizatori);
    }

    public void scriereGrupPermisiuni(Map<UUID, GrupPermisiune> grupuriPermisiuni) throws IOException {
        List<String> listaGrupuriPermisiuni = grupuriPermisiuni
                .entrySet()
                .stream()
                .map((entry) -> {
                    UUID uuid = entry.getKey();
                    GrupPermisiune grupPermisiune = entry.getValue();
                    return stringCSVDinLista(uuid.toString(), grupPermisiune.getNume());
                })
                .collect(Collectors.toList());

        List<String> listaModeratoriGrupuri = new ArrayList<>();
        List<String> listaPermisiuni = new ArrayList<>();
        List<String> listaUtilizatori = new ArrayList<>();
        List<String> listaGrupuriUtilizatori = new ArrayList<>();
        for (Map.Entry<UUID, GrupPermisiune> grup : grupuriPermisiuni.entrySet()) {
            UUID uuid = grup.getKey();
            GrupPermisiune grupPermisiune = grup.getValue();
            grupPermisiune.getModeratori().forEach(moderator -> listaModeratoriGrupuri.add(stringCSVDinLista(uuid.toString(), moderator.toString())));
            grupPermisiune.getPermisiuni().forEach(permisiune -> listaPermisiuni.add(stringCSVDinLista(uuid.toString(), permisiune.name())));
            grupPermisiune.getDateAccesUtilizatori().forEach(utilizator -> listaUtilizatori.add(stringCSVDinLista(uuid.toString(), utilizator.toString())));
            grupPermisiune.getDateAccesGrupUtilizatori().forEach(grupUtilizatori -> listaGrupuriUtilizatori.add(stringCSVDinLista(uuid.toString(), grupUtilizatori.toString())));
        }

        scrieFisierCSV(FOLDER_CSV, FISIER_GRUPURI_PERMISIUNI, HEADER_GRUPURI_PERMISIUNI, listaGrupuriPermisiuni);
        scrieFisierCSV(FOLDER_CSV, FISIER_GRUPURI_PERMISIUNI_MODERATORI, HEADER_GRUPURI_PERMISIUNI_MODERATORI, listaModeratoriGrupuri);
        scrieFisierCSV(FOLDER_CSV, FISIER_GRUPURI_PERMISIUNI_LISTA, HEADER_GRUPURI_PERMISIUNI_LISTA, listaPermisiuni);
        scrieFisierCSV(FOLDER_CSV, FISIER_GRUPURI_PERMISIUNI_UTILIZATORI, HEADER_GRUPURI_PERMISIUNI_UTILIZATORI, listaUtilizatori);
        scrieFisierCSV(FOLDER_CSV, FISIER_GRUPURI_PERMISIUNI_GRUP_UTILIZATORI, HEADER_GRUPURI_PERMISIUNI_GRUP_UTILIZATORI, listaGrupuriUtilizatori);
    }

    public void scrieFoldere(Map<UUID, Folder> foldere) throws IOException {
        List<String> listaFoldere = foldere
                .entrySet()
                .stream()
                .map((entry) -> {
                    UUID uuid = entry.getKey();
                    Folder folder = entry.getValue();
                    return stringCSVDinLista(uuid.toString(), Optional.ofNullable(folder.getParinteId()).orElse(UUID.nameUUIDFromBytes("0L".getBytes())).toString(), folder.getNume());
                })
                .collect(Collectors.toList());

        List<String> listaPermisiuni = new ArrayList<>();
        for (Map.Entry<UUID, Folder> grup : foldere.entrySet()) {
            UUID uuid = grup.getKey();
            Folder folder = grup.getValue();
            folder.getPermisiuni().forEach(idGrupPermisiuni -> listaPermisiuni.add(stringCSVDinLista(uuid.toString(), idGrupPermisiuni.toString())));
        }

        scrieFisierCSV(FOLDER_CSV, FISIER_LISTA_FOLDERE, HEADER_FOLDERE, listaFoldere);
        scrieFisierCSV(FOLDER_CSV, FISIER_LISTA_PERMISIUNI_FOLDERE, HEADER_PERMISIUNI_FOLDERE, listaPermisiuni);
    }

    public void scrieFisiere(Map<UUID, Fisier> fisiere) throws IOException {
        List<String> listaFisiere = fisiere
                .entrySet()
                .stream()
                .map((entry) -> {
                    UUID uuid = entry.getKey();
                    Fisier fisier = entry.getValue();
                    return stringCSVDinLista(uuid.toString(), fisier.getFolderParinte().toString(), fisier.getNume(), fisier.getTipFisier().name());
                })
                .collect(Collectors.toList());

        scrieFisierCSV(FOLDER_CSV, FISIER_LISTA_FISIERE, HEADER_FISIERE, listaFisiere);
    }

    public Map<UUID, Utilizator> citireUtilizatori() throws IOException {
        List<String> randuriUtilizatori = citesteFisierCSV(FOLDER_CSV, FISIER_UTILIZATORI, HEADER_UTILIZATORI);

        Map<UUID, Utilizator> utilizatori = new HashMap<>();
        randuriUtilizatori.forEach(dateUtilizator -> {
            String[] date = dateUtilizator.split(",");
            UUID uuid = UUID.fromString(date[0]);
            Utilizator utilizator = new Utilizator(date[1], date[2], new Integer(date[3]));
            utilizatori.put(uuid, utilizator);
        });
        return utilizatori;
    }

    public Map<UUID, GrupPermisiune> citireGrupPermisiuni() throws IOException {
        List<String> randuriGrupPermisiuni = citesteFisierCSV(FOLDER_CSV, FISIER_GRUPURI_PERMISIUNI, HEADER_GRUPURI_PERMISIUNI);
        List<String> randuriModeratori = citesteFisierCSV(FOLDER_CSV, FISIER_GRUPURI_PERMISIUNI_MODERATORI, HEADER_GRUPURI_PERMISIUNI_MODERATORI);
        List<String> randuriPermisiuni = citesteFisierCSV(FOLDER_CSV, FISIER_GRUPURI_PERMISIUNI_LISTA, HEADER_GRUPURI_PERMISIUNI_LISTA);
        List<String> randuriUtilizatori = citesteFisierCSV(FOLDER_CSV, FISIER_GRUPURI_PERMISIUNI_UTILIZATORI, HEADER_GRUPURI_PERMISIUNI_UTILIZATORI);
        List<String> randuriGrupUtilizatori = citesteFisierCSV(FOLDER_CSV, FISIER_GRUPURI_PERMISIUNI_GRUP_UTILIZATORI, HEADER_GRUPURI_PERMISIUNI_GRUP_UTILIZATORI);

        Map<UUID, GrupPermisiune> grupuriPermisiuni = new HashMap<>();
        randuriGrupPermisiuni.forEach(dateGrupPermisiuni -> {
            String[] date = dateGrupPermisiuni.split(",");
            UUID uuid = UUID.fromString(date[0]);
            GrupPermisiune grupPermisiuni = new GrupPermisiune(date[1]);
            grupuriPermisiuni.put(uuid, grupPermisiuni);
        });
        randuriModeratori.forEach(dateModerator -> {
            String[] date = dateModerator.split(",");
            UUID uuid = UUID.fromString(date[0]);
            UUID moderator = UUID.fromString(date[1]);
            grupuriPermisiuni.get(uuid).getModeratori().add(moderator);
        });
        randuriPermisiuni.forEach(datePermisiuni -> {
            String[] date = datePermisiuni.split(",");
            UUID uuid = UUID.fromString(date[0]);
            Permisiune permisiune = Permisiune.valueOf(date[1]);
            grupuriPermisiuni.get(uuid).getPermisiuni().add(permisiune);
        });
        randuriUtilizatori.forEach(dateUtilizatori -> {
            String[] date = dateUtilizatori.split(",");
            UUID uuid = UUID.fromString(date[0]);
            UUID utilizatori = UUID.fromString(date[1]);
            grupuriPermisiuni.get(uuid).getDateAccesUtilizatori().add(utilizatori);
        });
        randuriGrupUtilizatori.forEach(dateGrupUtilizatori -> {
            String[] date = dateGrupUtilizatori.split(",");
            UUID uuid = UUID.fromString(date[0]);
            UUID grupUtilizatori = UUID.fromString(date[1]);
            grupuriPermisiuni.get(uuid).getDateAccesGrupUtilizatori().add(grupUtilizatori);
        });
        return grupuriPermisiuni;
    }

    public Map<UUID, Folder> citesteFoldere() throws IOException {
        List<String> randuriFoldere = citesteFisierCSV(FOLDER_CSV, FISIER_LISTA_FOLDERE, HEADER_FOLDERE);
        List<String> randuriPermisiuniFoldere = citesteFisierCSV(FOLDER_CSV, FISIER_LISTA_PERMISIUNI_FOLDERE, HEADER_PERMISIUNI_FOLDERE);

        Map<UUID, Folder> foldere = new HashMap<>();
        randuriFoldere.forEach(dateFoldere -> {
            String[] date = dateFoldere.split(",");
            UUID uuid = UUID.fromString(date[0]);
            UUID idParinte = UUID.fromString(date[1]);
            Folder folder = new Folder(idParinte, date[2]);
            foldere.put(uuid, folder);
        });
        randuriPermisiuniFoldere.forEach(datePermisiuniFoldere -> {
            String[] date = datePermisiuniFoldere.split(",");
            UUID uuid = UUID.fromString(date[0]);
            UUID grupPermisiune = UUID.fromString(date[1]);
            foldere.get(uuid).getPermisiuni().add(grupPermisiune);
        });

        return foldere;
    }

    public Map<UUID, Fisier> citesteFisere() throws IOException {
        List<String> randuriFisiere = citesteFisierCSV(FOLDER_CSV, FISIER_LISTA_FISIERE, HEADER_FISIERE);

        Map<UUID, Fisier> fisiere = new HashMap<>();
        randuriFisiere.forEach(dateFisiere -> {
            String[] date = dateFisiere.split(",");
            UUID uuid = UUID.fromString(date[0]);
            UUID idParinte = UUID.fromString(date[1]);
            Fisier fisier = new Fisier(idParinte, date[2], TipFisier.valueOf(date[3]));
            fisiere.put(uuid, fisier);
        });
        return fisiere;
    }

    private String stringCSVDinLista(String... elemente) {
        return Stream.of(elemente).map(String::valueOf).collect(Collectors.joining(","));
    }

    private void scrieFisierCSV(String cale, String numeFisier, String header, List<String> randuri) throws IOException {
        String caleFinala = (System.getProperty("user.dir") + File.separator + cale);
        File caleFolder = new File(caleFinala);
        File fisierCSV = new File(caleFolder.getPath(), numeFisier);
        if (caleFolder.exists() && fisierCSV.exists()) {
            fisierCSV.delete();
        } else {
            caleFolder.mkdirs();
            fisierCSV.createNewFile();
        }
        FileWriter writer = new FileWriter(fisierCSV);
        writer.append(header).append(System.lineSeparator());
        for (String rand : randuri) {
            writer.append(rand).append(System.lineSeparator());
        }
        writer.flush();
        writer.close();
    }

    private List<String> citesteFisierCSV(String cale, String numeFisier, String header) throws IOException {
        String caleFinala = (System.getProperty("user.dir") + File.separator + cale);
        File caleFolder = new File(caleFinala);
        File fisierCSV = new File(caleFolder.getPath(), numeFisier);

        List<String> randuriFisier = new ArrayList<>();
        if (caleFolder.exists() && fisierCSV.exists()) {
            BufferedReader csvReader = new BufferedReader(new FileReader(fisierCSV));
            String rand;
            boolean headerVerificat = false;
            while ((rand = csvReader.readLine()) != null) {
                if (!headerVerificat && verificaRandHeader(rand, header)) {
                    if (verificaRandHeader(rand, header)) {
                        headerVerificat = true;
                    } else {
                        throw new IOException("Header gresit - " + numeFisier);
                    }
                } else {
                    randuriFisier.add(rand);
                }
            }
            csvReader.close();
        }
        return randuriFisier;
    }

    private boolean verificaRandHeader(String randCurent, String randHeader) {
        return Objects.equals(randCurent, randHeader);
    }
}
