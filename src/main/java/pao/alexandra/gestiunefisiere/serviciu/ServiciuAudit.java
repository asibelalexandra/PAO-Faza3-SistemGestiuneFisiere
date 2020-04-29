package pao.alexandra.gestiunefisiere.serviciu;

import java.io.*;
import java.sql.Timestamp;

public class ServiciuAudit {
    private String HEADER_AUDIT = "nume_actiune,timestamp,thread_name";

    private String FOLDER_AUDIT = "audit";
    private String FISIER_AUDIT = "audit.csv";

    public ServiciuAudit() throws IOException {
        genereazaFisierAudit(FOLDER_AUDIT, FISIER_AUDIT);
    }

    public void genereazaAudit(String actiune, Timestamp timestamp, String numeThread) throws IOException {
        String caleFinala = (System.getProperty("user.dir") + File.separator + FOLDER_AUDIT);
        File caleFolder = new File(caleFinala);
        File fisierCSV = new File(caleFolder.getPath(), FISIER_AUDIT);
        BufferedWriter writer = new BufferedWriter(new FileWriter(fisierCSV, true));
        writer.newLine();
        writer.write(actiune + "," + timestamp.toString() + "," + numeThread);
        writer.close();
    }

    private void genereazaFisierAudit(String cale, String numeFisier) throws IOException {
        String caleFinala = (System.getProperty("user.dir") + File.separator + cale);
        File caleFolder = new File(caleFinala);
        File fisierCSV = new File(caleFolder.getPath(), numeFisier);
        caleFolder.mkdirs();
        if (!fisierCSV.exists()) {
            fisierCSV.createNewFile();
            FileWriter writer = new FileWriter(fisierCSV);
            writer.append(HEADER_AUDIT);
            writer.flush();
            writer.close();
        }
    }
}
