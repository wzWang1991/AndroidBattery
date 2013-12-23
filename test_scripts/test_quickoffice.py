# Imports the monkeyrunner modules used by this program
from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice

# Connects to the current device, returning a MonkeyDevice object
device = MonkeyRunner.waitForConnection()


MonkeyRunner.sleep(1)


while (True):
	device.touch(239,281,MonkeyDevice.DOWN_AND_UP)
	MonkeyRunner.sleep(3)	
	device.drag((350, 1000), (350, 500), 0.5, 50)
	MonkeyRunner.sleep(1)	
	device.drag((350, 1000), (350, 500), 0.5, 50)
	MonkeyRunner.sleep(2)
	device.touch(51,120,MonkeyDevice.DOWN_AND_UP)
	MonkeyRunner.sleep(1)

	

# Takes a screenshot
#result = device.takeSnapshot()

# Writes the screenshot to a file
#result.writeToFile('shot1.png','png')