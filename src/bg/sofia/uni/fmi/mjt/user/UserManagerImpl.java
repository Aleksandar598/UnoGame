package bg.sofia.uni.fmi.mjt.user;

import bg.sofia.uni.fmi.mjt.exception.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.exception.WrongUserCredentialsException;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class UserManagerImpl implements UserManager {
    Map<String, User> users;
    Map<SocketChannel, String>  sessions;
    private static final String SUCESS_STRING = "Success";

    public UserManagerImpl() {
        users = new HashMap<>();
        sessions = new HashMap<>();
    }

    @Override
    public synchronized String register(String username, String password) throws UserAlreadyExistsException {
        if (users.containsKey(username)) {
            throw new UserAlreadyExistsException("User already exists");
        }

        users.put(username, new User(username, password));

        return SUCESS_STRING;
    }

    @Override
    public synchronized String login(String username, String password, SocketChannel channel) throws WrongUserCredentialsException {
        if (!users.containsKey(username)) {
            throw new WrongUserCredentialsException("wrong username");
        }
        if (!users.get(username).isPasswordCorrect(password)) {
            throw new WrongUserCredentialsException("wrong password");
        }

        sessions.put(channel, username);
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public void logout(SocketChannel channel) {
        if (channel == null) {

        }

    }

    @Override
    public boolean isLoggedIn(SocketChannel channel) {
        return false;
    }

    @Override
    public String getUsername(SocketChannel channel) {
        return "";
    }

    @Override
    public void save() {

    }
}
