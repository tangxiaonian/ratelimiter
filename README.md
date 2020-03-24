# Ratelimiter

限流方法:
- 基于guava的令牌桶算法
- redis限制同一请求的访问次数
- redis + Lua 分布式限流