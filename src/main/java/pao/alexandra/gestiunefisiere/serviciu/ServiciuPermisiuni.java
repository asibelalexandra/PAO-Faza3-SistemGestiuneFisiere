package pao.alexandra.gestiunefisiere.serviciu;

import pao.alexandra.gestiunefisiere.model.fisier.Folder;
import pao.alexandra.gestiunefisiere.model.securitate.GrupPermisiune;
import pao.alexandra.gestiunefisiere.model.securitate.Permisiune;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ServiciuPermisiuni {

    private Map<UUID, GrupPermisiune> permisiuni = new HashMap<>();

    public Map<UUID, GrupPermisiune> getPermisiuni() {
        return permisiuni;
    }

    public void setPermisiuni(Map<UUID, GrupPermisiune> permisiuni) {
        this.permisiuni = permisiuni;
    }

    public UUID adaugaGrupPermisiuneNoua(UUID id, String numePermisiune, UUID idUtilizator, Set<Permisiune> permisiuni) {
        GrupPermisiune permisiuneNoua = new GrupPermisiune(numePermisiune, idUtilizator, permisiuni);
        this.permisiuni.put(id, permisiuneNoua);
        return id;
    }

    public UUID adaugaUtilizatorLaGrupPermisiune(UUID id, UUID idAdmin, UUID idUtilizator) {
        this.permisiuni.get(id).adaugaUtilizator(idAdmin, idUtilizator);
        return id;
    }

    public boolean verificaPermisiune(UUID utilizator, Folder folderVerificat, Permisiune permisiune) {
        return folderVerificat
                .getPermisiuni()
                .stream()
                .anyMatch(idPermisiuni -> permisiuni.get(idPermisiuni).verificaAccesUtilizator(utilizator, permisiune));
    }
}
