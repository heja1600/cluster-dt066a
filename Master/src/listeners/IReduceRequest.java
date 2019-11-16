package listeners;

import java.util.ArrayList;
import java.util.HashMap;

public interface IReduceRequest {
    public void onReduceResponse(HashMap<String, ArrayList<String>> mappedValues);
}