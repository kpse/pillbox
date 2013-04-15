import annotation_example.Brufen;
import annotation_example.BrufenBottle;
import com.tw.annotation.PillScanner;
import com.tw.container.PillBox;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class PillScannerTest {
    @Test
    public void should_scan_for_pills_definition() throws Exception {
        PillScanner pillScanner = new PillScanner();
        pillScanner.scanPackage("annotation_example");
        PillBox pillBox = PillBox.fromScanner(pillScanner);
        Brufen brufen = pillBox.createPill("brufen");
        assertThat(brufen, notNullValue());
    }

    @Test
    public void should_scan_for_pills_combination() throws Exception {
        PillScanner pillScanner = new PillScanner();
        pillScanner.scanPackage("annotation_example");
        PillBox pillBox = PillBox.fromScanner(pillScanner);
        BrufenBottle bottle = pillBox.createPill("brufenBottle");
        assertThat(bottle.getPill(), notNullValue());
    }
}
