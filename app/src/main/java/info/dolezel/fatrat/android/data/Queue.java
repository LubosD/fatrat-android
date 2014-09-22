package info.dolezel.fatrat.android.data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lubos on 9/22/14.
 */
public class Queue {
    public String name;
    public UUID uuid;
    public String defaultDirectory, moveDirectory;
    public int transferLimitUp, transferLimitDown;
    public int speedLimitUp, speedLimitDown;
    public boolean upAsDown;

    public static Queue fromMap(Map<String, Object> map) {
        Queue q = new Queue();
        String strUUID;
        List<Object> array;

        q.name = map.get("name").toString();

        strUUID = map.get("uuid").toString();
        strUUID = strUUID.substring(1, strUUID.length()-1);
        q.uuid = UUID.fromString(strUUID);

        q.defaultDirectory = map.get("defaultDirectory").toString();
        q.moveDirectory = map.get("moveDirectory").toString();

        array = (List<Object>) map.get("speedLimits");
        q.speedLimitDown = ((Number) array.get(0)).intValue();
        q.speedLimitUp = ((Number) array.get(1)).intValue();

        array = (List<Object>) map.get("transferLimits");
        q.transferLimitDown = ((Number) array.get(0)).intValue();
        q.transferLimitUp = ((Number) array.get(1)).intValue();

        q.upAsDown = (Boolean) map.get("upAsDown");

        return q;
    }
}
