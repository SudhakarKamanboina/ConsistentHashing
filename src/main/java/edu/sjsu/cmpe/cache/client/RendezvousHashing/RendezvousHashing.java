package edu.sjsu.cmpe.cache.client.RendezvousHashing;

import com.google.common.collect.Sets;
import com.google.common.hash.Funnel;
import com.google.common.hash.HashFunction;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
/**
 * Created by sudh on 5/10/2015.
 */

public class RendezvousHashing<K, N> {

	/**
	 * A hashing function from guava, ie Hashing.murmur3_128()
	 */
	private HashFunction hasher;

	/**
	 * A funnel to describe how to take the key and add it to a hash.
	 * 
	 * @see com.google.common.hash.Funnel
	 */
	private Funnel<K> keyFunnel;

	/**
	 * Funnel describing how to take the type of the node and add it to a hash
	 */
	private Funnel<N> nodeFunnel;

	/**
	 * All the current nodes in the pool
	 */
	private Set<N> nodes;
	private volatile N[] sortedOrdered;

	/**
	 * Creates a new RendezvousHash with a starting set of nodes provided by init. The funnels will be used when generating the hash that combines the nodes and
	 * keys. The hasher specifies the hashing algorithm to use.
	 */
	public RendezvousHashing(HashFunction hasher, Funnel<K> keyFunnel, Funnel<N> nodeFunnel, Collection<N> init) {
		this.hasher = hasher;
		this.keyFunnel = keyFunnel;
		this.nodeFunnel = nodeFunnel;
		this.nodes = Sets.newHashSet();
		this.sortedOrdered = (N[]) new Object[0];
		for (N node: (N[]) init.toArray()) {
			add(node);
		}
	}

	/**
	 * Removes a node from the pool. Keys that referenced it should after this be evenly distributed amongst the other nodes
	 * 
	 * @return true if the node was in the pool
	 */
	public synchronized boolean remove(N node) {
		boolean ret = nodes.remove(node);
		N[] tmp = (N[]) nodes.toArray();
		Arrays.sort(tmp);
		sortedOrdered = tmp;
		return ret;
	}

	/**
	 * Add a new node to pool and take an even distribution of the load off existing nodes
	 * 
	 * @return true if node did not previously exist in pool
	 */
	public synchronized boolean add(N node) {
		boolean ret = nodes.add(node);
		N[] tmp = (N[]) nodes.toArray();
		Arrays.sort(tmp);
		sortedOrdered = tmp;
		return ret;
	}

	/**
	 * return a node for a given key
     * takes the maximum of the hash value to select a node
     * as the maximum would be same for a particular key it gets hash without much collisions
	 */
	public N get(K key) {
		long maxValue = Long.MIN_VALUE;
		N max = null;
		for (N node : sortedOrdered) {
			long nodesHash = hasher.newHasher()
					.putObject(key, keyFunnel)
					.putObject(node, nodeFunnel)
					.hash().asLong();
			if (nodesHash > maxValue) {
				max = node;
				maxValue = nodesHash;
			}
		}
		return max;
	}
}
