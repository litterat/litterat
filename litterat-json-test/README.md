
# Litterat:pep-test (Test and Sample classes for Litterat:pep)

This is a set of samples and test classes for the main litterat-json using the litterat-pep library.

## Running tests in eclipse

To execute JUnit tests within eclipse requires a few steps after running 'gradle eclipse'.

 - Move the JUnit libraries from class path to module path.
 - Remove the litterat-pep jar and add the litterat-pep project 
 - Add the following opens to VM arguments:
 
      --add-opens org.junit.platform.commons/org.junit.platform.commons.util=ALL-UNNAMED 
      --add-opens    org.junit.platform.commons/org.junit.platform.commons.logging=ALL-UNNAMED

## License

Litterat-pep-test is available under the Apache 2 License. Please see the LICENSE file for more information.

## Copyright

Copyright (c) 2020, Live Media Pty. Ltd. All Rights Reserved.
