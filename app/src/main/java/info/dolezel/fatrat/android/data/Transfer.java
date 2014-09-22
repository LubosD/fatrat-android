package info.dolezel.fatrat.android.data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lubos on 9/22/14.
 */
public class Transfer {
    public static enum State { Active, ForcedActive, Paused, Waiting, Completed};
    public static enum Mode { Download, Upload };

    public String name, clazz, message;
    public State state;
    public Mode mode, primaryMode;
    public String dataPath;
    public boolean dataPathIsDir;
    public long total, done;
    public UUID uuid;
    public String comment, object;
    public double timeRunning;
    public int speedDown, speedUp;
    public int speedLimitDown, speedLimitUp;

    public static Transfer fromMap(Map<String, Object> map) {
        Transfer t = new Transfer();
        List<Object> array;
        String strUUID;

        t.name = map.get("name").toString();
        t.clazz = map.get("class").toString();
        t.message = map.get("message").toString();
        t.dataPath = map.get("dataPath").toString();
        t.dataPathIsDir = ((Boolean) map.get("dataPathIsDir"));
        t.total = Long.parseLong(map.get("total").toString());
        t.done = Long.parseLong(map.get("done").toString());
        t.timeRunning = ((Double) map.get("timeRunning"));

        array = (List<Object>) map.get("speeds");
        t.speedDown = ((Number) array.get(0)).intValue();
        t.speedUp = ((Number) array.get(1)).intValue();

        array = (List<Object>) map.get("userSpeedLimits");
        t.speedLimitDown = ((Number) array.get(0)).intValue();
        t.speedLimitUp = ((Number) array.get(1)).intValue();

        strUUID = map.get("uuid").toString();
        strUUID = strUUID.substring(1, strUUID.length()-1);
        t.uuid = UUID.fromString(strUUID);

        t.comment = map.get("comment").toString();
        t.object = map.get("object").toString();

        t.mode = Mode.valueOf(map.get("mode").toString());
        t.primaryMode = Mode.valueOf(map.get("primaryMode").toString());
        t.state = State.valueOf(map.get("state").toString());

        return t;
    }
}
