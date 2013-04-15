package annotation_example;

import com.tw.annotation.AutoInject;
import com.tw.annotation.Pill;

@Pill(name="brufenBottle")
public class BrufenBottle {

    @AutoInject(name="brufen")
    private Brufen pill;

    public void setPill(Brufen pill) {
        this.pill = pill;
    }

    public Brufen getPill() {
        return pill;
    }
}
