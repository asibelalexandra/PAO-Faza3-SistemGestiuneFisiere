package pao.alexandra.gestiunefisiere.serviciu;

import java.util.*;

public class ServiciuGrupUtilizatori {

    private Map<UUID, List<UUID>> utilizatori = new HashMap<>();

    public UUID adaugaGrup(UUID id, UUID admin) {
        utilizatori.put(id, Collections.singletonList(admin));
        return id;
    }

    public UUID adaugaUtilizatorLaGrup(UUID id, UUID utilizator) {
        utilizatori.get(id).add(utilizator);
        return id;
    }
}
