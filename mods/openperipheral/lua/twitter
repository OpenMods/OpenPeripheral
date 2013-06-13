-- Code written by Cruor, after request by Mikee
-- Demonstration video can be found at http://www.youtube.com/watch?v=ElFNEgElPdo also by Cruor
-- Help page can be found at http://pastebin.com/2wKZWR0u

local function printUsage()
    local scriptName = shell.getRunningProgram()
    return 'Usage: ' .. scriptName .. ' <side>'
end

local args = {...}
local side = args[1]

assert(http, 'HTTP is required for this program.')
assert(side, printUsage())

local glass = assert(peripheral.wrap(side), printUsage())
glass.clear()

local settings = {
    displayTime = 10,
    autoDisplay = false,
    toastCount = 2,
    display = true,
    timerRate = 2.5
}

local lastID
local watching = {
    ftb_team = true
}

local tweets = {}

local function StatusBar(x, y)
    local t = {
        update = function(self)
            local str = 'You have: ' .. #tweets .. ' unread tweet(s).'
            if self.text.getText() ~= str and settings.display then
                self.text.setText(str)
            end
        end,
        hide = function(self)
            self.text.setText('')

            self.background.setAlpha(0)
            self.background.setAlpha2(0)

            self.background.setWidth(0)
            self.background.setHeight(0)

        end,
        show = function(self)
            if settings.display then
                self.background.setAlpha(0.7)
                self.background.setAlpha2(0.7)

                self.background.setWidth(200)
                self.background.setHeight(16)

                self:update()
            end
        end,
        delete = function(self)
            self.background.delete()
            self.text.delete()
        end,
        background = glass.addBox(x, y, 200, 16, 0x262626, 0),
        text = glass.addText(x + 4, y + 4, '', 0xFFFFFF)
    }
    t.background.setZIndex(0)
    t.text.setZIndex(1)

    return t
end

local function TweetToast(x, y)
    local t = {
        lines = {},
        hidden = true,
        show = function(self, title, text)
            self.hidden = false

            local i = 1
            local str = ''

            for w in string.gmatch(text, '%S+') do
                local currentWidth = glass.getStringWidth(str)
                local wordWidth = glass.getStringWidth(w)

                if currentWidth + wordWidth > 257 then
                    if self.lines[i] and settings.display then
                        self.lines[i].setText(str)
                    end
                    str = ''
                    i = i + 1
                end
                str = str .. w .. ' '
            end
            
            if settings.display then
                if self.lines[i] then
                    self.lines[i].setText(str)
                end

                self.title.setText(title)

                self.background.setAlpha(0.7)
                self.background.setAlpha2(0.7)

                self.background.setWidth(265)
                self.background.setHeight(112)

                self.displayTimeAcc = 0
            end
        end,
        update = function(self, dt)
            self.displayTimeAcc = self.displayTimeAcc + dt
            if not self.hidden and self.displayTimeAcc >= settings.displayTime then
                self:hide()
                self.hidden = true
            end
        end,
        hide = function(self)
            for i = 1, #self.lines do
                self.lines[i].setText('')
            end

            self.background.setWidth(0)
            self.background.setHeight(0)

            self.background.setAlpha(0)
            self.background.setAlpha2(0)

            self.title.setText('')

            self.hidden = true
        end,
        delete = function(self)
            self.background.delete()
            self.title.delete()

            for i = 1, #self.lines do
                self.lines[i].delete()
            end
        end,
        displayTimeAcc = settings.displayTime,
        background = glass.addBox(x, y, 265, 112, 0x262626, 0),
        title = glass.addText(x + 4, y + 4, '', 0xFFFFFF)
    }

    for i = 1, 7 do
        t.lines[i] = glass.addText(x + 4, y + 4 + i * 10, '', 0xFFFFFF)
        t.lines[i].setZIndex(1)
    end

    t.title.setZIndex(1)
    t.background.setZIndex(0)

    return t
end

local function parseTweet(msg)
    local unescapePattern = '&#(%x+);'
    local userPattern = '<a href="/(.-)" class="twitter%-atreply.-</a>'
    local linkPattern = '<a href="(.-)".-</a>'
    local searchPattern = '<a href="/search.-%%23(.-)&amp;.-</a>'

    local msg = string.gsub(msg, unescapePattern, function(s)
        return string.char(s)
    end)

    msg = string.gsub(msg, userPattern, function(s)
        return '@' .. s
    end)

    msg = string.gsub(msg, searchPattern, function(s)
        return '#' .. s
    end)

    msg = string.gsub(msg, linkPattern, function(s)
        return s
    end)

    return msg
end

local function pullTweet(name)
    local data

    local fh = http.get('https://twitter.com/' .. name)
    if fh then
        data = fh.readAll()
        fh.close()
    end

    if data then
        local pattern = 'data%-tweet%-id="(.-)".-data%-screen%-name="(.-)".-"(.-)".-title="(.-)".-<p class="js%-tweet%-text tweet%-text">(.-)</p>'

        local id, screenName, name, timestamp, text = string.match(data, pattern)
        local text = parseTweet(text)

        return id, text, name, screenName, timestamp
    end

    return
end

local function displayToasts(toasts, statusBar)
    for i = 1, #toasts do
        if toasts[i].displayTimeAcc >= settings.displayTime and settings.display then
            if tweets[1] then
                local tweet = tweets[1]
                local title = tweet['name'] .. ' (@' .. tweet['screenName'] .. ')'
                local text = tweet['text']

                toasts[i]:show(title, text)

                table.remove(tweets, 1)

                statusBar:update()
            end
        end
    end
end

local function loadConfig(filename)
    local fh = io.open(filename)
    local data = fh:read('*a')
    fh:close()

    local config = {}

    for line in string.gmatch(data, '[^\n]+') do
        local k, v = string.match(line, '([^ =]+)%s+=%s+(.*)')

        if v == 'true' then
            v = true

        elseif v == 'false' then
            v = false
        end

        if tonumber(v) then
            v = tonumber(v)
        end

        if k and v then
            config[k] = v
        end
    end

    return config
end

local function saveConfig(filename, t)
    local fh = io.open(filename, 'w')

    local data = ''

    for k, v in pairs(t) do
        data = data .. tostring(k) .. ' = ' .. tostring(v) .. '\n'
    end

    fh:write(data)
    fh:close()
end

local function loadWatchingList(filename)
    local fh = io.open(filename)
    local data = fh:read('*a')
    fh:close()

    local list = {}

    for line in string.gmatch(data, '[^\n]+') do
        list[line] = true
    end

    return list
end

local function saveWatchingList(filename, t)
    local fh = io.open(filename, 'w')

    local data = ''

    for k, v in pairs(t) do
        if v then
            data = data .. k .. '\n'
        end
    end

    fh:write(data)
    fh:close()
end

local function watchingNum(watching)
    local i = 0
    for k, v in pairs(watching) do
        if v then
            i = i + 1
        end
    end
    return i
end

local folder = '.twitter'
local settingsFilename = '.twitter/config'
local lastIDFilename = '.twitter/lastID'
local watchingFilename = '.twitter/watching'

if fs.exists(folder) and not fs.isDir(folder) then
    print('File with name .twitter allready exists.')
    print('Please delete this file and re run this program.')
end

if not fs.exists(folder) then
    fs.makeDir(folder)
end

if fs.exists(settingsFilename) then
    print('Loaded config')
    settings = loadConfig(settingsFilename)
else
    print('Saved config to: ' .. settingsFilename)
    saveConfig(settingsFilename, settings)
end

if fs.exists(lastIDFilename) then
    lastID = loadConfig(lastIDFilename)
else
    lastID = {}
end

if fs.exists(watchingFilename) then
    watching = loadWatchingList(watchingFilename)
else
    watching = {}
end


local statusBar = StatusBar(10, 10)
statusBar:show()

local toasts = {}

for i = 1, settings.toastCount do
    toasts[i] = TweetToast(10, 50 + 132 * (i - 1))
    toasts[i]:hide()
end

-- This is to make twitter not hate us!
local sleepLength = 24 * watchingNum(watching) + 1
local updateAcc = sleepLength

local timerID = os.startTimer(settings.timerRate)

while true do
    local event, par1, par2 = os.pullEvent()
    if event == 'timer' then
        timerID = os.startTimer(settings.timerRate)

        if #tweets > 0 and settings.autoDisplay then
            displayToasts(toasts, statusBar)
        end

        for i = 1, #toasts do
            toasts[i]:update(settings.timerRate)
        end

        if updateAcc >= sleepLength then
            updateAcc = updateAcc - sleepLength

            for k, v in pairs(watching) do
                if v then
                    local id, text, name, screenName, timestamp = pullTweet(k)
                    id = tonumber(id)
                    if id then
                        if not lastID[k] or id ~= lastID[k] then
                            lastID[k] = id

                            table.insert(tweets, {
                                text = text,
                                name = name,
                                screenName = screenName,
                                timestamp = timestamp
                            })
                        end
                    end
                end
            end
            statusBar:update()

            saveConfig(lastIDFilename, lastID)
        end

        updateAcc = updateAcc + settings.timerRate

    elseif event == 'chat_command' then
        local command = par1
        local user = par2
        local splitted = {}

        if user then
            print(user .. ' used command: ' .. command)
        else
            print('Command: ' .. command)
        end

        for w in string.gmatch(command, '[^ ]+') do
            table.insert(splitted, w)
        end

        if command == 'hide' then
            settings.display = false
            statusBar:hide()
            
            for i = 1, #toasts do
                toasts[i]:hide()
            end

            saveConfig(settingsFilename, settings)

        elseif command == 'show' then
            settings.display = true
            statusBar:show()

            saveConfig(settingsFilename, settings)

        elseif command == 'display' then
            if not settings.autoDisplay then
                displayToasts(toasts, statusBar)
            end

        elseif command == 'clear' then
            lastID = {}
            saveConfig(lastIDFilename, lastID)

        elseif splitted[1] == 'watch' then
            watching[splitted[2]] = true
            saveWatchingList(watchingFilename, watching)

            sleepLength = math.floor(3600 / 150 * watchingNum(watching)) + 1

            -- Maybe show on the HUD?
            print('Added "' .. splitted[2] .. '" to watching list.')

        elseif splitted[1] == 'unwatch' then
            watching[splitted[2]] = false
            saveWatchingList(watchingFilename, watching)

            sleepLength = 24 * watchingNum(watching) + 1

            print('Removed "' .. splitted[2] .. '" from watching list.')

        elseif splitted[1] == 'set' then
            if splitted[2] == 'displayTime' then
                settings.displaytime = tonumber(splitted[3])

            elseif splitted[2] == 'autoDisplay' then
                if splitted[3] == 'true' then
                    settings.autoDisplay = true

                elseif splitted[3] == 'false' then
                    settings.autoDisplay = false
                end

            elseif splitted[2] == 'toastCount' then
                settings.toastCount = tonumber(splitted[3])

            elseif splitted[2] == 'timerRate' then
                settings.timerRate = tonumber(splitted[3])
            end
            saveConfig(settingsFilename, settings)
        end
    end
end