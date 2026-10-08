package com.rapolus.apartmentpilotai.data;
import org.junit.Test;
import static org.junit.Assert.*;
public class AttachmentPolicyTest {
    @Test public void filenamesCannotSpecifyPaths() {
        assertEquals(".._private_bill.png",AttachmentPolicy.name("../private/bill","image/png"));
        assertEquals("attachment.pdf",AttachmentPolicy.name("..","application/pdf"));
        assertEquals("proof.JPG",AttachmentPolicy.name("proof.JPG","image/jpeg"));
    }
    @Test public void validSignaturesAreAccepted() {
        AttachmentPolicy.validate(new byte[]{(byte)137,80,78,71,13,10,26,10},"image/png");
        AttachmentPolicy.validate(new byte[]{37,80,68,70,45,49,46,55},"application/pdf");
        AttachmentPolicy.validate(new byte[]{(byte)255,(byte)216,(byte)255,1,2,3,4,5},"image/jpeg");
    }
    @Test public void mismatchedUnsupportedAndOversizedContentIsRejected() {
        assertThrows(IllegalArgumentException.class,()->AttachmentPolicy.validate(new byte[]{37,80,68,70,45,49,46,55},"image/png"));
        assertThrows(IllegalArgumentException.class,()->AttachmentPolicy.extension("text/html"));
        assertThrows(IllegalArgumentException.class,()->AttachmentPolicy.validate(new byte[AttachmentPolicy.MAX_BYTES+1],"application/pdf"));
    }
    @Test public void previewDimensionsAreBounded() {
        assertEquals(1,AttachmentPolicy.sample(900,1200));
        assertEquals(8,AttachmentPolicy.sample(8000,12000));
        assertThrows(IllegalArgumentException.class,()->AttachmentPolicy.sample(0,10));
    }
}
