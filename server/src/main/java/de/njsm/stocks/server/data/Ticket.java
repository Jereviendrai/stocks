package de.njsm.stocks.server.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.xml.bind.annotation.XmlRootElement;
import java.security.SecureRandom;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement
public class Ticket extends Data {
    public int deviceId;
    public String ticket;
    public String pemFile;

    @JsonIgnore
    public static String generateTicket() {
        int ticket_length = 64;         // this conforms to database ticket size
        SecureRandom rng = new SecureRandom();
        byte[] content = new byte[ticket_length];

        for (int i = 0; i < ticket_length; i++){
            byte b;
            do {
                b = (byte) rng.nextInt();
            } while (!Character.isLetterOrDigit(b));
            content[i] = b;
        }
        return new String(content);
    }
}