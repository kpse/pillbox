package annotation_example;

import com.tw.annotation.AutoInject;
import com.tw.annotation.Pill;

@Pill(name = "brufenBottle")
public class BrufenBottle {

    @AutoInject(name = "annotation_example.Brufen")
    private Brufen pill;

    public void setPill(Brufen pill) {
        this.pill = pill;
    }

    public Brufen getPill() {
        return pill;
    }
}
