# Resistor-Colour-Calculator
Hi, this is my first real -kind of useful- project I have made and I have decided to share it here if anyone is interested!  
This is supposed to be my first university project and I did this.  
One of the requirements were to use processing, mostly because they don't expect 1st year students to know a lot. I kind of cheated there but I used it for the GUI :3.  
Professor approved though, all good :D  
This is the main reason why Resistor colour calculator class has BigBox and Box as inner classes instead of separate classes. It's some weird thing with processing,  which I dont
understand yet.  It forced me to write Processing dependent objects in the main class which extends the PApplet.  
I could've have extracted the dependent methods out into different child objects and then extended these objects by their "processing equivalent" which contain those methods and   are part of the main class. But that's also ugly :/  
All of this I have written by myself with very little help (I looked some stuff up online, for example I/O things for the table and how to get processing to work in IntelliJ.  
