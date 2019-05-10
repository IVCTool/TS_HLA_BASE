FROM busybox
ENTRYPOINT [ "sh", "-c", " \
   if [ -n \"$VOLUME\" -a -n \"$1\" ]; then \
      if [ -n \"$OPTS\" ]; then \
         cp $OPTS $VOLUME $1; \
      else \
         cp -a $VOLUME $1; \
      fi; \
      echo \"Copied $VOLUME to $1\"; \
   fi" , \
"--" ]

ADD ./build/distributions/TS_HLA_Declaration-1.0.2-SNAPSHOT.tar /root/conf/TestSuites/
ENV VOLUMEDIR /root/conf/TestSuites/TS_HLA_Declaration-1.0.2-SNAPSHOT
ENV VOLUME ${VOLUMEDIR}/.
VOLUME ${VOLUMEDIR}
