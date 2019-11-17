package listeners;

import java.util.HashMap;

public interface IReduceRequest {
    public void onReduceResponse(HashMap<String, Integer> result);
}