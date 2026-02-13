package bg.sofia.uni.fmi.mjt.id;

public class CardIdGenerator implements IdGenerator {
    int id = 1;

    @Override
    public int getId() {
        return id++;
    }
}
