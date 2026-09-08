package bg.sofia.uni.fmi.mjt.server.dispatcher;

import bg.sofia.uni.fmi.mjt.command.serverCommand.Command;
import bg.sofia.uni.fmi.mjt.server.exception.UnknownCommandException;

interface ServerCommandCreator {
    Command create(String input) throws UnknownCommandException;
}
