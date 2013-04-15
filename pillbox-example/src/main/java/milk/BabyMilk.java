package milk;

public class BabyMilk {
    private final Protein protein;
    private final Fat fat;
    private final LinoleicAcid linoleicAcid;

    public BabyMilk(Protein protein, Fat fat, LinoleicAcid linoleicAcid) {
        this.protein = protein;
        this.fat = fat;
        this.linoleicAcid = linoleicAcid;
    }

    public Protein getProtein() {
        return protein;
    }

    public Fat getFat() {
        return fat;
    }

    public LinoleicAcid getLinoleicAcid() {
        return linoleicAcid;
    }
}
