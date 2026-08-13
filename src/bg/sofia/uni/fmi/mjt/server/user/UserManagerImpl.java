package bg.sofia.uni.fmi.mjt.server.user;

import bg.sofia.uni.fmi.mjt.exception.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.exception.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.exception.WrongUserCredentialsException;
import bg.sofia.uni.fmi.mjt.hashing.Hasher;

import java.io.*;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserManagerImpl implements UserManager {
    Map<String, User> users;
    Map<SocketChannel, String>  sessions;
    private static final String SUCCESS_STRING = "Success";
    private static final String USERS_FILE_PATH = "Users.txt";

    private static final UserManagerImpl INSTANCE = new UserManagerImpl();

    public static UserManagerImpl getInstance() {
        return INSTANCE;
    }

    private UserManagerImpl() {
        users = new ConcurrentHashMap<>();
        sessions = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized String register(String username, String password) throws UserAlreadyExistsException {
        if (users.containsKey(username)) {
            throw new UserAlreadyExistsException("User already exists");
        }

        users.put(username, new User(username, Hasher.hash(password)));

        return SUCCESS_STRING;
    }

    @Override
    public synchronized String login(String username, String password, SocketChannel channel) throws WrongUserCredentialsException {
        if (username == null || password == null || channel == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        User user = users.get(username);
        if (user == null) {
            throw new WrongUserCredentialsException("Wrong username");
        }
        if (!user.isPasswordCorrect(Hasher.hash(password))) {
            throw new WrongUserCredentialsException("Wrong password");
        }
        sessions.put(channel, username);
        return SUCCESS_STRING;
    }

    @Override
    public void logout(SocketChannel channel) {
        if (channel != null) {
            sessions.remove(channel);
        }

    }

    @Override
    public boolean isLoggedIn(SocketChannel channel) {
        if (channel == null) {
            return false;
        }
        return sessions.containsKey(channel);
    }

    @Override
    public String getUsername(SocketChannel channel) throws UserNotLoggedInException {
        if (channel == null) {
            return null;
        }
        if  (sessions.containsKey(channel)) {
            return sessions.get(channel);
        }
        else throw new UserNotLoggedInException("User is not logged in");
    }

    @Override
    public void save() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(USERS_FILE_PATH), StandardCharsets.UTF_8)) {
            for (Map.Entry<String, User> entry : users.entrySet()) {
                writer.write(entry.getValue().name() + ',' + entry.getValue().password() + System.lineSeparator());
            }
        }
    }

    private void load_users() throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Path.of(USERS_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                users.put(parts[0], new User(parts[0], parts[1]));
            }
        }
    }
}
