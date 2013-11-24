all:
	javac WC.java -classpath .:hadoop-core-1.2.1.jar
	jar cvf wc.jar WC.class WCMapper.class WCReducer.class

clean:
	rm wc.jar WC.class WCMapper.class WCReducer.class
