FROM opensuse/leap:15.4
# list of packages to installed are build and runtime dependencies from
# spacewalk-java package
RUN zypper -n ar --no-gpgcheck https://download.opensuse.org/repositories/systemsmanagement:/Uyuni:/Master/images/repo/Uyuni-Server-POOL-x86_64-Media1/ Uyuni-Server-POOL-x86_64 && \
    zypper -n  ar --no-gpgcheck https://download.opensuse.org/repositories/systemsmanagement:/Uyuni:/Master:/Other/openSUSE_Leap_15.4/ Uyuni-Server-Master-Other-Build-Deps && \
    zypper -n ar --no-gpgcheck obs://systemsmanagement:Uyuni:Utils systemsmanagement:uyuni:utils && \
    zypper ref -f && \ 
    zypper -n install \
        apache-commons-compress \
        jakarta-commons-discovery \
        apache-commons-fileupload \
        apache-commons-validator \
        ehcache \
        ant \
        ant-apache-regexp \
        ant-contrib \
        ant-junit \
        antlr \
        apache-commons-cli \
        apache-commons-codec \
        apache-commons-collections \
        apache-commons-el \
        apache-commons-io \
        apache-commons-jexl \
        apache-commons-lang3 \
        apache-commons-logging \
        bcel \
        byte-buddy \
        c3p0 \
        cglib \
        concurrent \
        dom4j \
        dwr \
        glassfish-jaxb-runtime \
        glassfish-jaxb-txw2 \
        google-gson \
        hibernate-commons-annotations \
        hibernate-types \
        httpcomponents-asyncclient \
        httpcomponents-client \
        ical4j \
        istack-commons-runtime \
        jade4j \
        java-11-openjdk-devel \
        java-saml \
        javamail \
        javapackages-tools \
        javassist \
        jboss-logging \
        jcommon \
        jdom \
        joda-time \
        jose4j \
        jpa-api \
        jsch \
        jta \
        libxml2 \
        log4j \
        log4j-slf4j \
        netty \
        objectweb-asm \
        perl \
        pgjdbc-ng \
        postgresql13 \
        postgresql-jdbc \
        prometheus-client-java \
        quartz \
        redstone-xmlrpc \
        salt-netapi-client \
        simple-core \
        simple-xml \
        sitemesh \
        snakeyaml \
        snapper \
        spark-core \
        spark-template-jade \
        statistics \
        stringtree-json \
        struts \
        tomcat \
        tomcat-lib \
        tomcat-taglibs-standard \
        uyuni-base-server \
        woodstox \
        xmlsec \
        # gnu-jaf \
        # glassfish-jaxb-api \
        velocity-engine-core \
        hibernate5-c3p0 \
        hibernate5-core \
        hibernate5-ehcache \
        ant-nodeps \
        classmate \
        perl \
        perl-DBI \
        perl-Params-Validate \
        spacewalk-schema \
        curl \
        patch \
        perl-Frontier-RPC \
        perl-XML-LibXML \
        perl-XML-SAX \
        perl-libwww-perl \
        policycoreutils \
        python3-certifi \
        perl-libwww-perl \
        perl-Satcon \
        python3-PyYAML \
        curl \
        perl-Mail-RFC822-Address \
        perl-Net-LibIDN \
        libxslt-devel \
        libxslt-tools \
        libxml2-tools && \
    zypper clean -a

# hacks

RUN zypper -n install --force java-11-openjdk-headless-11.0.15.0-150000.3.80.1 java-11-openjdk-11.0.15.0-150000.3.80.1.x86_64 java-11-openjdk-devel-11.0.15.0-150000.3.80.1.x86_64;
RUN ln -s /usr/bin/python3 /usr/bin/python;mkdir -p /etc/pki/tls/certs/; mkdir -p /etc/pki/tls/private;zypper -n remove busybox-xz;
RUN zypper -n install wget spacewalk-setup spacewalk-branding spacewalk-backend-tools spacewalk-certs-tools susemanager-sls make perl-DBD-Pg spacewalk-java patterns-uyuni_server apache-ivy obs-to-maven

