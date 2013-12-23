# Imports the monkeyrunner modules used by this program
from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice

# Connects to the current device, returning a MonkeyDevice object
device = MonkeyRunner.waitForConnection()

effectSwitchDelay = 1

MonkeyRunner.sleep(1)

device.touch(313,1120,MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(1)		
device.touch(80,317,MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(1)
device.touch(660,95,MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(1)
device.touch(589,193,MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(1)

while (True):
	for i in range(0,6):
		device.touch(525,134,MonkeyDevice.DOWN_AND_UP)
		MonkeyRunner.sleep(effectSwitchDelay)

	for i in range(0,7):
		device.touch(264,134,MonkeyDevice.DOWN_AND_UP)
		MonkeyRunner.sleep(1)	
	

# Takes a screenshot
#result = device.takeSnapshot()

# Writes the screenshot to a file
#result.writeToFile('shot1.png','png')