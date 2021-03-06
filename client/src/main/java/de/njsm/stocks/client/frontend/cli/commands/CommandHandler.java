package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;

public abstract class CommandHandler {

    protected String command;
    protected String description;
    protected Configuration c;

    public abstract void handle(Command command);

    public void printHelp() {
        System.out.println("No help page found...");
    }

    public boolean canHandle(String command) {
        return this.command.equals(command);
    }

    @Override
    public String toString() {
        int colWidth = 15;
        int count = colWidth - command.length();
        StringBuilder buf = new StringBuilder();
        buf.append(command);

        for (int i = 0; i < count; i++){
            buf.append(" ");
        }

        buf.append(description);
        return buf.toString();
    }

}
