run-dist:
	cd app/ && ./build/install/app/bin/app
say-hello:
	echo "Hello, World!"
install:
	cd app/ && gradle installDist
checkstyle:
	cd app/ && gradle checkstyleMain
	cd app/ && gradle checkstyleTest
clean-install:
	cd app/ && gradle clean build
restart: clean-install install run-dist
sonar-test:
	cd app/ && gradle build sonar --info
build: checkstyle
	cd app/ && gradle build test

.PHONY: build
