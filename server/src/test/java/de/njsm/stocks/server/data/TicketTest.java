package de.njsm.stocks.server.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TicketTest {

    private int idReference;
    private String ticketReference;
    private String pemReference;

    private Ticket uut;

    @Before
    public void setup() {
        idReference = 1;
        ticketReference = Ticket.generateTicket();
        pemReference = "my pem file";

        uut = new Ticket(idReference, ticketReference, pemReference);
    }

    @Test
    public void testConstructor() {
        Assert.assertEquals(idReference, uut.deviceId);
        Assert.assertEquals(ticketReference, uut.ticket);
        Assert.assertEquals(pemReference, uut.pemFile);
    }

    @Test
    public void testTicketCreation() {
        String ticket = Ticket.generateTicket();

        Assert.assertEquals(Ticket.TICKET_LENGTH, ticket.length());
        for (int i = 0; i < ticket.length(); i++) {
            Assert.assertTrue(Character.isLetterOrDigit(ticket.charAt(i)));
        }
    }
}
