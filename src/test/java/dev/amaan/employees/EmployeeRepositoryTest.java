package dev.amaan.employees;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
class EmployeeRepositoryTest {
    EmployeeRepository repo;
    Employee employee(String name) { return new Employee(UUID.randomUUID().toString(),name,"person@example.org","Engineering",new BigDecimal("2500.00")); }
    @BeforeEach void setup() throws Exception { repo=new EmployeeRepository("jdbc:h2:mem:"+UUID.randomUUID()+";DB_CLOSE_DELAY=-1","sa",""); repo.initialize(); }
    @Test void createReadUpdateDelete() throws Exception {
        var e=employee("Ada Lovelace"); repo.insert(e);
        assertEquals(e,repo.search("Ada").get(0));
        var updated=new Employee(e.id(),e.name(),e.email(),"Product",new BigDecimal("3000.00"));
        assertTrue(repo.update(updated)); assertEquals(updated,repo.search("Product").get(0));
        assertTrue(repo.delete(e.id())); assertTrue(repo.search("").isEmpty()); assertFalse(repo.delete(e.id())); assertFalse(repo.update(e));
    }
    @Test void quotesAndSqlPayloadAreStoredAsData() throws Exception {
        var e=employee("O'Connor'); DROP TABLE employees; --");repo.insert(e);
        assertEquals(e,repo.search("O'Connor").get(0));assertTrue(repo.search("' OR 1=1 --").isEmpty());
        repo.insert(employee("Ada"));assertEquals(2,repo.search("").size());
    }
    @Test void searchTreatsWildcardsLiterally() throws Exception {
        repo.insert(employee("100%_Team!"));repo.insert(employee("Other"));
        assertEquals(1,repo.search("%_").size());assertEquals(1,repo.search("!").size());
    }
    @Test void searchIgnoresCaseAcrossFields() throws Exception {
        repo.insert(employee("Ada"));assertEquals(1,repo.search("ADA").size());assertEquals(1,repo.search("EXAMPLE.ORG").size());assertEquals(1,repo.search("engineering").size());
    }
    @Test void duplicateIdsDoNotOverwriteData() throws Exception {
        var e=employee("Original"); repo.insert(e);assertThrows(java.sql.SQLException.class,()->repo.insert(e));assertEquals(1,repo.search("").size());
    }
    @Test void initializationIsRepeatable() throws Exception { repo.insert(employee("Ada")); repo.initialize();assertEquals(1,repo.search("").size()); }
    @Test void fileDatabasePersistsAcrossRepositoryInstances(@org.junit.jupiter.api.io.TempDir java.nio.file.Path dir) throws Exception {
        String url="jdbc:h2:"+dir.resolve("employees"); var first=new EmployeeRepository(url,"sa",""); first.initialize();var e=employee("Persistent");first.insert(e);
        assertEquals(e,new EmployeeRepository(url,"sa","").search("").get(0));
    }
}
