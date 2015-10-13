STATE_WALK = 0
STATE_ATTACK = 2

function attackSkill1(enemyPos, monsterPos)
  
  local animationState = "shoot"
  local magnification = 1
  local minimumRange = 0
  local maximumRange = 300
  local nuckback = 300
  local delay = 2000 --초
  local nuckbackSpeed = 10
  
  local maximumDistance = monsterPos.x - minimumRange;
  local minimumDistance = monsterPos.x - maximumRange;
  
  if enemyPos.x > minimumDistance and enemyPos.x < maximumDistance then 
    return STATE_ATTACK, animationState, magnification, minimumRange, maximumRange, delay, nuckback, nuckbackSpeed
  else 
    return STATE_WALK
  end

end

-- collision
function checkDistance(enemyPos, monsterPos, skill)
  local resultTable = {}
  local unitState, animationState, magnification, minimumRange, maximumRange, delay, nuckback, nuckbackSpeed = attackSkill1(enemyPos, monsterPos)
  
  skill.animationState = animationState
  skill.magnification = magnification
  skill.minimumRange = minimumRange
  skill.maximumRange = maximumRange
  skill.delay = delay
  skill.nuckback = nuckback
  skill.nuckbackSpeed = nuckbackSpeed
  skill.target = 1;
  
  if unitState == STATE_ATTACK then
    return unitState
  else 
    return STATE_WALK
  end
end