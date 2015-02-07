# breakdown
Work in progress: Integration Testing application. SoapUI doesn't fit the needs. 
Greenhat is too expensive. I don't know any other (free/open source) software
which meets my requirements so for Fun & Profit (mainly Fun) I decided to write
my own.

# Some thoughts
* Place configuration in single XML file. Meaning testsuites, testcases, input
message contents, output message contents, assertions, destinations, stubs, you
name it. Pros: easily portable. Cons: a GUI is *needed* to configure the tests.
* JMS provider independence is great, but for now we keep it to the one I only
need.
* HTTP stubs are handy but not a number one priority. The basics should contain
a queue sender, topic receiver, assertion.
* Console based: tests must be run headless, to feed it to Jenkins for instance.
* GUI: Swing (pretty old), SWT (nice, not sluggish, native controls), JavaFX
(new, perhaps fun to discover features etc.).
* Assertions on response. String assertions, XMLUnit based assertions, XPath
expressions.
* Exit codes. 0 = OK, 1 = assertions failed, 2 = configuration problems.
* Generation of reports. Optional of course. Stdout usage.
* Project contains 1 or more testsuites, which contain 1 or more testcases.
* It's important that a project file can be portable. So no hardcoded paths
or whatever. It must be a true standalone configuration which can be used in
a Linux or Windows environment.
* Classpath configuration to necessary third party JARs **must** be kept out of 
the project XML file. See previous point why. This will require users to adjust
the classpath once in the executable script or whatever.
