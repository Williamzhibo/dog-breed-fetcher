package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    private final BreedFetcher fetcher;
    private final Map<String, List<String>> cache;
    private int callsMade = 0;

    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.fetcher = fetcher;
        this.cache = new HashMap<>();
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        // If we have a cached result, return it without incrementing callsMade
        if (cache.containsKey(breed)) {
            return new ArrayList<>(cache.get(breed));
        }

        // We're about to make a call, so increment the counter
        callsMade++;
        
        try {
            // Try to get sub-breeds from the underlying fetcher
            List<String> subBreeds = fetcher.getSubBreeds(breed);
            
            // Only cache if the call was successful
            cache.put(breed, new ArrayList<>(subBreeds));
            
            return subBreeds;
        } catch (BreedNotFoundException e) {
            // The call was still made even though it failed
            throw e;
        }
    }

    public int getCallsMade() {
        return callsMade;
    }
}