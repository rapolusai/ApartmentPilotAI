package com.rapolus.apartmentpilotai.data;
import org.junit.Test;
import static org.junit.Assert.*;

public class RegistrationPolicyTest {
    @Test public void apartmentProfileRequiresAllBoundedReferenceFields() {
        assertEquals("",RegistrationPolicy.apartmentDetails(" Community "," Pune "," Street 1 "," 500001 "));
        assertFalse(RegistrationPolicy.apartmentDetails("","Pune","Street 1","500001").isEmpty());
        assertFalse(RegistrationPolicy.apartmentDetails("Community"," ","Street 1","500001").isEmpty());
        assertFalse(RegistrationPolicy.apartmentDetails("Community","Pune","","500001").isEmpty());
        for(String code:new String[]{"","50001","abcdef","5000001"})assertFalse(RegistrationPolicy.apartmentDetails("Community","Pune","Street 1",code).isEmpty());
        assertFalse(RegistrationPolicy.apartmentDetails(new String(new char[81]).replace('\0','x'),"Pune","Street 1","500001").isEmpty());
        assertFalse(RegistrationPolicy.apartmentDetails("Community",new String(new char[81]).replace('\0','x'),"Street 1","500001").isEmpty());
        assertFalse(RegistrationPolicy.apartmentDetails("Community","Pune",new String(new char[301]).replace('\0','x'),"500001").isEmpty());
    }
    @Test public void personRequiresConsentAndValidDetails() {
        assertEquals("",RegistrationPolicy.person("Resident","9876543210","123456",true,false));
        assertFalse(RegistrationPolicy.person("Resident","9876543210","123456",false,false).isEmpty());
        assertFalse(RegistrationPolicy.person(" ","9876543210","123456",true,true).isEmpty());
        assertFalse(RegistrationPolicy.person("Resident","1234567890","123456",true,false).isEmpty());
        assertFalse(RegistrationPolicy.person("Resident","9876543210","123",true,false).isEmpty());
        // New accounts use the six-digit rule shared with Change PIN.
        for(String pin:new String[]{"1234","12345"})assertFalse(RegistrationPolicy.person("Resident","9876543210",pin,true,false).isEmpty());
    }
    @Test public void apartmentRequiresReferenceDetails() {
        assertEquals("",RegistrationPolicy.apartment("Community","Street 1","500001","5"));
        assertEquals("",RegistrationPolicy.apartment("Community","Street 1","500001","50"));
        for(String count:new String[]{"4","51","","five","99999999999"})assertFalse(RegistrationPolicy.apartment("Community","Street 1","500001",count).isEmpty());
        assertFalse(RegistrationPolicy.apartment("Community","","500001","5").isEmpty());
        assertFalse(RegistrationPolicy.apartment("Community","Street 1","50001","5").isEmpty());
        assertFalse(RegistrationPolicy.apartment(null,"Street 1","500001","5").isEmpty());
    }
}
