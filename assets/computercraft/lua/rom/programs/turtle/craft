
if not turtle.craft then
    print( "Requires a Crafty Turtle" )
    return
end

local tArgs = { ... }
local nLimit = nil
if #tArgs < 1 then
	print( "Usage: craft [number]" )
	return
else
	nLimit = tonumber( tArgs[1] )
end

local nEmptySlot = nil
for n=1,16 do
    if turtle.getItemCount(n) == 0 then
        nEmptySlot = n
		turtle.select(nEmptySlot)
        break
	end
end

if nEmptySlot and turtle.craft( nLimit ) then
	local nCount = turtle.getItemCount(nEmptySlot)
	print( nCount.." items crafted" )
else
    print( "No items crafted" )
end
turtle.select(1)
