All of the libraries/JAR files in this directories are from external
projects:

  - the jdom1.jar library was extracted from the ZIP file downloaded directly
    from http://jdom.org/dist/binary/archive/jdom-1.1.3.zip; according to the
    project's FAQ at http://jdom.org/docs/faq.html#a0030 it's licensed under
    an Apache-style license, a copy of which should be in the file named
    jdom-LICENSE.txt in the same directory that this file is in
  - the xalan2.jar and serializer.jar libraries were taken from version
    2.7.2-2 of the Debian libxalan2-java package; according to the package's
    homepage at http://xalan.apache.org/xalan-j/ it's licensed under the
    Apache Software License Version 2.0, a copy of which should be in the
    file named Apache-2.0 in the same directory that this file is in
  - the xercesImpl.jar library was taken from version 2.12.0-1 of the Debian
    libxerces2-java package; according to the package's homepage at
    http://xerces.apache.org/xerces2-j/ it's licensed under the Apache
    Software License at http://www.apache.org/licenses/LICENSE-2.0, a copy of
    which should be in the file named Apache-2.0 in the same directory that
    this file is in
    - Note: this library doesn't appear to be strictly necessary, at least
      under Java 11, but its presence here prevents warnings about illegal
      reflective accesses by org.jdom.adapters.JAXPDOMAdapter, which warnings
      are apparently set to become actual errors in later versions of Java
