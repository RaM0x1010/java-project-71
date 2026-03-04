run-dist:
	cd app/ && ./build/install/app/bin/app
#install:
#	cd app/ && gradle installDist
#checkstyle:
#	cd app/ && gradle checkstyleMain
#clean-install:
#	cd app/ && gradle clean build
#restart: clean-install install run-dist
.PHONY: build
