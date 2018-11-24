########################
########################
#instructions for injecting code:
########################
#try:
	./inject
#or
	scala -cp /opt/java/jdk1.8.0_102/lib/tools.jar inject.scala


########################
#for testing code in scala (testScalaCode folder):
########################
#run:
	watch -n 0.5 scalac main.scala
#and in the meantime :
	sbt ~run  (will execute your code)



