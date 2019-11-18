
all:
	@source ~/env.sh; sbt fastOptJS
loop:
	@source ~/env.sh; sbt ~fastOptJS

clean: 
	@source ~/env.sh; sbt clean
prepare:
	npm install mkdirp


