package bg.sofia.uni.fmi.mjt.id;

import java.util.concurrent.atomic.AtomicInteger;

public class GameIdgenerator implements IdGenerator {
    private final AtomicInteger id = new AtomicInteger(1);

    @Override
    public int getId() {
        return id.getAndIncrement();
    }
}
