# Imports the monkeyrunner modules used by this program
from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice

# Connects to the current device, returning a MonkeyDevice object
device = MonkeyRunner.waitForConnection()


MonkeyRunner.sleep(1)


while (True):
	device.touch(200,281,MonkeyDevice.DOWN_AND_UP)
	MonkeyRunner.sleep(5)		
	device.touch(51,120,MonkeyDevice.DOWN_AND_UP)
	MonkeyRunner.sleep(1)
	device.touch(200,407,MonkeyDevice.DOWN_AND_UP)
	MonkeyRunner.sleep(5)
	device.touch(51,120,MonkeyDevice.DOWN_AND_UP)
	MonkeyRunner.sleep(1)
	device.touch(200,539,MonkeyDevice.DOWN_AND_UP)
	MonkeyRunner.sleep(5)
	device.touch(51,120,MonkeyDevice.DOWN_AND_UP)
	MonkeyRunner.sleep(1)	
	

# Takes a screenshot
#result = device.takeSnapshot()

# Writes the screenshot to a file
#result.writeToFile('shot1.png','png')