package bg.sofia.uni.fmi.mjt.id;

public class PlayerIdGenerator implements IdGenerator {
    private int id = 0;

    @Override
    public int getId() {
        return id++;
    }
}
