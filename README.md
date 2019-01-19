# yigal-bigPanda
yigal-bigPanda excersize

This repository contains an eclipse workspace with the yigal-bigPanda java maven project.
A runnable jar also exists in the project home directory (together with the windows splitter)
The program expect one string parameter - the path of the splitter.
For example you run from the project home directory:
java -jar panda.jar generator-windows-amd64.exe
 
Then, to get statistics, you can use simple HTTP get calls
for instance using those URLs from local browser: 
http://localhost:8000/cntEvents?foo
http://localhost:8000/cntWords?dolor

Next improvements ( upon requirements )
1. Improve the HTTP interface ,a.event types and data word  that are not URL-safe, more flexible API to get more stats in one call etc' 
2. Handle more complicated data (more than one word , different separators etc')
3. protecting (multi-threading-wise) the stats data to enable safely more than one writer
4. on very large scale - distributing the subscribing and calculate statistics to multiple threads (or on very large scale - multiple comupters )    
