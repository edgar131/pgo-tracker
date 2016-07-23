package pokemon.domain;

public enum Quadrant {
    NW(-1, 1),
    NE(1, 1),
    SW(-1, -1),
    SE(1, -1);

    private int latAdd;
    private int lonAdd;

    Quadrant(Integer latAdd, Integer lonAdd){
        this.latAdd = latAdd;
        this.lonAdd = lonAdd;
    };

    public int getLatAdd() {
        return latAdd;
    }

    public int getLonAdd() {
        return lonAdd;
    }
}
