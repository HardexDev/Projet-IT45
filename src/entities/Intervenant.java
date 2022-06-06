package entities;

public class Intervenant {
    private int id;
    private Competence competence;
    private Specialite specialite;
    private int quota;

    public Intervenant(int id, Competence competence, Specialite specialite, int quota) {
        this.id = id;
        this.competence = competence;
        this.specialite = specialite;
        this.quota = quota;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getQuota() {
        return quota;
    }

    public void setQuota(int quota) {
        this.quota = quota;
    }

    @Override
    public String toString() {
        return "Intervenant{" +
                "id=" + id +
                ", competence=" + competence +
                ", specialite=" + specialite +
                ", quota=" + quota +
                '}';
    }
}
