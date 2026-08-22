package bg.sofia.uni.fmi.mjt.server.user;

import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.exception.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.exception.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.exception.WrongUserCredentialsException;
import bg.sofia.uni.fmi.mjt.hashing.Hasher;
import bg.sofia.uni.fmi.mjt.id.IdGenerator;
import bg.sofia.uni.fmi.mjt.id.PlayerIdGenerator;
import bg.sofia.uni.fmi.mjt.player.Player;
import bg.sofia.uni.fmi.mjt.player.UnoPlayer;
import bg.sofia.uni.fmi.mjt.server.exception.ExceptionLogger;
import bg.sofia.uni.fmi.mjt.server.exception.UserAlreadyLoggedInException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UserManagerImpl implements UserManager {
    private final Map<String, User> users;
    private final Map<SocketChannel, String> sessions;
    private final Map<String, SocketChannel> sessionsByUsername;
    private final Map<SocketChannel, Player> unoPlayers;
    private final IdGenerator playerIdGenerator = new PlayerIdGenerator();

    private static final String SUCCESS_STRING = "Success";
    private static final String USERS_FILE_PATH = "Users.txt";

    private static final UserManagerImpl INSTANCE = new UserManagerImpl();

    public static UserManagerImpl getInstance() {
        return INSTANCE;
    }

    //package-private for tests
    UserManagerImpl() {
        users = new ConcurrentHashMap<>();
        sessions = new ConcurrentHashMap<>();
        sessionsByUsername = new ConcurrentHashMap<>();
        unoPlayers = new ConcurrentHashMap<>();
        try {
            loadUsers();
        } catch (IOException loadException) {
            try {
                ExceptionLogger.logException(loadException);
            } catch (IOException e) {
                System.err.println("Could not load users or write the error log");
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized String register(String username, String password) throws UserAlreadyExistsException {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Username and password cannot be blank");
        }
        if (users.containsKey(username)) {
            throw new UserAlreadyExistsException("User already exists");
        }

        users.put(username, new User(username, Hasher.hash(password)));

        return SUCCESS_STRING;
    }

    @Override
    public synchronized String login(String username, String password, SocketChannel channel)
            throws WrongUserCredentialsException, UserAlreadyLoggedInException {
        if (username == null || password == null || channel == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        User user = users.get(username);
        if (user == null) {
            throw new WrongUserCredentialsException("Wrong username");
        }
        if (this.isLoggedIn(channel) || sessionsByUsername.containsKey(username)) {
            throw new UserAlreadyLoggedInException("User is already logged in");
        }
        if (!user.isPasswordCorrect(Hasher.hash(password))) {
            throw new WrongUserCredentialsException("Wrong password");
        }
        sessions.put(channel, username);
        sessionsByUsername.put(username, channel);
        return SUCCESS_STRING;
    }

    @Override
    public synchronized void logout(SocketChannel channel) {
        if (channel != null) {
            String username = sessions.remove(channel);
            if (username != null) {
                sessionsByUsername.remove(username, channel);
            }
            unoPlayers.remove(channel);
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
            throw new UserNotLoggedInException("User is not logged in");
        }
        if  (sessions.containsKey(channel)) {
            return sessions.get(channel);
        } else throw new UserNotLoggedInException("User is not logged in");
    }

    @Override
    public Player bindSocketToPlayer(SocketChannel channel, String displayName) throws UserNotLoggedInException {
        if (channel == null || displayName == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        if (!this.isLoggedIn(channel)) {
            throw new UserNotLoggedInException("User is not logged in");
        }
        if (unoPlayers.containsKey(channel)) {
            throw new IllegalStateException("A player is already bound to this connection");
        }
        Player player = new UnoPlayer(displayName, playerIdGenerator.getId());
        this.unoPlayers.put(channel, player);
        return player;
    }

    @Override
    public Player getPlayer(SocketChannel channel) throws UnoUserException {
        if  (channel == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        if (!isLoggedIn(channel)) {
            throw new UnoUserException("User is not logged in");
        }
        if  (unoPlayers.containsKey(channel)) {
            return unoPlayers.get(channel);
        }
        throw new UnoUserException("User not created");
    }

    @Override
    public void unbindPlayer(SocketChannel channel) {
        if  (channel == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        unoPlayers.remove(channel);
    }

    @Override
    public void save() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(USERS_FILE_PATH), StandardCharsets.UTF_8)) {
            for (Map.Entry<String, User> entry : users.entrySet()) {
                writer.write(entry.getValue().name() + ',' + entry.getValue().password() + System.lineSeparator());
            }
        }
    }

    @Override
    public Set<SocketChannel> getConnectedSockets(Collection<Integer> playerIds) {
        if (playerIds == null) {
            throw new IllegalArgumentException("playerIds cannot be null");
        }
        Set<SocketChannel> sockets = new HashSet<>();

        for (Map.Entry<SocketChannel, Player> entry : unoPlayers.entrySet()) {
            if (isLoggedIn(entry.getKey()) && playerIds.contains(entry.getValue().getId())) {
                sockets.add(entry.getKey());
            }
        }

        return sockets;
    }

    private void loadUsers() throws IOException {
        Path usersFile = Path.of(USERS_FILE_PATH);
        if (Files.notExists(usersFile)) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(usersFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                users.put(parts[0], new User(parts[0], parts[1]));
            }
        }
    }
}
