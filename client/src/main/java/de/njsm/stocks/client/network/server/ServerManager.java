package de.njsm.stocks.client.network.server;

import de.njsm.stocks.client.data.*;
import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.network.HttpClientFactory;
import retrofit.*;

import java.io.IOException;
import java.util.logging.Level;

public class ServerManager {

    protected ServerClient backend;
    protected Configuration c;

    public ServerManager(Configuration c) {
        try {
            String url = String.format("https://%s:%d/",
                    c.getServerName(),
                    c.getServerPort());

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(HttpClientFactory.getClient())
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();

            backend = retrofit.create(ServerClient.class);
            this.c = c;
        } catch (Exception e) {
            // TODO Log
        }
    }

    public Update[] getUpdates() {
        Call<Update[]> call = backend.getUpdates();

        try {
            Response<Update[]> r = call.execute();

            if (r.isSuccess()) {
                return r.body();
            } else {
                // TODO Log
            }
        } catch (IOException e) {
            error(e);
        }
        return new Update[0];
    }

    public User[] getUsers() {
        Call<User[]> call = backend.getUsers();

        try {
            Response<User[]> r = call.execute();

            if (r.isSuccess()) {
                return r.body();
            } else {
                // TODO Log
            }
        } catch (IOException e) {
            error(e);
        }
        return new User[0];
    }

    public void addUser(User u) {
        Call<Void> call = backend.addUser(u);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccess()) {
                // TODO Log
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public void removeUser(User u) {
        Call<Void> call = backend.removeUser(u);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccess()) {
                // TODO Log
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public UserDevice[] getDevices() {
        Call<UserDevice[]> u = backend.getDevices();

        try {
            Response<UserDevice[]> r = u.execute();

            if (r.isSuccess()) {
                return r.body();
            } else {
// TODO Log
            }
        } catch (IOException e) {
            error(e);
        }
        return new UserDevice[0];
    }

    public Ticket addDevice(UserDevice d) {
        Call<Ticket> call = backend.addDevice(d);

        try {
            Response<Ticket> r = call.execute();

            if (!r.isSuccess()) {
// TODO Log
            } else {
                return r.body();
            }
        } catch (IOException e) {
            error(e);
        }
        return new Ticket();
    }

    public void removeDevice(UserDevice d) {
        Call<Void> call = backend.removeDevice(d);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccess()) {
// TODO Log
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public Location[] getLocations() {
        Call<Location[]> u = backend.getLocations();

        try {
            Response<Location[]> r = u.execute();

            if (r.isSuccess()) {
                return r.body();
            } else {
// TODO Log
            }
        } catch (IOException e) {
            error(e);
        }
        return new Location[0];
    }

    public void addLocation(Location l) {
        Call<Void> call = backend.addLocation(l);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccess()) {
// TODO Log
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public void removeLocation(Location l) {
        Call<Void> call = backend.removeLocation(l);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccess()) {
// TODO Log
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public void renameLocation(Location l, String newName) {
        Call<Void> call = backend.renameLocation(l, newName);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccess()) {
// TODO Log
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public Food[] getFood() {
        Call<Food[]> u = backend.getFood();

        try {
            Response<Food[]> r = u.execute();

            if (r.isSuccess()) {
                return r.body();
            } else {
// TODO Log
            }
        } catch (IOException e) {
            error(e);
        }
        return new Food[0];
    }

    public void addFood(Food f) {
        Call<Void> call = backend.addFood(f);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccess()) {
// TODO Log
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public void removeFood(Food f) {
        Call<Void> call = backend.removeFood(f);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccess()) {
// TODO Log
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public void renameFood(Food f, String newName) {
        Call<Void> call = backend.renameFood(f, newName);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccess()) {
// TODO Log
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public FoodItem[] getFoodItems() {
        Call<FoodItem[]> u = backend.getFoodItems();

        try {
            Response<FoodItem[]> r = u.execute();

            if (r.isSuccess()) {
                return r.body();
            } else {
// TODO Log
            }
        } catch (IOException e) {
            error(e);
        }
        return new FoodItem[0];
    }

    public void addItem(FoodItem f) {
        Call<Void> call = backend.addFoodItem(f);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccess()) {
// TODO Log
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public void removeItem(FoodItem f) {
        Call<Void> call = backend.removeFoodItem(f);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccess()) {
// TODO Log
            }
        } catch (IOException e) {
            error(e);
        }
    }

    public void move(FoodItem f, int newLoc) {
        Call<Void> call = backend.moveFoodItem(f, newLoc);

        try {
            Response<Void> r = call.execute();

            if (!r.isSuccess()) {
// TODO Log
            }
        } catch (IOException e) {
            error(e);
        }
    }

    protected void error(IOException e) {
// TODO Log
    }

}
