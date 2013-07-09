
local tArgs = { ... }
local nLimit = 1
if #tArgs > 1 then
	print( "Usage: refuel [number]" )
	return
elseif #tArgs > 0 then
	if tArgs[1] == "all" then
		nLimit = 64 * 16
	else
		nLimit = tonumber( tArgs[1] )
	end
end

if turtle.getFuelLevel() ~= "unlimited" then
	for n=1,16 do
		local nCount = turtle.getItemCount(n)
		if nCount > 0 then
			turtle.select( n )
			if nCount >= nLimit then
				if turtle.refuel( nLimit ) then
					break 
				end
			else
				if turtle.refuel( nCount ) then
					nLimit = nLimit - nCount
				end
			end
		end
	end
end

print( "Fuel level is "..turtle.getFuelLevel() )
