
all:
	@source ~/env.sh; sbt fastOptJS
loop:
	@source ~/env.sh; sbt ~fastOptJS
chunk:
		node src/main/js/chunker.js
allchunk: all chunk
clean:
	@source ~/env.sh; sbt clean
prepare:
	npm install mkdirp
