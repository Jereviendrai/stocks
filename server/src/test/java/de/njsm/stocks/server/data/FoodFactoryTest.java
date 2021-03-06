package de.njsm.stocks.server.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class FoodFactoryTest {

    private int idReference;
    private String nameReference;

    private int resultSetSize;

    private FoodFactory uut;
    private ResultSet rs;

    @Before
    public void setup() throws SQLException {
        idReference = 1;
        nameReference = "Carrot";

        resultSetSize = 3;

        rs = setupResultSet();
        uut = new FoodFactory();

    }

    @Test
    public void testSingleCreation() throws SQLException {
        Data rawResult = uut.createData(rs);

        assertReferenceEquality(rawResult);
    }

    @Test
    public void testBulkCreation() throws SQLException {
        List<Data> resultList = uut.createDataList(rs);

        Assert.assertEquals(resultSetSize, resultList.size());
        for (Data rawResult : resultList) {
            assertReferenceEquality(rawResult);
        }
    }

    @Test
    public void testGetQuery() {
        String expectedQuery = "SELECT * FROM Food";

        String actualQuery = uut.getQuery();

        Assert.assertEquals(expectedQuery, actualQuery);
    }

    private ResultSet setupResultSet() throws SQLException {
        ResultSet rs = Mockito.mock(ResultSet.class);

        Answer<Boolean> a = new Answer<Boolean>() {
            private int callCounter;

            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                callCounter++;
                return callCounter <= resultSetSize;
            }
        };
        Mockito.when(rs.next()).thenAnswer(a);

        Mockito.when(rs.getInt("ID")).thenReturn(idReference);
        Mockito.when(rs.getString("name")).thenReturn(nameReference);
        return rs;
    }

    private void assertReferenceEquality(Data rawResult) {
        Assert.assertTrue(rawResult instanceof Food);
        Food result = (Food) rawResult;
        Assert.assertEquals(idReference, result.id);
        Assert.assertEquals(nameReference, result.name);
    }
}
