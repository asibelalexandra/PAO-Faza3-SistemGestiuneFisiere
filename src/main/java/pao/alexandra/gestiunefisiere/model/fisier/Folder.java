package pao.alexandra.gestiunefisiere.model.fisier;

import pao.alexandra.gestiunefisiere.model.grup.Grup;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Folder extends Grup {

    private UUID parinteId;
    private Set<UUID> permisiuni = new HashSet<>();

    public Folder(String nume, Set<UUID> permisiuni) {
        super(nume);
        this.permisiuni.addAll(permisiuni);
    }

    public Folder(UUID parinteId, String nume, Set<UUID> permisiuni) {
        super(nume);
        this.parinteId = parinteId;
        this.permisiuni.addAll(permisiuni);
    }

    public Folder(UUID parinteId, String nume) {
        super(nume);
        this.parinteId = parinteId;
    }

    public Folder(Folder folder) {
        super(folder);
        this.parinteId = folder.parinteId;
        this.permisiuni = folder.permisiuni;
    }

    public UUID getParinteId() {
        return parinteId;
    }

    public Set<UUID> getPermisiuni() {
        return permisiuni;
    }

    @Override
    public String toString() {
        return getNume();
    }

    public void setParentId(UUID uuid) {
        this.parinteId = uuid;
    }
}