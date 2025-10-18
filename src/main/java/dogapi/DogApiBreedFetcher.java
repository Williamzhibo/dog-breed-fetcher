package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) {
        try {
            Request request = new Request.Builder()
                .url("https://dog.ceo/api/breeds/list/all")
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new BreedNotFoundException(breed);
                }

                String responseBody = response.body().string();
                JSONObject json = new JSONObject(responseBody);
                
                if (!"success".equals(json.getString("status"))) {
                    throw new BreedNotFoundException(breed);
                }

                JSONObject breeds = json.getJSONObject("message");

                if (!breeds.has(breed)) {
                    throw new BreedNotFoundException(breed);
                }

                JSONArray subBreeds = breeds.getJSONArray(breed);
                
                List<String> result = new ArrayList<>();
                for (int i = 0; i < subBreeds.length(); i++) {
                    result.add(subBreeds.getString(i));
                }
                
                return result;
            }
        } catch (IOException e) {
            throw new BreedNotFoundException(breed);
        }
    }
}