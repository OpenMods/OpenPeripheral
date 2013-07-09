
local tMoves = {
	function()
		turtle.up()
		turtle.down()
	end,
	function()
		turtle.up()
		turtle.turnLeft()
		turtle.turnLeft()
		turtle.turnLeft()
		turtle.turnLeft()
		turtle.down()
	end,
	function()
		turtle.up()
		turtle.turnRight()
		turtle.turnRight()
		turtle.turnRight()
		turtle.turnRight()
		turtle.down()
	end,
	function()
		turtle.turnLeft()
		turtle.turnLeft()
		turtle.turnLeft()
		turtle.turnLeft()
	end,
	function()
		turtle.turnRight()
		turtle.turnRight()
		turtle.turnRight()
		turtle.turnRight()
	end,
	function()
		turtle.turnLeft()
		turtle.back()
		turtle.back()
		turtle.turnRight()
		turtle.turnRight()
		turtle.back()
		turtle.back()
		turtle.turnLeft()
	end,
	function()
		turtle.turnRight()
		turtle.back()
		turtle.back()
		turtle.turnLeft()
		turtle.turnLeft()
		turtle.back()
		turtle.back()
		turtle.turnRight()
	end,
	function()
		turtle.back()
		turtle.turnLeft()
		turtle.back()
		turtle.turnLeft()
		turtle.back()
		turtle.turnLeft()
		turtle.back()
		turtle.turnLeft()
	end,
	function()
		turtle.back()
		turtle.turnRight()
		turtle.back()
		turtle.turnRight()
		turtle.back()
		turtle.turnRight()
		turtle.back()
		turtle.turnRight()
	end,
}

textutils.slowWrite( "Preparing to get down" )
textutils.slowPrint( "...", 1 )

local sAudioSide = nil
for n,sSide in pairs( peripheral.getNames() ) do
	if disk.hasAudio( sSide ) then
		disk.playAudio( sSide )
		print( "Jamming to "..disk.getAudioTitle( sSide ) )
		sAudioSide = sSide
		break
	end
end

print( "Press any key to stop the groove" )

local bEnd = false
parallel.waitForAll(
	function()
		while not bEnd do
			local event, key = os.pullEvent("key")
			if key ~= keys.escape then
				bEnd = true
			end
		end		
	end,
	function()
		while not bEnd do
			local fnMove = tMoves[math.random(1,#tMoves)]
			fnMove()
		end
	end
)

if sAudioSide then
	disk.stopAudio( sAudioSide )
end
