package milk;

public class ManualMilk {
    private Fat fat;
    private Protein protein;
    private LinoleicAcid linoleicAcid;

    public void setFat(Fat fat) {
        this.fat = fat;
    }

    public void setProtein(Protein protein) {
        this.protein = protein;
    }

    public void setLinoleicAcid(LinoleicAcid linoleicAcid) {
        this.linoleicAcid = linoleicAcid;
    }

    public Fat getFat() {
        return fat;
    }

    public Protein getProtein() {
        return protein;
    }

    public LinoleicAcid getLinoleicAcid() {
        return linoleicAcid;
    }
}
