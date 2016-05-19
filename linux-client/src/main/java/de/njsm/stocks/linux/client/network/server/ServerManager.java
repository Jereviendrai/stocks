package de.njsm.stocks.linux.client.network.server;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.Update;
import retrofit.*;

import java.io.IOException;
import java.util.logging.Level;

public class ServerManager {

    protected ServerClient backend;

    public ServerManager(Configuration c) {
        try {
            String url = String.format("https://%s:%d/",
                    c.getServerName(),
                    c.getServerPort());

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(c.getClient())
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();

            backend = retrofit.create(ServerClient.class);
        } catch (Exception e) {
            c.getLog().log(Level.SEVERE, "Failed to set up ServerManager: " + e.getMessage());
        }
    }

    public Update[] getUpdates() {
        Call<Update[]> u = backend.getUpdates();

        try {
            Response<Update[]> r = u.execute();

            if (r.isSuccess()) {
                return r.body();
            } else {
                throw new RuntimeException("failed to retrieve updates");
            }



        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}