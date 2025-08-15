local key = 'EXP_TABLE'
if redis.call('EXISTS', key) == 0 then
    local baseExp = 100
    local multiplier = 1.09
    local maxLevel = 100

    local pSum = 0;
    redis.call('ZADD', key, 0, tostring(1))

    for level = 2, maxLevel do
        local exp = math.ceil(baseExp * (multiplier ^ (level - 2)))
        pSum = pSum + exp
        redis.call('ZADD', key, pSum, tostring(level))
    end
end