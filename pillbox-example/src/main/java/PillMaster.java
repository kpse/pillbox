import com.tw.annotation.PillScanner;
import com.tw.container.PillBox;
import milk.BabyMilk;
import milk.ManualMilk;
import pills.Cocaine;
import pills.Marijuana;
import pills.Methamphetamine;
import scan_targets.Bullet;
import scan_targets.Gun;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import static com.google.common.io.Resources.getResource;

public class PillMaster {
    public static void main(String[] arg) throws Exception {
        configuredExample();
        scannedExample();
    }

    private static void scannedExample() throws Exception {
        PillScanner pillScanner = new PillScanner();
        pillScanner.scanPackage("scan_targets");
        final PillBox pillBox = PillBox.fromScanner(pillScanner);
        System.out.println(pillBox.<Bullet>createPill("bullet"));
        final Gun gun = pillBox.createPill("gun");
        System.out.println(gun);
        System.out.println(gun.getBullet());
    }

    private static void configuredExample() throws Exception {
        final URL resource = getResource("application_context.yml");
        final PillBox pillBox = PillBox.loadContext(resource.getFile());
        System.out.println(pillBox.<Methamphetamine>createPill("a"));
        System.out.println(pillBox.<Marijuana>createPill("b"));
        System.out.println(pillBox.<Cocaine>createPill("c"));

        BabyMilk milk = pillBox.createPill("milk");
        System.out.println(milk);
        System.out.println(milk.getFat());
        System.out.println(milk.getLinoleicAcid());
        System.out.println(milk.getProtein());
        ManualMilk milk2 = pillBox.createPill("manual_milk");
        System.out.println(milk2);
        System.out.println(milk2);
        System.out.println(milk2.getFat());
        System.out.println(milk2.getLinoleicAcid());
        System.out.println(milk2.getProtein());
    }
}
