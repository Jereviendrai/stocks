package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.data.Food;

public class FoodAddCommandHandler extends CommandHandler {

    public FoodAddCommandHandler(Configuration c) {
        this.c = c;
        this.command = "add";
        this.description = "Add a new food type";
    }

    @Override
    public void handle(Command command) {
        if (command.hasNext()) {
            addFood(command.next());
        } else {
            addFood();
        }
    }

    public void addFood() {
        String name = c.getReader().nextName("Creating a new food type\nName: ");
        addFood(name);
    }

    public void addFood(String name) {
        Food f = new Food();
        f.name = name;

        c.getServerManager().addFood(f);

        (new RefreshCommandHandler(c, false)).refresh();
    }
}
