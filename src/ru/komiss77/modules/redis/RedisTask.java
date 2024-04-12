package ru.komiss77.modules.redis;

import java.util.concurrent.Callable;
import redis.clients.jedis.UnifiedJedis;

public abstract class RedisTask<V> implements Runnable, Callable<V> {


  @Override
  public V call() throws Exception {
    return execute();
  }

  public abstract V unifiedJedisTask(UnifiedJedis unifiedJedis);

  @Override
  public void run() {
    this.execute();
  }

  public V execute(){
    // JedisCluster, JedisPooled in fact is just UnifiedJedis does not need new instance since its single instance anyway.
   // if (api.getMode() == RedisBungeeMode.SINGLE) {
   // JedisPoolProvider jedisSummoner = (JedisPoolProvider) RDS.summoner;
     // return this.unifiedJedisTask(jedisSummoner.obtainResource());
      return unifiedJedisTask(RDS.poolProvider.obtainResource());
    //}// else if (api.getMode() == RedisBungeeMode.CLUSTER) {
    //  JedisClusterSummoner jedisClusterSummoner = (JedisClusterSummoner) summoner;
    //  return this.unifiedJedisTask(jedisClusterSummoner.obtainResource());
   // }
   // return null;
  }

//  public RedisBungeePlugin<?> getPlugin() {
 //   if (plugin == null) {
 //     throw new NullPointerException("Plugin is null in the task");
   // }
   // return plugin;
 // }
}
