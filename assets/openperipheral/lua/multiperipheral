local sideNames = rs.getSides()

table.reduce = function (list, fn) 
    local acc
    for k, v in ipairs(list) do
        if 1 == k then
            acc = v
        else
            acc = fn(acc, v)
        end 
    end 
    return acc 
end

local multiperipheral = {

  -- wrap multiple targets at once
  wrap = function (self, ...)
    local meta = getmetatable(self)
    for k, name in pairs({...}) do
      table.insert(self.names, name)
      for k2, methodName in pairs(peripheral.getMethods(name)) do
        meta.__index[methodName] = function (a, ...)
          local returnVal = {}
          for k3, pName in pairs(a.names) do
            for k4, mName in pairs(peripheral.getMethods(pName)) do
              if mName == methodName then
                table.insert(returnVal, peripheral.call(pName, methodName, ...))
              end
            end
          end
          return returnVal
        end
      end
    end
    return self
  end,
  
  -- wrap multiple targets by their type
  types = function(self, ...)
    local checkTypes = { ... }
    local addNames = {}
    for k, side in pairs(sideNames) do
      local sideType = peripheral.getType(side)
      if sideType == "modem" then
        for k1, remoteName in pairs(peripheral.call(side, "getNamesRemote")) do
          local remoteType = peripheral.call(side, "getTypeRemote", remoteName)
          for k2, checkType in pairs(checkTypes) do
            if remoteType == checkType then
              table.insert(addNames, remoteName)
            end
          end
        end
      else
        for k2, checkType in pairs(checkTypes) do
          if sideType == checkType then
            table.insert(addNames, side)
          end
        end
      end
    end
    
    return self:wrap(unpack(addNames))
  end,
  
  getNames = function(self)
    return self.names
  end

}

local gmetatable = {
  __index = multiperipheral,
  __tostring = function(g) return g:tostring() end,
}

function new()
  local g = {
    names = {}
  }
  setmetatable(g, gmetatable)
  return g
end