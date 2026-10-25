package dev.amaan.employees;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
class EmployeeTest {
    Employee make(String name,String email,String salary) {return new Employee(UUID.randomUUID().toString(),name,email,"Engineering",new BigDecimal(salary));}
    @Test void acceptsInternationalNamesAndTrimsFields() {var e=make("  Zoë Müller  "," zoe@example.org ","0.00");assertEquals("Zoë Müller",e.name());assertEquals("zoe@example.org",e.email());}
    @Test void rejectsBlankOrOversizedNames() {assertThrows(IllegalArgumentException.class,()->make(" ","a@b.de","1"));assertThrows(IllegalArgumentException.class,()->make("a".repeat(121),"a@b.de","1"));}
    @Test void rejectsInvalidEmail() {for(String email:new String[]{"bad","a@b","a b@example.org",""}) assertThrows(IllegalArgumentException.class,()->make("Ada",email,"1"));}
    @Test void rejectsNegativeExcessiveOrOverpreciseSalary() {for(String salary:new String[]{"-1","10000000000","1.001"}) assertThrows(IllegalArgumentException.class,()->make("Ada","a@b.de",salary));}
    @Test void rejectsMissingDepartmentAndInvalidId() {assertThrows(IllegalArgumentException.class,()->new Employee(UUID.randomUUID().toString(),"Ada","a@b.de"," ",BigDecimal.ZERO));assertThrows(IllegalArgumentException.class,()->new Employee("bad","Ada","a@b.de","Engineering",BigDecimal.ZERO));}
}
