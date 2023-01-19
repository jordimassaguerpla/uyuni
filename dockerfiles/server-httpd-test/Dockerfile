FROM ghcr.io/jordimassaguerpla/uyuni/server-httpd-dev:master
#TODO
#COPY files
#RUN call make build and make install to build nodejs. See spacewalk-web
CMD cd web && make -f Makefile.spacewalk-web PERLARGS="INSTALLDIRS=vendor" 
# RUN echo "test" > /srv/www/htdocs/test.html
# CMD /usr/sbin/start_apache2 -DFOREGROUND -k start
