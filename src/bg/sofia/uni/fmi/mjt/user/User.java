package bg.sofia.uni.fmi.mjt.user;

public record User(String name, String password) {

    public User {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
    }

    boolean isPasswordCorrect(String password) {
        return password.equals(this.password);
    }
}
