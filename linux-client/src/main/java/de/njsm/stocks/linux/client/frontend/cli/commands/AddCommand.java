package de.njsm.stocks.linux.client.frontend.cli.commands;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.Food;
import de.njsm.stocks.linux.client.data.FoodItem;
import de.njsm.stocks.linux.client.data.Location;
import de.njsm.stocks.linux.client.exceptions.SelectException;
import de.njsm.stocks.linux.client.frontend.cli.InputReader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddCommand extends Command {

    public AddCommand(Configuration c) {
        command = "add";
        description = "Add a food item";
        this.c = c;

    }

    @Override
    public void handle(List<String> commands) {
        if (! commands.isEmpty() &&
            commands.get(0).equals("help")) {
            printHelp();
        } else {
            addFood();
        }
    }


    public void addFood() {
        try {
            InputReader scanner = new InputReader(System.in);
            System.out.print("What to add?  ");
            String type = scanner.next();
            Food[] foods = c.getDatabaseManager().getFood(type);
            int foodId = FoodCommand.selectFood(foods, type);
            int locId = selectLocation(foodId);

            System.out.print("Eat before:  ");
            Date date = scanner.nextDate();

            FoodItem item = new FoodItem();
            item.ofType = foodId;
            item.storedIn = locId;
            item.buys = c.getUserId();
            item.registers = c.getDeviceId();
            item.eatByDate = date;

            c.getServerManager().addItem(item);
            (new RefreshCommand(c)).refreshFoodItems();
        } catch (SelectException e) {
            System.out.println(e.getMessage());
        }
    }

    protected int selectLocation(int foodId) throws SelectException {
        InputReader scanner = new InputReader(System.in);
        Location[] l = c.getDatabaseManager().getLocationsForFoodType(foodId);

        if (l.length == 0) {
            return selectLocation();
        } else {
            int result;
            System.out.println("Some food already in:");
            for (Location loc : l) {
                System.out.println("\t" + loc.id + ": " + loc.name);
            }
            System.out.print("Choose one or type -1 for new location (default " + l[0].id + "): ");
            result = scanner.nextInt(l[0].id);

            if (result == -1) {
                return selectLocation();
            } else {
                return result;
            }
        }
    }

    protected int selectLocation() throws SelectException {
        InputReader scanner = new InputReader(System.in);
        System.out.print("Where is it stored?  ");
        String location = scanner.next();
        Location[] locs = c.getDatabaseManager().getLocations(location);
        return LocationCommand.selectLocation(locs, location);
    }

}
