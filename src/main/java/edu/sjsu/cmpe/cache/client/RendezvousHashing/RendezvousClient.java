package edu.sjsu.cmpe.cache.client.RendezvousHashing;

import java.util.ArrayList;
import java.util.List;

import com.google.common.hash.*;
import edu.sjsu.cmpe.cache.client.CacheService.CacheServiceInterface;
import edu.sjsu.cmpe.cache.client.CacheService.DistributedCacheService;

/**
 * Created by sudh on 5/10/2015.
 */

public class RendezvousClient {

    private static String server1="http://localhost:3000";
    private static  String server2="http://localhost:3001";
    private static String server3="http://localhost:3002";
    static char values[]={'a','b','c','d','e','f','g','h','i','j'};
    private static final Funnel<CharSequence> strFunnel = Funnels.stringFunnel();
    private static final Funnel<Integer> intFunnel = Funnels.integerFunnel();
    public static void main(String[] args) throws Exception {


        List<String> servers = new ArrayList<String>();

        System.out.println("Running the Rendezvous Hashing cache client..");
        // make a list of servers to be passed as nodes to the hashing constructor
        servers.add(server1);
        servers.add(server2);
        servers.add(server3);

        System.out.println("Getting values from different servers..");
        System.out.println("-----------------------------------------");
        System.out.println("Server                | Get(Key)=>Value");
        System.out.println("-----------------------------------------");
        for(int i=0;i<10;i++){
            RendezvousHashing<Integer, String> h = new RendezvousHashing(Hashing.murmur3_128(), intFunnel, strFunnel, servers);

            String distributedServerInstance=h.get(new Integer(i));
            CacheServiceInterface cacheServer = new DistributedCacheService(distributedServerInstance);
            cacheServer.put(i + 1, String.valueOf(values[i]));
            cacheServer.get(i + 1);
            int j=i+1;
            System.out.println(distributedServerInstance + " | Get(" + j + ")=>" + cacheServer.get(i + 1));


        }
        System.out.println("-----------------------------------------");
        System.out.println("Existing Cache Client...");

    }


}