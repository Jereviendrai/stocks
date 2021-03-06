package de.njsm.stocks.server.internal.auth;

import de.njsm.stocks.server.internal.auth.HttpsUserContextFactory;
import de.njsm.stocks.server.internal.auth.Principals;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class HttpsUserContextFactoryTest {

    @Test
    public void testValidNames() {
        List<String> inputList = new ArrayList<>();
        inputList.add("John");
        inputList.add("mike");
        inputList.add("fdsaiofpra");
        inputList.add("Henry8th");
        inputList.add("123flowerpower");
        inputList.add(">> Master <<");
        inputList.add("!\"\\^,.;:§%&/()?+*#'-_<>|");

        for (String input : inputList) {
            Assert.assertTrue(HttpsUserContextFactory.isNameValid(input));
        }
    }

    @Test
    public void testInvalidNames() {
        List<String> inputList = new ArrayList<>();
        inputList.add("Adversary$1");
        inputList.add("AdversaryDevice$1");
        inputList.add("CN=John");
        inputList.add("==> Fool <==");

        for (String input : inputList) {
            Assert.assertFalse(HttpsUserContextFactory.isNameValid(input));
        }
    }

    @Test
    public void testParsing() {
        String input = "/O=stocks/OU=User/CN=jan$1$laptop$2";

        HttpsUserContextFactory uut = new HttpsUserContextFactory();

        Principals p = uut.parseSubjectName(input);
        Assert.assertEquals("jan", p.getUsername());
        Assert.assertEquals("laptop", p.getDeviceName());
        Assert.assertEquals(1, p.getUid());
        Assert.assertEquals(2, p.getDid());
    }

    @Test
    public void testBasicParsing() {
        String input = "/CN=Jan$1$Handy$2";

        HttpsUserContextFactory uut = new HttpsUserContextFactory();

        Principals p = uut.parseSubjectName(input);

        Assert.assertEquals("Jan", p.getUsername());
        Assert.assertEquals("Handy", p.getDeviceName());
        Assert.assertEquals(1, p.getUid());
        Assert.assertEquals(2, p.getDid());
    }

    @Test
    public void testEmptyName() {
        String input = "/CN=$1$$1";

        HttpsUserContextFactory uut = new HttpsUserContextFactory();

        Principals p = uut.parseSubjectName(input);

        Assert.assertEquals("", p.getUsername());
        Assert.assertEquals("", p.getDeviceName());
        Assert.assertEquals(1, p.getUid());
        Assert.assertEquals(1, p.getDid());
    }

    @Test(expected = SecurityException.class)
    public void testMalformed() {
        String input = "/CN=$1$1";

        HttpsUserContextFactory uut = new HttpsUserContextFactory();

        uut.parseSubjectName(input);

    }

    @Test(expected = SecurityException.class)
    public void tooManyDollars() {
        String input = "/CN=omg$4$device$5$tooMuch";

        HttpsUserContextFactory uut = new HttpsUserContextFactory();

        Principals p = uut.parseSubjectName(input);

        Assert.assertEquals("", p.getUsername());
        Assert.assertEquals("", p.getDeviceName());
        Assert.assertEquals(1, p.getUid());
        Assert.assertEquals(1, p.getDid());
    }
}
