package bg.sofia.uni.fmi.mjt.server.dispatcher;

import bg.sofia.uni.fmi.mjt.server.game.GameManager;
import bg.sofia.uni.fmi.mjt.server.game.GameManagerImpl;
import bg.sofia.uni.fmi.mjt.server.user.UserManager;
import bg.sofia.uni.fmi.mjt.server.user.UserManagerImpl;

import java.nio.channels.SocketChannel;

public class CommandDispatcherImpl implements CommandDispatcher {

    @Override
    public String dispatch(String input, SocketChannel socket) {
        GameManager gameManager = GameManagerImpl.getInstance();
        UserManager userManager = UserManagerImpl.getInstance();


        return input;
    }
}
