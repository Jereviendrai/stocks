package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.User;
import de.njsm.stocks.linux.client.frontend.cli.InputReader;

import java.util.List;

public class UserRemoveCommand extends Command {

    public UserRemoveCommand(Configuration c) {
        this.c = c;
        this.command = "remove";
        this.description = "Remove a user";
    }

    @Override
    public void handle(List<String> commands) {
        if (commands.size() == 1) {
            removeUser(commands.get(0));
        } else {
            removeUser();
        }
    }

    public void removeUser() {
        InputReader scanner = new InputReader(System.in);
        System.out.print("Remove a user\nName: ");
        String name = scanner.next();
        removeUser(name);
    }

    public void removeUser(String name) {
        User[] users = c.getDatabaseManager().getUsers(name);
        int id = UserCommand.selectUser(users, name);

        for (User u : users) {
            if (u.id == id){
                c.getServerManager().removeUser(u);
                (new RefreshCommand(c)).refreshUsers();
            }
        }
    }
}