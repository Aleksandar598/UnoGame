package bg.sofia.uni.fmi.mjt.server.user;

import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.exception.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.exception.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.exception.WrongUserCredentialsException;
import bg.sofia.uni.fmi.mjt.player.Player;
import bg.sofia.uni.fmi.mjt.server.exception.UserAlreadyLoggedInException;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Set;

public interface UserManager {

    String register(String username, String password) throws UserAlreadyExistsException;

    String login(String username, String password, SocketChannel channel) throws WrongUserCredentialsException,
                                                                                UserAlreadyLoggedInException;

    void logout(SocketChannel channel);

    boolean isLoggedIn(SocketChannel channel);

    String getUsername(SocketChannel channel) throws UserNotLoggedInException;

    Player bindSocketToPlayer(SocketChannel channel, String displayName) throws UserNotLoggedInException;

    Player getPlayer(SocketChannel channel) throws UserNotLoggedInException, UnoUserException;

    void unbindPlayer(SocketChannel channel);

    void save() throws IOException;

    Set<SocketChannel> getConnectedSockets(Collection<Integer> playerIds);

}
