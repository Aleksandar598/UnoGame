package bg.sofia.uni.fmi.mjt.server.user;

import bg.sofia.uni.fmi.mjt.exception.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.exception.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.exception.WrongUserCredentialsException;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public interface UserManager {

    String register(String username, String password) throws UserAlreadyExistsException;

    String login(String username, String password, SocketChannel channel) throws WrongUserCredentialsException;

    void logout(SocketChannel channel);

    boolean isLoggedIn(SocketChannel channel);

    String getUsername(SocketChannel channel) throws UserNotLoggedInException;

    void save() throws IOException;
}
