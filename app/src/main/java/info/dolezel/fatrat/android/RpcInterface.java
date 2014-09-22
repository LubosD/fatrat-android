package info.dolezel.fatrat.android;

import java.util.List;
import java.util.Map;
import com.nmote.xr.XRMethod;

/**
 * Created by lubos on 9/22/14.
 */
public interface RpcInterface {

    @XRMethod("getQueues")
    public List<Map<String, String>> getQueues();

    @XRMethod("Queue.getTransfers")
    public List<Map<String, String>> getTransfers(String queueUUID);
}
