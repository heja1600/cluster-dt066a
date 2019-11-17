package listeners;

import java.util.ArrayList;
import java.util.HashMap;

public interface IReverseIndexRequest {
    public void onReverseIndexResponse(HashMap<String, ArrayList<String>> reversedValues);
}