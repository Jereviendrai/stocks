package de.njsm.stocks.linux.client.frontend.cli;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.frontend.MainHandler;
import de.njsm.stocks.linux.client.frontend.cli.commands.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class CliMainHandler implements MainHandler {

    protected List<Command> clients;
    protected Configuration c;

    public CliMainHandler(Configuration c) {
        this.c = c;

        clients = new LinkedList<>();
        clients.add(new HelpCommand());
        clients.add(new RefreshCommand(c));
        clients.add(new UserCommand(c));
        clients.add(new LocationCommand(c));
    }

    @Override
    public void run() {
        boolean endRequested = false;
        InputReader source = new InputReader(System.in);

        while (! endRequested) {
            System.out.print("stocks $ ");
            String command = source.next();

            if (command.equals("quit")) {
                endRequested = true;
            } else if (command.equals("")) {

            } else {
                forwardCommand(command);
            }
        }
    }

    public void forwardCommand(String command) {
        List<String> commandList = parseCommand(command);
        boolean commandFound = false;

        for (Command c : clients) {
            if (c.canHandle(commandList.get(0))){
                c.handle(commandList);
                commandFound = true;
                break;
            }
        }

        if (! commandFound){
            System.out.println("Unknown command: " + commandList.get(0));
        }
    }

    public List<String> parseCommand(String command) {
        String[] commands = command.split(" ");
        return Arrays.asList(commands);
    }
}
