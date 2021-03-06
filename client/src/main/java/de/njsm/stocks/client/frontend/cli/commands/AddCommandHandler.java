package de.njsm.stocks.client.frontend.cli.commands;

import de.njsm.stocks.client.data.Food;
import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.data.FoodItem;
import de.njsm.stocks.client.data.Location;
import de.njsm.stocks.client.exceptions.SelectException;

import java.text.ParseException;
import java.util.Date;

public class AddCommandHandler extends CommandHandler {

    protected String location;

    public AddCommandHandler(Configuration c) {
        command = "add";
        description = "Add a food item";
        this.c = c;

    }

    @Override
    public void handle(Command command) {
        String word = command.next();

        if (command.hasArg('l')) {
            location = command.getParam('l');
        } else {
            location = "";
        }

        if (word.equals("help")) {
            printHelp();
        } else if (word.equals("")) {
            addFood(command);
        } else {
            addFood(command, word);
        }
    }

    @Override
    public void printHelp() {
        String text = "Add food item to the store\n" +
                "\t--d date  \t\t\tdate: Eat before this date\n" +
                "\t--l string\t\t\tlocation: Where to put the food\n\n";

        System.out.print(text);

    }

    public void addFood(Command command) {
        String type = c.getReader().next("What to add?  ");
        addFood(command, type);
    }


    public void addFood(Command command, String type) {
        try {
            Food[] foods = c.getDatabaseManager().getFood(type);
            int foodId = FoodCommandHandler.selectFood(foods, type);
            int locId = selectLocation(foodId);

            Date date = getDate(command);

            FoodItem item = new FoodItem();
            item.ofType = foodId;
            item.storedIn = locId;
            item.buys = c.getUserId();
            item.registers = c.getDeviceId();
            item.eatByDate = date;

            c.getServerManager().addItem(item);
            (new RefreshCommandHandler(c, false)).refresh();
        } catch (SelectException e) {
            System.out.println(e.getMessage());
        }
    }

    protected Date getDate(Command c) {
        try {
            if (c.hasArg('d')) {
                return c.getParamDate('d');
            } else {
                return this.c.getReader().nextDate("Eat before:  ");
            }
        } catch (ParseException e) {
            return this.c.getReader().nextDate("Eat before:  ");
        }
    }

    protected int selectLocation(int foodId) throws SelectException {
        if (! location.equals("")) {
            Location[] inputLoc = c.getDatabaseManager().getLocations(location);
            return LocationCommandHandler.selectLocation(inputLoc, location);
        }
        Location[] l = c.getDatabaseManager().getLocationsForFoodType(foodId);

        if (l.length == 0) {
            return selectLocation();
        } else {
            int result;
            System.out.println("Some food already in:");
            for (Location loc : l) {
                System.out.println("\t" + loc.id + ": " + loc.name);
            }
            result = c.getReader().nextInt("Choose one or type -1 for new location", l[0].id);

            if (result == -1) {
                return selectLocation();
            } else {
                return result;
            }
        }
    }

    protected int selectLocation() throws SelectException {
        String location = c.getReader().next("Where is it stored?  ");
        Location[] locs = c.getDatabaseManager().getLocations(location);
        return LocationCommandHandler.selectLocation(locs, location);
    }

}
