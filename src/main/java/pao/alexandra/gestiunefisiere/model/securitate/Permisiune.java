package pao.alexandra.gestiunefisiere.model.securitate;

import java.util.HashSet;
import java.util.Set;

public enum Permisiune {
    CITIRE, SCRIERE, STERGERE, EDITARE, FARA_PERMISIUNI;

    public static Set<Permisiune> getDefault() {
        Set<Permisiune> setDefault = new HashSet<>();
        setDefault.add(Permisiune.FARA_PERMISIUNI);
        return setDefault;
    }

    public static Set<Permisiune> administrator() {
        Set<Permisiune> setDefault = new HashSet<>();
        setDefault.add(Permisiune.CITIRE);
        setDefault.add(Permisiune.SCRIERE);
        setDefault.add(Permisiune.STERGERE);
        setDefault.add(Permisiune.EDITARE);
        return setDefault;
    }
}
