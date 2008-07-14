#!/usr/bin/python
import sys


def toBinary(value):
	s = ""
	v = 128
	for i in range(8):
		if value & v:
			s += "1"
		else:
			s += "0"
		v /= 2
	return s

binstr = ""
for n in range(1, len(sys.argv)):
	try:
		value = int(sys.argv[n])
	except ValueError:
		value = int(sys.argv[n], 16)
	bin = toBinary(value)
	print "%3d = 0x%02x = %sb" % (value, value, bin),
	if (31 < value and value < 255):
		print " = %c" % value
	else:
		print ""
	binstr += bin + " "

print binstr
