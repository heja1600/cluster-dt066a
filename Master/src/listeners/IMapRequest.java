package listeners;

import java.util.ArrayList;
import java.util.HashMap;

public interface IMapRequest {
    public void onMapResponse(HashMap<String, ArrayList<String>> mappedValues);
}