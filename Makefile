
all:
	@source ~/env.sh; sbt fullOptJS
loop:
	@source ~/env.sh; sbt ~fullOptJS

clean: 
	@source ~/env.sh; sbt clean


