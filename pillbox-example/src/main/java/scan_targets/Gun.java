package scan_targets;

import com.tw.annotation.AutoInject;
import com.tw.annotation.Pill;

@Pill(name = "gun")
public class Gun {
    @AutoInject(name = "scan_targets.Bullet")
    private Bullet bullet;

    public Bullet getBullet() {
        return bullet;
    }

    public void setBullet(Bullet bullet) {
        this.bullet = bullet;
    }
}
