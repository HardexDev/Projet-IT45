package entities;

public class Mission {
    private int id;
    private int jour;
    private int heure_debut;
    private int heure_fin;
    private Competence competence;
    private Specialite specialite;

    public Mission(int id, int jour, int heure_debut, int heure_fin, Competence competence, Specialite specialite) {
        this.id = id;
        this.jour = jour;
        this.heure_debut = heure_debut;
        this.heure_fin = heure_fin;
        this.competence = competence;
        this.specialite = specialite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJour() {
        return jour;
    }

    public void setJour(int jour) {
        this.jour = jour;
    }

    public int getHeure_debut() {
        return heure_debut;
    }

    public void setHeure_debut(int heure_debut) {
        this.heure_debut = heure_debut;
    }

    public int getHeure_fin() {
        return heure_fin;
    }

    public void setHeure_fin(int heure_fin) {
        this.heure_fin = heure_fin;
    }

    public Competence getCompetence() {
        return competence;
    }

    public void setCompetence(Competence competence) {
        this.competence = competence;
    }

    public Specialite getSpecialite() {
        return specialite;
    }

    public void setSpecialite(Specialite specialite) {
        this.specialite = specialite;
    }

    @Override
    public String toString() {
        return "Mission{" +
                "id=" + id +
                ", jour=" + jour +
                ", heure_debut=" + heure_debut +
                ", heure_fin=" + heure_fin +
                ", competence=" + competence +
                ", specialite=" + specialite +
                '}';
    }
}
